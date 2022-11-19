package com.codingame.game;

import java.util.Arrays;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.toggle.ToggleModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.codingame.model.Board;
import com.codingame.model.InvalidMoveException;
import com.codingame.model.Point;
import com.codingame.parameter.Constant;
import com.codingame.view.ViewConstant;
import com.codingame.view.Viewer;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
  @Inject private MultiplayerGameManager<Player> gameManager;
  @Inject private GraphicEntityModule graphics;
  @Inject private TooltipModule tooltips;
  @Inject private ToggleModule toggleModule;
  @Inject private EndScreenModule endScreenModule;
  @Inject private Viewer viewer;

  Board board;
  Player currentPlayer;

  @Override
  public void init() {
    board = new Board();
    board.init();
    viewer.init(board);
    currentPlayer = gameManager.getPlayer(0);
    gameManager.setMaxTurns(Constant.HEIGHT * Constant.WIDTH);
    gameManager.setFirstTurnMaxTime(Constant.FIRST_ROUND_TIME_OUT);
    gameManager.setTurnMaxTime(Constant.TIME_OUT);
    gameManager.setFrameDuration(ViewConstant.FRAME_DURATION);
  }

  @Override
  public void gameTurn(int turn) {
    String nickName = currentPlayer.getNicknameToken();
    int teamId = currentPlayer.getIndex();
    try {
      sendInputs(turn);
      currentPlayer.execute();
      String[] outputs = currentPlayer.getOutputs().get(0).split(";", -1);
      if (outputs.length > 1) {
        String message = outputs[1];
        currentPlayer.setMessage(message);
      }
      outputs[0] = outputs[0].toUpperCase().trim();
      boolean isRandom = outputs[0].equals("RANDOM");
      Point move;
      Point tile = null;
      if (isRandom) {
        move = board.calculateRandomMove(teamId, gameManager.getRandom());
      } else {
        try {
          int[] coordinates =
              Arrays.stream(outputs[0].split(" ")).mapToInt(s -> Integer.parseInt(s)).toArray();
          if (coordinates.length != 4) {
            throw new InvalidMoveException(
                "You must give 4 integer coordinates separated by spaces.");
          }
          move = new Point(coordinates[0], coordinates[1]);
          tile = new Point(coordinates[2], coordinates[3]);
        } catch (NumberFormatException e) {
          throw new InvalidMoveException("You must give 4 integer coordinates separated by spaces.");
        }
      }
      board.movePawn(teamId, move);
      if (isRandom) {
        tile = board.calculateRandomRemovedTile(gameManager.getRandom());
      }
      board.removeTile(teamId, tile);
      viewer.applyAction(graphics, gameManager, move, tile, teamId);
    } catch (TimeoutException e) {
      gameManager.addToGameSummary(
          GameManager.formatErrorMessage(nickName + " did not output in time!"));
      currentPlayer.deactivate(nickName + " timeout.");
      currentPlayer.setScore(-1);
      gameManager.endGame();
      return;
    } catch (InvalidMoveException e) {
      gameManager.addToGameSummary(
          GameManager.formatErrorMessage(nickName + " Invalid action : " + e.getMessage()));
      currentPlayer.deactivate(nickName + " made an invalid action.");
      currentPlayer.setScore(-1);
      gameManager.endGame();
      return;
    }
    if (!board.canMove(1 - teamId)) {
      currentPlayer.setScore(1);
      gameManager.endGame();
      return;
    }
    currentPlayer = gameManager.getPlayer(1 - teamId);
  }

  public void sendInputs(int turn) {
    int id = currentPlayer.getIndex();
    if (turn <= 2) {
      currentPlayer.sendInputLine(Integer.toString(board.getTeamPosition(id).getX()));
      currentPlayer.sendInputLine(Integer.toString(board.getTeamPosition(id).getY()));
    }
    int oppId = 1 - id;
    currentPlayer.sendInputLine(Integer.toString(board.getTeamPosition(oppId).getX()));
    currentPlayer.sendInputLine(Integer.toString(board.getTeamPosition(oppId).getY()));

    currentPlayer.sendInputLine(Integer.toString(board.getLastRemovedTile(oppId).getX()));
    currentPlayer.sendInputLine(Integer.toString(board.getLastRemovedTile(oppId).getY()));
  }

  @Override
  public void onEnd() {
    int[] scores = {gameManager.getPlayer(0).getScore(), gameManager.getPlayer(1).getScore()};
    String[] text = new String[2];
    int winId = scores[0] > scores[1] ? 0 : 1;
    String nickName = gameManager.getPlayer(winId).getNicknameToken();
    gameManager.addToGameSummary(GameManager.formatSuccessMessage(nickName + " won"));
    gameManager.addTooltip(gameManager.getPlayer(winId), nickName + " won");
    text[1 - winId] = "Lost";
    text[winId] = "Won";
    endScreenModule.setScores(scores, text);
  }
}
