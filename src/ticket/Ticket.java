package ticket;

import java.time.LocalDate;

public class Ticket extends Note{

	private int id;
	private String note;
	private String description;
	private int resolver_id;
	private int creator_id;
	private LocalDate dateOfClosure;

	dbHandler handler;

	public Ticket(String note, String description, int resolver_id, int creator_id, String dateOfClosure) {
		super(note);
		this.description = description;
		this.resolver_id = resolver_id;
		this.creator_id = creator_id;
		this.dateOfClosure = LocalDate.parse(dateOfClosure);
	}

	public String getDescription() {
		return description;
	}


	public LocalDate getDateOfClosure() {
		return dateOfClosure;
	}

	void Close(int ID) {
		
	}
	
	void Upload(Ticket ticket) {
		
	}
	
	void addNote(int id) {
		
	}
	
}
