package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class AccessFBView {

    @FXML private TextField nameField;
    @FXML private TextField majorField;
    @FXML private TextField ageField;
    @FXML private Button writeButton;
    @FXML private Button readButton;
    @FXML private TextArea outputField;
    @FXML private TableView<Person> tableView;
    @FXML private TableColumn<Person, String> nameColumn;
    @FXML private TableColumn<Person, String> majorColumn;
    @FXML private TableColumn<Person, Integer> ageColumn;
    @FXML private ImageView profileImage;

    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        tableView.setItems(listOfUsers);

        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }

    @FXML
    private void uploadProfilePicture(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(profileImage.getScene().getWindow());
        if (file != null) {
            try {
                // Show image in UI immediately
                profileImage.setImage(new Image(file.toURI().toString()));

                // Upload to Firebase Storage
                Storage storage = StorageOptions.getDefaultInstance().getService();
                BlobId blobId = BlobId.of("fir-852db.appspot.com", "profiles/" + file.getName());
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
                storage.create(blobInfo, Files.readAllBytes(file.toPath()));

                System.out.println("Upload successful: " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addRecord(ActionEvent event) { addData(); }

    @FXML
    private void readRecord(ActionEvent event) { readFirebase(); }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("/files/WebContainer.fxml");
    }

    public void addData() {
        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());
        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    public boolean readFirebase() {
        key = false;
        listOfUsers.clear();
        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            if (documents.size() > 0) {
                for (QueryDocumentSnapshot document : documents) {
                    Person person = new Person(
                            String.valueOf(document.getData().get("Name")),
                            document.getData().get("Major").toString(),
                            Integer.parseInt(document.getData().get("Age").toString())
                    );
                    listOfUsers.add(person);
                }
                tableView.setItems(listOfUsers);
            } else {
                System.out.println("No data");
            }
            key = true;
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return key;
    }
    @FXML
    private void deleteRecord(ActionEvent event) {

        Person selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("No row selected");
            return;
        }

        try {

            ApiFuture<QuerySnapshot> future =
                    App.fstore.collection("References").get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {

                if (doc.getString("Name").equals(selected.getName())) {

                    App.fstore.collection("References")
                            .document(doc.getId())
                            .delete();

                    System.out.println("Deleted: " + doc.getId());
                }
            }

            listOfUsers.remove(selected);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }




