package ticket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource(("/ticket/mainWindow.fxml")));
		primaryStage.setTitle("Ticketing System");
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.show();
	}


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
		launch(args);

	}
}
