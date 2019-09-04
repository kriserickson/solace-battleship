import { inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { SolaceClient } from 'clients/SolaceClient';

@inject(Router, SolaceClient)
export class Join {
  // aurelia
  router = null;
  pageState = "PLAYER_DETAILS"; // PLAYER_DETAILS => WAITING
  // solace
  solaceClient: SolaceClient = null;
  // app logic
  playerNickname: string = null;

  constructor(router: Router, solaceClient: SolaceClient) {
    this.router = router;
    this.solaceClient = solaceClient;
  }

  activate(params, routeConfig) {
    this.connectToSolace();
  }

  async connectToSolace() {
    await this.solaceClient.connect();
  }

  navigatePageState(page: string) {
    if(page === "PLAYER_DETAILS" && !this.playerNickname) {
      alert("Please enter a nickname before continuing");
      return;
    }
    let topicName = `battleship/start`;
    this.solaceClient.publish(topicName, this.playerNickname);
    this.pageState = page;   
  }

  navigate(routeName: string) {
    this.router.navigateToRoute(routeName);
  }
}
