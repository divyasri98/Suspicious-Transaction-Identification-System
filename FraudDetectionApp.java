import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.text.*;


public class FraudDetectionApp extends JFrame {
    ArrayList<Transaction> transactions = new ArrayList<>();

    public FraudDetectionApp() {
        setTitle("");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel heading = new JLabel(
    "Suspicious Transaction Identification System",
    JLabel.CENTER
);

        heading.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel nameLabel = new JLabel("Customer Name:");
        JTextField nameField = new JTextField(20);

        JLabel accountLabel = new JLabel("Demo Account Number:");
        JTextField accountField = new JTextField(10);

((AbstractDocument) accountField.getDocument()).setDocumentFilter(
    new DocumentFilter() {
        public void insertString(FilterBypass fb, int offset,
                                 String string, AttributeSet attr)
                throws BadLocationException {
            if (string != null && string.matches("[0-9]+")
                    && fb.getDocument().getLength() + string.length() <= 10) {
                super.insertString(fb, offset, string, attr);
            }
        }

        public void replace(FilterBypass fb, int offset, int length,
                            String text, AttributeSet attrs)
                throws BadLocationException {
            if (text != null && text.matches("[0-9]*")
                    && fb.getDocument().getLength() - length
                       + text.length() <= 10) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
);

        JLabel amountLabel = new JLabel("Transaction Amount (Rs):");
        JTextField amountField = new JTextField(20);

        JLabel typeLabel = new JLabel("Transaction Type:");
        String[] types = {"UPI", "Card", "Bank Transfer"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        JLabel purposeLabel = new JLabel("Transaction Purpose:");

String[] purposes = {
    "Daily Payment",
    "College Fee",
    "Shopping",
    "Other"
};

JComboBox<String> purposeBox = new JComboBox<>(purposes);

        JButton checkButton = new JButton("Check Transaction");
        JButton historyButton = new JButton("View Transaction History");
        JButton clearButton = new JButton("New Transaction");
        JLabel resultLabel = new JLabel(
    "Enter sample details and check the transaction."
);
        checkButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      
        try {
            String name = nameField.getText().trim();
String status = "Normal";
String account = accountField.getText().trim();
String amountText = amountField.getText().trim();
String purpose = purposeBox.getSelectedItem().toString();

if (name.isEmpty() || account.isEmpty() || amountText.isEmpty()) {
    JOptionPane.showMessageDialog(
        null, "Please enter all details."
    );
    return;
}

double amount = Double.parseDouble(
    amountText.replace(",", "")
);
                if (name.trim().isEmpty() || account.trim().isEmpty()) {
    JOptionPane.showMessageDialog(
        null, "Please enter all details."
    );

    return;
}
if (account.length() != 10) {
    JOptionPane.showMessageDialog(
        null, "Account number must contain exactly 10 digits."
    );
    return;
}

if (amount <= 0) {
    JOptionPane.showMessageDialog(
        null, "Enter a valid positive amount."
    );
    return;
}
TransactionThread thread = new TransactionThread();
thread.start();
           int count = 0;

           boolean ownershipError = false;

for (Transaction t : transactions) {
    if (t.accountNumber.equals(account)
            && !t.customerName.equalsIgnoreCase(name)) {
        ownershipError = true;
        break;
    }
}

if (ownershipError) {
    JOptionPane.showMessageDialog(
        null, "Account Ownership Error: "
        + "This account belongs to another customer."
    );
    return;
}
String transactionType = typeBox.getSelectedItem().toString();

for (Transaction t : transactions) {
    if (t.accountNumber.equals(account)
            && t.amount == amount
            && t.transactionType.equals(transactionType)) {
        count++;
    }
}


Transaction transaction =
    new Transaction(name,account, amount, transactionType,purpose);
          
if (amount >= 50000 && !purpose.equals("College Fee")) {
    status = "Suspicious";
    JOptionPane.showMessageDialog(
        null, "Warning: High-value suspicious transaction!"
    );
}
else if (purpose.equals("Daily Payment") && amount > 10000) {
    status="Suspicious";
    JOptionPane.showMessageDialog(
        null, "Warning: Unusual amount for daily payment!"
    );
}
else if (purpose.equals("Shopping") && amount > 30000) {
    status = "Suspicious";
    JOptionPane.showMessageDialog(
        null, "Warning: Unusual amount for shopping!"
    );
}

else if (count >= 2) {
    status = "Suspicious";
    JOptionPane.showMessageDialog(
        null, "Warning: Repeated transaction detected!"
    );
}
else {
    status = "Normal";
    JOptionPane.showMessageDialog(
        null, "Transaction appears normal."
    );
}
if (status.equals("Normal")) {
    transactions.add(transaction);
}
try (FileWriter writer =
         new FileWriter("transaction_history.txt", true)) {

    String dateTime = LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    );
writer.write(
    transaction.transactionId + ","
    + name + "," + account + "," + amount + ","
    + transactionType + "," + purpose + ","
    + dateTime + "," + status + "\n"
);
    
    System.out.println("Transaction saved successfully!");

} catch (IOException ex) {
    JOptionPane.showMessageDialog(
        null, "Error saving transaction history."
    );
}
 }catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                null, "Please enter a valid numeric amount."
            );
        }
    }
});
historyButton.addActionListener(e -> {
    StringBuilder history = new StringBuilder();

    try (BufferedReader reader =
             new BufferedReader(new FileReader("transaction_history.txt"))) {

        String line;

        while ((line = reader.readLine()) != null) {

            System.out.println("Reading history: " + line);

            String[] details = line.split(",", -1);

            if (details.length == 8) {

                int savedId = Integer.parseInt(details[0]);

                if (savedId >= Transaction.nextId) {
                    Transaction.nextId = savedId + 1;
                }

                history.append("Transaction ID: ").append(details[0])
                       .append("\nCustomer: ").append(details[1])
                       .append("\nAccount: ").append(details[2])
                       .append("\nAmount: Rs. ").append(details[3])
                       .append("\nType: ").append(details[4])
                       .append("\nPurpose: ").append(details[5])
                       .append("\nDate & Time: ").append(details[6])
                       .append("\nTransaction Status: ").append(details[7])
                       .append("\n----------------------\n");
            }
        }

    } catch (IOException ex) {
        history.append("No previous transaction history found.");
    }

    if (history.length() == 0) {
        history.append("No transactions available.");
    }

    JTextArea textArea = new JTextArea(history.toString(), 20, 35);
    textArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane.showMessageDialog(
        this,
        scrollPane,
        "Transaction History",
        JOptionPane.INFORMATION_MESSAGE
    );
});


clearButton.addActionListener(e -> {
    nameField.setText("");
    accountField.setText("");
    amountField.setText("");
    typeBox.setSelectedIndex(0);
    purposeBox.setSelectedIndex(0);
    resultLabel.setText("Enter sample details and check the transaction.");
});

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));

        panel.add(nameLabel);
        panel.add(nameField);

        panel.add(accountLabel);
        panel.add(accountField);

        panel.add(amountLabel);
        panel.add(amountField);

        panel.add(typeLabel);
        panel.add(typeBox);

        panel.add(purposeLabel);
        panel.add(purposeBox);

        panel.add(checkButton);
        panel.add(historyButton);
        panel.add(clearButton);
        panel.add(resultLabel);

        add(heading, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        new FraudDetectionApp();
    }
}