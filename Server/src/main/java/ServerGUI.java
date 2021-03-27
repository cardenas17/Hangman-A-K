// Client GUI
// 		Thread for connecting a server to client 
// 		Uses Javafx for the GUI and Server for connection to client
// Angel Cardenas		651018873		acarde36
// Kartik Maheshwari	665023848		kmahes5
//
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerGUI extends Application {
	HangmanServer serverConnection;			// server object
	Stage ourStage;							// server stage
	ListView<String> log;					// Listview that holds all activities in the game

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ourStage = primaryStage;
		// for closing the window in the background after it's closed
		ourStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		ourStage.setTitle("Welcome to Hangman Server");
		ourStage.setScene(welcomeScene());
		ourStage.show();
	}
	
	/*returns the welcome screen with predetermined settings*/
	public Scene welcomeScene () {
		// Creating and grouping Javafx components in an orderly manner
		TextField prompt = new TextField("Enter Port: ");
		prompt.setEditable(false);
		prompt.setAlignment(Pos.CENTER);
		prompt.setPrefWidth(80);
		prompt.setStyle("-fx-background-color: white;");
		TextField portInput = new TextField();
		Button enter = new Button("Connect");
		Button instructions = new Button("Confused?");
		instructions.setOnAction(e-> {
				// creates a new Dialog box for displaying instructions
				Dialog<String> directions = new Dialog<String>();
				directions.setTitle("How to Login");
				directions.setResizable(true);
				directions.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				directions.getDialogPane().setMinWidth(500.0);
				
				// instructions for playing the game
				directions.setContentText("Enter the port that you want your clients to connect.\n Eg: '5555' or '5556' ");
				
				// Button to close the dialog box
				ButtonType ok = new ButtonType("OK", ButtonData.OK_DONE);
				directions.getDialogPane().getButtonTypes().add(ok);
				
				// display dialog box
				directions.showAndWait();
			}
		);
		
		enter.setOnAction(e-> {
			ourStage.setScene(ServerLogScene());
			ourStage.setTitle("Hangman Server Log");
			serverConnection = new HangmanServer(data->{
				Platform.runLater(()->{
					// populates the list view
					log.getItems().add(data.toString());
				});
			}, Integer.parseInt(portInput.getText()));
		});
		
		// Creating and grouping Javafx components in an orderly manner
		HBox instr = new HBox(instructions);
		instr.setAlignment(Pos.CENTER);
		HBox input = new HBox(prompt, portInput, enter);
		input.setAlignment(Pos.CENTER);
		VBox nodes = new VBox(input, instr);
		nodes.setAlignment(Pos.CENTER);
		BorderPane bPane = new BorderPane(nodes);
		
		// set background as matrix.jpg
		Image image1 = new Image("matrix.jpg");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		bPane.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		return new Scene(bPane, 480, 270);
	}
	
	/*returns the server log screen with predetermined settings*/
	public Scene ServerLogScene () {
		// Creating and grouping Javafx components in an orderly manner
		log = new ListView<String>();
		log.setPrefWidth(768);
		log.setPrefHeight(432);
		VBox logBox = new VBox(log);
		logBox.setAlignment(Pos.CENTER);
		HBox node = new HBox(logBox);
		node.setAlignment(Pos.CENTER);
		BorderPane bPane2 = new BorderPane(node);
		
		// set background as matrix.jpg
		Image image1 = new Image("matrix.jpg");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		bPane2.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		return new Scene(bPane2, 960, 540);
	}
}
