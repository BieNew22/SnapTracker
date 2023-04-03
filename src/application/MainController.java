/**
 * Writer - 안학룡(BieNew22)
 * Role of this file
 * 				- control Main.fxml
 * Date of latest update - 2023.04.03
 */


package application;

import com.fasterxml.jackson.databind.node.ArrayNode;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public class MainController {
	
	@FXML
	private Button menu;
	
	@FXML
	private Label deckName;
	
	@FXML
	private Label gameCount;
	
	@FXML
	private Label winCount;
	
	@FXML
	private Label loseCount;
	
	@FXML
	private Label tieCount;
	
	@FXML
	private Label getCubes;
	
	@FXML
	private ListView<BorderPane> gameLogs;
	
	@FXML
	public void initialize() {
		Database db = Database.getInstance();
		deckName.setText("Total");
		
		displayDeckInfor();
		
		ArrayNode reString = db.getDeckLog("Total");
	}
	
	public void displayDeckInfor() {
		Database db = Database.getInstance();
		
		String deckId = deckName.getText();
		gameCount.setText("Games : " + db.getDeckInfor(deckId, Database.TOTAL));
		winCount.setText("Wins : " + db.getDeckInfor(deckId, Database.WIN));
		loseCount.setText("Loses : " + db.getDeckInfor(deckId, Database.LOSE));
		tieCount.setText("Tie : " + db.getDeckInfor(deckId, Database.TIE));
		getCubes.setText("Cubes : " + db.getDeckInfor(deckId, Database.CUBE));
	}
}
