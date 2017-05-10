package saboteur.ai;
import saboteur.model.Game;
import saboteur.model.Operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationPathCard;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;

public abstract class AI extends Player {
	
	protected Map<Player,Float> isDwarf;	
	protected Difficulty difficulty;
	protected Map<Position,Float> estimatedGoldCardPosition;
	protected Map<Card, Integer> cardsWeight;
	

	public AI(Game game) {
		super(game);
		isDwarf = new HashMap<Player,Float>();
		for(Player p : game.getPlayerList()){
			isDwarf.put(p, (float) 50);
		}
		if(isSaboteur()){
			isDwarf.put(this, (float) -1073741824);
		}
		else{
			isDwarf.put(this, (float) 1073741824);
		}
		
		this.estimatedGoldCardPosition = new HashMap<Position, Float>();
		for (Position position : game.getBoard().getGoldCards()){
			this.estimatedGoldCardPosition.put(position, 1f/3f);
		}
	}

	@Override
	public void notify(Operation o){
		switch(o.getClass().getName()){
		case "OperationActionCardToBoard":
			updateTrust((OperationActionCardToBoard) o);
			break;
		case "OperationActionCardToPlayer":
			updateTrust((OperationActionCardToPlayer) o);
			break;
		case "OperationPathCard":
			updateTrust((OperationPathCard) o);
			break;
		default:
			System.err.println("Op�ration non reconnue");
		}
	}

	// Collapse card
	public void updateTrust(OperationActionCardToBoard o){
		switch(o.getCard().getClass().getName()){
		case "PlanCard":
			// Nothing to update
			break;
		case "CollapseCard":
			if(((PathCard) o.getCard()).isCulDeSac()){
				isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) + 20);
			}
			else{
				isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) - 20);
			}
			break;
		default:
			System.err.println("Operation ActionCardToBoard undetected");
		}
	}
	
	// Sabotage & Rescue card
	public void updateTrust(OperationActionCardToPlayer o){
		switch(o.getCard().getClass().getName()){
		case "SobotageCard":
			if(isDwarf.get(o.getSourcePlayer()) > isDwarf.get(o.getDestinationPlayer()) && (isDwarf.get(o.getDestinationPlayer()) <= 40)){
				// Ennemies of our ennemies are our allies
				isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) + 10);
			}
			else if(isDwarf.get(o.getSourcePlayer()) <= isDwarf.get(o.getDestinationPlayer()) && (isDwarf.get(o.getDestinationPlayer()) >= 60)){
				// Ennemies of our allies are our ennemies
				isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) - 10);
			}
			break;
		case "RescueCard":
		case "DoubleRescueCard":
			if(!o.getSourcePlayer().equals(o.getDestinationPlayer())){
				if(isDwarf.get(o.getDestinationPlayer()) <= 40){
					// Allies of our ennemies are our ennemies
					isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) - 10);
				}
				else if(isDwarf.get(o.getDestinationPlayer()) >= 60){
					// Allies of our allies are our allies
					isDwarf.put(o.getSourcePlayer(), isDwarf.get(o.getSourcePlayer()) + 10);
				}
			}
			break;
		default:
			System.err.println("Operation ActionCarToPlayer undetected");
		}
	}
	
	// Path card
	public void updateTrust(OperationPathCard o){
		int taxiDistance = this.getGame().getBoard().getTaxiDistance(o.getP(), getEstimatedGoldCardPosition());
		int neighborsAmount = this.getGame().getBoard().numberOfNeighbors(o.getP());
		
		if(((PathCard) o.getCard()).isCulDeSac()){ 
			// The closer the gold card, the heavier is the card.
			// The more neighbors, the merr... heavier is the card.
			isDwarf.put(o.getSourcePlayer(), (float) (isDwarf.get(o.getSourcePlayer()) - (40/(Math.pow(2, taxiDistance)))*(0.75+(neighborsAmount/4)) - 2));
		}
		else{
			isDwarf.put(o.getSourcePlayer(), (float) (isDwarf.get(o.getSourcePlayer()) + 40/(Math.pow(2, taxiDistance)) + 3));
		}
	}
	
	public AI setDifficulty(Difficulty difficulty){
		this.difficulty = difficulty;
		return this;
	}
	
	public Difficulty getDifficulty(){
		return difficulty;
	}
	
	public Position getEstimatedGoldCardPosition(){
		//TODO changer les positions par celles des cartes "objectif"
		if(estimatedGoldCardPosition.get(new Position(0,0)) != null){
			return new Position(0,0);
		}
		if(estimatedGoldCardPosition.get(new Position(1,1)) != null){
			return new Position(1,1);
		}
		return new Position(2,2);
	}
	
	public void changeEstimatedGoldCardPosition(Position p, Boolean b){
		//TODO changer les positions par celles des cartes "objectif"
		if(b){
			this.estimatedGoldCardPosition.put(p, 1f);
			this.setAllEstimatedGoldCardPositionExept(p, 0f);
		}else{
			this.estimatedGoldCardPosition.put(p, 0f);
		}
	}
	
	public void setAllEstimatedGoldCardPositionExept(Position position, Float probability){
		for(Position p : this.estimatedGoldCardPosition.keySet()){
			if(!p.equals(position)){
				this.estimatedGoldCardPosition.put(p, probability);
			}
		}
	}
	
	public void setAllEstimatedGoldCardPositionExept(List<Position> positions, Float probability){
		for(Position position : positions){
			this.setAllEstimatedGoldCardPositionExept(position, probability);
		}
	}
	
	public Position getMostProbableGoldCard(){
		Position[] positions = (Position[]) this.estimatedGoldCardPosition.values().toArray();
		Arrays.sort(positions);
		return positions[0];
	}
	
	public boolean knowsTheGoldCardPosition(){
		return this.estimatedGoldCardPosition.containsValue(1f);
	}
	
	@Override
	public void viewGoalCard(PathCard card){
		changeEstimatedGoldCardPosition(getGame().getBoard().getPositionCard(card), card.hasGold());
	}
	
	public void resetProbabilitiesToPlayEachCard(){
		cardsWeight.clear();
		for(Card c : getHand()){
			cardsWeight.put(c, 0);
		}
	}
	
	public boolean hasThisTypeOfCard(Card c){
		return true;
	}
	
	//TODO move this method somewhere
	public int positiveOrZero(int i){
		if(i>0){
			return i;
		}
		return 0;
	}
}
