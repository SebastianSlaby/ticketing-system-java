package ticket;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    private Label promptUsername;
    @FXML
    private Label promptFirstName;
    @FXML
    private Label promptLastName;
    @FXML
    private Label promptEmail;
    @FXML
    private Label promptPassword;

    private ArrayList<Label> prompts;
    private String[] fieldNames;


    @FXML
    public void registerUser (ActionEvent event) throws SQLException {
        fieldNames = new String[]{"Username", "First Name", "Last Name", "Email", "Password"};
        ArrayList<TextField> fields = new ArrayList<>();
        fields.add(usernameField);
        fields.add(firstNameField);
        fields.add(lastNameField);
        fields.add(emailField);
        fields.add(passwordField);
        fields.add(confirmPasswordField);

        prompts = new ArrayList<>();
        prompts.add(promptUsername);
        prompts.add(promptFirstName);
        prompts.add(promptLastName);
        prompts.add(promptEmail);
        prompts.add(promptPassword);

        if(validateFields(fields)) return;

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
        boolean hasError = false;
//        fields.forEach((el) -> {
////            el.getStyleClass().remove("focused-field");
//            el.getStyleClass().remove("incorrect-field");
//        });
        for(int i=0; i < fields.size(); i++) {
            fields.get(i).getStyleClass().remove("incorrect-field");
            if(i < prompts.size()) {
                prompts.get(i).setText("");
            }
        }

        if(!usernameField.getText().trim().isEmpty() && !isUniqueUsername(usernameField.getText().trim())) {
            highlightField(usernameField);
            promptUsername.setText("This username has already been taken");
            hasError=true;
        }

        if(!emailField.getText().trim().isEmpty() && !isValidEmail(emailField.getText().trim())) {
            highlightField(emailField);
            promptEmail.setText("Please enter a valid email");
            hasError = true;
        }

        if(!passwordField.getText().trim().isEmpty() && !confirmPasswordField.getText().trim().isEmpty() && !passwordField.getText().trim().equals(confirmPasswordField.getText().trim())) {
            highlightField(confirmPasswordField);
            promptPassword.setText("Confirm Password does not match the Password");
            hasError = true;
        }

        for(int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getText().trim().isEmpty()) {
                highlightField(fields.get(i));
                if(i < prompts.size()) {
                    prompts.get(i).setText(fieldNames[i] + " cannot be empty");
                }
                hasError = true;
            }
        }
        return hasError;
    }

    public void highlightField(TextField field) {
        if(!field.getStyleClass().contains("incorrect-field")){
            field.getStyleClass().add("incorrect-field");
        }
    }

    public boolean isUniqueUsername (String username) {
        String isUniqueUsernameSQLQuery = String.format("SELECT COUNT(*) FROM users WHERE username='%s'", username);
        boolean isUnique = false;
        int count = 0;
        try(
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement = connection.prepareStatement(isUniqueUsernameSQLQuery);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while(resultSet.next()) {
                count = resultSet.getInt(1);
                System.out.println(count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(count == 0) isUnique = true;
        return isUnique;
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
