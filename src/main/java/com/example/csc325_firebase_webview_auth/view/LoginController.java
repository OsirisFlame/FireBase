package com.example.csc325_firebase_webview_auth.view;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            // Verify user exists in Firebase Auth
            UserRecord user = FirebaseAuth.getInstance()
                    .getUserByEmail(email);
            // If no exception, user exists — go to main screen
            App.setRoot("/files/AccessFBView.fxml");
        } catch (FirebaseAuthException e) {
            messageLabel.setText("Invalid email or password.");
        } catch (Exception e) {
            messageLabel.setText("Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        try {
            App.setRoot("/files/Register.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}