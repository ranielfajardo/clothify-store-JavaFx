package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LaundryApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";

    public static String testConnection() {
        try {
            URL url = new URL(BASE_URL + "/test.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            conn.disconnect();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "API connection failed: " + e.getMessage();
        }
    }

    public static String getLaundryTransactions() {
        try {
            URL url = new URL(BASE_URL + "/laundry_transactions.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            conn.disconnect();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get laundry transactions: " + e.getMessage();
        }
    }

    public static String addLaundryTransaction(String customerName, double totalPrice, String status) {
        try {
            URL url = new URL(BASE_URL + "/add_laundry_transaction.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{"
                    + "\"customer_name\":\"" + customerName + "\","
                    + "\"total_price\":" + totalPrice + ","
                    + "\"status\":\"" + status + "\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8")
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            conn.disconnect();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add laundry transaction: " + e.getMessage();
        }
    }
}