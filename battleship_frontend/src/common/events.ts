/**
 * This shared library represents all the events and event related objects that are available to the battleship application
 */

export class TopicHelper {
    private _prefix: string;

    get prefix(): string{
        return this._prefix;
    }

    set prefix(prefix:string){
        this._prefix = prefix;
    }
}



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
  Player1: PlayerJoined;
  Player2: PlayerJoined;
}


// CellState for the Board
export type PrivateBoardCellState = 'ship' | 'empty';

// Cell state for the opponent's board
export type KnownBoardCellState = 'hit' | 'miss' | 'empty';

/**
 * Object that represents a players move response
 * Author: Thomas Kunnumpurath, Andrew Roberts
 */
export class Move {
  player: PlayerName;
  x: number;
  y: number;
}

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
  
 /**
  * Object representing a boardset event
  */ 

  export class BoardSetEvent {
      player: Player;
      shipsSet: number;
  }