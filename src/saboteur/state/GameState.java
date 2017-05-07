package saboteur.state;

import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;

public class GameState implements State{

    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    public GameState(GameStateMachine gsm, Game game, Stage primaryStage){
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

    }

    @Override
    public void onExit() {

    }
}