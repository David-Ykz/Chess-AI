/**
 * Stores all piece square tables
 * Uses array of boards to encourage pieces to move to good positions
 * @author David Ye
 */

public class EvaluationMaps {
    private double openingWeight = 0.01;
    // Piece square tables for all the pieces
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

    // Distance from the center
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

    // Returns the "strength" of a piece's position based on the piece square tables
    public double openingEvaluation(int position, int piece) {
        int xPos = position/10 - 1;
        int yPos = position%10 - 1;

        if (piece > 0) {
            return openingWeight * openingTable[piece - 1][yPos][xPos];
        } else {
            return -openingWeight * openingTable[-piece - 1][7 - yPos][xPos];
        }
    }

    // Finds how far a piece is from the center of the board
    public int findCMD(int position) {
        int xPos = position/10 - 1;
        int yPos = position%10 - 1;
        return centerManhattanDistance[yPos][xPos];
    }

    // Returns the distance between the 2 kings
    public int findMD(int whiteKingPos, int blackKingPos) {
        int xWhitePos = whiteKingPos / 10;
        int yWhitePos = whiteKingPos % 10;
        int xBlackPos = blackKingPos / 10;
        int yBlackPos = blackKingPos % 10;
        return Math.abs(xWhitePos - xBlackPos) + Math.abs(yWhitePos - yBlackPos);
    }
}
