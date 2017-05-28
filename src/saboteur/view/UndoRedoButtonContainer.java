package saboteur.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import saboteur.model.Game;
import saboteur.tools.Icon;

public class UndoRedoButtonContainer extends HBox{

	private Game game;
	private Button undoButton;
	private Button redoButton;

	public UndoRedoButtonContainer(Game game) {
		this.game = game;
		this.undoButton = new Button(" Undo");
		this.redoButton = new Button(" Redo");
		
		SVGPath undo = new SVGPath();
		undo.setFill(Color.BLACK);
		undo.setContent(Icon.undo);
		this.undoButton.setGraphic(undo);
		
		SVGPath redo = new SVGPath();
		redo.setFill(Color.BLACK);
		redo.setContent(Icon.redo);
		this.redoButton.setGraphic(redo);
		
		this.undoButton.setPrefHeight(50.0);
		this.undoButton.setPrefWidth(100.0);
		
		this.redoButton.setPrefHeight(50.0);
		this.redoButton.setPrefWidth(100.0);
				
		this.setSpacing(10);
		this.getChildren().addAll(this.undoButton, this.redoButton);
	}
	
    public void manageUndoRedoButton(){
        if(this.game.historyUndoIsEmpty()) {
            this.disableUndoButton();
        }
        else {
            this.enableUndoButton();
        }

        if(this.game.historyRedoIsEmpty()) {
            this.disableRedoButton();
        }
        else {
        	this.enableRedoButton();
        }
    }
    
    public void setUndoButtonAction(EventHandler<ActionEvent> event) {
    	this.undoButton.setOnAction(event);
    }
    
    public void setRedoButtonAction(EventHandler<ActionEvent> event) {
    	this.redoButton.setOnAction(event);
    }
    
	public void disableUndoButton() {
		this.undoButton.setDisable(true);
	}
	
	public void enableUndoButton() {
		this.undoButton.setDisable(false);
	}
	
	public void disableRedoButton() {
		this.redoButton.setDisable(true);
	}
	
	public void enableRedoButton() {
		this.redoButton.setDisable(false);
	}
}
