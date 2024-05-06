package bankaccount;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Manages the customer's account functionalities including deposit, withdrawal, balance management,
 * and online purchases.
 * 
 * This class is mutable.
 */
public class CustomerController {

    private String username;
    private double balance;
    private String level;
    
    @FXML
    private TextField depositAmountField;
    @FXML
    private TextField withdrawAmountField;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label insufficientFundsLabel;
    @FXML
    private TextField purchaseAmountField;

    private Stage stage;
    
    private CustomerState currentState;

    
    /**
     * Returns the current balance of the customer's account.
     * 
     * @return The current balance.
     */
    // Effects: Returns the current balance of the customer's account.
    public double getBalance() {
        return balance;
    }

    /**
     * Returns the label used to display insufficient funds messages.
     * 
     * @return The label for insufficient funds messages.
     */
    // Effects: Returns the label used to display insufficient funds messages.
    public Label getInsufficientFundsLabel() {
        return insufficientFundsLabel;
    }
    
    /**
     * Returns the text field for entering purchase amounts.
     * 
     * @return The text field for purchase amounts.
     */
    // Effects: Returns the text field for entering purchase amounts.
    public TextField getPurchaseAmountField() {
        return purchaseAmountField;
    }
    
    /**
     * Sets the balance of the customer's account.
     * 
     * @param newBalance The new balance to be set.
     */
    // Effects: Sets the balance of the customer's account to the specified new balance.
    // Modifies: balance
    public void setBalance(double newBalance) {
        this.balance = newBalance;
        updateBalanceLabel(); // Update the balance label after setting the new balance
    }
    
    /**
     * Sets the stage for this controller.
     * 
     * @param stage The JavaFX stage.
     */
    // Effects: Sets the stage for this controller to the specified JavaFX stage.
    // Modifies: stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    
    private void setCurrentState() {
        // Effects: Sets the current state of the customer based on their balance.
        // Modifies: currentState
        if (balance >= 20000) {
            currentState = new PlatinumState();
        } else if (balance >= 10000) {
            currentState = new GoldState();
        } else {
            currentState = new SilverState();
        }
        currentState.setContext(this); // Set the context of the current state to this CustomerController
    }
    
    /**
     * Initializes the customer controller with the given username.
     * 
     * @param username The username of the customer.
     */
    // Effects: Initializes the customer controller with the given username.
    // Modifies: this.username, balance, level, currentState
    public void initialize(String username) {
        this.username = username;
        loadBalance();
        updateLevel();
        updateBalanceLabel();
        setCurrentState();
    }

    /**
     * Updates the balance label to display the current balance.
     */
    // Effects: Updates the balance label to display the current balance.
    public void updateBalanceLabel() {
        balanceLabel.setText("Balance: $" + balance);
    }

    /**
     * Updates the level label to display the current level based on the balance.
     */
    // Effects: Updates the level label to display the current level based on the balance.
    // Modifies: level, currentState, levelLabel
    public void updateLevel() {
        if (balance >= 20000) {
            level = "Platinum";
            currentState = new PlatinumState();
            currentState.setContext(this);
        } else if (balance >= 10000) {
            level = "Gold";
            currentState = new GoldState(); 
            currentState.setContext(this);
        } else {
            level = "Silver";
            currentState = new SilverState();
            currentState.setContext(this);
        }
        levelLabel.setText("Level: " + level);
    }

    /**
     * Deposits the specified amount into the customer's account.
     * 
     * @throws NumberFormatException if the entered amount is not a valid number.
     * @throws NullPointerException if the deposit amount field is null.
     */
    public void deposit() {
        // Effects: Increases the account balance by the deposited amount.
        // Modifies: balance, balanceLabel, levelLabel, depositAmountField, insufficientFundsLabel
        // Requires: The deposit amount field must not be null.
        //           The entered amount must be a valid number, can not be negative.
        String amountText = depositAmountField.getText().trim();
        if (!amountText.isEmpty()) {
            double amount = Double.parseDouble(amountText);
            if (amount >0){
                balance += amount;
                updateBalanceLabel();
                updateLevel();
                saveBalanceToFile(amount);
                depositAmountField.clear();
                insufficientFundsLabel.setText("Deposit Succesful.");
            }else{
                insufficientFundsLabel.setText("You can not deposit a negative amount");
            }    
        }
    }

