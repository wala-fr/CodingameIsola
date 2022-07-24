package com.codingame.view;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.RoundedRectangle;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.codingame.model.Board;
import com.codingame.model.Point;
import com.codingame.parameter.Constant;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Viewer {

  @Inject private MultiplayerGameManager<Player> gameManager;
  @Inject private GraphicEntityModule graphics;
  @Inject private TooltipModule tooltips;

  private RoundedRectangle[][] rectangles;
  private Circle[] pawns = new Circle[2];
  private PlayerUI[] playerUIS = new PlayerUI[2];
  private int circleCoordinateDelta;

  public void init(Board board) {
    int viewerWidth = graphics.getWorld().getWidth();
    int viewerHeight = graphics.getWorld().getHeight();
    int height = Constant.HEIGHT;
    int width = Constant.WIDTH;
    rectangles = new RoundedRectangle[height][width];
    graphics
        .createRectangle()
        .setWidth(1920)
        .setHeight(1080)
        .setFillColor(ViewConstant.BACK_GROUND_COLOR);
    int rectangleSize = viewerHeight / (height + 1);
    int fontSize = rectangleSize / 2;

    int startX = (viewerWidth - rectangleSize * width + fontSize) / 2;
    int d = 11;
    // color the board
    graphics
        .createRectangle()
        .setX(startX - d)
        .setY(rectangleSize / 2 - fontSize / 2 - d)
        .setWidth(width * rectangleSize + 2 * d)
        .setHeight(width * rectangleSize + 2 * d)
        .setFillColor(ViewConstant.BOARD_COLOR);

    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        int xRectangle = startX + x * rectangleSize;
        int yRectangle = rectangleSize / 2 + y * rectangleSize - fontSize / 2;
        RoundedRectangle rectangle =
            graphics
                .createRoundedRectangle()
                .setWidth(rectangleSize)
                .setHeight(rectangleSize)
                .setX(xRectangle)
                .setY(yRectangle)
                .setFillColor(ViewConstant.RECTANGLE_COLOR)
                .setLineWidth(ViewConstant.TILE_GAP)
                .setLineColor(ViewConstant.BOARD_COLOR);
        rectangles[y][x] = rectangle;
        tooltips.setTooltipText(rectangle, "(" + x + ", " + y + ")");
        if (x == 0) {
          int xText = xRectangle - (int) (rectangleSize / 1.25) + fontSize / 2;
          int yText = yRectangle + fontSize / 2;
          graphics
              .createText(Integer.toString(y))
              .setX(xText)
              .setY(yText)
              .setFillColor(ViewConstant.BOARD_COLOR)
              .setFontFamily(ViewConstant.FONT)
              .setFontSize(fontSize);
        }
        if (y == height - 1) {
          int xText = xRectangle + (int) (fontSize / 1.5);
          int yText = yRectangle + rectangleSize + fontSize / 4;
          graphics
              .createText(Integer.toString(x))
              .setX(xText)
              .setY(yText)
              .setFillColor(ViewConstant.BOARD_COLOR)
              .setFontFamily(ViewConstant.FONT)
              .setFontSize(fontSize);
        }
      }
    }
    int circleGape = ViewConstant.TILE_GAP + ViewConstant.PAWN_GAP;
    int circleRadius = rectangleSize / 2 - circleGape;
    circleCoordinateDelta = circleRadius + circleGape;
    for (int i = 0; i < 2; i++) {
      Point teamPosition = board.getTeamPosition(i);
      pawns[i] =
          graphics
              .createCircle()
              .setRadius(circleRadius)
              .setFillColor(ViewConstant.PAWN_COLORS[i])
              .setLineColor(ViewConstant.BOARD_COLOR)
              .setLineWidth(ViewConstant.PAWN_LINE_WIDTH);
      setPawnPosition(i, teamPosition);
    }
    for (int i = 0; i < 2; ++i) {
      playerUIS[i] = new PlayerUI(gameManager.getPlayer(i), graphics, tooltips, this, board);
    }
  }

  public void applyAction(
      GraphicEntityModule graphics,
      MultiplayerGameManager<Player> gameManager,
      Point move,
      Point tile,
      int player) {
    playerUIS[player].group.setAlpha(1);
    playerUIS[1 - player].group.setAlpha(0.5);
    playerUIS[player].update(
        graphics, tooltips, gameManager.getPlayer(player).getMessage(), move, tile);
    graphics.commitEntityState(0, playerUIS[player].group);
    graphics.commitEntityState(0, playerUIS[1 - player].group);

    setPawnPosition(player, move);
    graphics.commitEntityState(ViewConstant.PAWN_TIME, pawns[player]);

    getRectangle(tile).setFillColor(ViewConstant.BOARD_COLOR);
    graphics.commitEntityState(1, getRectangle(tile));
  }

  private RoundedRectangle getRectangle(Point p) {
    return rectangles[p.getY()][p.getX()];
  }

  private void setPawnPosition(int teamId, Point teamPosition) {
    RoundedRectangle rectangle = getRectangle(teamPosition);
    pawns[teamId]
        .setX(rectangle.getX() + circleCoordinateDelta)
        .setY(rectangle.getY() + circleCoordinateDelta);
  }
}
