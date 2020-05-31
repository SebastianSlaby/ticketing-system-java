package ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	private static int loggedIn;

	public Account(String username, String firstName, String lastName, String email, String password) {
		this.username = username;
		this.salt = genSalt();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = hashPass(password, this.salt);
		this.user_type = 1; //1-user   2-resolver
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
	private static void setCurrentlyLoggedIn(int userId) {
		loggedIn = userId;
	}

	public static int getCurrentlyLoggedIn() {
		return loggedIn;
	}

	public static int comparePassword(String username, String password) throws SQLException {
		String passwordSQLQuery = String.format("SELECT id, pass, salt FROM users WHERE username='%s';", username);
		String dbPassword = null, dbSalt = null;
		int id = -1;
		try (
				Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
				PreparedStatement statement = connection.prepareStatement(passwordSQLQuery);
				ResultSet resultSet = statement.executeQuery()
		) {
			while (resultSet.next()) {
				id = resultSet.getInt("id");
				dbPassword = resultSet.getString("pass");
				dbSalt = resultSet.getString("salt");
			}
		}
		setCurrentlyLoggedIn(id);
		if(hashPass(password, dbSalt).equals(dbPassword)){
			id = getCurrentlyLoggedIn();
		}
		return id;
	}
	
	private String genSalt() {
		//generate random salt
		return "salt";
	}
	
	private static String hashPass(String pass, String salt) {
		//create hash based on provided pass and salt
		return "test123";
	}
	
}
