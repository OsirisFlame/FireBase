package com.example.csc325_firebase_webview_auth.view;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters.");
            return;
        }

        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            FirebaseAuth.getInstance().createUser(request);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Account created! Please sign in.");

            // Go back to login after 2 seconds
            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(
                            javafx.util.Duration.seconds(2));
            pause.setOnFinished(e -> {
                try {
                    App.setRoot("/files/Login.fxml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            pause.play();

        } catch (FirebaseAuthException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/files/Login.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
}