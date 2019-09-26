import { bindable } from "aurelia-framework";

export class GameOver {

  @bindable
  msg: string;

  activate(params, routeConfig, navigtationInstruction){
    this.msg = params.msg;
  }

}
