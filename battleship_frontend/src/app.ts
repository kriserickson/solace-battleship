import { gameConfig } from 'config/game-config';
import { PLATFORM } from 'aurelia-pal';
import './css/style.css';
import { inject } from 'aurelia-framework';
import { GameParams } from 'event-objects/game-params';

@inject(GameParams)
export class App {
  router: any;

  constructor(gameParams: GameParams){
    //Initializing the game params
    gameParams.allowedShips = gameConfig.allowed_ships;
    gameParams.gameboardDimensions = gameConfig.gameboard_dimensions;
  }
  
  configureRouter(config, router){
    config.title = 'Battleship';
    config.options.pushState = true;  // No # in URL
    config.map([
      { route: '/', moduleId: PLATFORM.moduleName('landing-page'), name: ''},
      { route: '/join', moduleId: PLATFORM.moduleName('join'), name: 'join'},
      { route: '/board-set', moduleId: PLATFORM.moduleName('board-set'), name: 'board-set'},
      { route: '/match', moduleId: PLATFORM.moduleName('match'), name: 'match'},
      { route: '/game-over/:msg', moduleId: PLATFORM.moduleName('game-over'), name: 'game-over'}
    ]);
  
    this.router = router;
  }

  attached(){
    let script = document.createElement('script');
    script.type="text/javascript";
    script.innerHTML='particlesJS.load("particles-js", "particles.json", null);';
    document.querySelector('body').appendChild(script);
  }
}
