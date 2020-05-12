package ticket;
import java.sql.*;


public class main {

	public static void main(String[] args) {

		dbHandler dbH = new dbHandler("jdbc:mysql://localhost:3306/ticketing?serverTimezone=UTC", "root","");

		Account testacc= new Account(dbH);
		testacc.email="joe@doe.com";
		testacc.handler=dbH;
		testacc.username="jdoe";
		testacc.pass="pass123";
		testacc.name="Joe";
		testacc.surname="Doe";
		
		testacc.Register();
	}
	
	





}
