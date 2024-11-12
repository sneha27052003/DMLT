
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Pair {
    double x;
    double y;

    Pair(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class singlelink {
    static void readcsv(String path, List<Pair> points) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("Data not found");
        }
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            double x = Double.parseDouble(values[0]);
            double y = Double.parseDouble(values[1]);
            Pair p = new Pair(x, y);
            points.add(p);
        }

    }

    static double calculatedist(Pair p1, Pair p2) {
        double x1 = p1.x;
        double y1 = p1.y;
        double x2 = p2.x;
        double y2 = p2.y;
        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        return distance;

    }

    static int[] calculateminimum(List<List<Double>> distance) {
        double mini = Double.MAX_VALUE;
        int[] index = new int[2];
        for (int i = 0; i < distance.size(); i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j) {
                    continue;
                }
                if (distance.get(i).get(j) < mini) {
                    mini = distance.get(i).get(j);
                    index[0] = i;
                    index[1] = j;
                }
            }
        }
        return index;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file path: ");
        String path = sc.nextLine();
        List<Pair> points = new ArrayList<>();
        readcsv(path, points);
        List<List<Double>> distance = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            List<Double> temp = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                double dist = calculatedist(points.get(i), points.get(j));
                temp.add(dist);
            }
            distance.add(temp);
        }
        for (int i = 0; i < distance.size(); i++) {
            for (int j = 0; j < distance.get(i).size(); j++) {
                System.out.printf("%.3f ", distance.get(i).get(j));
            }
            System.out.println();
        }
        int iteration = 0;
        while (true) {
            int[] index = calculateminimum(distance);
            if (index[0] == 0 && index[1] == 0) {
                break;
            }
            int small = 0;
            int large = 0;
            System.out.println("Merge points" + index[0] + " and " + index[1]);
            if (index[0] < index[1]) {
                small = index[0];
                large = index[1];
            } else {
                small = index[1];
                large = index[0];
            }
            for (int i = 0; i <= small; i++) {
                if (i == small) {
                    continue;
                }
                double minimum = Math.min(distance.get(small).get(i), distance.get(large).get(i));
                distance.get(small).set(i, minimum);

            }
            distance.remove(large);
            for (int i = large; i < distance.size(); i++) {
                distance.get(i).remove(large);
            }
            System.out.println("Iteration " + ++iteration);
            for (int i = 0; i < distance.size(); i++) {
                for (int j = 0; j < distance.get(i).size(); j++) {
                    System.out.printf("%.3f ", distance.get(i).get(j));
                }
                System.out.println();
            }

        }

    }
}