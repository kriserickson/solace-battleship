import { Player, PlayerJoined } from './event-objects/player-events';
import { inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { SolaceClient } from 'config/SolaceClient';

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
        console.log("Starting game...");
        console.log(msg.getBinaryAttachment());
        this.router.navigateToRoute("board-set");
        });
    }).catch(ex=>{
      console.log(ex);
    }
      );

    this.player.name=params.player;
    
    
  }

  async connectToSolace() {
    await this.solaceClient.connect();
  }

  joinGame() {
    if(!this.playerNickname) {
      alert("Please enter a nickname before continuing");
      return;
    }

   
    this.player.nickname=this.playerNickname;
    let topicName:string  = `battleship/join/${this.player.name}`;
    let playerJoined: PlayerJoined = new PlayerJoined();
    playerJoined.playerName = this.player.name;
    playerJoined.playerNickname = this.playerNickname;
    console.log("Publishing...")
    this.solaceClient.publish(topicName, JSON.stringify(playerJoined));
    this.pageState="WAITING";
  }

  navigate(routeName: string) {
    this.router.navigateToRoute(routeName);
  }

  
}
