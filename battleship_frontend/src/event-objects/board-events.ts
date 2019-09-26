import { PlayerName } from "./player-events";

// CellState for the Board
export type PrivateBoardCellState = 'ship' | 'empty';

// Cell state for the opponent's board
export type KnownBoardCellState = 'hit' | 'miss' | 'empty';

/**
 * Object that represents a players move
 * Author: Thomas Kunnumpurath, Andrew Roberts
 */
export class Move {
  player: PlayerName;
  x: number;
  y: number;
  action: KnownBoardCellState;
}
