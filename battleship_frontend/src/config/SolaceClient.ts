import solace from "solclientjs";
import { gameConfig } from "./game-config";
import { noView } from "aurelia-framework";

/**
 * The SubscriptionObject represents a combination of the callback function and
 *  whether the subscription has been applied on the PubSub+ broker
 *  @author Thomas Kunnumpurath
 */
class SubscriptionObject {
  callback: any;
  isSubscribed: boolean;

  constructor(_callback:any,_isSubscribed:boolean){
   this.callback=_callback;
   this.isSubscribed=_isSubscribed;
  }
}

/**
 * The SolaceClient object connects to the PubSub+ Broker and exposes convenience functions for 
 * publishing, subscribing, and for the request/reply pattern
 * @author Thomas Kunnumpurath, Andrew Roberts
 */
@noView
export class SolaceClient {

  //Solace session object  
  session = null;

  //Map that holds the topic subscription string and the associated callback function, subscription state
  topicSubscriptions: Map<string, any> = new Map<string,SubscriptionObject>();
  
  constructor() {
    //Initializing the solace client library
    let factoryProps = new solace.SolclientFactoryProperties();
    factoryProps.profile = solace.SolclientFactoryProfiles.version10;
    solace.SolclientFactory.init(factoryProps);
  }

  /**
   * Function that outputs to console with a timestamp
   * @param line String to log to the console
   */
  log(line: string) {
    let now = new Date();
    let time = [('0' + now.getHours()).slice(-2), ('0' + now.getMinutes()).slice(-2), ('0' + now.getSeconds()).slice(-2)];
    let timestamp = '[' + time.join(':') + '] ';
    console.log(timestamp + line);
  }

