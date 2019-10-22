import { inject, bindable } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "common/solace-client";
import {
  Player,
  PrivateBoardCellState,
  KnownBoardCellState,
  TopicHelper
} from "common/events";
import { GameParams } from 'common/game-params';


/**
 * Responsible for setting the board
 * @author Thomas Kunnumpurath, Andrew Roberts
 */
@inject(Router, SolaceClient, Player, GameParams, TopicHelper)
export class BoardSet {

  //State for the board
  private boardsSet: number = 0;
  private placedShips: number = 0;
  private donePlacing: boolean = false;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player, private gameParams: GameParams, private topicHelper: TopicHelper) {
    
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
    this.solaceClient.subscribe(this.topicHelper.prefix + '/BOARD/SET/*',(msg)=>{
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
        this.solaceClient.publish(`${this.topicHelper.prefix}/BOARD/SET/${this.player.name}`,`{"player":"${this.player.name}","placedShips":"${this.placedShips}"}`);
        this.donePlacing=true;
      }
    }

    detached(){
      this.solaceClient.unsubscribe(this.topicHelper.prefix + '/BOARD/SET/*');
    }
}
