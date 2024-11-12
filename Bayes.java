import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Bayes {

    static Double csvRead(String path, String e1, String e1Value, String e2, String e2Value,
            String e3, String e3Value, String e4, String e4Value,
            String targetValue, String targetColumn) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("CSV is empty");
        }

        String[] headers = line.split(",");
        int e1Index = -1, e2Index = -1, e3Index = -1, e4Index = -1, targetIndex = -1;

        // Find indices for the input features and target column
        for (int i = 0; i < headers.length; i++) {
            if (e1.equals(headers[i]))
                e1Index = i;
            else if (e2.equals(headers[i]))
                e2Index = i;
            else if (e3.equals(headers[i]))
                e3Index = i;
            else if (e4.equals(headers[i]))
                e4Index = i;
            else if (targetColumn.equals(headers[i]))
                targetIndex = i;
        }

        if (e1Index == -1 || e2Index == -1 || e3Index == -1 || e4Index == -1 || targetIndex == -1) {
            throw new IllegalArgumentException("One or more column names are incorrect.");
        }

        int e1Count = 0, e2Count = 0, e3Count = 0, e4Count = 0, targetCount = 0, totalRows = 0;

        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (values[targetIndex].trim().equals(targetValue)) {
                targetCount++;
                if (values[e1Index].trim().equals(e1Value))
                    e1Count++;
                if (values[e2Index].trim().equals(e2Value))
                    e2Count++;
                if (values[e3Index].trim().equals(e3Value))
                    e3Count++;
                if (values[e4Index].trim().equals(e4Value))
                    e4Count++;
            }
            totalRows++;
        }

        br.close();

        // Calculate and display individual probabilities
        Double targetProbability = (double) targetCount / totalRows;
        Double e1Probability = (double) e1Count / targetCount;
        Double e2Probability = (double) e2Count / targetCount;
        Double e3Probability = (double) e3Count / targetCount;
        Double e4Probability = (double) e4Count / targetCount;

        System.out.println("P(" + targetColumn + " = " + targetValue + ") = " + targetProbability);
        System.out.println(
                "P(" + e1 + " = " + e1Value + " | " + targetColumn + " = " + targetValue + ") = " + e1Probability);
        System.out.println(
                "P(" + e2 + " = " + e2Value + " | " + targetColumn + " = " + targetValue + ") = " + e2Probability);
        System.out.println(
                "P(" + e3 + " = " + e3Value + " | " + targetColumn + " = " + targetValue + ") = " + e3Probability);
        System.out.println(
                "P(" + e4 + " = " + e4Value + " | " + targetColumn + " = " + targetValue + ") = " + e4Probability);

        return targetProbability * e1Probability * e2Probability * e3Probability * e4Probability;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file path: ");
        String path = sc.nextLine();
        System.out.print("Enter the target column name for classification: ");
        String targetColumn = sc.nextLine();
        System.out.print("Enter the target value (e.g., 'Yes' or 'No'): ");
        String targetValue = sc.nextLine();

        System.out.print("Enter e1 column name: ");
        String e1 = sc.nextLine();
        System.out.print("Enter e1 column value to match: ");
        String e1Value = sc.nextLine();

        System.out.print("Enter e2 column name: ");
        String e2 = sc.nextLine();
        System.out.print("Enter e2 column value to match: ");
        String e2Value = sc.nextLine();

        System.out.print("Enter e3 column name: ");
        String e3 = sc.nextLine();
        System.out.print("Enter e3 column value to match: ");
        String e3Value = sc.nextLine();

        System.out.print("Enter e4 column name: ");
        String e4 = sc.nextLine();
        System.out.print("Enter e4 column value to match: ");
        String e4Value = sc.nextLine();

        try {
            Double positiveProbability = csvRead(path, e1, e1Value, e2, e2Value, e3, e3Value, e4, e4Value, targetValue,
                    targetColumn);
            Double negativeProbability = csvRead(path, e1, e1Value, e2, e2Value, e3, e3Value, e4, e4Value,
                    targetValue.equals("Yes") ? "No" : "Yes", targetColumn);

            System.out.println("Positive Probability: " + positiveProbability);
            System.out.println("Negative Probability: " + negativeProbability);

            if (positiveProbability > negativeProbability) {
                System.out.println(targetColumn + " = " + targetValue);
            } else {
                System.out.println(targetColumn + " = " + (targetValue.equals("Yes") ? "No" : "Yes"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
