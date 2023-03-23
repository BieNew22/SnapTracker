package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class MainController {
	
	@FXML
	private Button menu;
	
	@FXML
	private Label deckName;
	
	@FXML
	private Text gameCount;
	
	@FXML
	private Text winCount;
	
	@FXML
	private Text loseCount;
	
	@FXML
	private Text getCubes;
	
	@FXML
	public void initialize() {
		deckName.setText("Hello world is basic!");
	}
}
