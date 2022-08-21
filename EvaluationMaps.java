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








    public double openingEvaluation(int pos, int piece) {
        int xPos = pos/10 - 1;
        int yPos = pos%10 - 1;

        if (piece > 0) {
            return openingWeight * openingTable[piece - 1][yPos][xPos];
        } else {
            return -openingWeight * openingTable[-piece - 1][7 - yPos][xPos];
        }
    }
}
