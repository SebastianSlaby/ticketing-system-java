# ticketing-system-java

## Database design
* tickets (summary, description, creator, resolver, status, type, date added, date closed)
* users (name, surname, email, hashed password, salt, user type)
* ticket types ?
* user types (ticker creator, ticker resolver)
* notes (ticket, text)
* ticket status (in progress(INPROG), closed(CLOSED), waiting for customer response/action(SLAHOLD) )

## Stack
* Java
* MySQL
* GUI library to be determined

## Assumptions

* Everybody with access to the system is able to create a ticket
* A resolver is able to assign the ticket to himself 
* It's possible to add notes to a ticket to document the workflow
* Each ticket must have a summary and description
