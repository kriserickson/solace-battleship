import { JoinResult } from "./../common/events";
import { Player, PlayerJoined, TopicHelper, GameStart } from "../common/events";
import { inject } from "aurelia-framework";
import { Router } from "aurelia-router";
import { SolaceClient } from "common/solace-client";

/**
 * Class that represents the Join screen for the player
 * @author Thomas Kunnumpurath
 */
@inject(Router, SolaceClient, Player, TopicHelper, GameStart)
export class Join {
  pageState = "PLAYER_DETAILS"; // PLAYER_DETAILS => WAITING
  pageStatus = "Waiting for other player to join...";
  playerNickname: string = null;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player, private topicHelper: TopicHelper, private gameStart: GameStart) {}

  /**
   * Aurelia function that is called once route is activated
   * @param params
   * @param routeConfig
   */
  activate(params, routeConfig) {
    //Connect to Solace
    this.solaceClient
      .connect()
      .then(() => {
        //Warm up the subscription for the JOIN-REPLY
        this.solaceClient.subscribeReply(`${this.topicHelper.prefix}/JOIN-REPLY/${this.player.getPlayerNameForTopic()}/CONTROLLER`);
        //Subscribe to the GAME-START event
      })
      .catch(ex => {
        console.log(ex);
      });

    //Set the name for the player from the route parameter
    this.player.name = params.player;
  }

  /**
   * Function to join a game - asks for the Player's name before continuing
   */
  joinGame() {
    if (!this.playerNickname) {
      alert("Please enter a nickname before continuing");
      return;
    }

    this.player.nickname = this.playerNickname;
    let playerJoined: PlayerJoined = new PlayerJoined();
    playerJoined.playerName = this.player.name;
    playerJoined.playerNickname = this.playerNickname;
    //Publish a join request and change the pageState to waiting if the join request succeeded
    let topicName: string = `${this.topicHelper.prefix}/JOIN-REQUEST/${this.player.getPlayerNameForTopic()}`;
    let replyTopic: string = `${this.topicHelper.prefix}/JOIN-REPLY/${this.player.getPlayerNameForTopic()}/CONTROLLER`;
    this.solaceClient
      .sendRequest(topicName, JSON.stringify(playerJoined), replyTopic)
      .then((msg: any) => {
        let joinResult: JoinResult = JSON.parse(msg.getBinaryAttachment());
        if (joinResult.success) this.pageState = "WAITING";
        else this.pageStatus = "Join Request Failed - Player Already Joined!";
      })
      .catch(error => {
        this.pageStatus = "Join Request Failed!";
      });
  }

  detached() {
    //Unsubscribe from the <PREFIX>/GAME-START and <PREFIX>>/JOIN-REPLY/[PLAYER1 or PLAYER2]
    this.solaceClient.unsubscribe(`${this.topicHelper.prefix}/GAME-START/CONTROLLER`);
    this.solaceClient.unsubscribe(`${this.topicHelper.prefix}/JOIN-REPLY/${this.player.getPlayerNameForTopic()}/CONTROLLER`);
  }
}
