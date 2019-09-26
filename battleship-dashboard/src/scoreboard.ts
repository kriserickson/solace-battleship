import { inject, bindable } from "aurelia-framework";
import { GameParams } from "event-objects/game-params";

@inject(GameParams)
export class Scoreboard {

  @bindable
  private score: number;

  constructor(private gameparams: GameParams){
  }

  

}
