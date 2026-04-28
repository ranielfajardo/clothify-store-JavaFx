import api.LaundryApiClient;

public class ApiTest {

    public static void main(String[] args) {

        String test = LaundryApiClient.testConnection();
        System.out.println("TEST API:");
        System.out.println(test);

        String transactions = LaundryApiClient.getLaundryTransactions();
        System.out.println("TRANSACTIONS:");
        System.out.println(transactions);

        String addResult = LaundryApiClient.addLaundryTransaction();

        System.out.println("ADD RESULT:");
        System.out.println(addResult);

        String summary = LaundryApiClient.getLaundryDashboardSummary();

        System.out.println("LAUNDRY DASHBOARD SUMMARY:");
        System.out.println(summary);
    }
}