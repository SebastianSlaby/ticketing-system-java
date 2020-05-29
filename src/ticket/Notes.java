package ticket;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Notes {
//    private static Notes instance = new Notes();
    private ObservableList<Note> notes;

//    public Notes () {
//        this.notes = FXCollections.observableArrayList();
//    }

//    public Notes getInstance() {
//        return instance;
//    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ObservableList<Note> notes) {
        this.notes = notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }
    public void deleteNote(Note note) throws SQLException {
        try (
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement1 = connection.prepareStatement("DELETE FROM notes WHERE id=" + note.getNoteId() + ";");
        ) {
            statement1.executeUpdate();
            notes.remove(note);
        }
    }
    public void loadNotes() throws IOException {}
    public void storeNotes() throws IOException{}
}
