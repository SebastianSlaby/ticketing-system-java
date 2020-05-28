package ticket;

import java.time.LocalDate;

public class Note {
    private int noteId;
    private String noteTitle;
    private String noteDetails;
    private LocalDate noteDateOfCreation;


    public Note(int noteId, String noteTitle, String noteDetails, LocalDate noteDateOfCreation) {
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteDetails = noteDetails;
        this.noteDateOfCreation = noteDateOfCreation;
    }

    public int getNoteId() {
        return noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteDetails() {
        return noteDetails;
    }

    public LocalDate getNoteDateOfCreation() {
        return noteDateOfCreation;
    }
}
