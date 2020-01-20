import { Move, KnownBoardCellState, MoveResponseEvent, PlayerName, Player, TopicHelper, GameStart } from "../common/events";
import { inject } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "../common/solace-client";
import { GameParams } from "../common/game-params";

//type for the state of the page
type PAGE_STATE = "TURN_PLAYER1" | "TURN_PLAYER2";

//Construct that holds the player's scores
class ScoreMap {
  Player1: number;
  Player2: number;
}

/**
 * A class that represents the match for the player.
 *
 * @authors Thomas Kunnumpurath
 */
@inject(Router, SolaceClient, Player, GameParams, TopicHelper, GameStart)
export class Match {
  //Map of the score
  private scoreMap: ScoreMap = new ScoreMap();

  private pageState: PlayerName = "Player1";

  private enemyBoard: KnownBoardCellState[][] = [];

  private turnMessage: string;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player, private gameParams: GameParams, private topicHelper: TopicHelper, private gameStart: GameStart) {
    this.scoreMap["Player1"] = this.gameParams.allowedShips;
    this.scoreMap["Player2"] = this.gameParams.allowedShips;
    for (let i = 0; i < gameParams.gameboardDimensions; i++) {
      this.enemyBoard[i] = [];
      for (let j = 0; j < gameParams.gameboardDimensions; j++) {
        this.enemyBoard[i][j] = "empty";
      }
    }

    //Warm up the reply subscription
    this.solaceClient.subscribeReply(`${this.topicHelper.prefix}/MOVE-REPLY/${this.player.getPlayerNameForTopic()}/${this.player.getOtherPlayerNameForTopic()}`);

    // subscribe to the other player's moves here
  }

  /**
   * Function to rotate the turn page's message
   */
  rotateTurnMessage() {
    if ((this.player.name == "Player1" && this.pageState == "Player1") || (this.player.name == "Player2" && this.pageState == "Player2")) {
      this.turnMessage = "YOUR TURN";
    } else if (this.player.name == "Player1" && this.pageState == "Player2") {
      this.turnMessage = "PLAYER2'S TURN";
    } else {
      this.turnMessage = "PLAYER1'S TURN";
    }
  }

  attached() {
    if (this.player.name == "Player1") {
      this.turnMessage = "YOUR TURN";
    } else {
      this.turnMessage = `PLAYER1'S TURN`;
    }
  }

  //A selection for the board
  boardSelectEvent(column: number, row: number) {
    if (this.player.name == this.pageState && this.enemyBoard[column][row] == "empty") {
      let move: Move = new Move();
      move.x = column;
      move.y = row;
      move.player = this.player.name;
      //Send the Move Request
    }
  }

  /**
   * Function to decrement the score for a player if a ship is hit
   * @param shipHitOwner the player of the ship that was hit
   */
  shipHit(shipHitOwner: PlayerName) {
    //Ship hit logic
  }

  detached() {
    //Unsubcsribe for the events
    this.solaceClient.unsubscribe(`${this.topicHelper.prefix}/MOVE-REQUEST/${this.player.getOtherPlayerNameForTopic()}`);
    this.solaceClient.unsubscribe(`${this.topicHelper.prefix}/MOVE-REPLY/${this.player.getPlayerNameForTopic()}/${this.player.getOtherPlayerNameForTopic()}`);
  }
}