    /**
     * Withdraws the specified amount from the customer's account.
     * 
     * @throws NumberFormatException if the entered amount is not a valid number.
     * @throws NullPointerException if the withdraw amount field is null.
     */
    public void withdraw() {
        // Effects: Decreases the account balance by the withdrawn amount.
        // Modifies: balance, balanceLabel, levelLabel, withdrawAmountField, insufficientFundsLabel
        // Requires: The withdraw amount field must not be null.
        //           The entered amount must be a valid number, can not be negative.
        String amountText = withdrawAmountField.getText().trim();
        if (!amountText.isEmpty()) {
            double amount = Double.parseDouble(amountText);
            if (amount >0){
                
                if (balance >= amount) {
                    balance -= amount;
                    updateBalanceLabel();
                    updateLevel();
                    saveBalanceToFile(-amount);
                    withdrawAmountField.clear();
                    insufficientFundsLabel.setText("");
                } else {
                    insufficientFundsLabel.setText("Insufficient Funds");
                }
            }else{
                insufficientFundsLabel.setText("You can not withdraw a negative amount");
            }
        }
    }

    /**
     * Loads the customer's balance from the file.
     * 
     * @throws IOException if an I/O error occurs while reading the balance file.
     * @throws NumberFormatException if the balance file contains invalid numeric data.
     */
    private void loadBalance() {
        // Effects: Loads the customer's balance from the file and updates the balance label.
        // Modifies: balance, balanceLabel
        // Requires: The username must be valid and correspond to an existing balance file.
        File file = new File(username + ".txt");
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(username + ".txt"));
                balance = Double.parseDouble(lines.get(3));
                updateBalanceLabel();
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the customer's balance to the file.
     * 
     * @param amount The amount to be saved in the file.
     * @throws IOException if an I/O error occurs while writing to the balance file.
     * @throws NumberFormatException if the balance file contains invalid numeric data.
     */
    private void saveBalanceToFile(double amount) {
        // Effects: Saves the customer's balance to the file after adjusting it by the specified amount.
        // Modifies: balance file content
        // Requires: The username must be valid and correspond to an existing balance file.
        try {
            File file = new File(username + ".txt");
            if (file.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(username + ".txt"));
                double currentBalance = Double.parseDouble(lines.get(3));
                currentBalance += amount;
                lines.set(3, String.valueOf(currentBalance));
                Files.write(Paths.get(username + ".txt"), lines);
            } else {
                // Handle file not found or create new file
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initiates an online purchase for the customer.
     * 
     * @throws NumberFormatException if the entered purchase amount is not a valid number.
     * @throws NullPointerException if the purchase amount field is null.
     */
    public void purchaseOnline() {
        // Effects: Initiates an online purchase and adjusts the balance accordingly.
        // Modifies: balance, balanceLabel, levelLabel, purchaseAmountField, insufficientFundsLabel
        // Requires: The purchase amount field must not be null.
        //           The entered amount must be a valid number.
        String amountText = purchaseAmountField.getText().trim();
        if(!amountText.isEmpty()){
            double purchaseAmount = Double.parseDouble(amountText);
            currentState.purchaseOnline(purchaseAmount);
        }       
    }

    /**
     * Logs out the customer by navigating to the login screen.
     * 
     * @throws IOException if an I/O error occurs while loading the login screen.
     */
    public void logout() {
        // Effects: Logs out the customer by navigating to the login screen.
        // Modifies: stage
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            controller.setStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Abstraction Function:
     *   - Returns a string representation of the CustomerController object.
     * 
     * @return A string representation of the CustomerController object.
     */
    @Override
    public String toString() {
        return "CustomerController(username=" + username + ", balance=" + balance + ", level=" + level + ")";
    }

    /**
     * Requires: None
     * Modifies: None
     * Effects: Ensures that the rep invariant holds for this object.
     * 
     * @return True if the representation invariant holds, false otherwise.
     */
    private boolean repOk() {
        return username != null && balance >= 0 && (level.equals("Silver") || level.equals("Gold") || level.equals("Platinum"));
    }
}    
    
