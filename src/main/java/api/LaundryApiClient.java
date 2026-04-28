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

            br.close();
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

            br.close();
            conn.disconnect();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get laundry transactions: " + e.getMessage();
        }
    }

    public static String addLaundryTransaction() {
        try {
            URL url = new URL(BASE_URL + "/add_laundry_transaction.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{"
                    + "\"id_outlet\":1,"
                    + "\"id_member\":1,"
                    + "\"biaya_tambahan\":0,"
                    + "\"diskon\":0,"
                    + "\"pajak\":0,"
                    + "\"status\":\"baru\","
                    + "\"dibayar\":\"belum dibayar\","
                    + "\"id_user\":1"
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

            br.close();
            conn.disconnect();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add laundry transaction: " + e.getMessage();
        }
    }

    public static String getLaundryDashboardSummary() {
        try {
            URL url = new URL(BASE_URL + "/laundry_summary.php");
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

            br.close();
            conn.disconnect();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get laundry dashboard summary: " + e.getMessage();
        }
    }

    public static LaundrySummary getLaundrySummary() {
        LaundrySummary summary = new LaundrySummary();

        try {
            String json = getLaundryDashboardSummary();

            summary.setNewCount(extractInt(json, "new"));
            summary.setInProcessCount(extractInt(json, "in_process"));
            summary.setCompletedCount(extractInt(json, "completed"));
            summary.setPickedUpCount(extractInt(json, "picked_up"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return summary;
    }

    private static int extractInt(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int startIndex = json.indexOf(searchKey);

            if (startIndex == -1) {
                return 0;
            }

            startIndex += searchKey.length();

            while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
                startIndex++;
            }

            int endIndex = startIndex;

            while (endIndex < json.length() && Character.isDigit(json.charAt(endIndex))) {
                endIndex++;
            }

            return Integer.parseInt(json.substring(startIndex, endIndex).trim());

        } catch (Exception e) {
            return 0;
        }
    }

    public static class LaundrySummary {
        private int newCount;
        private int inProcessCount;
        private int completedCount;
        private int pickedUpCount;

        public int getNewCount() {
            return newCount;
        }

        public void setNewCount(int newCount) {
            this.newCount = newCount;
        }

        public int getInProcessCount() {
            return inProcessCount;
        }

        public void setInProcessCount(int inProcessCount) {
            this.inProcessCount = inProcessCount;
        }

        public int getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(int completedCount) {
            this.completedCount = completedCount;
        }

        public int getPickedUpCount() {
            return pickedUpCount;
        }

        public void setPickedUpCount(int pickedUpCount) {
            this.pickedUpCount = pickedUpCount;
        }
    }
}