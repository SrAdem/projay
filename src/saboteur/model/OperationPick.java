package saboteur.model;

import saboteur.model.Card.*;

public class OperationPick extends Operation {

	private static final long serialVersionUID = -2773080524392347028L;
	private Card cardPicked; //TO SAVE
	
	public OperationPick(Player sourcePlayer, Card cardPicked) {
		super(sourcePlayer, null);
		this.cardPicked = cardPicked;
	}
	
	@Override
	public void exec(Game game) {
		this.getSourcePlayer().addHandCard(this.cardPicked);
		
		//In situation where we do a redo
		if (game.observeFirstCard() == this.cardPicked){
			game.pick();
		}
	}

	@Override
	public void execReverse(Game game) {
		game.addCardToStack(this.cardPicked);
		
		this.getSourcePlayer().removeHandCard(this.cardPicked);
	}

	@Override
	public boolean isOperationPick(){
		return true;
	}

	public Card getCardPicked(){
		return this.cardPicked;
	}
}
