import {Move, Player, BoardState} from './board-events';

export default class DashboardEvent {
  boards: Record<Player,BoardState>;
  move: Move;
}
