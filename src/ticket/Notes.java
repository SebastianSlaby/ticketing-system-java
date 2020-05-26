package ticket;

import javafx.collections.ObservableList;

import java.io.IOException;

public class Notes {
    private static Notes instance = new Notes();
    private ObservableList<Note> notes;

    public static Notes getInstance() {
        return instance;
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ObservableList<Note> notes) {
        this.notes = notes;
    }

    public void addResolverNote (Note note) {
        notes.add(note);
    }
    public void deleteResolverNote (Note note) {
        notes.remove(note);
    }
    public void loadResolverNotes() throws IOException {}
    public void storeResolverNotes() throws IOException{}
}
