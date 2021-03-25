
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGUI extends Application {
	HangmanServer serverConnection;
	Stage ourStage;
	ListView<String> log;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		ourStage = primaryStage;
		// TODO Auto-generated method stub
		ourStage.setTitle("Welcome to Hangman Server");
		
		ourStage.setScene(welcomeScene());
		ourStage.show();
	}
	
	public Scene welcomeScene () {
		TextField portInput = new TextField();
		Button enter = new Button("Enter");
		
		enter.setOnAction(e-> {
			ourStage.setScene(ServerLogScene());
			ourStage.setTitle("Hangman Server Log");
			serverConnection = new HangmanServer(data->{
				Platform.runLater(()->{
					log.getItems().add(data.toString());
				});
			}, Integer.parseInt(portInput.getText()));
		});
		
		HBox input = new HBox(portInput, enter);
//		input.getChildren().addAll(portInput, enter);
		
		BorderPane bPane = new BorderPane(input);
//		bPane.getChildren().add(input);
//		bPane.setCenter(input);
		
		return new Scene(bPane, 500, 500);
	}
	
	public Scene ServerLogScene () {
		log = new ListView<String>();
		
		VBox logBox = new VBox(log);
//		logBox.getChildren().add(log);
		
		BorderPane bPane2 = new BorderPane(logBox);
//		bPane.getChildren().add(logBox);
//		bPane.setCenter(logBox);
		
		return new Scene(bPane2, 500, 500);
	}
}
