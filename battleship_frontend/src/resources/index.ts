import { GameParams } from './../event-objects/game-params';
import {FrameworkConfiguration, PLATFORM} from 'aurelia-framework';
import {gameConfig} from 'config/game-config';


export function configure(config: FrameworkConfiguration) {
  //Load the SolaceClient connection library on startup
  config.globalResources([PLATFORM.moduleName('../config/SolaceClient')]);
}
