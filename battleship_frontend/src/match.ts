import { MoveResponseEvent } from './event-objects/move-response-event';
import { Move, KnownBoardCellState } from './event-objects/board-events';
import { PlayerName } from './event-objects/player-events';
import { inject, bindable } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "config/SolaceClient";
import { Player } from "event-objects/player-events";
import { GameParams } from 'event-objects/game-params';

//type for the state of the page
type PAGE_STATE = "TURN_PLAYER1" | "TURN_PLAYER2";

//Construct that holds the player's scores
class ScoreMap {
  Player1: number;
  Player2: number
}

/**
 * A class that represents the match for the player.
 * 
 * @authors Thomas Kunnumpurath
 */
@inject(Router, SolaceClient, Player, GameParams)
export class Match {

  //Map of the score
  private scoreMap: ScoreMap = new ScoreMap();

  private pageState: PlayerName = "Player1";

  private enemyBoard: KnownBoardCellState[][] =[];

  private turnMessage: string;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player, private gameParams: GameParams) {
     this.scoreMap['Player1']=this.gameParams.allowedShips;
     this.scoreMap['Player2']=this.gameParams.allowedShips;
     for(let i=0;i<gameParams.gameboardDimensions;i++){
       this.enemyBoard[i]=[];
       for(let j=0;j<gameParams.gameboardDimensions;j++){
         this.enemyBoard[i][j]='empty';
       }
     }
     this.solaceClient.subscribe(`SOLACE/BATTLESHIP/${this.player.name=='Player1'?'Player2':'Player1'}/MOVE`,(msg)=>{
        let move: Move = JSON.parse(msg.getBinaryAttachment());
        let moveResponseEvent : MoveResponseEvent = new MoveResponseEvent();
        console.log(this.player);
        moveResponseEvent.move = move;
        moveResponseEvent.playerBoard = this.player.publicBoardState;
        moveResponseEvent.player = this.player.name;
        moveResponseEvent.moveResult=this.player.internalBoardState[move.x][move.y];
        this.solaceClient.sendReply(msg,JSON.stringify(moveResponseEvent));
        if(this.player.internalBoardState[move.x][move.y] == 'ship'){
          this.shipHit(this.player.name);
          this.player.publicBoardState[move.x][move.y]='hit';
        }else{
          this.player.publicBoardState[move.x][move.y]='miss';
        }

        this.pageState = this.player.name;
        this.rotateTurnMessage();
     });
  }

  rotateTurnMessage(){
    if((this.player.name=='Player1' && this.pageState=='Player1') || (this.player.name=='Player2' && this.pageState == 'Player2')){
      this.turnMessage = 'YOUR TURN';
    }else if(this.player.name=='Player1' && this.pageState == 'Player2'){
      this.turnMessage = 'PLAYER2\'S TURN';
    }else{
      this.turnMessage = 'PLAYER1\'S TURN';
    }
  }

  attached() {
    if(this.player.name=='Player1'){
      this.turnMessage='YOUR TURN';
    }else {
      this.turnMessage=`PLAYER1'S TURN`;
    }
  }


  //A selection for the board
  boardSelectEvent(row, column) {
      if (
      ((this.player.name == this.pageState) ) && 
      this.enemyBoard[row][column]=="empty"
    ) {

      let move: Move = new Move();
      move.x=row;
      move.y=column;
      move.player=this.player.name;
      this.solaceClient.sendRequest(`SOLACE/BATTLESHIP/${this.player.name}/MOVE`,JSON.stringify(move),`SOLACE/BATTLESHIP/${this.player.name}/MOVE-REPLY`).then((msg:any)=>{
        let moveResponseEvent: MoveResponseEvent = JSON.parse(msg.getBinaryAttachment());
        this.enemyBoard = moveResponseEvent.playerBoard;

        if(moveResponseEvent.moveResult=='ship'){
          this.enemyBoard[move.x][move.y]='hit';
          this.shipHit(this.player.name=='Player1'?'Player2':'Player1');
        }else{
          this.enemyBoard[move.x][move.y]='miss';
        }
        this.pageState=(this.player.name=='Player1'?'Player2':'Player1');
        this.rotateTurnMessage();
      }).catch((failedMessage)=>{
        console.log(failedMessage);
        this.turnMessage += ' ...TRY AGAIN!'
      });
    }
  }

  /**
   * Function to decrement the score for a player if a ship is hit
   * @param shipHitOwner the player of the ship that was hit
   */
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
}
