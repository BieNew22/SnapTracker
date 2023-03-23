package application;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Database {
	private static Database instance = new Database();
	
	private JsonNode db = null;
	
	private Database() {}
	
	//basic folder : C:\Users\name\AppData\LocalLow\Second Dinner\SNAP\Standalone\States\nvprod
	public static Database getInstance() {
		return instance;
	}
	
	public void init() {
		
		ObjectMapper mapper = new ObjectMapper();
		
		// initialization user db information
		File dbFile = new File("./src/db.json");
		
		try {
			db = mapper.readTree(dbFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(db);
		System.out.println(db.at("/deck_name/infor/total").asInt());
		
		// initialization data from marvel snap AppData
	}
}
