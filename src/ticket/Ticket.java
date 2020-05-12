package ticket;

import java.sql.Date;

public class Ticket {

	int id;
	String summary;
	String description;
	int resolver_id;
	int creator_id;
	Date date_added;
	Date date_closed;
	
	dbHandler handler;
	
	
	void Close(int ID) {
		
	}
	
	void Upload(Ticket ticket) {
		
	}
	
	void addNote(int id) {
		
	}
	
}
