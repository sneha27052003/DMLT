import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LinearRegression {

    static ArrayList<Double> readcsv(String path, String X) throws NumberFormatException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        if (line == null) {
            throw new IOException("CSV is empty");
        }

        String[] headers = line.split(",");
        int index = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equals(X)) {
                index = i;
                break;
            }
        }
        ArrayList<Double> ans = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            for (int i = 0; i < values.length; i++) {
                if (index == i) {
                    ans.add(Double.parseDouble(values[i]));
                    break;
                }
            }
        }

        br.close();
        return ans;

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter File Path: ");
        String path = sc.nextLine();
        System.out.println("Enter Column name as X: ");
        String X = sc.nextLine();
        System.out.println("Enter the Column name as Y: ");
        String Y = sc.nextLine();

        try {
            ArrayList<Double> xdata = readcsv(path, X);
            ArrayList<Double> ydata = readcsv(path, Y);

            System.out.println(".................Linear Regression Coefficients..............");
            Double xsum = 0.0;
            Double ysum = 0.0;
            Double xysum = 0.0;
            Double xsq = 0.0;
            for (int i = 0; i < xdata.size(); i++) {
                xsum += xdata.get(i);
                ysum += ydata.get(i);
                xysum += (xdata.get(i) * ydata.get(i));
                xsq += Math.pow(xdata.get(i), 2);
            }
            System.out.println("xsum" + xsum);
            System.out.println("ysum" + ysum);
            System.out.println("xysum" + xysum);
            System.out.println("xsq" + xsq);

            int n = xdata.size();
            Double m = 0.0;
            m = ((n * xysum) - (xsum * ysum)) / ((n * xsq) - Math.pow(xsum, 2));
            Double c = 0.0;
            c = ((ysum * xsq) - (xsum * xysum)) / ((n * xsq) - Math.pow(xsum, 2));
            System.out.println("M: " + m);
            System.out.println("C: " + c);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
