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

public class DBSCANBYME {

    static void readCSV(String path, List<Pair> ls) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("DATA NOT FOUND");
        }
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            double x = Double.parseDouble(values[0]);
            double y = Double.parseDouble(values[1]);
            Pair p = new Pair(x, y);
            ls.add(p);
        }

    }

    static double DistanceCalculate(Pair p1, Pair p2) {
        double x1 = p1.x;
        double y1 = p1.y;
        double x2 = p2.x;
        double y2 = p2.y;
        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        return distance;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter File Path: ");
        String path = sc.nextLine();
        System.out.println("Enter Minpoints: ");
        int minpoint = sc.nextInt();
        System.out.println("Enter Epsilon distance: ");
        double epsilon = sc.nextDouble();
        List<Pair> ls = new ArrayList<>();
        try {
            readCSV(path, ls);
            int n = ls.size();
            double[][] distance = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = i; j < n; j++) {
                    double dist = DistanceCalculate(ls.get(i), ls.get(j));
                    distance[i][j] = dist;
                }
            }
            Map<Pair, List<Pair>> core = new HashMap<>();
            Map<Pair, List<Pair>> mp = new HashMap<>();
            for (int i = 0; i < n; i++) {
                mp.put(ls.get(i), new ArrayList<>());
                for (int j = 0; j < i; j++) {
                    if (distance[j][i] <= epsilon) {
                        mp.get(ls.get(i)).add(ls.get(j));
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (distance[i][j] <= epsilon) {
                        mp.get(ls.get(i)).add(ls.get(j));
                    }
                }
            }
            List<Pair> noise = new ArrayList<>();
            for (Map.Entry<Pair, List<Pair>> en : mp.entrySet()) {
                if (en.getValue().size() >= minpoint - 1) {
                    core.put(en.getKey(), en.getValue());
                } else {
                    noise.add(en.getKey());
                }
            }
            List<Pair> border = new ArrayList<>();
            for (int i = 0; i < noise.size(); i++) {
                for (Map.Entry<Pair, List<Pair>> en : core.entrySet()) {
                    int flag = 0;
                    for (int j = 0; j < en.getValue().size(); j++) {
                        if (noise.get(i) == en.getValue().get(j)) {
                            border.add(noise.get(i));
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 1) {
                        break;
                    }
                }
            }

            System.out.println("Border Points: ");
            for (int i = 0; i < border.size(); i++) {
                System.out.println(border.get(i).x + " " + border.get(i).y);
            }
            List<Pair> newnoise = new ArrayList<>();
            for (int i = 0; i < noise.size(); i++) {
                int flag = 0;
                for (int j = 0; j < border.size(); j++) {
                    if (noise.get(i) == border.get(j)) {
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0) {
                    newnoise.add(noise.get(i));
                }
            }

            noise.clear();
            System.out.println("NOise Points: ");
            for (int i = 0; i < newnoise.size(); i++) {
                System.out.println(newnoise.get(i).x + " " + newnoise.get(i).y);

            }
            System.out.println("Core Points: ");
            for (Map.Entry<Pair, List<Pair>> en : core.entrySet()) {
                System.out.println(en.getKey().x + " " + en.getKey().y + " :");
                for (int i = 0; i < en.getValue().size(); i++) {
                    System.out.println(en.getValue().get(i).x + " " + en.getValue().get(i).y);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
