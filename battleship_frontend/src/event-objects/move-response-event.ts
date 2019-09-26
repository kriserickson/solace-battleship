import {
  Move,
  KnownBoardCellState
} from "./board-events";

/**
 * Object representing the response to a move
 * player1Board - Represents the public state of Player1's board
 * player2Board - Represents the public state of Player2's board
 * move - represents the move that result of the move that was just made
 * Author: Thomas Kunnumpurath, Andrew Roberts
 */
export class MoveResponseEvent {
  player1Board: KnownBoardCellState[][];
  player2Board: KnownBoardCellState[][];
  move: Move;
}
