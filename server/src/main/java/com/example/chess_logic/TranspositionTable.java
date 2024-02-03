package com.example.chess_logic;

import java.util.HashMap;

public class TranspositionTable {
    public static final int EXACT = 0;
    public static final int ALPHA = 1;
    public static final int BETA = 2;

    public HashMap<Long, TableEntry> table;

    public TranspositionTable() {
        this.table = new HashMap<>();
    }

    public void store(long key, int depth, int flag, EvalMove move) {
        if (depth >= 3) {
            table.put(key, new TableEntry(depth, flag, move));
        }
    }

    public TableEntry probe(long key) {
        return table.get(key);
    }

    public static class TableEntry {
        public int depth;
        public int flag;
        public EvalMove move;

        public TableEntry(int depth, int flag, EvalMove move) {
            this.depth = depth;
            this.flag = flag;
            this.move = move;
        }

    }
}


