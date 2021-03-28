// Client GUI
// 		Thread for connecting a client to server 
// 		Uses Javafx for the GUI and Client for connection to server
// Angel Cardenas		651018873		acarde36
// Kartik Maheshwari	665023848		kmahes5
//

import java.util.ArrayList;

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
	Client clientConnection;	// initializing a client thread
	// Javafx nodes
	Stage ourStage;				// hold current stage to be displayed
	Button animalsButton, citiesButton, foodButton, submitLetter, submitWord;					// all the buttons on the client game screen
	Text animalsTries, citiesTries, foodTries, wordToGuess, letterGuessLeft, wordGuessLeft;		// all the text on the client game screen
	TextField guessLetter, guessWord;		// all the text boxes on the client game screen.
	PauseTransition pause;	// initialized for having a pause after each lose/win
	
	
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
		ourStage.setTitle("Welcome to Hangman Client");
		pause = new PauseTransition(Duration.seconds(2));
				
		ourStage.setScene(welcomeScene());
		ourStage.show();
	}
	
	/*returns the welcome screen with predetermined settings*/
	public Scene welcomeScene() {
		// initializing all the Javafx components on the welcome screen.
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
		
		// working of the dialog box
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
		
		// when login is pressed on welcome screen
		// After the login is pressed we wait to set the flags in serializable object
		enter.setOnAction(e-> {
			ourStage.setTitle("Hangman Game");
			clientConnection = new Client(data->{
				Platform.runLater(()->{
					clientConnection.guessData = (SerializableWord) data;
					if (clientConnection.guessData.isReplay) {
						clientConnection.guessData.isReplay = false;
						ourStage.setScene(gameScene());
					}
					// when client inputs an incorrect port number
					if (clientConnection.guessData.isConnectionFail) {
						ourStage.setScene(connectionFailScene());
					}
					// when client picks a category
					if (clientConnection.guessData.isCatChoice) {
						if (clientConnection.guessData.catChoice.equals("animals")) {
							animalsTries.setText(clientConnection.guessData.animalAttempts +"/3");
						}
						else if (clientConnection.guessData.catChoice.equals("cities")) {
							citiesTries.setText(clientConnection.guessData.citiesAttempts +"/3");
						}
						else if (clientConnection.guessData.catChoice.equals("food")) {
							foodTries.setText(clientConnection.guessData.foodAttempts +"/3");
						}
						
						wordToGuess.setText(emptyWord(clientConnection.guessData.wordSize));
						submitLetter.setDisable(false);
						submitWord.setDisable(false);
						clientConnection.guessData.isCatChoice = false;
					}
					// when client guesses a letter
					else if (clientConnection.guessData.isGuessLetter) {
						if (clientConnection.guessData.isLetterCorrect) {
							wordToGuess.setText(updateWord(clientConnection.guessData.guessLetter, clientConnection.guessData.letterPositions));
							clientConnection.guessData.isLetterCorrect = false;
						}
						clientConnection.guessData.isGuessLetter = false;
						letterGuessLeft.setText("Letter Guesses Left: " + clientConnection.guessData.letterGuessesLeft + "/6 \t");
						// when client is out of letter guesses for the current word
						if (clientConnection.guessData.letterGuessesLeft == 0) {
							submitLetter.setDisable(true);
						}
					}
					// when client guesses a word
					else if (clientConnection.guessData.isGuessWord) {
						if (clientConnection.guessData.isWordCorrect) {
							wordToGuess.setText(clientConnection.guessData.guessWord);
							clientConnection.guessData.isWordCorrect = false;
						}
						wordGuessLeft.setText("Word Guesses Left: " + clientConnection.guessData.wordGuessesLeft + "/3");
						clientConnection.guessData.isGuessWord = false;
						// when client is out of word guesses for the current word
						if (clientConnection.guessData.wordGuessesLeft == 0) {
							submitWord.setDisable(true);
						}
					}
					// when client finished the word, the game gets updated
					if (!containsDash(wordToGuess.getText())) {
						submitLetter.setDisable(true);
						submitWord.setDisable(true);
						
						pause.setOnFinished(t-> {
							wordToGuess.setText(wordToGuess.getText());
							ourStage.setScene(gameScene());
							submitLetter.setDisable(false);
							submitWord.setDisable(false);
						});
						pause.play();
					}
					// when all three categories words are solved, we go to the win screen
					if (clientConnection.guessData.isAnimalsDone &&
							clientConnection.guessData.isCitiesDone&&
							clientConnection.guessData.isFoodDone) {
						// transition to win
						submitLetter.setDisable(true);
						submitWord.setDisable(true);
						
						pause.setOnFinished(t-> {
							wordToGuess.setText(wordToGuess.getText());
							ourStage.setScene(winScene());
							submitLetter.setDisable(false);
							submitWord.setDisable(false);
						});
						pause.play();
					}
					// when client runs out of guesses for the current word
					if (clientConnection.guessData.letterGuessesLeft == 0 && clientConnection.guessData.wordGuessesLeft == 0) {
						// update category attempts left text
						int currentAttemptsLeft = 3;	// counter for attempts for the current word.
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
						// if zero category attempts left then game over
						if (currentAttemptsLeft == 0) {
							submitLetter.setDisable(true);
							submitWord.setDisable(true);
							
							pause.setOnFinished(t-> {
								ourStage.setScene(loseScene());
								submitLetter.setDisable(false);
								submitWord.setDisable(false);
							});
							pause.play();
						} else {
							submitLetter.setDisable(true);
							submitWord.setDisable(true);
							
							pause.setOnFinished(t-> {
								submitLetter.setDisable(false);
								submitWord.setDisable(false);
								wordToGuess.setText(wordToGuess.getText());
								ourStage.setScene(gameScene());
							});
							pause.play();
						}
					}
				});
			}, Integer.parseInt(portInput.getText()));		// end of line 113
			clientConnection.start();			// after login is pressed a connection is established
			ourStage.setScene(gameScene());		// the game scene is set
		});
		
		// Creating and grouping Javafx components in an orderly manner
		HBox input = new HBox(direction, portInput, enter);
		input.setAlignment(Pos.CENTER);
		VBox node = new VBox(input, namesBox, instructions);
		node.setAlignment(Pos.CENTER);
		BorderPane bPane = new BorderPane(node);
		
		// set background as welcome.png
		Image image1 = new Image("welcome.png");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		bPane.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(bPane, 730, 411);
	}
	
	/*Helper function that checks if there are more characters left in the word to guess*/
	public boolean containsDash(String s) {
		for (int i = 0; i < s.length(); i++) {
			int compare = Character.compare(s.charAt(i), '-');
			if ( compare == 0) {
				return true;
			}
		}
		return false;
	}
	
	/*Helper function that creates a string of empty words i.e. "-"*/
	public String emptyWord(int size) {
		String empty = "";
		for (int i = 0; i < size; i++) {
			empty += "-";
		}
		return empty;
	}
	
	/*Helper function that changes wordToGuess according to user guesses*/
	public String updateWord(char c, ArrayList<Integer> ints) {
		String newWord = "";
		int j = 0;
		
		for (int i = 0; i < wordToGuess.getText().length(); i++) {
			if (j < ints.size()) {
				if (i == ints.get(j)) {
					newWord += c;
					j++;
				} else {
					newWord += wordToGuess.getText().charAt(i);
				}
			} else {
				newWord += wordToGuess.getText().charAt(i);
			}
		}
		
		return newWord;
	}

	/*returns the game screen with predetermined settings*/
	public Scene gameScene() {
		// checks the connection flag and accordingly changes the screen to fail connection.
		if (clientConnection.guessData.isConnectionFail) {
			return connectionFailScene();
		}
		// setting up category buttons
		animalsButton = new Button("Animals");
		animalsButton.setFont(Font.font(20));
		citiesButton = new Button("Cities");
		citiesButton.setFont(Font.font(20));
		foodButton = new Button("Food");
		foodButton.setFont(Font.font(20));
		
		// Disabling the button and changing the background color as per to the current game situation
		// for animals
		if (clientConnection.guessData.isAnimalsDone) {
			animalsButton.setDisable(true);
			animalsButton.setStyle("-fx-background-color: green;");
		} else {
			animalsButton.setDisable(false);
			animalsButton.setStyle("-fx-background-color: red;");
		}
		// for cities
		if (clientConnection.guessData.isCitiesDone) {
			citiesButton.setDisable(true);
			citiesButton.setStyle("-fx-background-color: green;");
		} else {
			citiesButton.setDisable(false);
			citiesButton.setStyle("-fx-background-color: red;");
		}
		// for food
		if (clientConnection.guessData.isFoodDone) {
			foodButton.setDisable(true);
			foodButton.setStyle("-fx-background-color: green;");
		} else {
			foodButton.setDisable(false);
			foodButton.setStyle("-fx-background-color: red;");
		}
		
		// when animal category is picked, certain flags are set.
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
		// when city category is picked, certain flags are set.
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
		// when food category is picked, certain flags are set.
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
		
		// initializing the attempts left for each categories
		animalsTries = new Text(clientConnection.guessData.animalAttempts + "/3");
		animalsTries.setStyle("-fx-background-color: white;");
		animalsTries.setFont(Font.font(20));
		
		citiesTries = new Text(clientConnection.guessData.citiesAttempts + "/3");
		citiesTries.setStyle("-fx-background-color: white;");
		citiesTries.setFont(Font.font(20));
		
		foodTries = new Text(clientConnection.guessData.foodAttempts + "/3");
		foodTries.setStyle("-fx-background-color: white;");
		foodTries.setFont(Font.font(20));
		
		// setting up Javafx elements on the game screen and grouping them in orderly manner  
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
		
		// when user guesses a character and hits guess, we get the word from the text field and set flags
		submitLetter.setOnAction(e-> {
			if (guessLetter.getText().equals("")) {
				clientConnection.guessData.guessLetter = '\0';
			} else {
				clientConnection.guessData.guessLetter = guessLetter.getText().toLowerCase().charAt(0);
			}
			clientConnection.guessData.isGuessLetter = true;
			clientConnection.send(clientConnection.guessData);
		});

		// setting up Javafx elements on the game screen and grouping them in orderly manner 
		guessWord = new TextField();
		guessWord.setPromptText("Enter a word");
		guessWord.setFont(Font.font(15));
		submitWord = new Button("Guess Word!");
		submitWord.setDisable(true);
		submitWord.setFont(Font.font(15));
		HBox wordFields = new HBox(guessWord, submitWord);
		wordFields.setAlignment(Pos.CENTER);
		
		// when user guesses a word and hits guess, we get the word from the text field and set flags
		submitWord.setOnAction(e-> {
			clientConnection.guessData.guessWord = guessWord.getText().toLowerCase();
			clientConnection.guessData.isGuessWord = true;
			clientConnection.send(clientConnection.guessData);
		});
		
		// setting up Javafx elements on the game screen and grouping them in orderly manner 
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
		
		// set background as main.gif
		Image image1 = new Image("main.gif");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		bPane.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(bPane, 500, 500);
	}
	
	/*returns the winning screen with predetermined settings*/
	public Scene winScene() {
		Text message = new Text("Yeppi You Won!!");
		message.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		
		Button replay = new Button("Replay?");
		Button quit = new Button("Quit.");
		
		replay.setOnAction(e-> {
			clientConnection.guessData.isReplay = true;
			clientConnection.send(clientConnection.guessData);
		});
		
		quit.setOnAction(e-> {
			Platform.exit();
            System.exit(0);
			ourStage.close();
		});
		
		HBox options = new HBox(replay, quit);
		options.setAlignment(Pos.CENTER);
		
		VBox alignment = new VBox(message, options);
		alignment.setAlignment(Pos.CENTER);
		
		BorderPane win = new BorderPane(alignment);
		
		// set background as winning.gif
		Image image1 = new Image("winning.gif");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		win.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(win, 716, 605);
	}
	
	/*returns the losing screen with predetermined settings*/
	public Scene loseScene() {
		Text message = new Text("I am sorry! You Lose");
		message.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		
		Button replay = new Button("Replay?");
		Button quit = new Button("Quit.");
		
		replay.setOnAction(e-> {
			clientConnection.guessData.isReplay = true;
			clientConnection.send(clientConnection.guessData);
		});
		
		quit.setOnAction(e-> {
			Platform.exit();
            System.exit(0);
			ourStage.close();
		});
		
		HBox options = new HBox(replay, quit);
		options.setAlignment(Pos.CENTER);
		
		VBox alignment = new VBox(message, options);
		alignment.setAlignment(Pos.CENTER);
		
		BorderPane lose = new BorderPane(alignment);
		
		// set background as losing.gif
		Image image1 = new Image("losing.gif");
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		lose.setBackground(new Background(new BackgroundImage(image1,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundRepeat.NO_REPEAT,
	            BackgroundPosition.CENTER,
	            bSize)));
		
		return new Scene(lose, 650, 650);
	}
	
	/*returns the connection fail screen with predetermined settings*/
	public Scene connectionFailScene() {
		Text error = new Text("Error connecting to server.");
		HBox hAlign = new HBox(error);
		hAlign.setAlignment(Pos.CENTER);
		VBox vAlign = new VBox(hAlign);
		vAlign.setAlignment(Pos.CENTER);
		BorderPane bPane = new BorderPane(vAlign);
		
		// set background as sadface.gif
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
