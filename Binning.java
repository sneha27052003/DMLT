import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

class Binning {

    static ArrayList<Double> csvread(String path, String column) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("CSV is empty");
        }
        String[] headers = line.split(",");
        int index = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equals(column)) {
                index = i;
                break;
            }
        }
        ArrayList<Double> data = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            for (int i = 0; i < values.length; i++) {
                if (i == index) {
                    data.add(Double.parseDouble(values[i]));
                }
            }
        }
        br.close();
        return data;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String path = sc.nextLine();
        System.out.print("Enter column name: ");
        String column = sc.nextLine();
        try {
            ArrayList<Double> ans = csvread(path, column);
            Collections.sort(ans);
            System.out.print("Enter number of beans: ");
            int bean = sc.nextInt();
            Double mini = Collections.min(ans);
            Double maxi = Collections.max(ans);
            Double width = Math.ceil((maxi - mini) / bean);

            Map<Double, List<Double>> mp = new TreeMap<>();
            Double st = mini;
            for (int i = 0; i < bean; i++) {
                st = st + width;
                mp.put(st, new ArrayList<>());
            }
            System.out.println(ans);
            for (int i = 0; i < ans.size(); i++) {
                Double value = ans.get(i);
                for (Map.Entry<Double, List<Double>> en : mp.entrySet()) {
                    if (value <= en.getKey()) {
                        en.getValue().add(value);
                        break;
                    }
                }

            }
            System.out.println(mp);
            System.out.println("..................Smoothing by Bean means...................");
            ArrayList<ArrayList<Double>> smean = new ArrayList<>();
            for (Map.Entry<Double, List<Double>> en : mp.entrySet()) {
                Double sum = 0.0;
                for (Double val : en.getValue()) {
                    sum += val;
                }
                Double mean = sum / (double) en.getValue().size();
                int len = en.getValue().size();
                ArrayList<Double> temp = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    temp.add(mean);
                }
                smean.add(temp);

            }

            System.out.println(smean);

            System.out.println("..................Smoothing by Bean medians...................");
            ArrayList<ArrayList<Double>> smedian = new ArrayList<>();
            for (Map.Entry<Double, List<Double>> en : mp.entrySet()) {
                List<Double> temp = en.getValue();

                Double median = 0.0;
                int n = temp.size();
                if (temp.size() % 2 != 0) {
                    median = temp.get(n / 2);
                } else {
                    median = (temp.get(n / 2) + temp.get((n / 2) - 1)) / 2;
                }

                ArrayList<Double> lt = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    lt.add(median);
                }
                smedian.add(lt);

            }
            System.out.println(smedian);
            System.out.println("..................Smoothing by Bean Boundaries...................");
            ArrayList<List<Double>> sboundary = new ArrayList<>();
            for (Map.Entry<Double, List<Double>> en : mp.entrySet()) {
                List<Double> temp = en.getValue();
                Double minitemp = Collections.min(temp);
                Double maxitemp = Collections.max(temp);
                List<Double> temp2 = new ArrayList<>();
                for (int i = 0; i < temp.size(); i++) {
                    Double a = Math.abs(temp.get(i) - minitemp);
                    Double b = Math.abs(temp.get(i) - maxitemp);
                    if (a < b) {
                        temp2.add(minitemp);
                    } else {
                        temp2.add(maxitemp);
                    }
                }
                sboundary.add(temp2);

            }
            System.out.println(sboundary);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
