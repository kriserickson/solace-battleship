import { PlayerName } from './event-objects/player-events';
import { inject, bindable } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "clients/SolaceClient";
import { Player } from "event-objects/player-events";
import {
  PrivateBoardCellState,
  KnownBoardCellState
} from "event-objects/board-events";
import { GameParams } from 'event-objects/game-params';


type PAGE_STATE = "TURN_PLAYER1" | "TURN_PLAYER2";


@inject(Router, SolaceClient, Player, GameParams)
export class Match {

  private pageState: PAGE_STATE = "TURN_PLAYER1";
  private player1Score: number = 5;
  private player2Score: number = 5;


  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player, private gameParams: GameParams) {
  }

  activate(params, routeConfig) {
    // this.connectToSolace();
  }

  boardSelectEvent(row, column) {
      if (
      (this.player.name == "Player1" && this.pageState == "TURN_PLAYER1") ||
      (this.player.name == "Player2" && this.pageState == "TURN_PLAYER2")
    ) {
      let tmpBoard = JSON.parse(
        JSON.stringify(this.player.knownOpponentBoardState)
      ); // a lazy man's deep copy


      

      // check if guess was a hit
      if(this.player.boardState[row][column] == "ship"){
        tmpBoard[row][column] = "hit";
        if(this.player.name=="Player1"){
          this.player1Score--;
        }else{
          this.player2Score--;
        }
      }else{
        tmpBoard[row][column] = "miss";
      }

      this.player.knownOpponentBoardState = tmpBoard;
      return;
    }
  }

 

  navigatePageState(page: PAGE_STATE) {
    this.pageState = page;
  }

  navigate(routeName: string) {
    this.router.navigateToRoute(routeName);
  }
}
