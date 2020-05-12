package ticket;

public class Account {

	dbHandler handler;
	int id=0;
	String username;
	String salt;
	String pass;
	String name;
	String surname;
	String email;
	int user_type;
	
	Boolean loggedIn;


	public Account(dbHandler handler) {
		this.handler = handler;
		
	}
	
	void Register() {
		this.salt=genSalt();
		String hash = hashPass(this.pass, this.salt);
		String query = String.format("INSERT INTO `users` VALUES (NULL, '%s',' %s',  '%s'  , '%s' , '%s', '%s', 1);", this.username, this.salt , hash , this.name, this.surname, this.email );

		handler.insert(query);
		
		
		
	}
	
	

	//	returns int to set the currently logged in user id
	int Login(String username, String password) {
		return 1; // change to user_id
	}
	
	String genSalt() {
		//generate random salt
		return "salt";
	}
	
	String hashPass(String pass, String salt) {
		//create hash based on provided pass and salt
		return "test123";
	}
	
}
