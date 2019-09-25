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

@inject(Router, SolaceClient, Player, GameParams)
export class BoardSet {

  private boardsSet: number = 0;
  private placedShips: number = 0;
  private donePlacing: boolean = false;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player, private gameParams: GameParams) {
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

    console.log(this.gameParams);

    this.player.boardState = playerBoard;
    this.player.knownOpponentBoardState = knownOpponentBoard;
    this.player.isTurn=false;

    this.solaceClient.subscribe('battleship/board/set/*',(msg)=>{
    let playerObj = JSON.parse(msg.getBinaryAttachment());
    console.log(`${playerObj.player} has set the board`);
    this.boardsSet++;

    if(this.boardsSet==2){
        this.router.navigate("match");
      }
    });

  }

  
  boardSelectEvent(row, column) {
      // toggle cell state
      if (this.player.boardState[row][column] == "empty") {
        if (this.placedShips >= this.gameParams.allowedShips) {
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

    beginMatch(){
      if(this.placedShips==5){
        console.log(`battleship/board/set/${this.player.name}`);
        this.solaceClient.publish(`battleship/board/set/${this.player.name}`,`{"player":"${this.player.name}","placedShips":"${this.allowedShips}"}`);
        this.donePlacing=true;
      }
    }
}
