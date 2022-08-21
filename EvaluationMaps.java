public class EvaluationMaps {
    private double openingWeight = 0.01;
    private double[][][] openingTable = {
            {
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 0, 0, 0},
                    {1, 1, 4, 5, 5, 0, 0, 0},
                    {1, 1, 3, 3, 3, 0, 0, 1},
                    {0, 0, -1, -2, -2, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
            },

            {
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 5, 0, 1, 1, 0, 5, 0},
                    {0, 0, 5, 1, 1, 5, 0, 0},
                    {0, 1, 0, 0, 0, 0, 1, 0},
                    {0, 3, 0, 2, 2, 0, 3, 0},
                    {0, 0, -1, 0, 0, 0, -1, 0}
            },

            {
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 1, 1, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 3, 0, 0, 3, 0, 0},
                    {0, 0, 0, 2, 2, 0, 0, 0},
                    {0, -1, 0, 0, 0, 0, -1, 0}
            },

            {
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {1, -1, 0, 1, 1, 1, -1, 1},
            },

            {
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 0, 0, 0, 0},
                    {0, 0, 0, 6, 0, 0, 0, 0},
            },

            {
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 2, 0, 2, 0, 2, 0},
            }
    };

    private int[][] centerManhattanDistance = {
            {6, 5, 4, 3, 3, 4, 5, 6},
            {5, 4, 3, 2, 2, 3, 4, 5},
            {4, 3, 2, 1, 1, 2, 3, 4},
            {3, 2, 1, 0, 0, 1, 2, 3},
            {3, 2, 1, 0, 0, 1, 2, 3},
            {4, 3, 2, 1, 1, 2, 3, 4},
            {5, 4, 3, 2, 2, 3, 4, 5},
            {6, 5, 4, 3, 3, 4, 5, 6}
    };

    public double openingEvaluation(int position, int piece) {
        int xPos = position/10 - 1;
        int yPos = position%10 - 1;

        if (piece > 0) {
            return openingWeight * openingTable[piece - 1][yPos][xPos];
        } else {
            return -openingWeight * openingTable[-piece - 1][7 - yPos][xPos];
        }
    }
    public int findCMD(int position) {
        int xPos = position/10 - 1;
        int yPos = position%10 - 1;
        return centerManhattanDistance[yPos][xPos];
    }
    public int findMD(int whiteKingPos, int blackKingPos) {
        int xWhitePos = whiteKingPos / 10;
        int yWhitePos = whiteKingPos % 10;
        int xBlackPos = blackKingPos / 10;
        int yBlackPos = blackKingPos % 10;
        return Math.abs(xWhitePos - xBlackPos) + Math.abs(yWhitePos - yBlackPos);
    }

}
