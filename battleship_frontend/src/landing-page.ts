import { PlayerJoined, GameStart } from './event-objects/player-events';
import { inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { SolaceClient } from 'config/SolaceClient';



@inject(Router, SolaceClient)
export class LandingPage {

  player1Joined: boolean = false;
  player2Joined: boolean = false;

  gameStart: GameStart;

  constructor(private router: Router, private solaceClient: SolaceClient) {
    this.gameStart = new GameStart();
  
  }

  activate(params, routeConfig) {
    // solace logic
    this.connectToSolace().then(()=>{
      this.solaceClient.subscribe("battleship/join/*", (msg) => {
        console.log(msg.getBinaryAttachment());
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

  startGame(){
    if(this.player1Joined && this.player2Joined){
      this.solaceClient.publish("battleship/game/start",JSON.stringify(this.gameStart));
      this.solaceClient.disconnect()
    }
  }
}
