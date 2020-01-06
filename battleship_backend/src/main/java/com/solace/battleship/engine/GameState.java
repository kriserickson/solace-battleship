package com.solace.battleship.engine;

/**
 * The various states for the Game
 */
public enum GameState {
  WAITING_FOR_JOIN, WAITING_FOR_BOARD_SET, PLAYER1_TURN, PLAYER2_TURN, GAME_OVER
}