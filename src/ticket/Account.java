package ticket;

public class Account {

	private int userId;
	private String username;
	private String salt;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private int user_type;
	private boolean confirmDelete;
	
	Boolean loggedIn;

	public Account(String username, String firstName, String lastName, String email, String password) {
		this.username = username;
		this.salt = genSalt();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = hashPass(password, this.salt);
		this.user_type = 1;
		this.confirmDelete = false;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getSalt() {
		return salt;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public int getUser_type() {
		return user_type;
	}

	public boolean isConfirmDelete() {
		return confirmDelete;
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
