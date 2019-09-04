import { inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { SolaceClient } from 'clients/SolaceClient';

@inject(Router, SolaceClient)
export class LandingPage {
  // aurelia
  router = null;
  pageState = "PLAYER_DETAILS"; // PLAYER_DETAILS => QR_CODE
  // solace
  solaceClient = null;
  // app logic
  qrCodeSrc = null;
  player1Nickname = null;
  player2Nickname = null;

  constructor(router: Router, solaceClient: SolaceClient) {
    this.router = router;
    this.solaceClient = solaceClient;
  }

  activate(params, routeConfig) {
    // solace logic
    this.connectToSolace();
    // form url to embed in QR code 
    let url = `localhost:12345/join`;
    // embed url in QR code
    let src = `https://api.qrserver.com/v1/create-qr-code/?data=${url}&amp;size=200x200&amp;color=00CB95&amp;bgcolor=333333>` 
    this.qrCodeSrc = src;
  }

  async connectToSolace() {
    await this.solaceClient.connect();
    this.solaceClient.subscribe("battleship/start", (msg) => {
      if(msg.getBinaryAttachment()) {
        let player2Nickname = msg.getBinaryAttachment();
        console.log(`Player 2 joined the game using nickname "${player2Nickname}"!`);
        this.player2Nickname = player2Nickname;
      }
    });
  }

  navigatePageState(page: string) {
    if(page === "PLAYER_DETAILS" && !this.player1Nickname) {
      alert("Please enter a nickname before continuing!");
      return;
    }
    this.pageState = page;   
  }
 
}