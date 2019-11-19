import { PlayerJoined, GameStart, TopicHelper, BoardSetEvent } from "./common/events";
import { inject } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "common/solace-client";

/**
 * Class that represents a landing page
 */
@inject(Router, SolaceClient, TopicHelper, GameStart)
export class LandingPage {
  player1Status: string = "Waiting for Player1 to Join...";
  player2Status: string = "Waiting for Player2 to Join...";

  boardsSet: number = 0;

  constructor(private router: Router, private solaceClient: SolaceClient, private topicHelper: TopicHelper, private gameStart: GameStart) {}

  /**
   * Aurelia function that is called when the page is navigated to
   * @param params
   * @param routeConfig
   */
  activate(params, routeConfig) {
    // solace logic
    this.connectToSolace().then(() => {
      //Listener for join events
      this.solaceClient.subscribe(`${this.topicHelper.prefix}/JOIN/*`, msg => {
        if (msg.getBinaryAttachment()) {
          let playerJoined: PlayerJoined = JSON.parse(msg.getBinaryAttachment());
          this.gameStart[playerJoined.playerName] = playerJoined;
          if (playerJoined.playerName == "Player1") {
            this.player1Status = "Player1 Joined!";
          } else {
            this.player2Status = "Player2 Joined!";
          }
          this.startGame();
        }
      });

      this.solaceClient.subscribe(`${this.topicHelper.prefix}/BOARD/SET/*`, msg => {
        let boardSetEvent: BoardSetEvent = JSON.parse(msg.getBinaryAttachment());
        if (boardSetEvent.playerName == "Player1") {
          this.player1Status = "Player1 Board Set!";
          this.boardsSet++;
        } else {
          this.player2Status = "Player2 Board Set!";
          this.boardsSet++;
        }

        if (this.boardsSet == 2) {
          this.solaceClient.disconnect();
        }
      });
    });
  }

  async connectToSolace() {
    await this.solaceClient.connect();
  }

  /**
   * Function to start the game if both players joined. Immedeiately disconnect thereafter to prevent events from coming through
   */
  startGame() {
    if (this.gameStart.Player1 && this.gameStart.Player2) {
      this.solaceClient.publish(`${this.topicHelper.prefix}/GAME/START`, JSON.stringify(this.gameStart));
      this.player1Status = "Waiting for Player1 to set board..";
      this.player2Status = "Waiting for Player2 to set board..";
    }
  }

  detached() {
    this.solaceClient.unsubscribe(`${this.topicHelper.prefix}/JOIN/*`);
  }
}
