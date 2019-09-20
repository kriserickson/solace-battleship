import { PlayerName } from "./player-events";

export type PrivateBoardCellState = 'ship' | 'empty';
export type KnownBoardCellState = 'hit' | 'miss' | 'empty';

export class Move {
  player: PlayerName;
  x: number;
  y: number;
  action: KnownBoardCellState;
}
