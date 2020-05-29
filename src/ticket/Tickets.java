package ticket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Tickets {
    private static Tickets instance = new Tickets();
    private ObservableList<Ticket> tickets;
    private dbHandler handler;

    public static Tickets getInstance() {
        return instance;
    }

    public ObservableList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ObservableList<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void addTicket (Ticket ticket) {
        tickets.add(ticket);
    }

    public void deleteTicket (Ticket ticket) throws SQLException {
        String deleteTicketSQLQuery = String.format("DELETE FROM tickets WHERE id=%d;", ticket.getTicketId());
        String deleteRelatedNotesSQLQuery = String.format("DELETE FROM notes WHERE ticket_id=%d;", ticket.getTicketId());
        try (
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement1 = connection.prepareStatement(deleteTicketSQLQuery);
                PreparedStatement statement2 = connection.prepareStatement(deleteRelatedNotesSQLQuery);
        ) {
            statement1.executeUpdate();
            statement2.executeUpdate();
            tickets.remove(ticket);
        }
    }

    public void loadTickets () throws SQLException {
        tickets = FXCollections.observableArrayList();
        int userId = Account.getCurrentlyLoggedIn();
        String loadTicketsSQLQuery = String.format("SELECT * FROM tickets WHERE creator_id=%d OR resolver_id=%d;", userId, userId);
        try (
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement1 = connection.prepareStatement(loadTicketsSQLQuery);
                ResultSet resultSet1 = statement1.executeQuery();
                ) {
            ObservableList<Ticket> ticketsOL = FXCollections.observableArrayList();

            while(resultSet1.next()) {
                int ticketId = resultSet1.getInt("id");
                String ticketTitle = resultSet1.getString("summary");
                String ticketDetails = resultSet1.getString("description");
                int resolverId = resultSet1.getInt("resolver_id");
                int creatorId = resultSet1.getInt("creator_id");
                int statusId = resultSet1.getInt("status_id");
                LocalDate ticketDateAdded = resultSet1.getDate("date_added").toLocalDate();
                LocalDate ticketDateClosed = null;
                if(resultSet1.getDate("date_closed") != null) {
                ticketDateClosed = resultSet1.getDate("date_closed").toLocalDate();
                }
                ObservableList<Note> notesOL = FXCollections.observableArrayList();
                String loadNotesSQLQuery = String.format("SELECT * FROM notes WHERE notes.ticket_id = %d;", ticketId);

                try (
                        PreparedStatement statement2 = connection.prepareStatement(loadNotesSQLQuery);
                        ResultSet resultSet2 = statement2.executeQuery();
                        ) {
                    while (resultSet2.next()) {
                        int noteId = resultSet2.getInt("id");
                        String noteTitle = resultSet2.getString("summary");
                        String noteDetails = resultSet2.getString("description");
                        LocalDate noteDateAdded = resultSet2.getDate("date_added").toLocalDate();
                        Note newNote = new Note(noteId, noteTitle, noteDetails, noteDateAdded);
                        notesOL.add(newNote);
                    }
                }

                Notes notes = new Notes();
                notes.setNotes(notesOL);
                Ticket newTicket;
                if(ticketDateClosed != null) {
                    newTicket = new Ticket(ticketId, ticketTitle, ticketDetails, resolverId, creatorId, statusId, ticketDateAdded, ticketDateClosed, notes);
                } else {
                    newTicket = new Ticket(ticketId, ticketTitle, ticketDetails, resolverId, creatorId, statusId, ticketDateAdded, notes);
                }
                ticketsOL.add(newTicket);
            }
//            if(ticketsOL != null) {
                Tickets.getInstance().setTickets(ticketsOL);
//            } else {
//                ObservableList<Ticket> emptyList = FXCollections.observableArrayList();
//                Tickets.getInstance().setTickets(emptyList);
//            }
        }

    }
}
