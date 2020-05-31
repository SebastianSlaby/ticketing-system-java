package ticket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.*;
import java.time.LocalDate;

public class NoteDialogController {
    @FXML
    private TextField noteTitleField;
    @FXML
    private TextArea noteDetailsArea;


    public Note processResults (Ticket selectedTicket) throws SQLException {
        int ticketId = selectedTicket.getTicketId();
        String noteTitleString = noteTitleField.getText().trim();
        String noteDetailsString = noteDetailsArea.getText().trim();
        LocalDate noteDateOfCreation = LocalDate.now();
        String noteSQLQuery = String.format("INSERT INTO notes (`ticket_id`, `summary`,`description`,`date_added`) " +
                "VALUES ('%d', '%s', '%s', '%s');", ticketId, noteTitleString, noteDetailsString, Date.valueOf(noteDateOfCreation));

        try (
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement = connection.prepareStatement(noteSQLQuery, Statement.RETURN_GENERATED_KEYS)
        ) {
            int insertAndGetId  = statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            int noteId = 0;
            while (resultSet.next()) {
                noteId = resultSet.getInt(1);
            }

            Note newNote = new Note(noteId,noteTitleString, noteDetailsString, noteDateOfCreation);
            if(selectedTicket.getNotes().getNotes() != null) {
                selectedTicket.getNotes().addNote(newNote);
            } else {
                ObservableList<Note> notesOL = FXCollections.observableArrayList();
                notesOL.add(newNote);
                selectedTicket.getNotes().setNotes(notesOL);
            }
            return newNote;
        }

    }
}
