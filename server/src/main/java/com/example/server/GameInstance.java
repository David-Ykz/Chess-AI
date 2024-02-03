package com.example.server;

import com.github.bhlangonijr.chesslib.Board;
import java.time.Duration;
import java.time.LocalDateTime;

public class GameInstance {
    public final long instanceLifetimeLimit = 3; // 3 hours
    public LocalDateTime createDate;
    public Board board;

    GameInstance() {
        this.createDate = LocalDateTime.now();
        this.board = new Board();
    }

    public boolean exceededLifetime() {
        return Duration.between(createDate, LocalDateTime.now()).toHours() >= instanceLifetimeLimit;
    }
}
