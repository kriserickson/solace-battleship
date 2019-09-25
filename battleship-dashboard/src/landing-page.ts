import { Player } from './event-objects/player-events';
import { inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { SolaceClient } from 'clients/SolaceClient';

@inject(Router, SolaceClient, Player)
export class LandingPage {
  pageState = "PLAYER_DETAILS"; // PLAYER_DETAILS => QR_CODE
  // solace
  // app logic
  qrCodeSrc = null;
  player1Nickname = null;
  player2Nickname = null;

  constructor(private router: Router, private solaceClient: SolaceClient, private player: Player) {
  }

  activate(params, routeConfig) {
    // solace logic
    this.connectToSolace().then(()=>{
      this.solaceClient.subscribe("battleship/join", (msg) => {
        if(msg.getBinaryAttachment()) {
          let player2Nickname = msg.getBinaryAttachment();
          console.log(`Player 2 joined the game using nickname "${player2Nickname}"!`);
          this.player2Nickname = player2Nickname;
        }
      })
    }
    );
   
    // form url to embed in QR code 
    let url = `localhost:12345/join`;
    // embed url in QR code
    let src = `https://api.qrserver.com/v1/create-qr-code/?data=${url}&amp;size=200x200&amp;color=00CB95&amp;bgcolor=333333>` 
    this.qrCodeSrc = src;
  }

  async connectToSolace() {
    await this.solaceClient.connect();
    
  }

  startGame(){
    if(this.player2Nickname && this.player1Nickname){
      this.solaceClient.publish("battleship/game/start",`${this.player1Nickname} started a game with ${this.player2Nickname}`);
      this.router.navigateToRoute("board-set");
    }
  }

  navigatePageState(page: string) {
    if(page === "PLAYER_DETAILS" && !this.player1Nickname) {
      alert("Please enter a nickname before continuing!");
      return;
    }

    this.player.name = 'Player1';
    this.player.nickname = this.player1Nickname;
    
    this.pageState = page;   
  }
 
}
