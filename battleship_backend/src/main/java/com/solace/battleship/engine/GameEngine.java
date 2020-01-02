package com.solace.battleship.engine;

import java.util.HashMap;
import java.util.Map;

import com.solace.battleship.events.*;

import org.springframework.stereotype.Component;

/**
 * GameEngine
 */
@Component
public class GameEngine implements IGameEngine {

    private final Map<String, GameSession> gameSessionMap = new HashMap<>();

    public static final String SESSION_DOES_NOT_EXIST_ERROR = "Your session has either expired or a server error has occurred.";
    public static final String GAME_ALREADY_STARTED_ERROR = "Game has already started!";
    public static final String PLAYER_JOIN_SUCCESS = "Player has joined the game successfully!";
    public static final String PLAYER_ALREADY_JOINED_ERROR="Player has already joined the game!";
    public static final String BOARD_SET_SUCCESS = "Board has been set!";
    public static final String BOARD_SET_SERVER_ERROR = "Something has gone wrong on the server, this is not your fault.";
    public static final String BOARD_ALREADY_SET_ERROR = "Board has already been set!";


    @Override
    public JoinResult requestToJoinGame(PlayerJoined request) {
        String returnMessage = "";
        boolean joinRequestResult;

        gameSessionMap.putIfAbsent(request.getSessionId(), new GameSession(request.getSessionId()));
        GameSession session = gameSessionMap.get(request.getSessionId());
        if (session.getGameState() != GameState.WAITING_FOR_JOIN) {
            returnMessage = GAME_ALREADY_STARTED_ERROR;
            joinRequestResult = false;
        } else {
            joinRequestResult = session.getGameStart().setPlayerJoined(request);
            if (joinRequestResult) {
                returnMessage = PLAYER_JOIN_SUCCESS;
            } else {
                returnMessage = PLAYER_ALREADY_JOINED_ERROR;
            }
        }
        return new JoinResult(request.getPlayerName(), joinRequestResult, returnMessage);
    }

    @Override
    public boolean canGameStart(String sessionId) {
        GameSession session = gameSessionMap.get(sessionId);

        if (session != null && session.getGameState() == GameState.WAITING_FOR_JOIN) {
            return session.getGameStart().getPlayer1() != null && session.getGameStart().getPlayer2() != null;
        }

        return false;
    }

    @Override
    public GameStart getGameStartAndStartGame(String sessionId) {
        GameSession session = gameSessionMap.get(sessionId);
        if (canGameStart(sessionId)) {
            session.setGameState(GameState.WAITING_FOR_BOARD_SET);
            return session.getGameStart();
        }
        return null;
    }

    @Override
    public BoardSetResult requestToSetBoard(BoardSetRequest request) {
        String returnMessage = "";
        boolean boardSetRequestResult;

        GameSession session = gameSessionMap.get(request.getSessionId());
        if(session.equals(null)) {
            returnMessage = SESSION_DOES_NOT_EXIST_ERROR;
            boardSetRequestResult = false;
        }
        else if (session.getGameState() != GameState.WAITING_FOR_BOARD_SET) {
            returnMessage = BOARD_ALREADY_SET_ERROR;
            boardSetRequestResult = false;
        }
        else {
            boardSetRequestResult = session.setBoard(request);
            if (boardSetRequestResult) {
                returnMessage = BOARD_SET_SUCCESS;
            } else {
                returnMessage = BOARD_SET_SERVER_ERROR;
            }
        }
        // update MatchStart object
        if(request.getPlayerName() == PlayerName.Player1) {
            session.getMatchStart().setPlayer1Board(new BoardSetResult(request.getPlayerName(), boardSetRequestResult, returnMessage));
        }
        else if (request.getPlayerName() == PlayerName.Player2) {
            session.getMatchStart().setPlayer2Board(new BoardSetResult(request.getPlayerName(), boardSetRequestResult, returnMessage));
        }

        return new BoardSetResult(request.getPlayerName(), boardSetRequestResult, returnMessage);
    }

    @Override
    public boolean canMatchStart(String sessionId){
        GameSession session = gameSessionMap.get(sessionId);
        if (session != null && session.getGameState() == GameState.WAITING_FOR_BOARD_SET) {
            return session.getMatchStart().getPlayer1Board() != null && session.getMatchStart().getPlayer2Board() != null;
        }
        return false;
    }

    @Override
    public MatchStart getMatchStartAndStartMatch(String sessionId){
        GameSession session = gameSessionMap.get(sessionId);
        if (canMatchStart(sessionId)) {
            session.setGameState(GameState.PLAYER1_TURN);
            return session.getMatchStart();
        }
        return null;
    }

    @Override
    public MoveResponseEvent requestToMakeMove(Move request){
        GameSession session = gameSessionMap.get(request.getSessionId());
        if(session.equals(null)) {
            // I don't know if we want to implement error classes too .. seems unnecessary
            return new MoveResponseEvent();
        }
        return session.makeMove(request);
    }

    @Override
    public boolean shouldMatchEnd(String sessionId){
        GameSession session = gameSessionMap.get(sessionId);
        if(session.equals(null)) {
            // I don't know if we want to implement error classes too .. seems unnecessary
            return false;
        }
        return (session.getPlayer1Score() == 0) || (session.getPlayer2Score() == 0);
    }

    @Override
    public MatchEnd endMatch(String sessionId){
        GameSession session = gameSessionMap.get(sessionId);
        if(session.equals(null)) {
            // I don't know if we want to implement error classes too .. seems unnecessary
            return new MatchEnd();
        }

        if(this.shouldMatchEnd(sessionId)){
            session.setGameState(GameState.GAME_OVER);
            MatchEnd finalScore = new MatchEnd();
            finalScore.setSessionId(sessionId);
            finalScore.setPlayer1Score(session.getPlayer1Score());
            finalScore.setPlayer2Score(session.getPlayer2Score());
            return finalScore;
        }

        // error
        return new MatchEnd();
    }
}