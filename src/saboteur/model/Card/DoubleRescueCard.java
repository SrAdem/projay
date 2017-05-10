package saboteur.model.Card;

public class DoubleRescueCard extends ActionCardToPlayer {
	public Tool rescueType1;
	public Tool rescueType2;
	
	public DoubleRescueCard(Tool type1, Tool type2){
		this.rescueType1 = type1;
		this.rescueType2 = type2;
	}

	public Tool getRescueType1() {
		return this.rescueType1;
	}

	public Tool getRescueType2() {
		return this.rescueType2;
	}
	
	
}
