import { Router } from 'aurelia-router';
import { GameParams } from 'common/game-params';
import { MoveResponseEvent, TopicHelper, GameStart } from './common/events';
import {bindable,inject} from 'aurelia-framework';
import {SolaceClient} from 'common/solace-client';

//Construct that holds the player's scores
class ScoreMap {
    Player1: number;
    Player2: number
}

class MoveResultMap {
    Player1: MoveResponseEvent;
    Player2: MoveResponseEvent;
}

@inject(SolaceClient, TopicHelper, GameParams, Router, GameStart)
export class Dashboard {

  private action: string;
  private moveResultMap : MoveResultMap = new MoveResultMap();
  private scoreMap: ScoreMap = new ScoreMap();
  private turnMessage: string;

  constructor(private solaceClient: SolaceClient, private topicHelper: TopicHelper, private gameParams: GameParams, private router:Router, private gameStart: GameStart){
      this.scoreMap.Player1=gameParams.allowedShips;
      this.scoreMap.Player2=gameParams.allowedShips;
      this.turnMessage = 'Player1\'s Turn';
  }
 
  attached(){
    this.solaceClient.subscribe(`${this.topicHelper.prefix}/*/MOVE-REPLY`, (msg) => {
       let moveResponseEvent: MoveResponseEvent = JSON.parse(msg.getBinaryAttachment());
       this.moveResultMap[moveResponseEvent.player]=moveResponseEvent;
       if(moveResponseEvent.moveResult=='ship'){
           this.action = 'hit';
           this.scoreMap[moveResponseEvent.player]-=1;
           if(this.scoreMap[moveResponseEvent.player]==0){
                this.router.navigateToRoute('game-over',{msg:`${moveResponseEvent.player=='Player1'?'Player2':'Player1'} WINS!!!!`});
           }
       }else{
           this.action='miss';
       }
       this.turnMessage=`${moveResponseEvent.player}'s Turn`;
    });
  }

}