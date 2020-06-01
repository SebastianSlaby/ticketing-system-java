package ticket;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
		this.statusId = statusId; //1-unsolved 2-solved
		this.ticketDateOfCreation = ticketDateOfCreation;
		this.notes = notes;
	}

	public Ticket(int ticketId, String ticketTitle, String ticketDetails, int resolverId, int creatorId, int statusId, LocalDate ticketDateOfCreation, LocalDate ticketDateOfClosure, Notes notes) {
		this.ticketId = ticketId;
		this.ticketTitle = ticketTitle;
		this.ticketDetails = ticketDetails;
		this.resolverId = resolverId;
		this.creatorId = creatorId;
		this.statusId = statusId;
		this.ticketDateOfCreation = ticketDateOfCreation;
		this.ticketDateOfClosure = ticketDateOfClosure;
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

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int ticketId, int statusId) throws SQLException {
		this.statusId = statusId;
		String statusIdSQLQuery = String.format("UPDATE tickets SET status_id='%d' WHERE id=%d;", statusId, ticketId);

		try(
					Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
					PreparedStatement statement = connection.prepareStatement(statusIdSQLQuery)
		) {
			statement.executeUpdate();
			if(statusId > 1 && this.ticketDateOfClosure == null){
				setTicketDateOfClosure(ticketId, Date.valueOf(LocalDate.now()));
			} else if (statusId == 1) {
				setTicketDateOfClosure(ticketId, null);
			}
		}
	}

	private void setTicketDateOfClosure(int id, Date date) {
		Date dateOfClosure = date;
		String dateOfClosureSQLQuery;
		if(dateOfClosure != null) {
			dateOfClosureSQLQuery = String.format("UPDATE tickets SET date_closed='%s' WHERE id=%d;", dateOfClosure, id);
		} else {
			dateOfClosureSQLQuery = String.format("UPDATE tickets SET date_closed=null WHERE id=%d;", id);
		}
		try(
				Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
				PreparedStatement statement = connection.prepareStatement(dateOfClosureSQLQuery)
		) {
			statement.executeUpdate();
			if(dateOfClosure != null) {
				this.ticketDateOfClosure = dateOfClosure.toLocalDate();
			} else {
				this.ticketDateOfClosure = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
