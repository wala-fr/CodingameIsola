package com.codingame.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.codingame.model.Board;
import com.codingame.model.InvalidMoveException;
import com.codingame.model.Point;
import com.codingame.parameter.Constant;

public class BoardTest {

  @ParameterizedTest
  @MethodSource("testMovePawn1")
  public void testMovePawn(
      int teamId,
      Point position,
      Point nextPosition,
      Point opponentPosition,
      Point removedTilePosition,
      boolean exception)
      throws InvalidMoveException {
    Board board = new Board();
    board.setTeamPosition(teamId, position);
    if (opponentPosition != null) {
      board.setTeamPosition(1 - teamId, opponentPosition);
    }
    if (removedTilePosition != null) {
      board.removeTile(0, removedTilePosition);
    }
    if (exception) {
      assertThrows(InvalidMoveException.class, () -> board.movePawn(teamId, nextPosition));
    } else {
      board.movePawn(teamId, nextPosition);
      assertEquals(board.getTeamPosition(teamId), nextPosition);
    }
  }

  public static Stream<Arguments> testMovePawn1() {
    List<Arguments> arguments = new ArrayList<>();
    for (int teamId = 0; teamId < 2; teamId++) {
      boolean exception = true;
      Point position = new Point(0, 0);
      // stay put
      arguments.add(Arguments.of(teamId, position, position, null, null, exception));
      // move to opponent pawn
      arguments.add(
          Arguments.of(teamId, position, new Point(1, 0), new Point(1, 0), null, exception));
      // move to removed tile
      arguments.add(
          Arguments.of(teamId, position, new Point(1, 0), null, new Point(1, 0), exception));
      // move out
      arguments.add(Arguments.of(teamId, position, new Point(-1, 0), null, null, exception));
      arguments.add(
          Arguments.of(teamId, position, new Point(Constant.WIDTH, 0), null, null, exception));
      arguments.add(Arguments.of(teamId, position, new Point(1, -1), null, null, exception));
      arguments.add(
          Arguments.of(teamId, position, new Point(1, Constant.HEIGHT), null, null, exception));

      position = new Point(2, 2);
      // move too far
      for (int i = 0; i < 5; i++) {
        arguments.add(Arguments.of(teamId, position, new Point(0, i), null, null, exception));
        arguments.add(Arguments.of(teamId, position, new Point(4, i), null, null, exception));
        arguments.add(Arguments.of(teamId, position, new Point(i, 0), null, null, exception));
        arguments.add(Arguments.of(teamId, position, new Point(i, 4), null, null, exception));
      }
      exception = false;
      for (int i = 1; i <= 3; i++) {
        arguments.add(Arguments.of(teamId, position, new Point(1, i), null, null, exception));
        arguments.add(Arguments.of(teamId, position, new Point(3, i), null, null, exception));
        arguments.add(Arguments.of(teamId, position, new Point(i, 1), null, null, exception));
        arguments.add(Arguments.of(teamId, position, new Point(i, 3), null, null, exception));
      }
    }
    return arguments.stream();
  }

  @Test
  public void testRemoveTile() throws InvalidMoveException {
    Board board = new Board();
    Point position = new Point(0, 0);
    board.setTeamPosition(0, position);
    board.setTeamPosition(1, new Point(2, 2));

    assertThrows(InvalidMoveException.class, () -> board.removeTile(0, position));
    assertEquals(board.getLastRemovedTile(0), new Point(-1, -1));
    int tileNb = Constant.WIDTH * Constant.HEIGHT - 2;
    assertEquals(board.calculatePossibleRemovedTiles().size(), tileNb);
    board.setTeamPosition(0, new Point(1, 1));
    assertFalse(board.isTileRemoved(position));
    board.removeTile(0, position);
    assertEquals(board.getLastRemovedTile(0), position);

    assertTrue(board.isTileRemoved(position));
    assertEquals(board.calculatePossibleRemovedTiles().size(), tileNb - 1);

    assertThrows(InvalidMoveException.class, () -> board.removeTile(0, position));
  }

  @Test
  public void testCanMove() throws InvalidMoveException {
    for (int teamId = 0; teamId < 2; teamId++) {
      Board board = new Board();
      board.setTeamPosition(teamId, new Point(1, 1));
      board.setTeamPosition(1 - teamId, new Point(0, 0));
      assertTrue(board.canMove(teamId));
      Point[] tiles = {
        new Point(1, 0),
        new Point(2, 0),
        new Point(0, 1),
        new Point(2, 1),
        new Point(0, 2),
        new Point(1, 2),
        new Point(2, 2)
      };
      int possibleMoveNb = 7;
      for (Point tile : tiles) {
        assertTrue(board.canMove(teamId));
        assertEquals(board.calculatePossibleMoves(teamId).size(), possibleMoveNb);
        board.removeTile(0, tile);
        possibleMoveNb--;
      }
      assertFalse(board.canMove(teamId));
      assertEquals(board.calculatePossibleMoves(teamId).size(), possibleMoveNb);
    }
  }
}
