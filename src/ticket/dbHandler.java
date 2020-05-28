package ticket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class dbHandler {
	 
	 Connection connection;

	public dbHandler(String url, String username, String password) {
		
		connection=getcon( url, username, password);
		
	}
	
	
	public Connection getcon(String url, String username, String password){
	    try{
	        Class.forName("com.mysql.cj.jdbc.Driver");
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
		}catch (SQLException e)
	    {
		      System.err.println("Got an exception! ");
		      System.err.println(e.getMessage());
		      e.printStackTrace();
		    };
	}
}
