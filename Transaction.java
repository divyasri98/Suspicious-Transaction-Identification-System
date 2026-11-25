

public class Transaction {
    static int nextId = 1001;
    int transactionId;
    String customerName;
    String accountNumber;
    double amount;
    String transactionType;
    String purpose;

    public Transaction(String customerName, String accountNumber,
                       double amount, String transactionType,
                       String purpose) {
        this.transactionId = nextId++;
        this.customerName = customerName;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.purpose = purpose;
    }
}
