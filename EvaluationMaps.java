public class EvaluationMaps {


    public static double developmentWeight = 0.001;
    public static double bishopOpeningWeight = 0.1;
    public static double knightOpeningWeight = 0.2;
    public static double majorPieceOpeningWeight = -0.05;
    public static int[][] developmentMap = {
            {0, 1, 2, 4, 2, 5, 3, 0},
            {0, 2, 3, 6, 6, 2, 2, 0},
            {2, 4, 8, 6, 7, 9, 4, 2},
            {3, 6, 8, 10, 9, 6, 6, 2},
            {4, 6, 8, 10, 9, 6, 4, 3},
            {2, 4, 8, 6, 7, 9, 4, 2},
            {0, 1, 2, 5, 4, 2, 2, 0},
            {0, 1, 2, 3, 2, 4, 3, 0}
    };

    public static double[] openingWeights = {0.002, 0.001, 0.002, 0.001, 0.001, 0.002};
    public static int[][] bishopOpeningMap = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, -3, 0, -2, -2, 0, -3, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 5, -5, 0, 0, -5, 5, 0},
            {0, -5, 5, 0, 0, 5, -5, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 3, 0, 2, 2, 0, 3, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    public static int[][] knightOpeningMap = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, -2, -2, 0, 0, 0},
            {0, 0, -3, 0, 0, -3, 0, 0},
            {0, 0, 0, 0, 1, 0, 1, 0},
            {0, 0, 0, 0, -1, 0, -1, 0},
            {0, 0, 3, 0, 0, 3, 0, 0},
            {0, 0, 0, 2, 2, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };



    public double openingEvaluation(int pos, int piece) {
        int xPos = pos/10 - 1;
        int yPos = pos%10 - 1;

        if (Math.abs(piece) == 2) {
            return bishopOpeningWeight * bishopOpeningMap[yPos][xPos];
        } else if (Math.abs(piece) == 3) {
            return knightOpeningWeight * knightOpeningMap[yPos][xPos];
        } else if (piece > 0 && yPos + 1 != 8) {
            return majorPieceOpeningWeight * piece;
        } else if (piece < 0 && yPos + 1 != 1) {
            return majorPieceOpeningWeight * piece;
        } else {
            return 0;
        }
    }





    public static int[][] knightMap = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 3, 0, 0, 3, 0, 0},
            {0, 0, 0, 0, 1, 0, 1, 0},
            {0, 0, 0, 0, 1, 0, 1, 0},
            {0, 0, 3, 0, 0, 3, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    public static int[][] pawnMap = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 2, 2, 0, 0, 0},
            {0, 0, 0, 2, 2, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

}
