package ticket;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.*;
import java.time.LocalDate;

public class TicketDialogController {
    @FXML
    private TextField ticketTitleField;
    @FXML
    private TextArea ticketDetailsArea;

    public Ticket processResults () throws SQLException {
        String ticketSummary = ticketTitleField.getText().trim();
        String ticketDescription = ticketDetailsArea.getText().trim();
        int resolverId = 1; // TODO: 28.05.2020  
        int creator_id = 1; // TODO: 28.05.2020  
        int status_id = 1; // TODO: 28.05.2020
        LocalDate ticketDateOfCreation = LocalDate.now();

        String noteSQLQuery = String.format( "INSERT INTO tickets (`summary`, `description`, `resolver_id`, `creator_id`, `status_id`, `date_added`) " +
                "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');", ticketSummary, ticketDescription, resolverId, creator_id, status_id, Date.valueOf(ticketDateOfCreation));

        try (
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement = connection.prepareStatement(noteSQLQuery, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            int ticketId = 0;
            while (resultSet.next()) {
                ticketId = resultSet.getInt(1);
            }

            Ticket newTicket = new Ticket(ticketId, ticketSummary, ticketDescription, resolverId, creator_id, status_id, ticketDateOfCreation, new Notes());
            Tickets.getInstance().addTicket(newTicket);
            return newTicket;
        }

    }
}
