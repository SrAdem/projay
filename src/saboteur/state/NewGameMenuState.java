package saboteur.state;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.ai.TemporarAI;
import saboteur.model.Game;
import saboteur.model.Human;
import saboteur.model.Player;
import saboteur.view.NewPlayerHBox;

public class NewGameMenuState implements State{
	
	@FXML private VBox playerContainer;
	
	private int nbPlayer = 3;
    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    public NewGameMenuState(GameStateMachine gsm, Game game, Stage primaryStage){
        this.gsm = gsm;
        this.game = game;
        this.primaryStage = primaryStage;
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/newGameMenu.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            addPlayer(1, true);
            addPlayer(2, false);
            addPlayer(3, false);
            this.primaryStage.setScene(scene);
            this.primaryStage.setFullScreen(true);
            this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {

    }
    
    @FXML
    private void backButtonAction() {
    	this.gsm.change("mainMenu");
    }
    
    @FXML
    private void addPlayerButtonAction() {
    	if(this.nbPlayer < 10) {
    		nbPlayer++;
        	EventHandler<MouseEvent> deleteEvent = new EventHandler<MouseEvent>(){
				public void handle(MouseEvent event) {
					nbPlayer--;
					Pane p = (Pane)event.getSource();
                    NewPlayerHBox h = (NewPlayerHBox)p.getParent();
					playerContainer.getChildren().remove(h);
			    }
			};
        	addPlayer(nbPlayer, false, deleteEvent);
    	}
    }
    
    @FXML
    private void startNewGameButtonAction() {
        for (Node n: this.playerContainer.getChildren()) {
            NewPlayerHBox playerBox = (NewPlayerHBox) n;
            String playerName = playerBox.getPlayerName().getCharacters().toString();
            String playerType = playerBox.getSelectPlayerMenu().getValue();
            Player player;
            if (playerType.equals("Humain")){
                player = new Human(this.game);
            } else{
                player = new TemporarAI(this.game, "temp");
            }
            player.setName(playerName);
            this.game.addPlayer(player);
            this.gsm.change("game");
        }
        this.gsm.change("game");
    }
    
    private void addPlayer(int num, boolean b) {
    	NewPlayerHBox newPlayer = new NewPlayerHBox(num, b);
    	this.playerContainer.getChildren().add(newPlayer);
    }
    
    private void addPlayer(int num, boolean b, EventHandler<MouseEvent> event) {
    	NewPlayerHBox newPlayer = new NewPlayerHBox(num, b);
    	newPlayer.setRemovePlayerAction(event);
    	this.playerContainer.getChildren().add(newPlayer);
    }
}
