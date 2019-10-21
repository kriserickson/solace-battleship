import { PlayerName } from './player-events';
import {
  Move,
  KnownBoardCellState,
  PrivateBoardCellState
} from "./board-events";

/**
 * Object representing the response to a move
 * player - The name of the player
 * playerBoard - Represents the public state of Player2's board
 * move - represents the move that result of the move that was just made
 * @author: Thomas Kunnumpurath, Andrew Roberts
 */
export class MoveResponseEvent {
  player: PlayerName;
  playerBoard: KnownBoardCellState[][];
  move: Move;
  moveResult : PrivateBoardCellState;
}
