public class EvaluationMaps {


    public static double developmentWeight = 0.001;
    public static double bishopOpeningWeight = 0.025;
    public static double knightOpeningWeight = 0.05;
    public static double majorPieceOpeningWeight = -0.1;
    public static int[][] oldDevelopmentMap = {
            {0, 1, 2, 4, 2, 5, 3, 0},
            {0, 2, 3, 6, 6, 2, 2, 0},
            {2, 4, 8, 6, 7, 9, 4, 2},
            {3, 6, 8, 10, 9, 6, 6, 2},
            {4, 6, 8, 10, 9, 6, 4, 3},
            {2, 4, 8, 6, 7, 9, 4, 2},
            {0, 1, 2, 5, 4, 2, 2, 0},
            {0, 1, 2, 3, 2, 4, 3, 0}
    };

    public static int[][] developmentMap = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 2, 2, 2, 2, 1, 0},
            {0, 1, 2, 3, 3, 2, 1, 0},
            {0, 1, 2, 3, 3, 2, 1, 0},
            {0, 1, 2, 2, 2, 2, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
    public static double[] openingWeights = {0.01, 0.01, 0.01, -0.001, -0.001, -0.001};

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


    public double positionEvaluation(int pos, int piece) {
        int xPos = pos/10 - 1;
        int yPos = pos%10 - 1;
        return developmentMap[yPos][xPos] * openingWeights[Math.abs(piece) - 1];
    }

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








//    public double openingEvaluation() {
//        double evaluation = 0;
//        for (Integer position : pieces.keySet()) {
//            int piece = pieces.get(position) % 10;
//            int color = pieceColor(position);
//            if (piece == 1 && position / 10 > 2 && position / 10 < 6) {
//                evaluation += (7 - position % 10) * pawnCenterWeight;
//            } else if (piece == -1 && position / 10 > 2 && position / 10 < 6) {
//                evaluation -= (position % 10 - 2) * pawnCenterWeight;
//            } else {
//                evaluation += evalMap.openingEvaluation(position, piece);
//            }
//        }
//        evaluation += mobilityWeight * (allLegalMoves(1).size() - allLegalMoves(-1).size());
//        evaluation += threatWeight * (allCaptureMoves(1).size() - allCaptureMoves(-1).size());
//        return evaluation;
//    }
//
//    public double midgameEvaluation() {
//        return 0;
//    }
//
//    public double endgameEvaluation() {
//        return 0;
//    }












}
