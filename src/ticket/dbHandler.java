package ticket;
import java.sql.*;

public class dbHandler {
	 
	 Connection connection;

	public dbHandler(String url, String username, String password) {
		
		connection=getcon( url, username, password);
		
	}
	
	
	public Connection getcon(String url, String username, String password){
	    try{
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        String unicode="useSSL=false&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
	        return DriverManager.getConnection(url, username, password);
	    }catch(Exception ex){
	        System.out.println(ex.getMessage());
	        System.out.println("couldn't connect!");
	        throw new RuntimeException(ex);
	    }
	}
	
	
	void insert(String query) {
		try {
			Statement st = connection.createStatement();
			st.executeUpdate(query);
		}catch (Exception e)
	    {
		      System.err.println("Got an exception! ");
		      System.err.println(e.getMessage());
		    };
	}
	
}
