import { PlayerName } from './event-objects/player-events';
import { inject, bindable } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "config/SolaceClient";
import { Player } from "event-objects/player-events";
import {
  PrivateBoardCellState,
  KnownBoardCellState
} from "event-objects/board-events";
import { GameParams } from 'event-objects/game-params';


type PAGE_STATE = "TURN_PLAYER1" | "TURN_PLAYER2";

class ScoreMap {
  Player1: number;
  Player2: number
}

@inject(Router, SolaceClient, Player, GameParams)
export class Match {

  private scoreMap: ScoreMap = new ScoreMap();

  private pageState: PAGE_STATE = "TURN_PLAYER1";

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player, private gameParams: GameParams) {
     this.scoreMap['Player1']=this.gameParams.allowedShips;
     this.scoreMap['Player2']=this.gameParams.allowedShips;
  }

  activate(params, routeConfig) {
    // this.connectToSolace();
  }

  boardSelectEvent(row, column) {
      if (
      ((this.player.name == "Player1" && this.pageState == "TURN_PLAYER1") ||
      (this.player.name == "Player2" && this.pageState == "TURN_PLAYER2") ) && 
      this.player.knownOpponentBoardState[row][column]=="empty"
    ) {

      /**
       * Fill in the request/response messaging pattern here
       */


      let tmpBoard = JSON.parse(
        JSON.stringify(this.player.knownOpponentBoardState)
      ); // a lazy man's deep copy
      

      // check if guess was a hit
      if(this.player.boardState[row][column] == "ship"){
        tmpBoard[row][column] = "hit";
        this.shipHit(this.player.name=="Player1"?"Player2":"Player1");
      }else{
        tmpBoard[row][column] = "miss";
      }

      this.player.knownOpponentBoardState = tmpBoard;
      return;
    }
  }


  shipHit(shipHitOwner: PlayerName){
   this.scoreMap[shipHitOwner]--;
   if(this.scoreMap[shipHitOwner]==0){
     if(shipHitOwner==this.player.name){
       this.router.navigateToRoute('game-over',{msg:'YOU LOSE!'});
     }else{
      this.router.navigateToRoute('game-over',{msg:'YOU WON!'});
     }
   }
  }

  navigatePageState(page: PAGE_STATE) {
    this.pageState = page;
  }

  navigate(routeName: string) {
    this.router.navigateToRoute(routeName);
  }
}
