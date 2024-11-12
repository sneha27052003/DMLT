import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Asso {

    public static Map<Set<String>, Integer> generateFrequentItemsets(List<Set<String>> transactions, int minSupport) {
        Map<Set<String>, Integer> currentFrequentItemsets = new HashMap<>();
        Set<String> uniqueItems = new HashSet<>();

        for (Set<String> transaction : transactions) {
            uniqueItems.addAll(transaction);
        }

        Set<Set<String>> candidateItemsets = new HashSet<>();
        for (String item : uniqueItems) {
            Set<String> singleItemset = new HashSet<>(Collections.singleton(item));
            candidateItemsets.add(singleItemset);
        }

        int k = 1;

        while (!candidateItemsets.isEmpty()) {
            Map<Set<String>, Integer> itemsetCounts = countSupport(candidateItemsets, transactions);
            Map<Set<String>, Integer> frequentItemsets = pruneItemsets(itemsetCounts, minSupport);
            currentFrequentItemsets.putAll(frequentItemsets);
            candidateItemsets = generateNextLevelItemsets(frequentItemsets.keySet(), ++k);
        }

        return currentFrequentItemsets;
    }

    private static Map<Set<String>, Integer> countSupport(Set<Set<String>> candidateItemsets,
            List<Set<String>> transactions) {
        Map<Set<String>, Integer> supportCounts = new HashMap<>();

        for (Set<String> candidate : candidateItemsets) {
            for (Set<String> transaction : transactions) {
                if (transaction.containsAll(candidate)) {
                    supportCounts.put(candidate, supportCounts.getOrDefault(candidate, 0) + 1);
                }
            }
        }

        return supportCounts;
    }

    private static Map<Set<String>, Integer> pruneItemsets(Map<Set<String>, Integer> itemsetCounts, int minSupport) {
        Map<Set<String>, Integer> frequentItemsets = new HashMap<>();
        for (Map.Entry<Set<String>, Integer> entry : itemsetCounts.entrySet()) {
            if (entry.getValue() >= minSupport) {
                frequentItemsets.put(entry.getKey(), entry.getValue());
            }
        }
        return frequentItemsets;
    }

    private static Set<Set<String>> generateNextLevelItemsets(Set<Set<String>> currentFrequentItemsets, int k) {
        Set<Set<String>> nextLevelItemsets = new HashSet<>();
        List<Set<String>> itemsetsList = new ArrayList<>(currentFrequentItemsets);

        for (int i = 0; i < itemsetsList.size(); i++) {
            for (int j = i + 1; j < itemsetsList.size(); j++) {
                Set<String> unionSet = new HashSet<>(itemsetsList.get(i));
                unionSet.addAll(itemsetsList.get(j));

                if (unionSet.size() == k) {
                    if (hasFrequentSubsets(unionSet, currentFrequentItemsets, k)) {
                        nextLevelItemsets.add(unionSet);
                    }
                }
            }
        }

        return nextLevelItemsets;
    }

    private static boolean hasFrequentSubsets(Set<String> candidate, Set<Set<String>> currentFrequentItemsets, int k) {
        List<String> list = new ArrayList<>(candidate);
        for (int i = 0; i < list.size(); i++) {
            Set<String> subset = new HashSet<>(candidate);
            subset.remove(list.get(i));
            if (!currentFrequentItemsets.contains(subset)) {
                return false;
            }
        }
        return true;
    }

    public static List<Set<String>> readTransactionsFromCSV(String filePath) throws IOException {
        List<Set<String>> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                Set<String> transaction = new HashSet<>();
                for (String item : items) {
                    transaction.add(item.trim());
                }
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public static List<String> generateAssociationRules(Map<Set<String>, Integer> frequentItemsets,
            Map<Set<String>, Integer> supportMap, int minConfidence, int totalTransactions) {
        List<String> rules = new ArrayList<>();

        for (Set<String> itemset : frequentItemsets.keySet()) {
            if (itemset.size() < 2)
                continue;

            Set<Set<String>> subsets = getAllNonEmptySubsets(itemset);

            for (Set<String> antecedent : subsets) {
                Set<String> consequent = new HashSet<>(itemset);
                consequent.removeAll(antecedent);

                if (consequent.isEmpty())
                    continue;

                Integer supportItemset = frequentItemsets.get(itemset);
                Integer supportAntecedent = supportMap.get(antecedent);

                if (supportAntecedent == null)
                    continue;

                double confidence = ((double) supportItemset / supportAntecedent) * 100;

                if (confidence >= minConfidence) {
                    String rule = String.join(", ", antecedent) + " ⇒ " + String.join(", ", consequent)
                            + " (Confidence: " + String.format("%.2f", confidence) + "%)";
                    rules.add(rule);
                }
            }
        }

        return rules;
    }

    private static Set<Set<String>> getAllNonEmptySubsets(Set<String> set) {
        Set<Set<String>> allSubsets = new HashSet<>();
        List<String> list = new ArrayList<>(set);
        int n = list.size();
        for (int i = 1; i < (1 << n); i++) {
            Set<String> subset = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    subset.add(list.get(j));
                }
            }
            allSubsets.add(subset);
        }
        return allSubsets;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to your CSV file: ");
        String filePath = scanner.nextLine();
        System.out.print("Enter the minimum support threshold: ");
        int minSupport = scanner.nextInt();
        System.out.print("Enter the minimum confidence threshold (in %): ");
        int minConfidence = scanner.nextInt();

        try {
            List<Set<String>> transactions = readTransactionsFromCSV(filePath);
            int totalTransactions = transactions.size();
            Map<Set<String>, Integer> frequentItemsets = generateFrequentItemsets(transactions, minSupport);

            System.out.println("\nFrequent Itemsets (min support = " + minSupport + "):");
            for (Map.Entry<Set<String>, Integer> entry : frequentItemsets.entrySet()) {
                System.out.println("{ " + String.join(", ", entry.getKey()) + " }, Support Count: " + entry.getValue());
            }

            List<String> associationRules = generateAssociationRules(frequentItemsets, frequentItemsets, minConfidence,
                    totalTransactions);

            System.out.println("\nAssociation Rules (min confidence = " + minConfidence + "%):");
            if (associationRules.isEmpty()) {
                System.out.println("No association rules meet the minimum confidence threshold.");
            } else {
                for (String rule : associationRules) {
                    System.out.println(rule);
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
