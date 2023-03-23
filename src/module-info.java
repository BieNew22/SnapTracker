module SnapTracker {
	requires javafx.controls;
	requires javafx.fxml;
	requires com.fasterxml.jackson.databind;
	
	opens application to javafx.graphics, javafx.fxml;
}
