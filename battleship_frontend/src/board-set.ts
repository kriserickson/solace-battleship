import { inject, bindable } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "config/SolaceClient";
import { Player } from "event-objects/player-events";
import {
  PrivateBoardCellState,
  KnownBoardCellState
} from "event-objects/board-events";
import { GameParams } from 'event-objects/game-params';


/**
 * Responsible for setting the board
 * @author Thomas Kunnumpurath, Andrew Roberts
 */
@inject(Router, SolaceClient, Player, GameParams)
export class BoardSet {

  //State for the board
  private boardsSet: number = 0;
  private placedShips: number = 0;
  private donePlacing: boolean = false;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player, private gameParams: GameParams) {
    
     //Warm up the subscription to the MOVE-REPLIES
     this.solaceClient.subscribe(`SOLACE/BATTLESHIP/${this.player.name}/MOVE-REPLY`,(msg)=>{console.log(msg.getBinaryAttachment)})
   
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


    this.player.internalBoardState = playerBoard;
    this.player.publicBoardState = knownOpponentBoard;
    this.player.isTurn=false;

    //Subscribe to the board set event
    this.solaceClient.subscribe('battleship/board/set/*',(msg)=>{
    let playerObj = JSON.parse(msg.getBinaryAttachment());
    console.log(`${playerObj.player} has set the board`);
    this.boardsSet++;

   
    //If the boards have been set, then begin the match
    if(this.boardsSet==2){
        this.router.navigate("match");
      }
    });

  }

  /**
   * Set the state of the player's board
   * @param row the row for the board piece
   * @param column the column for the board piece
   */
  boardSelectEvent(row: number, column: number) {
    if(!this.donePlacing){
      // toggle cell state
      if (this.player.internalBoardState[row][column] == "empty") {
        if (this.placedShips >= this.gameParams.allowedShips) {
          alert("No ships remaining, remove one first!");
          return;
        }
        let tmpBoard = JSON.parse(JSON.stringify(this.player.internalBoardState)); // a lazy man's deep copy
        tmpBoard[row][column] = "ship";
        this.player.internalBoardState = tmpBoard;
        ++this.placedShips;
        return;
      }

      let tmpBoard = JSON.parse(JSON.stringify(this.player.internalBoardState));
      tmpBoard[row][column] = "empty";
      this.player.internalBoardState = tmpBoard;
      --this.placedShips;
      return;
    }
    }

    /**
     * Function to begin a match - it publishes a message and then sets the done placing variable to true
     */
    beginMatch(){
      if(this.placedShips==5){
        console.log(`battleship/board/set/${this.player.name}`);
        this.solaceClient.publish(`battleship/board/set/${this.player.name}`,`{"player":"${this.player.name}","placedShips":"${this.allowedShips}"}`);
        this.donePlacing=true;
      }
    }
}
