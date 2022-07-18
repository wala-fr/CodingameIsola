package com.codingame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.codingame.parameter.Constant;

public class Board {

  private boolean[][] removedTiles = new boolean[Constant.HEIGHT][];
  private Point[] teamPositions = new Point[Constant.TEAM_NB];
  private Point[] lastRemovedTile = {new Point(-1, -1), new Point(-1, -1)};

  {
    for (int i = 0; i < removedTiles.length; i++) {
      removedTiles[i] = new boolean[Constant.WIDTH];
    }
    for (int i = 0; i < teamPositions.length; i++) {
      teamPositions[i] = new Point();
    }
  }

  public void init() {
    int y = (Constant.HEIGHT - 1) / 2;
    setTeamPosition(0, new Point(0, y));
    setTeamPosition(1, new Point(Constant.WIDTH - 1, y));
  }

  public void removeTile(int teamId, Point tilePosition) throws InvalidMoveException {
    canRemoveTile(tilePosition);
    removedTiles[tilePosition.getY()][tilePosition.getX()] = true;
    lastRemovedTile[teamId].copy(tilePosition);
  }

  public void canRemoveTile(Point tilePosition) throws InvalidMoveException {
    tilePosition.testIsIn();
    for (int i = 0; i < teamPositions.length; i++) {
      if (tilePosition.equals(teamPositions[i])) {
        throw new InvalidMoveException("You can not removed a tile occupied by a pawn.");
      }
    }
    if (isTileRemoved(tilePosition)) {
      throw new InvalidMoveException("The tile " + tilePosition + " is already removed.");
    }
  }
  
  public boolean isTileRemoved(Point tilePosition) {
    return removedTiles[tilePosition.getY()][tilePosition.getX()];
  }

  public void movePawn(int teamId, Point nextPosition) throws InvalidMoveException {
    canMovePawn(teamId, nextPosition);
    setTeamPosition(teamId, nextPosition);
  }

  private void canMovePawn(int teamId, Point nextPosition) throws InvalidMoveException {
    nextPosition.testIsIn();
    if (teamPositions[1 - teamId].equals(nextPosition)) {
      throw new InvalidMoveException(
          "You can't move to " + nextPosition + ". It's occupied by the opponent pawn.");
    }
    Point actualPosition = teamPositions[teamId];
    if (actualPosition.equals(nextPosition)) {
      throw new InvalidMoveException("You can't stay put.");
    }
    if (Math.abs(actualPosition.getX() - nextPosition.getX()) > 1
        || Math.abs(actualPosition.getY() - nextPosition.getY()) > 1) {
      throw new InvalidMoveException(
          "You can't move from " + actualPosition + " to " + nextPosition + ".");
    }
    if (removedTiles[nextPosition.getY()][nextPosition.getX()]) {
      throw new InvalidMoveException("You can't move to " + nextPosition + ". There's no tile.");
    }
  }

  public void setTeamPosition(int teamId, Point nextPosition) {
    teamPositions[teamId].copy(nextPosition);
  }

  public Point getTeamPosition(int teamId) {
    return teamPositions[teamId];
  }
  
  public Point getLastRemovedTile(int teamId) {
    return lastRemovedTile[teamId];
  }

  public boolean canMove(int teamId) {
    Point actualPosition = teamPositions[teamId];
    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        try {
          canMovePawn(teamId, new Point(actualPosition.getX() + x, actualPosition.getY() + y));
          return true;
        } catch (InvalidMoveException e) {
        }
      }
    }
    return false;
  }

  public Point calculateRandomMove(int teamId, Random random) {
    List<Point> possibleMoves = calculatePossibleMoves(teamId);
    return possibleMoves.get(random.nextInt(possibleMoves.size()));
  }

  public List<Point> calculatePossibleMoves(int teamId) {
    List<Point> possibleMoves = new ArrayList<>();
    Point actualPosition = teamPositions[teamId];
    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        try {
          Point point = new Point(actualPosition.getX() + x, actualPosition.getY() + y);
          canMovePawn(teamId, point);
          possibleMoves.add(point);
        } catch (InvalidMoveException e) {
        }
      }
    }
    return possibleMoves;
  }

  public Point calculateRandomRemovedTile(Random random) {
    List<Point> possibleMoves = calculatePossibleRemovedTiles();
    return possibleMoves.get(random.nextInt(possibleMoves.size()));
  }
  
  public List<Point> calculatePossibleRemovedTiles() {
    List<Point> possibleRemoveTiles = new ArrayList<>();
    for (int y = 0; y < removedTiles.length; y++) {
      for (int x = 0; x < removedTiles[0].length; x++) {
        try {
          Point point = new Point(x, y);
          canRemoveTile(point);
          possibleRemoveTiles.add(point);
        } catch (InvalidMoveException e) {
        }
      }
    }
    return possibleRemoveTiles;
  }
}
