import { Move, Player,  CellState } from './board-events';

export default class DashboardEvent {
  Player1Board: CellState[][];
  Player2Board: CellState[][];
  move: Move;

}
