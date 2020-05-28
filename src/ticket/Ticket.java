package ticket;

import java.time.LocalDate;

public class Ticket{

	private int ticketId;
	private String ticketTitle;
	private String ticketDetails;
	private int resolverId;
	private int creatorId;
	private int statusId;
	private LocalDate ticketDateOfCreation;
	private LocalDate ticketDateOfClosure;
	private Notes notes;


	public Ticket(int ticketId, String ticketTitle, String ticketDetails, int resolverId, int creatorId, int statusId, LocalDate ticketDateOfCreation, Notes notes) {
		this.ticketId = ticketId;
		this.ticketTitle = ticketTitle;
		this.ticketDetails = ticketDetails;
		this.resolverId = resolverId;
		this.creatorId = creatorId;
		this.statusId = statusId;
		this.ticketDateOfCreation = ticketDateOfCreation;
		this.notes = notes;
	}

	public int getTicketId() {
		return ticketId;
	}

	public String getTicketTitle() {
		return ticketTitle;
	}

	public Notes getNotes() {
		return notes;
	}

	public String getTicketDetails() {
		return ticketDetails;
	}

	public LocalDate getTicketDateOfCreation() {
		return ticketDateOfCreation;
	}

	public LocalDate getTicketDateOfClosure() {
		return ticketDateOfClosure;
	}
	
}
