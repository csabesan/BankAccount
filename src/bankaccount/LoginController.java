package bankaccount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }

        if (username.equals("admin") && password.equals("admin")) {
            messageLabel.setText("Manager login successful.");
            loadManagerInterface();
            return;
        }

        File file = new File(username + ".txt");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String storedUsername = br.readLine();
                String storedPassword = br.readLine();
                String role = br.readLine();
                if (storedUsername.equals(username) && storedPassword.equals(password) && role.equals("customer")) {
                    messageLabel.setText("Customer login successful.");
                    loadCustomerInterface();
                    return;
                } else {
                    messageLabel.setText("Invalid username or password.");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        messageLabel.setText("Invalid username or password.");
    }

    private void loadManagerInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("manager.fxml"));
            Parent root = loader.load();
            ManagerController controller = loader.getController();
            Scene scene = new Scene(root);
            Stage managerStage = new Stage();
            managerStage.setScene(scene);
            controller.setStage(managerStage);
            stage.close();
            managerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customer.fxml"));
            Parent root = loader.load();
            CustomerController controller = loader.getController();
            controller.initialize(usernameField.getText().trim());
            Scene scene = new Scene(root);
            Stage customerStage = new Stage();
            customerStage.setScene(scene);
            controller.setStage(customerStage);
            stage.close();
            customerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