  /**
   * Asynchronous function that connects to the Solace Broker and returns a promise.
   */
  async connect() {
    return new Promise((resolve, reject) => {
      if (this.session !== null) {
        this.log('Already connected and ready to subscribe.');
        reject();
      }
      // if there's no session, create one with the properties imported from the game-config file
      try {
        this.session = solace.SolclientFactory.createSession({
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

      //The UP_NOTICE dictates whether the session has been established
      this.session.on(solace.SessionEventCode.UP_NOTICE,  (sessionEvent) => {
          this.log('=== Successfully connected and ready to subscribe. ===');
          resolve(); 
      });

      //The CONNECT_FAILED_ERROR implies a connection failure
      this.session.on(solace.SessionEventCode.CONNECT_FAILED_ERROR, (sessionEvent) => {
          this.log('Connection failed to the message router: ' + sessionEvent.infoStr +
              ' - check correct parameter values and connectivity!');
          reject(sessionEvent.infoStr);
      });

      //DISCONNECTED implies the client was disconnected
      this.session.on(solace.SessionEventCode.DISCONNECTED, (sessionEvent) => {
          this.log('Disconnected.');
          if (this.session !== null) {
            this.session.dispose();
            //this.subscribed = false;
            this.session = null;
          }
      });

      //ACKNOWLEDGED MESSAGE implies that the broker has confirmed message receipt
      this.session.on(solace.SessionEventCode.ACKNOWLEDGED_MESSAGE,  (sessionEvent) => {
        this.log('Delivery of message with correlation key = ' +
          sessionEvent.correlationKey + ' confirmed.');
       });
    
       //REJECTED_MESSAGE implies that the broker has rejected the message
       this.session.on(solace.SessionEventCode.REJECTED_MESSAGE_ERROR,  (sessionEvent) => {
        this.log('Delivery of message with correlation key = ' + sessionEvent.correlationKey +
            ' rejected, info: ' + sessionEvent.infoStr);
        });

      //SUBSCRIPTION ERROR implies that there was an error in subscribing on a topic
      this.session.on(solace.SessionEventCode.SUBSCRIPTION_ERROR, (sessionEvent) => {
          this.log('Cannot subscribe to topic: ' + sessionEvent.correlationKey);
          //remote the topic from the TopicSubscriptionMap
          this.topicSubscriptions.delete(sessionEvent.correlationKey);
      });

      //SUBSCRIPTION_OK implies that a subscription was succesfully applied/removed from the broker
      this.session.on(solace.SessionEventCode.SUBSCRIPTION_OK, (sessionEvent) => {
          //Check if the topic exists in the map
          if (this.topicSubscriptions.get(sessionEvent.correlationKey)) 
          {
            //If the subscription shows as subscribed, then this is a callback for unsubscripition
            if(this.topicSubscriptions.get(sessionEvent.correlationKey).isSubscribed) {
              //Remove the topic from the map
              this.topicSubscriptions.delete(sessionEvent.correlationKey);
              this.log(`Successfully unsubscribed from topic: ${sessionEvent.correlationKey}`);
            } else {
              //Otherwise, this is a callback for subscribing
              this.topicSubscriptions.get(sessionEvent.correlationKey).isSubscribed = true;
              this.log(`Successfully subscribed to topic: ${sessionEvent.correlationKey}`);
            }
          }
      });

      //Message callback function
      this.session.on(solace.SessionEventCode.MESSAGE, (message) => {

        //Get the topic name from the message's destination
        let topicName: string = message.getDestination().getName();
        
        //Iterate over all subscriptions in the subscription map
        for(let sub of Array.from(this.topicSubscriptions.keys())){

         //Replace all * in the topic filter with a .* to make it regex compatible
         let regexdSub = sub.replace(/\*/g,'.*');

         //if the last character is a '>', replace it with a .* to make it regex compatible
         if(sub.lastIndexOf('>')==sub.length-1)
            regexdSub=regexdSub.substring(0,regexdSub.length-1).concat('.*');
         
         let matched=topicName.match(regexdSub);

         //if the matched index starts at 0, then the topic is a match with the topic filter 
          if(matched && matched.index==0){

            //Edge case if the pattern is a match but the last character is a *
            if(regexdSub.lastIndexOf('*')==sub.length-1){
              //Check if the number of topic sections are equal
              if(regexdSub.split("/").length!=topicName.split("/").length)
                return;
            }
            //Proceed with the message callback for the topic subscription
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
  
  /**
   * Function that sends a request message to the message broker and waits for a reply
   * @param topicName Topic string of the request
   * @param payload Payload of the request
   */
  async sendRequest(topicName: string, payload: string){
    return new Promise((resolve,reject)=>{

      let request = solace.SolclientFactory.createMessage();
      request.setDestination(solace.SolclientFactory.createTopic(topicName));
      request.setSdtContainer(solace.SDTField.create(solace.SDTFieldType.STRING, payload));
      request.setDeliveryMode(solace.MessageDeliveryModeType.DIRECT);

      try {
        this.session.sendRequest(
          request,
          5000,
          (session,message)=>{
            resolve(message);
          },
          (session,event)=>{
            reject(event)
          },
          null
        );
      }catch(error){
        this.log(error.toString());
      }
    });

  }

  /**
   * Function that subscribes to the topic
   * @param topicName Topic string for the subscription
   * @param callback Callback for the function
   */
  subscribe(topicName: string, callback: any) {
    //Check if the session has been established
    if(!this.session) {
      this.log("[WARNING] Cannot subscribe because not connected to Solace message router!")
      return;
    }
    //Check if the subscription already exists
    if(this.topicSubscriptions.get(topicName)) {
      this.log(`[WARNING] Already subscribed to ${topicName}.`);
      return;
    }
    this.log(`Subscribing to ${topicName}`);
    //Create a subscription object with the callback, upon succesful subscription, the object will be updated
    let subscriptionObject:SubscriptionObject = new SubscriptionObject(callback,false);
    this.topicSubscriptions.set(topicName,subscriptionObject); 
    try {
      //Session subscription
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

  /**
   * Publish a message on a topic
   * @param topic Topic to publish on
   * @param payload Payload on the topic
   */
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
