public class TransactionThread extends Thread {

    public void run() {
        System.out.println("Transaction processing started...");
        System.out.println("Transaction processing completed.");
    }

    public static void main(String[] args) {
        TransactionThread t = new TransactionThread();
        t.start();
    }
}
