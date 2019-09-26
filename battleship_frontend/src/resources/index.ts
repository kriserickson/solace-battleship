import { GameParams } from './../event-objects/game-params';
import {FrameworkConfiguration, PLATFORM} from 'aurelia-framework';
import {gameConfig} from 'clients/game-config';


export function configure(config: FrameworkConfiguration) {
  config.globalResources([PLATFORM.moduleName('../clients/SolaceClient')]);
}
