import { Player, PlayerName } from './event-objects/player-events';
import { inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { SolaceClient } from 'config/SolaceClient';

class PlayerJoinedMessage{
  playerName: PlayerName;
  playerNickname: string;
}


@inject(Router, SolaceClient, Player)
export class LandingPage {
  player1QR: string;
  player2QR: string;
  player1Joined: boolean = false;
  player2Joined: boolean = false;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player) {
    let player1JoinURL=encodeURI(`http://${location.host}/join/player1`);
    let player2JoinURL=encodeURI(`http://${location.host}/join/player2`);

    this.player1QR = `https://api.qrserver.com/v1/create-qr-code/?data=${player1JoinURL}&amp;size=200x200&amp;color=00CB95&amp;bgcolor=333333` 
    this.player2QR = `https://api.qrserver.com/v1/create-qr-code/?data=${player2JoinURL}&amp;size=200x200&amp;color=00CB95&amp;bgcolor=333333` 
  }

  activate(params, routeConfig) {
    // solace logic
    this.connectToSolace().then(()=>{
      this.solaceClient.subscribe("battleship/join/*", (msg) => {
        if(msg.getBinaryAttachment()) {
          let playerJoinedMessage: PlayerJoinedMessage = JSON.parse(msg.getBinaryAttachment());
          if(playerJoinedMessage.playerName=="Player1"){
            this.player1Joined=true;
          }else{
            this.player2Joined=true;
          }
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
      this.solaceClient.publish("battleship/game/start",`Game started!`);
    }
  }
}
