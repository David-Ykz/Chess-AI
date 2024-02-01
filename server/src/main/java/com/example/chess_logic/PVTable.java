package com.example.chess_logic;
import java.util.HashMap;

public class PVTable {
    public HashMap<Long, EvalMove> table;

    public PVTable() {
        this.table = new HashMap<>();
    }

    public void store(long key, EvalMove move) {
        table.remove(key);
        table.put(key, move);
    }

    public EvalMove probe(long key) {
        return table.get(key);
    }
}




