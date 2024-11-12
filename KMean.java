import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Pair {
    double x;
    double y;

    Pair(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

public class KMean {

    public static List<Pair> readCsv(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("NO DATA");
        }
        List<Pair> ls = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            double x = Double.parseDouble(values[0]);
            double y = Double.parseDouble(values[1]);
            Pair p = new Pair(x, y);
            ls.add(p);

        }
        return ls;

    }

    public static double calculate(Pair point, Pair centroid) {
        double x1 = point.x;
        double y1 = point.y;
        double x2 = centroid.x;
        double y2 = centroid.y;
        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        return distance;
    }

    public static Map<Pair, Map<Pair, Double>> distancecal(List<Pair> centroids, List<Pair> ls) {
        Map<Pair, Map<Pair, Double>> mp = new HashMap<>();
        for (int i = 0; i < centroids.size(); i++) {
            mp.put(centroids.get(i), new HashMap<Pair, Double>());
        }
        for (int i = 0; i < ls.size(); i++) {
            double mindistance = Double.MAX_VALUE;
            Pair temp = new Pair(0.0, 0.0);
            for (int j = 0; j < centroids.size(); j++) {
                double dist = calculate(ls.get(i), centroids.get(j));
                if (dist < mindistance) {
                    mindistance = dist;
                    temp.x = centroids.get(j).x;
                    temp.y = centroids.get(j).y;

                }
            }
            for (Map.Entry<Pair, Map<Pair, Double>> en : mp.entrySet()) {
                if (en.getKey().x == temp.x && en.getKey().y == temp.y) {
                    mp.get(en.getKey()).put(ls.get(i), (Math.round(mindistance * 1000.0) / 1000.0));
                    break;
                }
            }
        }
        return mp;
    }

    static void Display(Map<Pair, Map<Pair, Double>> mp) {
        for (Map.Entry<Pair, Map<Pair, Double>> en : mp.entrySet()) {
            System.out.println("Centroid: " + en.getKey().x + " " + en.getKey().y);
            System.out.println("Points under centroid: ");
            int cnt = 1;
            for (Map.Entry<Pair, Double> temp : en.getValue().entrySet()) {

                System.out.println("Point " + cnt + " " + temp.getKey().x + " " + temp.getKey().y + " Distance "
                        + temp.getValue());
                cnt++;
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file path: ");
        String path = sc.nextLine();

        try {
            List<Pair> ls = readCsv(path);
            System.out.println("Enter initial k clusters: ");
            int k = sc.nextInt();
            System.out.println("Enter now points :");
            List<Pair> centroids = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                centroids.add(new Pair(x, y));
                sc.nextLine();
            }
            Map<Pair, Map<Pair, Double>> mp = new HashMap<>();

            mp = distancecal(centroids, ls);
            double EPSILON = 0.001;
            int iteration = 0;
            while (true) {
                System.out.println("Iteration: " + iteration);
                Display(mp);
                List<Pair> newcentroids = new ArrayList<>();
                for (Map.Entry<Pair, Map<Pair, Double>> en : mp.entrySet()) {
                    double xsum = 0.0;
                    double ysum = 0.0;
                    Map<Pair, Double> ncen = en.getValue();
                    int cnt = 0;
                    for (Map.Entry<Pair, Double> navg : ncen.entrySet()) {
                        xsum += navg.getKey().x;
                        ysum += navg.getKey().y;
                        cnt++;
                    }
                    double nxsum=xsum/cnt;
                    double nysum=ysum/cnt;
                    Pair p = new Pair(
                        Math.round(nxsum * 1000.0) / 1000.0,  
                        Math.round(nysum * 1000.0) / 1000.0   
                    );
                    newcentroids.add(p);

                }
                double total = 0.0;
                int ct = 0;
                for (Map.Entry<Pair, Map<Pair, Double>> en : mp.entrySet()) {
                    double distance = calculate(newcentroids.get(ct++), en.getKey());
                    total += distance;
                }
                if (total < EPSILON) {
                    System.out.println("Convergence reached");
                    break;
                }
                mp = distancecal(newcentroids, ls);
                iteration++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
