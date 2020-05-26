package ticket;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class DialogController {
    @FXML
    private TextArea textAreaNewNote;

    public Note processResults () {
        String noteString = textAreaNewNote.getText().trim();

        Note newNote = new Note(noteString);
        Notes.getInstance().addResolverNote(newNote);

        return newNote;
    }
}
