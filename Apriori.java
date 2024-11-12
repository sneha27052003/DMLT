import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Apriori {

    public static Map<Set<String>, Integer> generateItemsetSupportCount(int k, List<Set<String>> transactions,
            int minSupport) {
        Map<Set<String>, Integer> itemsetCounts = new HashMap<>();
        Set<String> uniqueItems = new HashSet<>();

        for (Set<String> transaction : transactions) {
            uniqueItems.addAll(transaction);
        }

        // Generate k-itemsets and count support
        for (Set<String> itemset : getCombinations(uniqueItems, k)) {
            for (Set<String> transaction : transactions) {
                if (transaction.containsAll(itemset)) {
                    itemsetCounts.put(itemset, itemsetCounts.getOrDefault(itemset, 0) + 1);
                }
            }
        }

        // Remove itemsets that don't meet minimum support
        itemsetCounts.entrySet().removeIf(entry -> entry.getValue() < minSupport);
        return itemsetCounts;
    }

    private static Set<Set<String>> getCombinations(Set<String> set, int k) {
        Set<Set<String>> combinations = new HashSet<>();
        List<String> list = new ArrayList<>(set);
        boolean[] used = new boolean[list.size()];
        generateCombinations(combinations, list, used, k, 0);
        return combinations;
    }

    private static void generateCombinations(Set<Set<String>> combinations, List<String> list, boolean[] used, int k,
            int index) {
        if (k == 0) {
            Set<String> combination = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                if (used[i]) {
                    combination.add(list.get(i));
                }
            }
            combinations.add(combination);
            return;
        }
        for (int i = index; i <= list.size() - k; i++) {
            used[i] = true;
            generateCombinations(combinations, list, used, k - 1, i + 1);
            used[i] = false;
        }
    }

    public static List<Set<String>> readTransactionsFromCSV(String filePath) throws IOException {
        List<Set<String>> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                Set<String> transaction = new HashSet<>(Arrays.asList(items));
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to your CSV file: ");
        String filePath = scanner.nextLine();
        System.out.print("Enter the minimum support threshold: ");
        int minSupport = scanner.nextInt();

        try {
            List<Set<String>> transactions = readTransactionsFromCSV(filePath);
            Set<String> uniqueItems = new HashSet<>();
            for (Set<String> transaction : transactions) {
                uniqueItems.addAll(transaction);
            }
            int maxItemsetSize = uniqueItems.size();

            System.out.println("Itemset Support Counts (min support = " + minSupport + "):");

            for (int k = 1; k <= maxItemsetSize; k++) {
                Map<Set<String>, Integer> itemsetCounts = generateItemsetSupportCount(k, transactions, minSupport);

                if (!itemsetCounts.isEmpty()) {
                    System.out.println("\nItemsets of size " + k + ":");
                    for (Map.Entry<Set<String>, Integer> entry : itemsetCounts.entrySet()) {
                        System.out.println(
                                "{ " + String.join(", ", entry.getKey()) + " }, Support Count: " + entry.getValue());
                    }
                } else {
                    System.out.println("\nNo itemsets of size " + k + " meet the minimum support threshold.");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
