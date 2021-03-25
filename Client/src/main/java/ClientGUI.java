import java.io.Serializable;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientGUI extends Application {
	Stage ourStage;
	Client clientConnection;
	int count = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		ourStage = primaryStage;
		// TODO Auto-generated method stub
		ourStage.setTitle("Welcome to Hangman Game");
				
		ourStage.setScene(welcomeScene());
		ourStage.show();
	}
	
	public Scene welcomeScene() {
		TextField portInput = new TextField();
		Button enter = new Button("Enter");
		
		enter.setOnAction(e-> {
			ourStage.setScene(gameScene());
			ourStage.setTitle("Hangman Game");
			
			clientConnection = new Client(data->{
				Platform.runLater(()->{
					System.out.println("Before animalAttempts = 69");
					clientConnection.guessData.animalAttempts = 69;
					if (count != 2) {
						System.out.println("Before .send()");
						clientConnection.send(clientConnection.guessData);
						count++;
					}
				});
			}, Integer.parseInt(portInput.getText()));
			
			clientConnection.start();
		});
		
		HBox input = new HBox(portInput, enter);
		
		BorderPane bPane = new BorderPane(input);
//		bPane.setCenter(input);
		
		return new Scene(bPane, 500, 500);
	}
	
	public Scene gameScene() {
		Button testButton = new Button("Test");
		SerializableWord ligma = new SerializableWord();
		testButton.setOnAction(e-> clientConnection.send(ligma));
		return new Scene(new BorderPane(testButton), 500, 500);
	}

}
