import solace from "solclientjs";
import { solaceBrokerConfig } from "./solace-broker-config";
import { noView } from "aurelia-framework";

@noView
export class SolaceClient {
  session = null;
  topicSubscriptions: Record<string, any> = {};
  
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
          delete this.topicSubscriptions[sessionEvent.correlationKey];
      });
      this.session.on(solace.SessionEventCode.SUBSCRIPTION_OK, (sessionEvent) => {
          if (this.topicSubscriptions[sessionEvent.correlationKey] && this.topicSubscriptions[sessionEvent.correlationKey].isSubscribed) {
            delete this.topicSubscriptions[sessionEvent.correlationKey];
            this.log(`Successfully unsubscribed from topic: ${sessionEvent.correlationKey}`);
          } else {
            this.topicSubscriptions[sessionEvent.correlationKey].isSubscribed = true;
            this.log(`Successfully subscribed to topic: ${sessionEvent.correlationKey}`);
          }
      });
      // define message event listener
      this.session.on(solace.SessionEventCode.MESSAGE, (message) => {
        let topicName = message.getDestination().getName();
        this.topicSubscriptions[topicName].callback(message);
      });
      // connect the session
      try {
        this.session.connect();
      } catch (error) {
        this.log(error.toString());
      }
    });
  }  

  subscribe(topicName: string, callback: any) {
    if(!this.session) {
      this.log("[WARNING] Cannot subscribe because not connected to Solace message router!")
      return;
    }
    if(this.topicSubscriptions[topicName]) {
      this.log(`[WARNING] Already subscribed to ${topicName}.`);
      return;
    }
    this.log(`Subscribing to ${topicName}`);
    this.topicSubscriptions[topicName] = {callback: callback, isSubscribed: false}; // gets updated asynchronously
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

//   subscriber.exit = function () {
//       subscriber.unsubscribe();
//       subscriber.disconnect();
//       setTimeout(function () {
//           process.exit();
//       }, 1000); // wait for 1 second to finish
//   };

//   // Unsubscribes from topic on Solace message router
//   subscriber.unsubscribe = function () {
//       if (subscriber.session !== null) {
//           if (subscriber.subscribed) {
//               subscriber.log('Unsubscribing from topic: ' + subscriber.topicName);
//               try {
//                   subscriber.session.unsubscribe(
//                       solace.SolclientFactory.createTopicDestination(subscriber.topicName),
//                       true, // generate confirmation when subscription is removed successfully
//                       subscriber.topicName, // use topic name as correlation key
//                       10000 // 10 seconds timeout for this operation
//                   );
//               } catch (error) {
//                   subscriber.log(error.toString());
//               }
//           } else {
//               subscriber.log('Cannot unsubscribe because not subscribed to the topic "'
//                   + subscriber.topicName + '"');
//           }
//       } else {
//           subscriber.log('Cannot unsubscribe because not connected to Solace message router.');
//       }
//   };

//   // Gracefully disconnects from Solace message router
//   subscriber.disconnect = function () {
//       subscriber.log('Disconnecting from Solace message router...');
//       if (subscriber.session !== null) {
//           try {
//               subscriber.session.disconnect();
//           } catch (error) {
//               subscriber.log(error.toString());
//           }
//       } else {
//           subscriber.log('Not connected to Solace message router.');
//       }
//   };

}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Solace Systems Node.js API
 * Publish/Subscribe tutorial - Topic Subscriber
 * Demonstrates subscribing to a topic for direct messages and receiving messages
 */

/*jslint es6 node:true devel:true*/

// var TopicSubscriber = function (solaceModule, topicName) {
//   'use strict';
//   var solace = solaceModule;
//   var subscriber = {};
//   subscriber.session = null;
//   subscriber.topicName = topicName;
//   subscriber.subscribed = false;

//   // Logger
//   subscriber.log = function (line) {
//       var now = new Date();
//       var time = [('0' + now.getHours()).slice(-2), ('0' + now.getMinutes()).slice(-2),
//           ('0' + now.getSeconds()).slice(-2)];
//       var timestamp = '[' + time.join(':') + '] ';
//       console.log(timestamp + line);
//   };

//   subscriber.log('\n*** Subscriber to topic "' + subscriber.topicName + '" is ready to connect ***');

//   // main function
//   subscriber.run = function (argv) {
//       subscriber.connect(argv);
//   };

