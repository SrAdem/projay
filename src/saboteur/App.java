package saboteur;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import saboteur.model.Game;
import saboteur.state.*;
import javafx.scene.layout.StackPane;
import saboteur.tools.Resources;


public class App extends Application {

    private static Game game;
    private static Stage primaryStage;

    public void run() {
        game = new Game();
        launch();
    }

    public void start(Stage stage) throws Exception {

        primaryStage = stage;
        initStage();

        Resources.loadMusic().play();

        GameStateMachine gsm = new GameStateMachine();
        gsm.add("mainMenu", new MainMenuState(gsm, game, primaryStage));
        gsm.add("newGameMenu", new NewGameMenuState(gsm, game, primaryStage));
        gsm.add("game", new GameState(gsm, game, primaryStage));
        gsm.add("pauseMenu", new PauseMenuState(gsm, game, primaryStage));
        gsm.add("loadGame", new LoadGameState(gsm, game, primaryStage));
        gsm.add("saveGame", new SaveGameState(gsm, game, primaryStage));
        gsm.add("options", new OptionsState(gsm, game, primaryStage));
        gsm.add("gameOptions", new GameOptionsState(gsm, game, primaryStage));
        gsm.add("score", new ScoreState(gsm, game, primaryStage));
        gsm.add("help", new HelpState(gsm, game, primaryStage));
        gsm.add("playerBeginOfTurn", new PlayerBeginOfTurnState(gsm, game, primaryStage));
        gsm.add("playerWait", new PlayerWaitState(gsm, game, primaryStage));
        gsm.add("playerSelectedPath", new PlayerSelectedPathCardState(gsm, game, primaryStage));
        gsm.add("playerSelectedAction", new PlayerSelectedActionCardToPlayerState(gsm, game, primaryStage));
        gsm.add("playerSelectedPlan", new PlayerSelectedPlanCardState(gsm, game, primaryStage));
        gsm.add("playerPlayCard", new PlayerPlayCardState(gsm, game, primaryStage));
        gsm.add("roundIsFinished", new RoundIsFinishedState(gsm, game, primaryStage));
        gsm.add("gameIsFinished", new GameIsFinishedState(gsm, game, primaryStage));

        gsm.change("mainMenu");

        GameLoop gameLoop = new GameLoop(gsm);
        gameLoop.start();
    }

    private void initStage(){
        primaryStage.setTitle("Saboteur");
        primaryStage.getIcons().add(new Image("/resources/pioche.png"));
        StackPane rootLayout = new StackPane();
        rootLayout.getStyleClass().add("root-layout");
        rootLayout.getStylesheets().add(Resources.getStylesheet());

        primaryStage.setScene(new Scene(rootLayout));
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();
    }
}
