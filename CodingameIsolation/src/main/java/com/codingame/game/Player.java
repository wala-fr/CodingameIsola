package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

public class Player extends AbstractMultiplayerPlayer {
  private String message = "";

  @Override
  public int getExpectedOutputLines() {
    return 1;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
