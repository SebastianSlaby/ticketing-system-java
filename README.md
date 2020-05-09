# ticketing-system-java

## Database design
* tickets (summary, description, creator, resolver, status, type, date added, date closed)
* users (name, surname, email, hashed password, salt, user type)
* ticket types (infrastructure, software, hardware)
* user types (ticket creator, ticket resolver)
* notes (ticket,summary, description)
* ticket status (in progress(INPROG), closed(CLOSED), waiting for customer response/action(SLAHOLD) )

## Stack
* Java
* MySQL
* GUI library to be determined

## Assumptions

* Each user requires an account
* Every password is hashed with a salt
* Everybody with access to the system is able to create a ticket
* Resolvers are able to see every ticket, while a creator is only able to see tickets created by them
* A resolver is able to assign the ticket to himself 
* It's possible to add notes to a ticket to document the workflow
* Each ticket must have a summary and description
