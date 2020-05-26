package ticket;

import javafx.collections.ObservableList;

public class Tickets {
    private static Tickets instance = new Tickets();
    private ObservableList<Ticket> tickets;

    public static Tickets getInstance() {
        return instance;
    }

    public ObservableList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ObservableList<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void addTicket (Ticket ticket) {
        tickets.add(ticket);
    }

    public void deleteTicket (Ticket ticket) {
        tickets.remove(ticket);
    }
}
