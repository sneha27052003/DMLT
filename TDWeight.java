import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TDWeight {

    static Map<String, List<Integer>> csvread(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("CSV is empty");
        }

        // Read headers dynamically
        String[] headers = line.split(",");
        Map<String, List<Integer>> dataMap = new HashMap<>();

        // Populate data map with each row entry
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            String region = values[0];
            List<Integer> data = new ArrayList<>();
            for (int i = 1; i < values.length; i++) {
                data.add(Integer.parseInt(values[i]));
            }
            dataMap.put(region, data);
        }
        br.close();
        return dataMap;
    }

    static double calculate(int actualValue, int total) {
        double temp = (double) total / (double) actualValue;
        return 100.0 / temp;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file path: ");
        String path = sc.nextLine();

        try {
            Map<String, List<Integer>> dataMap = csvread(path);

            // Horizontal Totals: Sum of each row by region
            Map<String, Integer> htotal = new HashMap<>();
            for (Map.Entry<String, List<Integer>> entry : dataMap.entrySet()) {
                int sum = entry.getValue().stream().mapToInt(Integer::intValue).sum();
                htotal.put(entry.getKey(), sum);
            }

            // Vertical Totals: Sum of each column across regions
            int numColumns = dataMap.values().iterator().next().size();
            List<Integer> vtotal = new ArrayList<>(Collections.nCopies(numColumns, 0));
            for (List<Integer> values : dataMap.values()) {
                for (int i = 0; i < values.size(); i++) {
                    vtotal.set(i, vtotal.get(i) + values.get(i));
                }
            }

            // Calculate and display t-wt and d-wt for each region and category
            System.out.println("Region-wise t-wt and d-wt by category:");
            for (String region : dataMap.keySet()) {
                List<Integer> values = dataMap.get(region);
                System.out.println(region + " t-wt and d-wt:");
                for (int i = 0; i < values.size(); i++) {
                    double tWeight = calculate(values.get(i), htotal.get(region));
                    double dWeight = calculate(values.get(i), vtotal.get(i));
                    System.out.printf("  Category %d - t-wt: %.2f%%, d-wt: %.2f%%%n", i + 1, tWeight, dWeight);
                }
            }

            // Calculate t-wt and d-wt for combined regions by category
            System.out.println("\nCombined t-wt and d-wt for all regions:");
            int totalHorizontal = htotal.values().stream().mapToInt(Integer::intValue).sum();
            int totalVertical = vtotal.stream().mapToInt(Integer::intValue).sum();
            for (int i = 0; i < numColumns; i++) {
                double combinedTWeight = calculate(vtotal.get(i), totalVertical);
                double combinedDWeight = calculate(htotal.values().stream().mapToInt(Integer::intValue).sum(),
                        totalHorizontal);
                System.out.printf("  Category %d - Combined t-wt: %.2f%%, Combined d-wt: %.2f%%%n", i + 1,
                        combinedTWeight, combinedDWeight);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
