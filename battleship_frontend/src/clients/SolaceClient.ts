import solace from "solclientjs";
import { gameConfig } from "./game-config";
import { noView } from "aurelia-framework";
import * as _ from "lodash";

class SubscriptionObject {
  callback: any;
  isSubscribed: boolean;

  constructor(_callback:any,_isSubscribed:boolean){
   this.callback=_callback;
   this.isSubscribed=_isSubscribed;
  }
 
}

@noView
export class SolaceClient {

 

  session = null;
  topicSubscriptions: Map<string, any> = new Map<string,SubscriptionObject>();
  
  
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
            url:      gameConfig.solace_hostUrl,
            vpnName:  gameConfig.solace_vpn,
            userName: gameConfig.solace_userName,
            password: gameConfig.solace_password,
            publisherProperties: {
              acknowledgeMode: solace.MessagePublisherAcknowledgeMode.PER_MESSAGE,
          },
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

      this.session.on(solace.SessionEventCode.ACKNOWLEDGED_MESSAGE,  (sessionEvent) => {
        this.log('Delivery of message with correlation key = ' +
          sessionEvent.correlationKey + ' confirmed.');
       });
    
       this.session.on(solace.SessionEventCode.REJECTED_MESSAGE_ERROR,  (sessionEvent) => {
        this.log('Delivery of message with correlation key = ' + sessionEvent.correlationKey +
            ' rejected, info: ' + sessionEvent.infoStr);
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

         //Replace all * in the topic filter with a .* to make it regex compatible
         let regexdSub = sub.replace(/\*/g,'.*');

         //if the last character is a '>', replace it with a .* to make it regex compatible
         if(sub.lastIndexOf('>')==sub.length-1)
            regexdSub=regexdSub.substring(0,regexdSub.length-1).concat('.*');
         
         let matched=topicName.match(regexdSub);

         //if the matched index starts at 0, then the topic is a match with the topic filter and proceed with callback invocation
          if(matched && matched.index==0){
            this.topicSubscriptions.get(sub).callback(message);
          }
        }

       
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
    let subscriptionObject:SubscriptionObject = new SubscriptionObject(callback,false);
    this.topicSubscriptions.set(topicName,subscriptionObject); // gets updated asynchronously
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
    message.setCorrelationKey(topic);
    message.setDeliveryMode(solace.MessageDeliveryModeType.PERSISTENT);
    try {
        this.session.send(message);
        this.log('Message published.');
    } catch (error) {
        this.log(error.toString());
    }
  }
}
