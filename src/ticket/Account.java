package ticket;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class Account {

	private int userId;
	private String username;
	private byte[] salt;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private int user_type;
	private boolean confirmDelete;
	private static int loggedIn;

	public Account(String username, String firstName, String lastName, String email, String password) throws NoSuchProviderException, NoSuchAlgorithmException {
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

	public byte[] getSalt() {
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

	public static int comparePassword(String username, String password) throws SQLException, UnsupportedEncodingException {
		String passwordSQLQuery = String.format("SELECT id, pass, salt FROM users WHERE username='%s';", username);
		String dbPassword = null, dbSalt = null;
		int loggedId = -1, dbId = -1;
		try (
				Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
				PreparedStatement statement = connection.prepareStatement(passwordSQLQuery);
				ResultSet resultSet = statement.executeQuery()
		) {
			while (resultSet.next()) {
				dbId = resultSet.getInt("id");
				dbPassword = resultSet.getString("pass");
				dbSalt = resultSet.getString("salt");
			}
		}
		if(dbId > -1 && hashPass(password, Base64.getDecoder().decode(dbSalt)).equals(dbPassword)){
			setCurrentlyLoggedIn(dbId);
			loggedId = getCurrentlyLoggedIn();
		}
		return loggedId;
	}
	
	private static byte[] genSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}
	
	private static String hashPass(String pass, byte[] salt) {
		String generatedPassword = null;
		try{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(salt);
			byte[] bytes = messageDigest.digest(Base64.getDecoder().decode(pass));
			StringBuilder str = new StringBuilder();
			for(int i = 0; i < bytes.length; i++) {
				str.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = str.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}
	
}
