export type CellState = 'hit' | 'miss' | 'empty';
export type Player = 'Player1' | 'Player2';


export class Move {
  player: Player;
  x: number;
  y: number;
  action: CellState;
}
