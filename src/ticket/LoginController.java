package ticket;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    private GridPane loginGridPane;


    @FXML
    public void loginUser(ActionEvent event) throws SQLException, UnsupportedEncodingException {

        ArrayList<TextField> fields = new ArrayList<>();
        fields.add(usernameField);
        fields.add(passwordField);
        if(!validateFields(fields)) return;

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if(Account.comparePassword(username, password) > 0) {
            Parent root;
            try {
                Tickets.getInstance().loadTickets();
                root = FXMLLoader.load(getClass().getResource("/ticket/mainWindow.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Ticketing System");
                stage.setScene(new Scene(root, 800, 600));
                stage.show();
                ((Node) (event.getSource())).getScene().getWindow().hide();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            highlightField(passwordField);
        }
    }

    public boolean validateFields (ArrayList<TextField> fields) {
        boolean hasError = true;
        fields.forEach((el) -> {
//            el.getStyleClass().remove("focused-field");
            el.getStyleClass().remove("incorrect-field");
        });

        for(int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getText().trim().isEmpty()) {
                highlightField(fields.get(i));
                hasError = false;
            }
        }
        return hasError;
    }

    public void highlightField(TextField field) {
        if(!field.getStyleClass().contains("incorrect-field")){
            field.getStyleClass().add("incorrect-field");
        }
    }

    @FXML
    public void openRegisterWindow(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("registerDialog.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleExit() {
        Platform.exit();
    }
}
