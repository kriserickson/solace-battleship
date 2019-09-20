import { PlayerName } from './event-objects/player-events';
import { inject, bindable } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "clients/SolaceClient";
import { Player } from "event-objects/player-events";
import {
  PrivateBoardCellState,
  KnownBoardCellState
} from "event-objects/board-events";

@inject(Router, SolaceClient, Player)
export class Match {
  // aurelia
  pageState = "BOARD_SELECTION"; // BOARD_SELECTION => [ TURN_PLAYER1 || TURN_PLAYER2 ] => MATCH_ENDED
  // solace
  // game logic
  boardSize = 5;
  allowedShips = 5;
  placedShips = 0;

  boardsSet=0;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player) {
    // initialize empty boards
    let playerBoard: PrivateBoardCellState[][] = [];
    let knownOpponentBoard: KnownBoardCellState[][] = [];
    for (let i = 0; i < 6; i++) {
      playerBoard[i] = [];
      knownOpponentBoard[i] = [];
      for (let j = 0; j < 6; j++) {
        playerBoard[i][j] = "empty";
        knownOpponentBoard[i][j] = "empty";
      }
    }
    
    this.player.boardState = playerBoard;
    this.player.knownOpponentBoardState = knownOpponentBoard;
    this.player.isTurn=false;



    this.solaceClient.subscribe('battleship/board/set/*',(msg)=>{
      // console.log(msg.getBinaryAttachment());
      let playerObj = JSON.parse(msg.getBinaryAttachment());
      console.log(`${playerObj.player} has set the board`);
      this.boardsSet++;
    });

    
  }

  activate(params, routeConfig) {
    // this.connectToSolace();
  }

  boardSelectEvent(row, column) {
    if (this.pageState == "BOARD_SELECTION") {
      // toggle cell state
      if (this.player.boardState[row][column] == "empty") {
        if (this.placedShips >= this.allowedShips) {
          alert("No ships remaining, remove one first!");
          return;
        }
        let tmpBoard = JSON.parse(JSON.stringify(this.player.boardState)); // a lazy man's deep copy
        tmpBoard[row][column] = "ship";
        this.player.boardState = tmpBoard;
        ++this.placedShips;
        return;
      }

      let tmpBoard = JSON.parse(JSON.stringify(this.player.boardState));
      tmpBoard[row][column] = "empty";
      this.player.boardState = tmpBoard;
      --this.placedShips;
      return;
    }
    if (
      (this.player.name == "Player1" && this.pageState == "TURN_PLAYER1") ||
      (this.player.name == "Player2" && this.pageState == "TURN_PLAYER2")
    ) {
      let tmpBoard = JSON.parse(
        JSON.stringify(this.player.knownOpponentBoardState)
      ); // a lazy man's deep copy

      // check if guess was a hit
      this.player.boardState[row][column] == "ship"
        ? (tmpBoard[row][column] = "hit")
        : (tmpBoard[row][column] = "miss");

      this.player.knownOpponentBoardState = tmpBoard;
      return;
    }
  }

  beginMatch(){
    if(this.placedShips==5){
      console.log(`battleship/board/set/${this.player.name}`);
      this.solaceClient.publish(`battleship/board/set/${this.player.name}`,`{"player":"${this.player.name}","placedShips":"${this.allowedShips}"}`);
    }
  }

  navigatePageState(page: string) {
    this.pageState = page;
  }

  navigate(routeName: string) {
    this.router.navigateToRoute(routeName);
  }
}
