
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
	HangmanServer serverConnection;
	Stage ourStage;
	ListView<String> log;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ourStage = primaryStage;
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
	
	public Scene welcomeScene () {
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
					log.getItems().add(data.toString());
				});
			}, Integer.parseInt(portInput.getText()));
		});
		
		HBox instr = new HBox(instructions);
		instr.setAlignment(Pos.CENTER);
		HBox input = new HBox(prompt, portInput, enter);
		input.setAlignment(Pos.CENTER);
		VBox nodes = new VBox(input, instr);
		nodes.setAlignment(Pos.CENTER);
		BorderPane bPane = new BorderPane(nodes);
		Image image1 = new Image("matrix.jpg");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		bPane.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		return new Scene(bPane, 480, 270);
	}
	
	public Scene ServerLogScene () {
		log = new ListView<String>();
		log.setPrefWidth(768);
		log.setPrefHeight(432);
		VBox logBox = new VBox(log);
		logBox.setAlignment(Pos.CENTER);
		HBox node = new HBox(logBox);
		node.setAlignment(Pos.CENTER);
		BorderPane bPane2 = new BorderPane(node);
		
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
