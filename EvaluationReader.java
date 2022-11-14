/**
 * Reads and translates the opening database from a csv
 * Stores fen strings and their respective evaluations into a hashmap
 * @author David Ye
 */

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class EvaluationReader {
    private HashMap<String, Integer> evaluations = new HashMap<>();

    EvaluationReader(String fileName) {
        try {
            File file = new File(fileName);
            Scanner input = new Scanner(file);
            input.nextLine();
            // Reads through each line and splits apart the fen string and evaluations
            while (input.hasNext()) {
                String rawMessage = input.nextLine();
                String[] message = rawMessage.split(",", -1);
                String fen = message[0];
                fen = fen.substring(0, fen.indexOf(" ") + 2);

                int evaluation;
                if (message[1].contains("#")) {
                    evaluation = evaluationToInt(message[1].substring(1)) * 1000;
                } else {
                    evaluation = evaluationToInt(message[1]);
                }
                evaluations.put(fen, evaluation);
            }
            input.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public int evaluationToInt(String message) {
        if (message.contains("-")) {
            return -Integer.parseInt(message.substring(message.indexOf("-") + 1));
        } else if (message.contains("+")) {
            return Integer.parseInt(message.substring(message.indexOf("+") + 1));
        }
        return 0;
    }

    public HashMap<String, Integer> getEvaluations() {
        return this.evaluations;
    }

}
