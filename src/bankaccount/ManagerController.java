package bankaccount;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ManagerController {
    @FXML
    private TextField newUsernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label managerMessageLabel;
    @FXML
    private TextField deleteUsernameField;

    private Stage stage;
    private Scene loginScene;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLoginScene(Scene loginScene) {
        this.loginScene = loginScene;
    }

    public void addCustomer() {
        String newUsername = newUsernameField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            managerMessageLabel.setText("Please enter new username and password.");
            return;
        }

        if (new File(newUsername + ".txt").exists()) {
            managerMessageLabel.setText("Customer already exists.");
            return;
        }

        try {
            Files.write(Paths.get(newUsername + ".txt"), (newUsername + "\n" + newPassword + "\ncustomer\n100.0").getBytes());
            managerMessageLabel.setText("Customer added successfully with initial balance of $100.0.");
            newUsernameField.clear();
            newPasswordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
            managerMessageLabel.setText("Failed to add customer.");
        }
    }

    public void deleteCustomer() {
        String usernameToDelete = deleteUsernameField.getText().trim();

        if (usernameToDelete.isEmpty()) {
            managerMessageLabel.setText("Please enter the username to delete.");
            return;
        }

        File customerFile = new File(usernameToDelete + ".txt");
        if (customerFile.exists()) {
            if (customerFile.delete()) {
                managerMessageLabel.setText("Customer '" + usernameToDelete + "' deleted successfully.");
                deleteUsernameField.clear();
            } else {
                managerMessageLabel.setText("Failed to delete customer '" + usernameToDelete + "'.");
            }
        } else {
            managerMessageLabel.setText("Customer '" + usernameToDelete + "' does not exist.");
        }
    }

    public void logout() {
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
}
