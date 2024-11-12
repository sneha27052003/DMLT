import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Pair {
    int yes;
    int no;

    Pair(int y, int n) {
        yes = y;
        no = n;
    }
}

public class GiniByMe {

    static int wholeyes = 0;
    static int wholeno = 0;

    static public Map<String, Map<String, Pair>> readcsv(String path, String target) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("NO data found");
        }
        int pos = 0;
        int neg = 0;
        String[] head = line.split(",");
        Map<String, Map<String, Pair>> mp = new HashMap<>();
        int index = 0;
        for (int i = 0; i < head.length; i++) {

            if (target.equals(head[i])) {
                index = i;
            } else {
                mp.put(head[i], new HashMap<String, Pair>());
            }
        }

        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (values[index].equals("Yes")) {
                wholeyes++;
            } else {
                wholeno++;
            }
            for (int i = 0; i < values.length; i++) {
                if (i == index) {
                    continue;
                }
                if (mp.get(head[i]).containsKey(values[i]) == true) {
                    if (values[index].equals("Yes")) {
                        int y = mp.get(head[i]).get(values[i]).yes;
                        int n = mp.get(head[i]).get(values[i]).no;
                        mp.get(head[i]).put(values[i], new Pair(y + 1, n));

                    } else {
                        int y = mp.get(head[i]).get(values[i]).yes;
                        int n = mp.get(head[i]).get(values[i]).no;
                        mp.get(head[i]).put(values[i], new Pair(y, n + 1));

                    }
                } else {
                    if (values[index].equals("Yes")) {

                        mp.get(head[i]).put(values[i], new Pair(1, 0));

                    } else {

                        mp.get(head[i]).put(values[i], new Pair(0, 1));

                    }
                }
            }
        }
        return mp;
    }

    static double Entropycalculation(String prime, String cla, Map<String, Map<String, Pair>> mp) {
        Map<String, Pair> ls = mp.get(prime);
        int n = ls.get(cla).no + ls.get(cla).yes;
        double yesprob = (double) ls.get(cla).yes / n;
        double noprob = (double) ls.get(cla).no / n;
        double entropy = 0.0;
        if (yesprob > 0) {
            entropy -= yesprob * (Math.log(yesprob) / Math.log(2));
        }
        if (noprob > 0) {
            entropy -= noprob * (Math.log(noprob) / Math.log(2));
        }
        System.out.printf(cla + " yes probability: %.3f%n", yesprob);
        System.out.printf(cla + " no probability: %.3f%n", noprob);
        if (Double.isNaN(entropy)) {
            System.out.printf(cla + " entropy: %.3f%n", 0);
        } else {
            System.out.printf(cla + " entropy: %.3f%n", entropy);
        }
        return entropy;
    }

    static void GainCalculation(String cla, Map<String, Map<String, Double>> entro, Map<String, Map<String, Pair>> mp,
            double target, Map<String, Double> infogain) {
        int n = wholeno + wholeyes;

        Map<String, Double> sib = entro.get(cla);
        Map<String, Pair> pt = mp.get(cla);
        double afterentro = 0.0;
        for (Map.Entry<String, Pair> en : pt.entrySet()) {
            double temp = (double) (en.getValue().yes + en.getValue().no) / n;
            afterentro += (temp * sib.get(en.getKey()));

        }
        infogain.put(cla, (target - afterentro));
        System.out.println("INFO GAIN OF " + cla + " is: " + infogain.get(cla));

    }

    static double GiniIndexCalculation(String target, String subclass, Map<String, Map<String, Pair>> mp) {
        Map<String, Pair> temp = mp.get(target);
        int n = temp.get(subclass).yes + temp.get(subclass).no;
        int yes = temp.get(subclass).yes;
        int no = temp.get(subclass).no;
        double sum = Math.pow((double) yes / n, 2) + Math.pow((double) no / n, 2);
        double ans = 1 - sum;
        System.out.printf("Gini Index of %s is: %.3f%n", subclass, ans);
        return ans;

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file path: ");
        String path = sc.nextLine();
        System.out.println("Enter name of the target class: ");
        String target = sc.nextLine();

        Map<String, Map<String, Pair>> mp = new HashMap<>();
        try {
            mp = readcsv(path, target);
            int n = wholeyes + wholeno;
            System.out.println("yes: " + wholeyes);
            System.out.println("no: " + wholeno);
            double yesprob = (double) wholeyes / n;

            double noprob = (double) wholeno / n;

            double entropy = -(yesprob * (Math.log(yesprob) / Math.log(2)))
                    - (noprob * (Math.log(noprob) / Math.log(2)));
            System.out.printf("Probability of yes: %.3f%n", yesprob);
            System.out.printf("Probability of no: %.3f%n", noprob);
            System.out.printf("Entropy of the entire dataset: %.3f%n", entropy);

            System.out.println("Info gain of all columns......");
            Map<String, Map<String, Double>> entro = new HashMap<>();
            Map<String, Double> infogain = new HashMap<>();
            Map<String, Map<String, Double>> Gini = new HashMap<>();
            for (Map.Entry<String, Map<String, Pair>> en : mp.entrySet()) {
                Map<String, Pair> t = en.getValue();
                if (!entro.containsKey(en.getKey())) {
                    entro.put(en.getKey(), new HashMap<>());
                }
                for (Map.Entry<String, Pair> ent : t.entrySet()) {

                    entro.get(en.getKey()).put(ent.getKey(), 0.0);
                    double ans = Entropycalculation(en.getKey(), ent.getKey(), mp);

                    entro.get(en.getKey()).put(ent.getKey(), ans);
                }

                GainCalculation(en.getKey(), entro, mp, entropy, infogain);

            }

            int total = wholeno + wholeyes;

            double sum = Math.pow((double) wholeyes / total, 2) + Math.pow((double) wholeno / total, 2);

            System.out.println(sum);
            System.out.printf("Gini index of Target class: %.3f%n", 1 - sum);

            Map<String, Double> headGini = new HashMap<>();
            for (Map.Entry<String, Map<String, Pair>> en : mp.entrySet()) {
                if (!Gini.containsKey(en.getKey())) {
                    Gini.put(en.getKey(), new HashMap<>());
                }
                Map<String, Pair> t = en.getValue();
                double result = 0.0;
                for (Map.Entry<String, Pair> ent : t.entrySet()) {
                    Gini.get(en.getKey()).put(ent.getKey(), 0.0);
                    double ans = GiniIndexCalculation(en.getKey(), ent.getKey(), mp);
                    int m = ent.getValue().no + ent.getValue().yes;
                    int wn = wholeno + wholeyes;
                    result += ((double) m / wn) * ans;
                    Gini.get(en.getKey()).put(ent.getKey(), ans);
                }

                System.out.printf("Gini Index Of %s is: %.3f%n", en.getKey(), result);

                headGini.put(en.getKey(), result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
