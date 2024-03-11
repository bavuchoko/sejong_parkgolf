package com.pjs.golf.game.dto;

public enum GameStatus {
    OPEN, CLOSED, PLAYING, END;

    public boolean isForwardStep(GameStatus v1) {
        return this.ordinal()+1 == v1.ordinal();
    }
}
