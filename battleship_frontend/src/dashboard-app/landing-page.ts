import { JoinResult } from "./../common/events";
import { PlayerJoined, GameStart, TopicHelper, BoardSetEvent } from "../common/events";
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
    // Connect to Solace
    this.solaceClient.connect().then(() => {
      //Listener for join request
      //Listener for board set events
    });
  }

  /**
   * Function to start the game if both players joined. Immedeiately disconnect thereafter to prevent events from coming through
   */
  startGame() {
    if (this.gameStart.Player1 && this.gameStart.Player2) {
      this.solaceClient.publish(`${this.topicHelper.prefix}/GAME-START/DASHBOARD`, JSON.stringify(this.gameStart));
      this.player1Status = "Waiting for Player1 to set board..";
      this.player2Status = "Waiting for Player2 to set board..";
    }
  }

  detached() {
    //Unsubscribe from the ../JOIN-REQUEST/* event
  }
}
