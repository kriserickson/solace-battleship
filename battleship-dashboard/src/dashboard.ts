import {bindable,inject} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';
import {SolaceClient} from 'clients/SolaceClient';
import {ResponseEvent} from 'event-objects/dashboard-events';

@inject(EventAggregator, SolaceClient)
export class Dashboard {

  @bindable action: String;

  constructor(private ea: EventAggregator, private solaceClient: SolaceClient){
    this.connectToSolace();
  }
  
  async connectToSolace() {
    await this.solaceClient.connect();
  }

  attached(){
    this.solaceClient.subscribe("battleship/response/event", (msg) => {
       let responseEventJSON: string = msg.getSdtContainer().getValue();
       if(typeof responseEventJSON){
         let responseEvent: ResponseEvent = JSON.parse(responseEventJSON);
         this.action = responseEvent.move.action;
         this.ea.publish(responseEvent);
       }
    });
  }

}
