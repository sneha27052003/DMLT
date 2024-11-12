import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Normalize {

    public static ArrayList<Double> readcsv(String path, String column) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("csv is empty");
        }
        String[] headers = line.split(",");
        int index = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equals(column)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Column '" + column + "' does not exist in the CSV file.");
        }
        ArrayList<Double> ans = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            for (int i = 0; i < values.length; i++) {
                if (i == index) {
                    ans.add(Double.parseDouble(values[i]));
                    break;
                }
            }
        }
        br.close();
        return ans;
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file path: ");
        String path = sc.nextLine();
        System.out.print("Enter column name which you want to normalize: ");
        String column = sc.nextLine();
        System.out.print("Enter minimum value for min-max normalization: ");
        int min = sc.nextInt();
        System.out.print("Enter Maximum value for min-max normalization: ");
        int max = sc.nextInt();
        ArrayList<Double> data = new ArrayList<>();
        try {
            data = readcsv(path, column);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double minA = Collections.min(data);
        double maxA = Collections.max(data);
        ArrayList<Double> minmax = new ArrayList<>();
        ArrayList<Double> zScore = new ArrayList<>();
        double sum = 0.0;
        for (int i = 0; i < data.size(); i++) {
            sum += data.get(i);
        }
        double mean = sum / data.size();
        double varsum = 0.0;
        for (int i = 0; i < data.size(); i++) {
            varsum += Math.pow(data.get(i) - mean, 2);
        }

        double variance = varsum / data.size();
        double stddev = Math.sqrt(variance);
        for (int i = 0; i < data.size(); i++) {
            double value = data.get(i);
            double minmaxvalue = (value - minA) / (maxA - minA) * (max - min) + min;
            double zscorevalue = (value - mean) / stddev;
            minmax.add(Math.round(minmaxvalue * 1000.0) / 1000.0);
            zScore.add(Math.round(zscorevalue * 1000.0) / 1000.0);
        }
        System.out.println("******************Min-Max Normalization********************");
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i) + " " + minmax.get(i));
        }
        System.out.println("******************Zscore Normalization********************");
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i) + " " + zScore.get(i));
        }
        sc.close();
    }

}
