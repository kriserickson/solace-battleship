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

    /**
     * Function to request setting ships on a board
     *
     * @param request   A board set request
     * @return The result of a board set request
     */
    public BoardSetResult requestToSetBoard(BoardSetRequest request);

    /**
     * Function to check if the match can start
     *
     * @param sessionId
     * @return true if both players have set their ships
     */
    public boolean canMatchStart(String sessionId);

    /**
     * Function to get the MatchStart event
     *
     * @param sessionId
     * @return a GameStart event object
     */
    public MatchStart getMatchStartAndStartMatch(String sessionId);

    /**
     * Function to request making a move
     *
     * @param request   A move request
     * @return The result of the move request
     */
    public MoveResponseEvent requestToMakeMove(Move request);

    /**
     * Function to check if the match can end
     *
     * @param sessionId
     * @return true if a player's score is 0
     */
    public boolean shouldMatchEnd(String sessionId);

    /**
     * Function to end the match and report out the final score
     *
     * @param sessionId
     * @return a MatchEnd object containing the final scores
     */
    public MatchEnd endMatch(String sessionId);
}
