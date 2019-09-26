import { KnownBoardCellState, PrivateBoardCellState } from "./board-events";

export type PlayerName = 'Player1' | 'Player2';
/**
 * Object that represents the player in a game
 * name: name of the player
 * nickname: nickname for the player
 * boardState: state of the player's board
 * knownOpponentBoardState: known state of the opponent's board
 * isTurn: whether its the players turn
 * @author: Thomas Kunnumpurath, Andrew Roberts
 */
export class Player {
  name: PlayerName;
  nickname: string;
  boardState: PrivateBoardCellState[][];
  knownOpponentBoardState: KnownBoardCellState[][];
  isTurn: boolean;
}