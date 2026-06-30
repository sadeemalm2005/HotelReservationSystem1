package hotelreservationsystem1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application Class for Hotel Reservation System
 * Launches the JavaFX application starting with the Homepage
 *
 * @author HUAWEI
 * @version 1.0
 */
public class HotelReservationSystem1 extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load Homepage.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Homepage.fxml"));
            Parent root = loader.load();

            // Create scene with the homepage
            Scene scene = new Scene(root);

            // Configure the primary stage
            primaryStage.setTitle("Hotel Reservation System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(700);

            // Show the stage
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Homepage: " + e.getMessage());
        }
    }

    /**
     * Main method to launch the application
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
