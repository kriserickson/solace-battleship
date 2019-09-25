import { gameConfig } from 'clients/game-config';
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
    gameParams.gamecellPixelSize = gameConfig.gamecell_pixelSize;
  }
  
  configureRouter(config, router){
    config.title = 'Battleship';
    config.options.pushState = true;  // No # in URL
    config.map([
      { route: '/', moduleId: PLATFORM.moduleName('landing-page'), name: ''},
      { route: '/join', moduleId: PLATFORM.moduleName('join'), name: 'join'},
      { route: '/board-set', moduleId: PLATFORM.moduleName('board-set'), name: 'board-set'},
      { route: '/match', moduleId: PLATFORM.moduleName('match'), name: 'match'},
    ]);
  
    this.router = router;
  }
}
