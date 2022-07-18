package com.codingame.view;

import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.RoundedRectangle;
import com.codingame.gameengine.module.entities.Text;
import com.codingame.model.Board;
import com.codingame.model.Point;

public class PlayerUI {
  public Group group;
  private Text tileRemoved;
  private Text message;

  PlayerUI(Player player, GraphicEntityModule graphics, Viewer viewer, Board board) {
    int teamId = player.getIndex();
    int startX = teamId == 0 ? 200 : graphics.getWorld().getWidth() - 200;
    int startY = 150;
    group = graphics.createGroup();
    RoundedRectangle playerRectangle =
        graphics
            .createRoundedRectangle()
            .setY(startY - 20)
            .setX(startX - 150)
            .setWidth(300)
            .setHeight(700)
            .setLineWidth(15)
            .setFillColor(ViewConstant.AVATAR_COLOR)
            .setLineColor(ViewConstant.PAWN_COLORS[teamId]);
    group.add(playerRectangle);
    group.add(
        graphics
            .createText(player.getNicknameToken())
            .setX(startX)
            .setY(startY + ViewConstant.Y_NICKNAME)
            .setFontSize(ViewConstant.FONT_TEXT)
            .setFontFamily(ViewConstant.FONT)
            .setFontWeight(Text.FontWeight.BOLD)
            .setAnchorX(0.5)
            .setFillColor(ViewConstant.PAWN_COLORS[teamId]));
    group.add(
        graphics
            .createSprite()
            .setImage(player.getAvatarToken())
            .setX(startX)
            .setY(startY + ViewConstant.Y_AVATAR)
            .setAnchorX(0.5)
            .setBaseWidth(200)
            .setBaseHeight(200));
    Text lastTileRemoved =
        graphics
            .createText(" last tile\nremoved")
            .setX(startX)
            .setY(startY + ViewConstant.Y_LAST_TILE_TITLE)
            .setFontSize(ViewConstant.FONT_TEXT)
            .setFontFamily(ViewConstant.FONT)
            .setAnchorX(0.5)
            .setFillColor(ViewConstant.WRITE_COLOR);
    group.add(lastTileRemoved);

    tileRemoved =
        graphics
            .createText("-")
            .setX(startX)
            .setY(startY + ViewConstant.Y_LAST_TILE)
            .setFontSize(ViewConstant.FONT_LAST_TILE)
            .setFontFamily(ViewConstant.FONT)
            .setAnchorX(0.5)
            .setFillColor(ViewConstant.WRITE_COLOR);
    group.add(tileRemoved);
    message =
        graphics
            .createText("")
            .setX(startX - 5)
            .setY(startY + ViewConstant.Y_MESSAGE)
            .setFontSize(ViewConstant.FONT_MESSAGE)
            .setFontFamily(ViewConstant.FONT)
            .setAnchorX(0.5)
            .setFillColor(ViewConstant.WRITE_COLOR);
    group.add(message);
  }

  void update(GraphicEntityModule graphics, String msg, Point move, Point tile) {
    if (!msg.equals(message.getText())) {
      message.setText(msg);
      graphics.commitEntityState(0.1, message);
    }
    tileRemoved.setText(tile.toInputString());
    graphics.commitEntityState(1, tileRemoved);
  }
}
