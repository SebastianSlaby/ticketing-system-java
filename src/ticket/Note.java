package ticket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Note {
    private String note;
    private LocalDate dateOfCreation;
    private DateTimeFormatter formatter;


    public Note(String note) {
        this.note = note;
        this.dateOfCreation = LocalDate.now();
    }

    public String getNote() {
        return note;
    }


    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }
}
