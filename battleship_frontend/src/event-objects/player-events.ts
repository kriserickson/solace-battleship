import { KnownBoardCellState, PrivateBoardCellState } from "./board-events";

export type PlayerName = 'Player1' | 'Player2';
/**
 * Object that represents the player in a game
 * name: name of the player
 * nickname: nickname for the player
 * boardState: state of the player's board
 * publicBoardState: public state of the board
 * isTurn: whether its the players turn
 * @author: Thomas Kunnumpurath, Andrew Roberts
 */
export class Player {
  name: PlayerName;
  nickname: string;
  internalBoardState: PrivateBoardCellState[][];
  publicBoardState: KnownBoardCellState[][];
  isTurn: boolean;
}

/**
 * Object that represents the player joined event
 * playerName: the name of the player (Player1 or player2)
 * playerNickname: the nickname of hte player
 */
export class PlayerJoined{
  playerName: PlayerName;
  playerNickname: string;
}

/**
 * Object that represents the start event of the game
 * player1: The PlayerJoined object for the start of the game
 * player2: The PlayerJoined object for the start of the game
 */
export class GameStart{
  player1: PlayerJoined;
  player2: PlayerJoined;
}
