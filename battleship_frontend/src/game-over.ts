import { bindable } from "aurelia-framework";

/**
 * Class that represents the Game Over Screen
 * @author Thomas Kunnumpurath
 */
export class GameOver {
  @bindable
  msg: string;

  activate(params, routeConfig, navigtationInstruction) {
    this.msg = params.msg;
  }
}
