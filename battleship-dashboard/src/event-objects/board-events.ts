type CellState = 'hit' | 'miss' | 'empty';
type Player = 'Player1' | 'Player2';


class BoardState {
  boardState: CellState [][];
}

class Move {
  player: Player;
  move: CellState;
}

