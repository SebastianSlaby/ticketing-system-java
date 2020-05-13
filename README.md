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
* A user's permissions may only be changed by someone with Resolver privileges through the GUI
* A user's password is stored in a the form of a secure hash. Each user is given a random salt which is combined with the password to create the hash. It's impossible to recover a user's password in case it's forgotten.

## Architecture
* Ticket, Account and dbHandler classes are used to interface with the program
* The Ticket class represents a ticket and provides methods to change the state of a ticket, add notes, create a new one.
* The Account class represents a user account. It handles the registration and login validation of a user
* The dbHandler class is a wrapper around the native Java ways of interfacing with a database. It's easy to set up and exposes methods that enable to both change the state of the database and to request information from it.
* The Ticket and Account classess rely on an instance of the dbHandler class in order to communicate with the databae. It reduces the amount of required connections to 1, which is established only once throughout the whole lifecycle of the application.


## Progress
Done:
* User registration
* User login
* Database handler 
* Architectural design
* Database design and implementation

TODO:
* GUI
* Ticket creation, closure, adding notes
