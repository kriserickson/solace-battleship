package com.solace.battleship.engine;

import java.util.HashMap;
import java.util.Map;

import com.solace.battleship.events.GameStart;
import com.solace.battleship.events.JoinResult;
import com.solace.battleship.events.PlayerJoined;

import org.springframework.stereotype.Component;

/**
 * GameEngine
 */
@Component
public class GameEngine implements IGameEngine {

    private final Map<String, GameSession> gameSessionMap = new HashMap<>();

    public static final String GAME_ALREADY_STARTED_ERROR = "Game has already started!";
    public static final String PLAYER_JOIN_SUCCESS = "Player has joined the game successfully!";
    public static final String PLAYER_ALREADY_JOINED_ERROR="Player has already joined the game!";


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
}