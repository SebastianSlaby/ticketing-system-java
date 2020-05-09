package ticket;

public class Account {

	int id;
	String username;
	String salt;
	String pass;
	String name;
	String surname;
	String email;
	int user_type;
	
	
	
	
	void Register(Account account) {
		
		
	}

	//	returns int to set the currently logged in user id
	int Login(String username, String password) {
		return 1; // change to user_id
	}
	
	
	
}
