/**
 * Writer - 안학룡(BieNew22)
 * Role of this file
 * 				- Just like DB manager
 *              - Update user DB
 *              - Read and manage game DB
 * Date of latest update - 2023.03.29
 */

package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;


class Deck {
	public String id = null;
	public String name = null;
	public ArrayList<String> cardList = new ArrayList<>();
	
	public Deck(JsonNode data) {
		id = data.get("Id").asText("Error");
		name = data.get("Name").asText("Error");
		
		if (id.equals("Error") || name.equals("Error")) {
			System.out.println("deck data error");
			System.exit(1);
		}
		
		ArrayNode cards = (ArrayNode) data.get("Cards");
		for (int i = 0; i < cards.size(); i++) {
			cardList.add(new String(cards.get(i).get("CardDefId").asText("Erro")));
		}
		
		// test print
		System.out.println(id + " " + name);
		for (int i = 0 ; i < cardList.size(); i++) {
			System.out.println(cardList.get(i));
		}
	}
}

public class Database {
	private static Database instance = new Database();
	
	private JsonNode userDB = null;
	
	private ArrayList<Deck> deckList = new ArrayList<>();
	
	private String userSelectedDeck = null; //PlayState.json -> SelectedDeckId
	
	private Database() {}
	
	//basic folder : C:\Users\name\AppData\LocalLow\Second Dinner\SNAP\Standalone\States\nvprod
	public static Database getInstance() {
		return instance;
	}

	public void init() {
		ObjectMapper mapper = new ObjectMapper();
		
		// initialization user userDB information
		userDB = openJson(mapper, "./src/db.json");
		
		System.out.println(userDB);
		System.out.println(userDB.at("/deck_name/infor/total").asInt());
		
		//--- initialization data from marvel snap AppData ---//
		String dir =  System.getProperty("user.dir").split("Desktop")[0] + "AppData/LocalLow/Second Dinner/"
				+ "SNAP/Standalone/States/nvprod";
		//test directory
		dir =  System.getProperty("user.dir").split("Desktop")[0] + "Downloads";
		
		// 1. read deck information
		// open & and get Deck list(ArrayNode)
		JsonNode deckDB = openJson(mapper, dir + "/CollectionState.json");
		
		ArrayNode deckNode = (ArrayNode) deckDB.at("/ServerState/Decks");
		for (int i = 0; i < deckNode.size(); i++) {
			deckList.add(new Deck(deckNode.get(i)));
		}
	}
	
	private JsonNode openJson(ObjectMapper mapper, String dir) {
		File file = new File(dir);
		
		JsonNode res = null;
		
		try {
			res = mapper.readTree(file);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return res;
	}
}
