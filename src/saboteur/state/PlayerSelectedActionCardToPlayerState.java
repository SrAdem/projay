package saboteur.state;

import java.util.LinkedList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.model.Card.ActionCardToPlayer;
import saboteur.model.Card.Card;
import saboteur.model.Card.DoubleRescueCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;
import saboteur.model.Card.Tool;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;

public class PlayerSelectedActionCardToPlayerState extends State{
	
	private Card selectedCard;
	private ActionCardToPlayer card;
	private PlayerArc playersArc;
	private Button endOfTurnButton;
	private LinkedList<Player> playerList;	
	private int toolValue1 = -1;
	private int toolValue2 = -1;
	private boolean playerSelected;

    public PlayerSelectedActionCardToPlayerState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        System.out.println("action card");
        
        this.selectedCard = (Card) param;
    	this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
    	this.playerSelected = false;
        
    	EventHandler<MouseEvent> mouseEvent = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectedActionCardToPlayer(event);
			}
		};
    	
        //Put a correct value on toolValue depending of the selected card.
        this.toolValue1 = -1;
        this.toolValue2 = -1;
        this.card = (ActionCardToPlayer) this.selectedCard;
        if(this.selectedCard.isSabotageCard()) {
        	this.toolValue1 = ((SabotageCard)this.selectedCard).getSabotageType().getValue();
        	this.playerList = this.game.getPlayers(this.card);
			for(Player p : this.game.getPlayers(this.card)) {
				this.playersArc.getCircles(p)[this.toolValue1].toFront();
				this.playersArc.getCircles(p)[this.toolValue1].setStroke(Color.RED);
				this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
			}
		}
    	else if(this.selectedCard.isRescueCard()) {
    		this.toolValue1 = ((RescueCard)this.selectedCard).getTool().getValue();
        	this.playerList = this.game.getPlayers(this.card);
			for(Player p : this.game.getPlayers(this.card)) {
					this.playersArc.getCircles(p)[toolValue1].setStroke(Color.GREEN);
					this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
			}
		}
    	else if(this.selectedCard.isDoubleRescueCard()) {
    		
    		this.toolValue1 = ((DoubleRescueCard)this.selectedCard).getTool1().getValue();
    		this.toolValue2 = ((DoubleRescueCard)this.selectedCard).getTool2().getValue();
        	this.playerList = this.game.getPlayers(this.card);
        	for(Player p : this.game.getPlayers( new RescueCard(this.intToTool(this.toolValue1)))) {
				this.playersArc.getCircles(p)[this.toolValue1].setStroke(Color.GREEN);
				this.playersArc.getCircles(p)[this.toolValue1].setOnMouseClicked(mouseEvent);
        	}
        	for(Player p : this.game.getPlayers( new RescueCard(this.intToTool(this.toolValue2)))) {
				this.playersArc.getCircles(p)[this.toolValue2].setStroke(Color.GREEN);
				this.playersArc.getCircles(p)[this.toolValue2].setOnMouseClicked(mouseEvent);
        	}
		}
    }

    @Override
    public void onExit() {
    	if(!this.playerSelected) {
        	//Delete event on click
            for(int i = 0; i < this.game.getPlayerList().size(); i++) {
    	    	this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue1].setOnMouseClicked(null);
    	    	if(this.toolValue2 != -1){
    	    		this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue2].setOnMouseClicked(null);
    	    	}
            }
            
    		for(Player p : this.playerList) {
    			for(int i = 0; i < 3; i++) {
    				this.playersArc.getCircles(p)[i].setStroke(Color.BLACK);
    			}
    		}
    	}
    }
    
    private void selectedActionCardToPlayer(MouseEvent event) {    	
    	if(event.getTarget() instanceof Circle) {
            if(this.selectedCard.isSabotageCard()) {
            	Circle circle = (Circle) event.getTarget();
            	
            	this.playersArc.refreshCircles(circle, this.toolValue1, true);
            	
            	for(Player p : this.game.getPlayers(this.card)) {
            		if( this.playersArc.getCircles(p)[this.toolValue1] == circle )
            			this.game.getCurrentPlayer().playCard(p);
    			}
            	
    		}
        	else {
        		Circle circle = (Circle) event.getTarget();
            	
            	for(Player p : this.game.getPlayers(this.card)) {
            		if( this.playersArc.getCircles(p)[this.toolValue1] == circle ){
            			this.game.getCurrentPlayer().playCard(p, this.intToTool(toolValue1));
                		this.playersArc.refreshCircles(circle, this.toolValue1, false);
            		}
            		if( this.toolValue2 != -1 && this.playersArc.getCircles(p)[this.toolValue2] == circle ) {
            			this.game.getCurrentPlayer().playCard(p, this.intToTool(toolValue2));
                		this.playersArc.refreshCircles(circle, this.toolValue2, false);
            		}
    			}
        	}
    	}
    	this.beforEnd();
    	this.playerSelected = true;
    }
    
    private void beforEnd() {
    	Button trashButton = (Button)this.primaryStage.getScene().lookup("#trashButton");
    	trashButton.setDisable(true);
    	
    	//Delete event on click
        for(int i = 0; i < this.game.getPlayerList().size(); i++) {
	    	this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue1].setOnMouseClicked(null);
	    	if(this.toolValue2 != -1){
	    		this.playersArc.getCircles(this.game.getPlayerList().get(i))[this.toolValue2].setOnMouseClicked(null);
	    	}
        }
        
		for(Player p : this.playerList) {
			for(int i = 0; i < 3; i++) {
				this.playersArc.getCircles(p)[i].setStroke(Color.BLACK);
			}
		}
		
    	this.game.getCurrentPlayer().getHand().remove(this.selectedCard);
    	
		//Code : Go to EndOfTurn, generate new hand card image and delete event of the card selection
    	this.game.getCurrentPlayer().pickCard();
    	GameCardContainer cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#cardContainer");
    	cardContainer.setOnMouseClicked(null);
    	cardContainer.generateHandCardImage(); 
    	
    	this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
    	this.endOfTurnButton.setDisable(false);
    	this.endOfTurnButton.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override public void handle(ActionEvent e) {
    	        endOfTurn();
    	    }
    	});
    }
    private void endOfTurn() {
    	this.endOfTurnButton.setOnAction(null);
    	this.gsm.pop();
	}
    
    private Tool intToTool(int intOfTool) {
    	Tool tool = null;
    	switch(intOfTool) {
    		case 0 :
    			tool = Tool.PICKAXE;
    			break;
    		case 1 :
    			tool = Tool.LANTERN;
    			break;
    		case 2 : 
    			tool = Tool.CART;
    			break;
    	}
    	return tool;
    }
}

