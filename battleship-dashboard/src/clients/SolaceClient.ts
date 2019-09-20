import solace from "solclientjs";
import { solaceBrokerConfig } from "./solace-broker-config";
import { noView } from "aurelia-framework";
import * as _ from "lodash";

@noView
export class SolaceClient {
  session = null;
  topicSubscriptions: Map<string, any> = new Map<string,any>();
  
  
  constructor() {
    
    let factoryProps = new solace.SolclientFactoryProperties();
    factoryProps.profile = solace.SolclientFactoryProfiles.version10;
    solace.SolclientFactory.init(factoryProps);
  }

  log(line: string) {
    let now = new Date();
    let time = [('0' + now.getHours()).slice(-2), ('0' + now.getMinutes()).slice(-2), ('0' + now.getSeconds()).slice(-2)];
    let timestamp = '[' + time.join(':') + '] ';
    console.log(timestamp + line);
  }

  async connect() {
    return new Promise((resolve, reject) => {
      if (this.session !== null) {
        this.log('Already connected and ready to subscribe.');
        reject();
      }
      // if there's no session, create one
      try {
        this.session = solace.SolclientFactory.createSession({
            // solace.SessionProperties
            url:      solaceBrokerConfig.hostUrl,
            vpnName:  solaceBrokerConfig.vpn,
            userName: solaceBrokerConfig.userName,
            password: solaceBrokerConfig.password,
        });
      } catch (error) {
        this.log(error.toString());
      }
      // define session event listeners
      this.session.on(solace.SessionEventCode.UP_NOTICE,  (sessionEvent) => {
          this.log('=== Successfully connected and ready to subscribe. ===');
          resolve();
      });
      this.session.on(solace.SessionEventCode.CONNECT_FAILED_ERROR, (sessionEvent) => {
          this.log('Connection failed to the message router: ' + sessionEvent.infoStr +
              ' - check correct parameter values and connectivity!');
      });
      this.session.on(solace.SessionEventCode.DISCONNECTED, (sessionEvent) => {
          this.log('Disconnected.');
          if (this.session !== null) {
            this.session.dispose();
            //this.subscribed = false;
            this.session = null;
          }
      });
      this.session.on(solace.SessionEventCode.SUBSCRIPTION_ERROR, (sessionEvent) => {
          this.log('Cannot subscribe to topic: ' + sessionEvent.correlationKey);
          this.topicSubscriptions.delete(sessionEvent.correlationKey);
      });
      this.session.on(solace.SessionEventCode.SUBSCRIPTION_OK, (sessionEvent) => {
          if (this.topicSubscriptions.get(sessionEvent.correlationKey) && this.topicSubscriptions.get(sessionEvent.correlationKey).isSubscribed) {
            this.topicSubscriptions.delete(sessionEvent.correlationKey);
            this.log(`Successfully unsubscribed from topic: ${sessionEvent.correlationKey}`);
          } else {
            this.topicSubscriptions.get(sessionEvent.correlationKey).isSubscribed = true;
            this.log(`Successfully subscribed to topic: ${sessionEvent.correlationKey}`);
          }
      });
      // define message event listener
      this.session.on(solace.SessionEventCode.MESSAGE, (message) => {
        let topicName: string = message.getDestination().getName();
        
        for(let sub of Array.from(this.topicSubscriptions.keys())){
          console.log(`${sub} inner loop`);
          OUTER:
          if(sub.indexOf("*")!=-1 || sub.indexOf(">")!=-1){
            
            let subscriptionTopicSections = sub.split("/");
            let topicNameSections = topicName.split("/");

            //if the stored subscriptions do not contain a > and the lengths of the subscriptions do not match, then end the iteration
            //A/B/C/> == A/B/C/D/E
            if((subscriptionTopicSections.length<topicNameSections.length && sub.indexOf(">")!=sub.length-1) || subscriptionTopicSections.length>topicNameSections.length){
              return;
            }

            //Do a regex match on topic replacing * with .*, if any of the sections do not match then the match fails and try the next one
            for(let i=0;i<subscriptionTopicSections.length-1;i++){
                if(!topicNameSections[i].match(subscriptionTopicSections[i].replace("*",".*")))
                  break OUTER;
            }

            //check the last topic section for > or for an exact match (if the lengths are equivalent)
            if(subscriptionTopicSections[subscriptionTopicSections.length-1]==">" || (topicNameSections.length==subscriptionTopicSections.length &&
               topicNameSections[subscriptionTopicSections.length-1].match(subscriptionTopicSections[subscriptionTopicSections.length-1].replace("*",".*")))){
              this.topicSubscriptions.get(sub).callback(message);
            }
          }
        }

        if(this.topicSubscriptions.get(topicName))
          this.topicSubscriptions.get(topicName).callback(message); 
      });
      // connect the session
      try {
        this.session.connect();
      } catch (error) {
        this.log(error.toString());
      }
    });
  } 
  
  sendRequest(topicName: string, payload: string, callback: any){
      let request = solace.SolclientFactory.createMessage();
      request.setDestination(solace.SolclientFactory.createTopic(topicName));
      request.setSdtContainer(solace.SDTField.create(solace.SDTFieldType.STRING, payload));
      request.setDeliveryMode(solace.MessageDeliveryModeType.DIRECT);

      try {
        this.session.sendRequest(
          request,
          5000,
          (session,message)=>{
            callback(session,message,true);
          },
          (session,event)=>{
            callback(session,event,false);
          },
          null
        );
      }catch(error){
        this.log(error.toString());
      }

  }

  subscribe(topicName: string, callback: any) {
    if(!this.session) {
      this.log("[WARNING] Cannot subscribe because not connected to Solace message router!")
      return;
    }
    if(this.topicSubscriptions.get(topicName)) {
      this.log(`[WARNING] Already subscribed to ${topicName}.`);
      return;
    }
    this.log(`Subscribing to ${topicName}`);
    this.topicSubscriptions.set(topicName,{callback: callback, isSubscribed: false}); // gets updated asynchronously
    try {
      this.session.subscribe(
        solace.SolclientFactory.createTopicDestination(topicName),
        true, // generate confirmation when subscription is added successfully
        topicName, // use topic name as correlation key
        10000 // 10 seconds timeout for this operation
      );
    } catch (error) {
      this.log(error.toString());
    }
  }

  publish(topic: string, payload: string) {
    if(!this.session) {
      this.log("[WARNING] Cannot publish because not connected to Solace message router!")
      return;
    }
    this.log(`Publishing message ${payload} to topic ${topic}...`);
    let message = solace.SolclientFactory.createMessage();
    message.setDestination(solace.SolclientFactory.createTopicDestination(topic));
    message.setBinaryAttachment(payload);
    message.setDeliveryMode(solace.MessageDeliveryModeType.DIRECT);
    try {
        this.session.send(message);
        this.log('Message published.');
    } catch (error) {
        this.log(error.toString());
    }
  }
}
