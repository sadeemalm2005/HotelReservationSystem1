package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller for the Homepage/Landing page
 * Displays system description and provides navigation to login
 */
public class HomepageController {

    @FXML
    private Button loginButton;

    /**
     * Handles navigation to Login page
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            // Load Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Parent loginView = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
            stage.setTitle("Hotel Reservation System - Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Login page: " + e.getMessage());
        }
    }
}
