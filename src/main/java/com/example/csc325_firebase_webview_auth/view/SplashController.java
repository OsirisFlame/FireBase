package com.example.csc325_firebase_webview_auth.view;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void initialize() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        // Wait 3 seconds then switch to main screen
        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(Duration.seconds(3));

        pause.setOnFinished(event -> {
            try {
                Parent root = FXMLLoader.load(
                        getClass().getResource("/files/Login.fxml"));
                Stage stage = (Stage) progressBar.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }
}