//   // // Establishes connection to Solace message router
//   // subscriber.connect = function (argv) {
//   //     if (subscriber.session !== null) {
//   //         subscriber.log('Already connected and ready to subscribe.');
//   //         return;
//   //     }
//   //     // extract params
//   //     if (argv.length < (2 + 3)) { // expecting 3 real arguments
//   //         subscriber.log('Cannot connect: expecting all arguments' +
//   //             ' <protocol://host[:port]> <client-username>@<message-vpn> <client-password>.\n' +
//   //             'Available protocols are ws://, wss://, http://, https://, tcp://, tcps://');
//   //         process.exit();
//   //     }
//   //     var hosturl = argv.slice(2)[0];
//   //     subscriber.log('Connecting to Solace message router using url: ' + hosturl);
//   //     var usernamevpn = argv.slice(3)[0];
//   //     var username = usernamevpn.split('@')[0];
//   //     subscriber.log('Client username: ' + username);
//   //     var vpn = usernamevpn.split('@')[1];
//   //     subscriber.log('Solace message router VPN name: ' + vpn);
//   //     var pass = argv.slice(4)[0];
//       // create session
//       // try {
//       //     subscriber.session = solace.SolclientFactory.createSession({
//       //         // solace.SessionProperties
//       //         url:      hosturl,
//       //         vpnName:  vpn,
//       //         userName: username,
//       //         password: pass,
//       //     });
//       // } catch (error) {
//       //     subscriber.log(error.toString());
//       // }
//       // // define session event listeners
//       // subscriber.session.on(solace.SessionEventCode.UP_NOTICE, function (sessionEvent) {
//       //     subscriber.log('=== Successfully connected and ready to subscribe. ===');
//       //     subscriber.subscribe();
//       // });
//       // subscriber.session.on(solace.SessionEventCode.CONNECT_FAILED_ERROR, function (sessionEvent) {
//       //     subscriber.log('Connection failed to the message router: ' + sessionEvent.infoStr +
//       //         ' - check correct parameter values and connectivity!');
//       // });
//       // subscriber.session.on(solace.SessionEventCode.DISCONNECTED, function (sessionEvent) {
//       //     subscriber.log('Disconnected.');
//       //     subscriber.subscribed = false;
//       //     if (subscriber.session !== null) {
//       //         subscriber.session.dispose();
//       //         subscriber.session = null;
//       //     }
//       // });
//       // subscriber.session.on(solace.SessionEventCode.SUBSCRIPTION_ERROR, function (sessionEvent) {
//       //     subscriber.log('Cannot subscribe to topic: ' + sessionEvent.correlationKey);
//       // });
//       // subscriber.session.on(solace.SessionEventCode.SUBSCRIPTION_OK, function (sessionEvent) {
//       //     if (subscriber.subscribed) {
//       //         subscriber.subscribed = false;
//       //         subscriber.log('Successfully unsubscribed from topic: ' + sessionEvent.correlationKey);
//       //     } else {
//       //         subscriber.subscribed = true;
//       //         subscriber.log('Successfully subscribed to topic: ' + sessionEvent.correlationKey);
//       //         subscriber.log('=== Ready to receive messages. ===');
//       //     }
//       // });
//       // // define message event listener
//       // subscriber.session.on(solace.SessionEventCode.MESSAGE, function (message) {
//       //     subscriber.log('Received message: "' + message.getBinaryAttachment() + '", details:\n' +
//       //         message.dump());
//       // });
//       // connect the session
//       try {
//           subscriber.session.connect();
//       } catch (error) {
//           subscriber.log(error.toString());
//       }
//   };

//   // Subscribes to topic on Solace message router
//   subscriber.subscribe = function () {
//       if (subscriber.session !== null) {
//           if (subscriber.subscribed) {
//               subscriber.log('Already subscribed to "' + subscriber.topicName
//                   + '" and ready to receive messages.');
//           } else {
//               subscriber.log('Subscribing to topic: ' + subscriber.topicName);
//               try {
//                   subscriber.session.subscribe(
//                       solace.SolclientFactory.createTopicDestination(subscriber.topicName),
//                       true, // generate confirmation when subscription is added successfully
//                       subscriber.topicName, // use topic name as correlation key
//                       10000 // 10 seconds timeout for this operation
//                   );
//               } catch (error) {
//                   subscriber.log(error.toString());
//               }
//           }
//       } else {
//           subscriber.log('Cannot subscribe because not connected to Solace message router.');
//       }
//   };

//   subscriber.exit = function () {
//       subscriber.unsubscribe();
//       subscriber.disconnect();
//       setTimeout(function () {
//           process.exit();
//       }, 1000); // wait for 1 second to finish
//   };

//   // Unsubscribes from topic on Solace message router
//   subscriber.unsubscribe = function () {
//       if (subscriber.session !== null) {
//           if (subscriber.subscribed) {
//               subscriber.log('Unsubscribing from topic: ' + subscriber.topicName);
//               try {
//                   subscriber.session.unsubscribe(
//                       solace.SolclientFactory.createTopicDestination(subscriber.topicName),
//                       true, // generate confirmation when subscription is removed successfully
//                       subscriber.topicName, // use topic name as correlation key
//                       10000 // 10 seconds timeout for this operation
//                   );
//               } catch (error) {
//                   subscriber.log(error.toString());
//               }
//           } else {
//               subscriber.log('Cannot unsubscribe because not subscribed to the topic "'
//                   + subscriber.topicName + '"');
//           }
//       } else {
//           subscriber.log('Cannot unsubscribe because not connected to Solace message router.');
//       }
//   };

//   // Gracefully disconnects from Solace message router
//   subscriber.disconnect = function () {
//       subscriber.log('Disconnecting from Solace message router...');
//       if (subscriber.session !== null) {
//           try {
//               subscriber.session.disconnect();
//           } catch (error) {
//               subscriber.log(error.toString());
//           }
//       } else {
//           subscriber.log('Not connected to Solace message router.');
//       }
//   };

//   return subscriber;
// };

// var solace = require('solclientjs').debug; // logging supported

// // Initialize factory with the most recent API defaults
// var factoryProps = new solace.SolclientFactoryProperties();
// factoryProps.profile = solace.SolclientFactoryProfiles.version10;
// solace.SolclientFactory.init(factoryProps);

// // enable logging to JavaScript console at WARN level
// // NOTICE: works only with ('solclientjs').debug
// solace.SolclientFactory.setLogLevel(solace.LogLevel.WARN);

// // create the subscriber, specifying the name of the subscription topic
// var subscriber = new TopicSubscriber(solace, 'tutorial/topic');

// // subscribe to messages on Solace message router
// subscriber.run(process.argv);

// // wait to be told to exit
// subscriber.log('Press Ctrl-C to exit');
// process.stdin.resume();

// process.on('SIGINT', function () {
//   'use strict';
//   subscriber.exit();
// });
