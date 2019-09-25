import { Player } from './event-objects/player-events';
import { inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { SolaceClient } from 'clients/SolaceClient';

@inject(Router, SolaceClient, Player)
export class Join {
  // aurelia
  pageState = "PLAYER_DETAILS"; // PLAYER_DETAILS => WAITING
  // app logic
  playerNickname: string = null;

  constructor(private router: Router,private solaceClient: SolaceClient, private player: Player) {

  }

  activate(params, routeConfig) {
    this.connectToSolace().then(()=>{
      this.solaceClient.subscribe("battleship/game/start",(msg)=>{
        console.log(msg.getBinaryAttachment());
        this.router.navigateToRoute("board-set");
        });
    });
    
  }

  async connectToSolace() {
    await this.solaceClient.connect();
  }

  navigatePageState(page: string) {
    if(page === "PLAYER_DETAILS" && !this.playerNickname) {
      alert("Please enter a nickname before continuing");
      return;
    }

    this.player.name = 'Player2';
    this.player.nickname=this.playerNickname;
    let topicName = `battleship/join`;
    this.solaceClient.publish(topicName, this.playerNickname);
    this.pageState = page;   
  }

  navigate(routeName: string) {
    this.router.navigateToRoute(routeName);
  }
}
