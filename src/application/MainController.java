/**
 * Writer - 안학룡(BieNew22)
 * Role of this file
 * 				- control Main.fxml
 * Date of latest update - 2023.04.02
 */


package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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
	private Label getCubes;
	
	@FXML
	private ListView<String> gameLogs;
	
	@FXML
	public void initialize() {
		deckName.setText("Total");
		
		Database db = Database.getInstance();
		
		for (int i = 0; i < 100; i++) {
			gameLogs.getItems().add("smaple" + i);
		}
		for (String id: db.getDeckIds()) {
			gameLogs.getItems().add(id);
		}
	}
}
