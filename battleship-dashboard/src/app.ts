import {bindable,inject} from 'aurelia-framework';
import DashboardEvent from './event-objects/dashboard-events';
import {EventAggregator} from 'aurelia-event-aggregator';


@inject(EventAggregator)
export class App {

  constructor(private ea: EventAggregator){
  
  }

  attached(){
  
  }


}
