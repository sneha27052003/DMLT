import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Practice {

    public static ArrayList<Integer> readfromcsv(String path, String column) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("CSV file is empty.");
        }
        String[] headers = line.split(",");
        int index = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(column)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalArgumentException("Column '" + column + "' does not exist in the CSV file.");
        }
        ArrayList<Integer> ans = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            for (int i = 0; i < values.length; i++) {
                if (i == index) {
                    ans.add(Integer.parseInt(values[i]));
                }
            }
        }
        br.close();
        return ans;

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file path: ");
        String path = sc.nextLine();
        System.out.print("Enter column name: ");
        String column = sc.nextLine();
        ArrayList<Integer> ans = new ArrayList<>();
        try {
            ans = readfromcsv(path, column);
        } catch (Exception e) {
            e.printStackTrace();

        }
        int q1 = 0;
        int median = 0;
        int q3 = 0;
        int mini = 0;
        int maxi = 0;
        int n = ans.size();
        mini = Collections.min(ans);
        maxi = Collections.max(ans);
        Collections.sort(ans);

        if (n % 2 == 0) {
            median = (ans.get((n / 2) - 1) + ans.get(n / 2)) / 2;
            ArrayList<Integer> sub1 = new ArrayList<>();
            for (int i = 0; i < n / 2; i++) {
                sub1.add(ans.get(i));
            }
            ArrayList<Integer> sub2 = new ArrayList<>();
            for (int i = n / 2; i < n; i++) {
                sub2.add(ans.get(i));
            }

            if (sub1.size() % 2 == 0) {
                int n1 = sub1.size();
                q1 = (sub1.get((n1 / 2) - 1) + sub1.get(n1 / 2)) / 2;
            } else {
                int n1 = sub1.size();
                q1 = sub1.get(n1 / 2);
            }
            if (sub2.size() % 2 == 0) {
                int n1 = sub2.size();
                q3 = (sub2.get((n1 / 2) - 1) + sub2.get(n1 / 2)) / 2;
            } else {
                int n1 = sub2.size();
                q3 = sub2.get(n1 / 2);
            }

        } else {
            median = ans.get(n / 2);
            ArrayList<Integer> sub1 = new ArrayList<>();
            for (int i = 0; i < n / 2; i++) {
                sub1.add(ans.get(i));
            }
            ArrayList<Integer> sub2 = new ArrayList<>();
            for (int i = (n / 2) + 1; i < n; i++) {
                sub2.add(ans.get(i));
            }

            if (sub1.size() % 2 == 0) {
                int n1 = sub1.size();
                q1 = (sub1.get((n1 / 2) - 1) + sub1.get(n1 / 2)) / 2;
            } else {
                int n1 = sub1.size();
                q1 = sub1.get(n1 / 2);
            }
            if (sub2.size() % 2 == 0) {
                int n1 = sub2.size();
                q3 = (sub2.get((n1 / 2) - 1) + sub2.get(n1 / 2)) / 2;
            } else {
                int n1 = sub2.size();
                q3 = sub2.get(n1 / 2);
            }

        }
        int IQR = q3 - q1;
        double lowerwhisker = q1 - 1.5 * IQR;
        double upperwhisker = q3 + 1.5 * IQR;
        System.out.println("Minimum element:" + mini);
        System.out.println("Maximum element: " + maxi);
        System.out.println("q1: " + q1);
        System.out.println("q3: " + q3);
        System.out.println("Median: " + median);
        System.out.println("IQR: " + IQR);
        System.out.println("lower whisker: " + lowerwhisker);
        System.out.println("Upper whisker: " + upperwhisker);
        ArrayList<Integer> outlier = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (ans.get(i) < lowerwhisker && ans.get(i) > upperwhisker) {
                outlier.add(ans.get(i));
            }
        }
        System.out.println("Outliers: " + outlier);

        sc.close();
    }
}