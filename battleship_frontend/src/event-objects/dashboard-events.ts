import {
  Move,
  KnownBoardCellState
} from "./board-events";

export class ResponseEvent {
  Player1KnownOpponentBoard: KnownBoardCellState[][];
  Player2KnownOpponentBoard: KnownBoardCellState[][];
  move: Move;
}
