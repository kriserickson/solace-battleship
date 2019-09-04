import {PLATFORM} from 'aurelia-pal';
import './css/style.css';

export class App {
  router: any;
  
  configureRouter(config, router){
    config.title = 'Battleship';
    config.options.pushState = true;  // No # in URL
    config.map([
      { route: '/', moduleId: PLATFORM.moduleName('landing-page'), name: ''},
      { route: '/join', moduleId: PLATFORM.moduleName('join'), name: 'join'},
    ]);
  
    this.router = router;
  }
}