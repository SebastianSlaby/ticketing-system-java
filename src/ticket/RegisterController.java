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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;


    @FXML
    public void registerUser (ActionEvent event) throws SQLException {
        ArrayList<TextField> fields = new ArrayList<>();
        fields.add(usernameField);
        fields.add(firstNameField);
        fields.add(lastNameField);
        fields.add(emailField);
        fields.add(passwordField);
        fields.add(confirmPasswordField);

        if(!validateFields(fields)) return;

        String username = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        firstName = firstName.substring(0,1).toUpperCase() + firstName.substring(1).toLowerCase();
        String lastName = lastNameField.getText().trim().toLowerCase();
        lastName = lastName.substring(0,1).toUpperCase() + lastName.substring(1).toLowerCase();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        Account newAccount = new Account(username, firstName, lastName, email, password);


        String accountSQLQuery = String.format("INSERT INTO users (`username`, `salt`, `pass`, `name`, `surname`, `email`, `user_type`, `confirm_delete`) " +
                            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%d', %b);", newAccount.getUsername(), newAccount.getSalt(),
                newAccount.getPassword(), newAccount.getFirstName(), newAccount.getLastName(), newAccount.getEmail(), newAccount.getUser_type(), newAccount.isConfirmDelete());

        try(
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement = connection.prepareStatement(accountSQLQuery, Statement.RETURN_GENERATED_KEYS);
                ) {
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            int accountId = 0;
            while(resultSet.next()) {
                accountId = resultSet.getInt(1);
            }
            newAccount.setUserId(accountId);
        }

        backToLogin(event);

    }

    public boolean validateFields (ArrayList<TextField> fields) {
        boolean hasError = true;
        fields.forEach((el) -> {
//            el.getStyleClass().remove("focused-field");
            el.getStyleClass().remove("incorrect-field");
        });

        if(!isValidEmail(emailField.getText().trim())) {
            highlightField(emailField);
            hasError = false;
        }
        if(!passwordField.getText().trim().equals(confirmPasswordField.getText().trim())) {
            highlightField(confirmPasswordField);
            hasError = false;
        }
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


    public boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    @FXML
    public void backToLogin(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Login Window");
            stage.setScene(new Scene(root, 350, 400));
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
