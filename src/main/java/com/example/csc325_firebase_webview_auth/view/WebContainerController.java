package com.example.csc325_firebase_webview_auth.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebContainerController implements Initializable {
    Document doc;
    private DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static String HTML_STRING2 =
            "<html><head><script language='javascript'>" +
                    "function changeBgColor() {" +
                    "var color = document.getElementById('ueberschr').value;" +
                    "document.body.style.backgroundColor = color;" +
                    "}" +
                    "</script></head><body>" +
                    "<h2>This is Html content</h2>" +
                    "<input id='ueberschr' value='yellow' />" +
                    "<button onclick='app12.showTime();changeBgColor();'>Call To JavaFX</button>" +
                    "</body></html>";

    private static String HTML_STRING =
            "<html><head><script language='javascript'>" +
                    "function changeBgColor() {" +
                    "var color = document.getElementById('color').value;" +
                    "document.body.style.backgroundColor = color;" +
                    "}" +
                    "</script></head><body>" +
                    "<h2>This is Html content</h2>" +
                    "<b>Enter Color:</b>" +
                    "<input id='color' value='yellow' />" +
                    "<button onclick='changeBgColor();'>Change Bg Color</button>" +
                    "</body></html>";

    @FXML
    Label label;

    @FXML
    WebView webView;
    private WebEngine webEngine;

    @FXML
    private void handleClose(ActionEvent e) {
        javafx.application.Platform.exit();
    }

    @FXML
    private void handleDelete(ActionEvent e) {
        label.setText("Deleted.");
        webEngine.loadContent("<html><body><h2>Content cleared.</h2></body></html>");
    }

    @FXML
    private void handleAbout(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("CSC325 Firebase App");
        alert.setContentText("Built with JavaFX + Firebase\nAuthor: Your Name");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent e) {
        try {
            App.setRoot("/files/Login.fxml");
        } catch (IOException ex) {
            Logger.getLogger(WebContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void goAction(ActionEvent evt) {
        webEngine.load("http://google.com");
    }

    @FXML
    private void setLabel(ActionEvent e) {
        doc.getElementById("ueberschr").setAttribute("value", "Red");
    }

    @FXML
    private void swithcBackStage(ActionEvent e) {
        try {
            App.setRoot("/files/AccessFBView.fxml");
        } catch (IOException ex) {
            Logger.getLogger(WebContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            webEngine = webView.getEngine();
            webEngine.loadContent(HTML_STRING2);
            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov, State t, State newState) {
                    if (newState == State.SUCCEEDED) {
                        doc = webEngine.getDocument();
                        JSObject jsobj = (JSObject) webEngine.executeScript("window");
                        jsobj.setMember("app12", new Bridge());
                    }
                }
            });
            webView.setContextMenuEnabled(false);
            webEngine.setJavaScriptEnabled(true);
        } catch (Exception ex) {
            Logger.getLogger(WebContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class Bridge {
        public void showTime() {
            label.setText("Now is: " + df.format(new Date()));
        }
    }
}