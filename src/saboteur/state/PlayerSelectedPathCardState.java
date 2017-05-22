package saboteur.state;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Board;
import saboteur.model.Game;
import saboteur.model.Position;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;
import saboteur.tools.Icon;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;

public class PlayerSelectedPathCardState extends State{

	private GameBoardGridPane gameBoardGridPane;
	private List<Position> possiblePositionList;
	private List<Object> boardEffect;
	private HashMap<Object, Position> possiblePositions;
	private Card selectedCard;
	private boolean positionSelected;
	
	
    public PlayerSelectedPathCardState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
        System.out.println("path card");
        double cardWidth = 108/3;
        double cardHeight = 166/3;
        

        this.selectedCard = (Card) param;
        this.gameBoardGridPane = (GameBoardGridPane) this.primaryStage.getScene().lookup("#gameBoardGridPane");
        this.gameBoardGridPane.toFront();
        this.boardEffect = new LinkedList<>();
		this.possiblePositions = new HashMap<Object, Position>();
		this.positionSelected = false;
        
		EventHandler<MouseEvent> event = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectPositionOnBoard(event);
			}
		};
		
		
		if(this.selectedCard.isCollapseCard()) {
			Map<Position, PathCard> pathCardsPosition = this.game.getBoard().getPathCardsPosition();
			//If the map content more than the 1st path card (entry)
			if(pathCardsPosition.size() > 1) {
				pathCardsPosition.remove(Board.getStart());
				
				for(Position posiCard : pathCardsPosition.keySet()) {
					SVGPath svg = new SVGPath();
					svg.setFill(Color.RED);
					svg.setContent(Icon.minus);
					GridPane.setHalignment(svg, HPos.CENTER);
					this.boardEffect.add(svg);
					
					this.possiblePositions.put(svg, posiCard);
//					this.possiblePositions.put(this.gameBoardGridPane. ,posiCard);
					
					this.gameBoardGridPane.add(svg, posiCard.getcX(), posiCard.getcY());
				}
				this.gameBoardGridPane.setOnMouseClicked(event);
			}
			
		}
		else {
			PathCard card  = (PathCard) this.selectedCard;
			this.possiblePositionList = this.game.getBoard().getPossiblePositionPathCard(card);
			for(Position posiCard : this.possiblePositionList) {
				SVGPath svg = new SVGPath();
				svg.setFill(Color.GREEN);
				svg.setContent(Icon.plus);
				GridPane.setHalignment(svg, HPos.CENTER);
				
				ImageView img = new ImageView("/resources/cards/"+card.getFrontImage());	
				img.setFitHeight(cardHeight);
				img.setFitWidth(cardWidth);
				img.setOpacity(0);
				
				this.boardEffect.add(svg);
				this.boardEffect.add(img);
				this.possiblePositions.put(img, posiCard);

				this.gameBoardGridPane.add(svg, posiCard.getcX(), posiCard.getcY());
				this.gameBoardGridPane.add(img, posiCard.getcX(), posiCard.getcY());
			}
			this.gameBoardGridPane.setOnMouseClicked(event);
		}
    }

    @Override
    public void onExit() {
		for(Object obj : this.boardEffect) {
			this.gameBoardGridPane.getChildren().remove(obj);
		}
		
		if(this.positionSelected) {
    		this.game.getCurrentPlayer().getHand().remove(this.selectedCard);
        	
    		//Code : Go to EndOfTurn, generate new hand card image and delete event of the card selection
        	GameCardContainer cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#cardContainer");
        	cardContainer.setOnMouseClicked(null);
        	cardContainer.generateHandCardImage(); 
        	this.gsm.push("playerEndOfTurn");
    	}
		
		this.gameBoardGridPane.setOnMouseClicked(null);
    }
    
    private void selectPositionOnBoard(MouseEvent event) {
    	if(event.getTarget() instanceof ImageView || event.getTarget() instanceof SVGPath) {
    		Position position = this.possiblePositions.get(event.getTarget());
    		if(this.selectedCard.isCollapseCard()) {
    			this.gameBoardGridPane.removeCardOfBoard(position);
    		}
    		else {
        		this.gameBoardGridPane.addCardToBoard((PathCard)this.selectedCard, position);
    		}
    		this.positionSelected = true;
    		this.gsm.pop();
    	}
    }
    
}
