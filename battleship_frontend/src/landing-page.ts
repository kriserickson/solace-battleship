import { PlayerJoined, GameStart } from './event-objects/player-events';
import { inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { SolaceClient } from 'config/SolaceClient';


/**
 * Class that represents a landing page
 */
@inject(Router, SolaceClient)
export class LandingPage {

  player1Joined: boolean = false;
  player2Joined: boolean = false;

  gameStart: GameStart;

  constructor(private router: Router, private solaceClient: SolaceClient) {
    this.gameStart = new GameStart();
  
  }

  /**
   * Aurelia function that is called when the page is navigated to
   * @param params 
   * @param routeConfig 
   */
  activate(params, routeConfig) {
    // solace logic
    this.connectToSolace().then(()=>{
      //Listener for join events
      this.solaceClient.subscribe("battleship/join/*", (msg) => {
        if(msg.getBinaryAttachment()) {
          let playerJoined: PlayerJoined = JSON.parse(msg.getBinaryAttachment());
          if(playerJoined.playerName=="Player1"){
            this.player1Joined=true;
            this.gameStart.player1 = playerJoined;
          }else{
            this.player2Joined=true;
            this.gameStart.player2 = playerJoined;
          }
          this.startGame();
        }
      })
    }
    );
  }

  async connectToSolace() {
    await this.solaceClient.connect();
  }

  /**
   * Function to start the game if both players joined. Immedeiately disconnect thereafter to prevent events from coming through
   */
  startGame(){
    if(this.player1Joined && this.player2Joined){
      this.solaceClient.publish("battleship/game/start",JSON.stringify(this.gameStart));
      this.solaceClient.disconnect()
    }
  }
}
