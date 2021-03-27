// Client GUI
// 		Thread for connecting a client to server 
// 		Uses Javafx for the GUI and Client for connection to server
// Angel Cardenas		651018873		acarde36
// Kartik Maheshwari	665023848		kmahes5
//

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class ClientGUI extends Application {
	Stage ourStage;
	Client clientConnection;
	int count = 0;
	Button animalsButton, citiesButton, foodButton, submitLetter, submitWord;
	Text animalsTries, citiesTries, foodTries, wordToGuess, letterGuessLeft, wordGuessLeft;
	TextField guessLetter, guessWord;
	PauseTransition pause;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
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
		ourStage.setTitle("Welcome to Hangman Client");
				
		ourStage.setScene(welcomeScene());
		ourStage.show();
		
		pause = new PauseTransition(Duration.seconds(3));
	}
	
	public Scene welcomeScene() {
		TextField portInput = new TextField();
		portInput.setFont(Font.font(25));
		Button enter = new Button("Login");
		enter.setFont(Font.font(25));
		Text names = new Text("BY: Angel Cardenas & Kartik Maheshwari");
		HBox namesBox = new HBox(names);
		namesBox.setPadding(new Insets(10,10,10,0)); 
		namesBox.setAlignment(Pos.CENTER);
		names.setFont(Font.font("SanSerif", 20));
		
		Text direction = new Text("Enter Port: ");
		direction.setFont(Font.font(30));
		Button instructions = new Button("Confused?");
		instructions.setAlignment(Pos.CENTER);
		
		instructions.setOnAction(e-> {
				// creates a new Dialog box for displaying instructions
				Dialog<String> directions = new Dialog<String>();
				directions.setTitle("How to Login");
				directions.setResizable(true);
				directions.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				directions.getDialogPane().setMinWidth(500.0);
				
				// instructions for playing the game
				directions.setContentText("Enter the port that the server is listening to.\nEg: '5555' or '5556'");
				
				// Button to close the dialog box
				ButtonType ok = new ButtonType("OK", ButtonData.OK_DONE);
				directions.getDialogPane().getButtonTypes().add(ok);
				
				// display dialog box
				directions.showAndWait();
			}
		);
		
		enter.setOnAction(e-> {
			ourStage.setTitle("Hangman Game");
			clientConnection = new Client(data->{
				Platform.runLater(()->{
					clientConnection.guessData = (SerializableWord) data;
					if (clientConnection.guessData.isConnectionFail) {
						ourStage.setScene(connectionFailScene());
					}
					// Add parse messages here
					if (clientConnection.guessData.isCatChoice) {
						wordToGuess.setText(clientConnection.guessData.serverWord);
						submitLetter.setDisable(false);
						submitWord.setDisable(false);
						clientConnection.guessData.isCatChoice = false;
					}
					else if (clientConnection.guessData.isGuessLetter) {
						wordToGuess.setText(clientConnection.guessData.serverWord);
						clientConnection.guessData.isGuessLetter = false;
						letterGuessLeft.setText("Letter Guesses Left: " + clientConnection.guessData.letterGuessesLeft + "/6 \t");
						if (clientConnection.guessData.letterGuessesLeft == 0) {
							submitLetter.setDisable(true);
						}
					}
					else if (clientConnection.guessData.isGuessWord) {
						wordToGuess.setText(clientConnection.guessData.serverWord);
						wordGuessLeft.setText("Word Guesses Left: " + clientConnection.guessData.wordGuessesLeft + "/3");
						clientConnection.guessData.isGuessWord = false;
						if (clientConnection.guessData.wordGuessesLeft == 0) {
							submitWord.setDisable(true);
						}
					}
					if (!containsDash(wordToGuess.getText())) {
						pause.play();
						ourStage.setScene(gameScene());
					}
					if (clientConnection.guessData.isAnimalsDone &&
							clientConnection.guessData.isCitiesDone&&
							clientConnection.guessData.isFoodDone) {
						// transition to win
						pause.play();
						ourStage.setScene(winScene());
					}
					if (clientConnection.guessData.letterGuessesLeft == 0 && clientConnection.guessData.wordGuessesLeft == 0) {
						// update cat attempts left text
						// if zero cat attempts left then game over
						int currentAttemptsLeft = 3;
						if (clientConnection.guessData.catChoice.equals("animals")) {
							currentAttemptsLeft = clientConnection.guessData.animalAttempts;
							animalsTries.setText(currentAttemptsLeft +"/3");
						}
						else if (clientConnection.guessData.catChoice.equals("cities")) {
							currentAttemptsLeft = clientConnection.guessData.citiesAttempts;
							citiesTries.setText(currentAttemptsLeft +"/3");
						}
						else if (clientConnection.guessData.catChoice.equals("food")) {
							currentAttemptsLeft = clientConnection.guessData.foodAttempts; 
							foodTries.setText(currentAttemptsLeft +"/3");
						}
						
						if (currentAttemptsLeft == 0) {
							pause.play();
							ourStage.setScene(loseScene());
						} else {
							pause.play();
							ourStage.setScene(gameScene());
						}
					}
				});
			}, Integer.parseInt(portInput.getText()));
			clientConnection.start();
			ourStage.setScene(gameScene());
		});
		
		HBox input = new HBox(direction, portInput, enter);
		input.setAlignment(Pos.CENTER);
		VBox node = new VBox(input, namesBox, instructions);
		node.setAlignment(Pos.CENTER);
		BorderPane bPane = new BorderPane(node);
		Image image1 = new Image("welcome.png");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		bPane.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(bPane, 730, 411);
	}
	
	public boolean containsDash(String s) {
		for (int i = 0; i < s.length(); i++) {
			int compare = Character.compare(s.charAt(i), '-');
			if ( compare == 0) {
				return true;
			}
		}
		return false;
	}

	public Scene gameScene() {
		if (clientConnection.guessData.isConnectionFail) {
			return connectionFailScene();
		}
		animalsButton = new Button("Animals");
		animalsButton.setFont(Font.font(20));
		citiesButton = new Button("Cities");
		citiesButton.setFont(Font.font(20));
		foodButton = new Button("Food");
		foodButton.setFont(Font.font(20));
		
		if (clientConnection.guessData.isAnimalsDone) {
			animalsButton.setDisable(true);
			animalsButton.setStyle("-fx-background-color: green;");
		} else {
			animalsButton.setDisable(false);
			animalsButton.setStyle("-fx-background-color: red;");
		}
		
		if (clientConnection.guessData.isCitiesDone) {
			citiesButton.setDisable(true);
			citiesButton.setStyle("-fx-background-color: green;");
		} else {
			citiesButton.setDisable(false);
			citiesButton.setStyle("-fx-background-color: red;");
		}
		
		if (clientConnection.guessData.isFoodDone) {
			foodButton.setDisable(true);
			foodButton.setStyle("-fx-background-color: green;");
		} else {
			foodButton.setDisable(false);
			foodButton.setStyle("-fx-background-color: red;");
		}
		
		animalsButton.setOnAction(e-> {
			animalsButton.setStyle("-fx-background-color: yellow;");
			animalsButton.setDisable(true);
			citiesButton.setDisable(true);
			foodButton.setDisable(true);
			guessLetter.setDisable(false);
			guessWord.setDisable(false);
			clientConnection.guessData.isCatChoice = true;
			clientConnection.guessData.catChoice = "animals";
			clientConnection.send(clientConnection.guessData);
		});
		
		citiesButton.setOnAction(e-> {
			citiesButton.setStyle("-fx-background-color: yellow;");
			animalsButton.setDisable(true);
			citiesButton.setDisable(true);
			foodButton.setDisable(true);
			guessLetter.setDisable(false);
			guessWord.setDisable(false);
			clientConnection.guessData.isCatChoice = true;
			clientConnection.guessData.catChoice = "cities";
			clientConnection.send(clientConnection.guessData);
		});
		
		foodButton.setOnAction(e-> {
			foodButton.setStyle("-fx-background-color: yellow;");
			citiesButton.setDisable(true);
			animalsButton.setDisable(true);
			foodButton.setDisable(true);
			guessLetter.setDisable(false);
			guessWord.setDisable(false);
			clientConnection.guessData.isCatChoice = true;
			clientConnection.guessData.catChoice = "food";
			clientConnection.send(clientConnection.guessData);
		});
		
		animalsTries = new Text(clientConnection.guessData.animalAttempts + "/3");
		animalsTries.setStyle("-fx-background-color: white;");
		animalsTries.setFont(Font.font(20));
		
		citiesTries = new Text(clientConnection.guessData.citiesAttempts + "/3");
		citiesTries.setStyle("-fx-background-color: white;");
		citiesTries.setFont(Font.font(20));
		
		foodTries = new Text(clientConnection.guessData.foodAttempts + "/3");
		foodTries.setStyle("-fx-background-color: white;");
		foodTries.setFont(Font.font(20));
		
		Text catText = new Text("Categories:");
		catText.setFont(Font.font(20));
		HBox catTextBox = new HBox(catText);
		catTextBox.setPadding(new Insets(10,10,10,0)); 
		Text totalAttempts = new Text("Attempts Left:");
		totalAttempts.setFont(Font.font(20));
		HBox totalAttemptsBox = new HBox(totalAttempts);
		
		VBox labels = new VBox(catTextBox, totalAttemptsBox);
		labels.setAlignment(Pos.CENTER);
		VBox animalsNodes = new VBox(animalsButton, animalsTries);
		animalsNodes.setAlignment(Pos.CENTER);
		VBox citiesNodes = new VBox(citiesButton, citiesTries);
		citiesNodes.setAlignment(Pos.CENTER);
		VBox foodNodes = new VBox(foodButton, foodTries);
		foodNodes.setAlignment(Pos.CENTER);
		
		HBox catNodes = new HBox(labels, animalsNodes, citiesNodes, foodNodes);
		catNodes.setAlignment(Pos.CENTER);
		
		Text prompt = new Text("WORD TO GUESS:   ");
		prompt.setFont(Font.font(25));
		wordToGuess = new Text("-PICK A CATEGORY-");
		wordToGuess.setFont(Font.font(25));
		HBox guessTexts = new HBox(prompt, wordToGuess);
		guessTexts.setAlignment(Pos.CENTER);
		
		guessLetter = new TextField();
		guessLetter.setPromptText("Enter a letter");
		guessLetter.setFont(Font.font(15));
		submitLetter = new Button("Guess Letter!");
		submitLetter.setDisable(true);
		submitLetter.setFont(Font.font(15));
		HBox letterFields = new HBox(guessLetter, submitLetter);
		letterFields.setAlignment(Pos.CENTER);
		
		submitLetter.setOnAction(e-> {
			clientConnection.guessData.guessLetter = guessLetter.getText().toLowerCase().charAt(0);
			clientConnection.guessData.isGuessLetter = true;
			clientConnection.send(clientConnection.guessData);
		});

		guessWord = new TextField();
		guessWord.setPromptText("Enter a word");
		guessWord.setFont(Font.font(15));
		submitWord = new Button("Guess Word!");
		submitWord.setDisable(true);
		submitWord.setFont(Font.font(15));
		HBox wordFields = new HBox(guessWord, submitWord);
		wordFields.setAlignment(Pos.CENTER);
		
		submitWord.setOnAction(e-> {
			clientConnection.guessData.guessWord = guessWord.getText().toLowerCase();
			clientConnection.guessData.isGuessWord = true;
			clientConnection.send(clientConnection.guessData);
		});
		
		letterGuessLeft = new Text("Letter Guesses Left: 6/6 \t");
		letterGuessLeft.setFont(Font.font(20));
		wordGuessLeft = new Text("Word Guesses Left: 3/3");
		wordGuessLeft.setFont(Font.font(20));
		HBox guessLeft = new HBox(letterGuessLeft, wordGuessLeft);
		guessLeft.setAlignment(Pos.CENTER);
		
		Text catHeading = new Text("Hangman");
		catHeading.setFont(Font.font(45));
		HBox headingBox = new HBox(catHeading);
		headingBox.setAlignment(Pos.CENTER);
		headingBox.setPadding(new Insets(0,0,15,0)); 


		VBox gameElements = new VBox(headingBox, catNodes, guessTexts, letterFields, wordFields, guessLeft);
		gameElements.setAlignment(Pos.CENTER);
		BorderPane bPane = new BorderPane(gameElements);
		Image image1 = new Image("main.gif");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		bPane.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(bPane, 500, 500);
	}
	
	public Scene winScene() {
		Text message = new Text("Yeppi You Won!!");
		message.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		
		BorderPane win = new BorderPane(message);
		
		Image image1 = new Image("winning.gif");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		win.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(win, 716, 605);
	}
	
	public Scene loseScene() {
		Text message = new Text("I am sorry! You Lose");
		message.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		
		BorderPane lose = new BorderPane(message);
		
		Image image1 = new Image("losing.gif");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		lose.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		
		return new Scene(lose, 650, 650);
	}
	
	public Scene connectionFailScene() {
		Text error = new Text("Error connecting to server.");
		
		HBox hAlign = new HBox(error);
		hAlign.setAlignment(Pos.CENTER);
		VBox vAlign = new VBox(hAlign);
		vAlign.setAlignment(Pos.CENTER);
		
		BorderPane bPane = new BorderPane(vAlign);
		Image image1 = new Image("sadface.gif");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		
		bPane.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(bPane, 512, 512);
	}

}
