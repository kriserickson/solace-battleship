package com.solace.battleship.engine;

import com.solace.battleship.events.*;

/**
 * Interface for the battleship game engine. Determines all rules and functions
 * associated with the game.
 * 
 * @Author Andrew Roberts, Thomas Kunnumpurath
 */
public interface IGameEngine {

    /**
     * Function to request for a game to be joined
     * 
     * @param sessionId a unique identified for the game's session
     * @param request   A player join request
     * @return The result of a Join request
     */
    public JoinResult requestToJoinGame(PlayerJoined request);

    /**
     * Function to check if a game can start
     * 
     * @param sessionId
     * @return true if both players have joined
     */
    public boolean canGameStart(String sessionId);

    /**
     * Function to get the GameStart event
     * 
     * @param sessionId
     * @return a GameStart event object
     */
    public GameStart getGameStartAndStartGame(String sessionId);
}
