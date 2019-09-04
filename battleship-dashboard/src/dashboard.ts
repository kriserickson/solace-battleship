import {bindable,inject} from 'aurelia-framework';
import {EventAggregator} from 'aurelia-event-aggregator';

@inject(EventAggregator)
export class Dashboard {

  @bindable action: String;

  constructor(private ea: EventAggregator){
    this.ea.subscribe('Action', msg => {
      this.action = msg.action;;
    });
  }

  attached(){
  }

}