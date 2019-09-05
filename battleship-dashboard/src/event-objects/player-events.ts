import { KnownBoardCellState, PrivateBoardCellState } from "./board-events";

export type PlayerName = 'Player1' | 'Player2';

export class Player {
  name: PlayerName;
  nickname: string;
  boardState: PrivateBoardCellState[][];
  knownOpponentBoardState: KnownBoardCellState[][];
  isTurn: boolean;
}