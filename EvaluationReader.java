import java.io.File;
import java.util.HashMap;
import java.util.Scanner;


import java.io.PrintWriter;




public class EvaluationReader {
    private HashMap<Integer, HashMap<String, Integer>> evaluations = new HashMap<>();

    EvaluationReader(String fileName) {
        try {
            File file = new File(fileName);
            Scanner input = new Scanner(file);
            input.nextLine();
            while (input.hasNext()) {
                String[] message = input.nextLine().split(",", -1);
                String fen = message[0];
                fen = fen.substring(0, fen.indexOf(" ") + 2);
                int evaluation;
                if (message[1].contains("#")) {
                    evaluation = evaluationToInt(message[1].substring(1)) * 1000;
                } else {
                    evaluation = evaluationToInt(message[1]);
                }
                putInHashMap(fen, evaluation);
            }
            input.close();
            for (Integer num : evaluations.keySet()) {
                System.out.println("Size: " + num);
                System.out.println(evaluations.get(num).size());
            }


            File write = new File("size32.txt");
            PrintWriter output = new PrintWriter(write);
            HashMap<String, Integer> map = evaluations.get(32);
            for (String fen : map.keySet()) {
                output.println(fen);
            }
            output.close();



        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void putInHashMap(String fen, int evaluation) {
        int numPieces = numberOfPieces(fen.substring(0, fen.length() - 1));
        if (evaluations.containsKey(numPieces)) {
            evaluations.get(numPieces).put(fen, evaluation);
        } else {
            evaluations.put(numPieces, new HashMap<String, Integer>());
            evaluations.get(numPieces).put(fen, evaluation);
        }
    }

    public static int numberOfPieces(String fen) {
        int counter = 0;
        String pieceNames = "kqrbnpKQRBNP";
        for (int i = 0; i < fen.length(); i++) {
            if (pieceNames.contains(fen.substring(i, i + 1))) {
                counter++;
            }
        }

        return counter;
    }

    public int evaluationToInt(String message) {
        if (message.contains("-")) {
            return Integer.parseInt(message.substring(message.indexOf("-") + 1));
        } else if (message.contains("+")) {
            return Integer.parseInt(message.substring(message.indexOf("+") + 1));
        }
        return 0;
    }

    public HashMap<Integer, HashMap<String, Integer>> getEvaluations() {
        return this.evaluations;
    }

    public static void main(String[]args) {
        EvaluationReader evaluationReader = new EvaluationReader("chessData.csv");

    }

}