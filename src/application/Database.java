/**
 * Writer - 안학룡(BieNew22)
 * Role of this file
 * 				- Just like DB manager
 *              - Update user DB
 *              - Read and manage game DB
 * Date of latest update - 2023.04.03
 */

package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Database {
	private static Database instance = new Database();
	
	// data information type
	public static final String TOTAL = "/infor/total";
	public static final String WIN = "/infor/win";
	public static final String LOSE = "/infor/lose";
	public static final String TIE = "/infor/tie";
	public static final String CUBE = "/infor/cube";
	public static final String LOG = "/log";
	
	// data from user DB
	private ObjectNode userDB = null;
	
	// data from server : deckList, selectedDeckId, matches.
	private ArrayList<Deck> deckList = new ArrayList<>();
	
	private String selectedDeckId = null; //PlayState.json -> SelectedDeckId
	
	private int[] matches = new int[3]; // 0 : win, 1 : lose, 2 : tie -> user to check is play?
	
	// singleton pattern
	private Database() {}
	
	public static Database getInstance() {
		return instance;
	}

	
	//basic folder : C:\Users\name\AppData\LocalLow\Second Dinner\SNAP\Standalone\States\nvprod
	public void init() {
		ObjectMapper mapper = new ObjectMapper();
		
		// initialization user userDB information
		userDB = (ObjectNode) openJson(mapper, "./src/db.json");
		
		//--- initialization data from marvel snap AppData ---//
		String dir =  System.getProperty("user.dir").split("Desktop")[0] + "AppData/LocalLow/Second Dinner/"
				+ "SNAP/Standalone/States/nvprod";
		
		//test directory
		dir =  System.getProperty("user.dir").split("Desktop")[0] + "Downloads";
		
		// 1. read deck information
		JsonNode deckDB = openJson(mapper, dir + "/CollectionState.json");
		
		// get Deck list(ArrayNode)
		ArrayNode deckNode = (ArrayNode) deckDB.at("/ServerState/Decks");
		for (int i = 0; i < deckNode.size(); i++) {
			deckList.add(new Deck(deckNode.get(i)));
		}
		
		// 2. read now user selected deck information
		JsonNode selectDeck = openJson(mapper, dir + "/PlayState.json");
		
		selectedDeckId = selectDeck.get("SelectedDeckId").asText("Error");
		
		// 3. read total number of match (win, lose, tie)
		JsonNode match = openJson(mapper, dir + "/ProfileState.json");
		
		matches[0] = match.at("/ServerState/Account/WinsInPlaytestEnvironment").asInt();
		matches[1] = match.at("/ServerState/Account/LossesInPlaytestEnvironment").asInt();
		matches[2] = match.at("/ServerState/Account/TiesInPlaytestEnvironment").asInt();
		
		// sync db.json and server deck info and save
		syncDatabase(mapper);
		storeUserDB();
	}
	
	// open Json file, read file and return JsonNode
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
	
	// convert string to jsonNode; --it may change private function to public function--
	private JsonNode stringToJson(ObjectMapper mapper, String str) {
		JsonNode res = mapper.createArrayNode();
		
		try {
			res = mapper.readTree(str);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return res;
	}
	
	// synchronize server information and user DB.
	private void syncDatabase(ObjectMapper mapper) {
		JsonNode basicNode = stringToJson(mapper, "{\"infor\" : {"
				+ "			\"total\": 0,"
				+ "			\"win\": 0,"
				+ "			\"lose\": 0,"
				+ "			\"tie\": 0,"
				+ "			\"cube\": 0"
				+ "		}, \"log\": [{"
				+ "				\"who\": \"\","
				+ "				\"state\": \"\","
				+ "				\"cube\": 0"
				+ "			}]}");
		
		// add new deck
		for (Deck d: deckList) {
			if (userDB.at("/" + d.id).asText("None").equals("None")) {
				userDB.set(d.id, basicNode);
				//userDB.set(d.id, 101); - for PrimitiveType
			}
		}
		
		// remove deleted deck
		ArrayList<String> key = new ArrayList<>();
		userDB.fieldNames().forEachRemaining(key::add);
		
		
		for (String id: key) {
			if(!id.equals("Total") && findDeckFromServer(id) == null) {
				userDB.remove(id);
			}
		}
	}
	
	public void storeUserDB() {
		try {
			File file = new File("./src/db.json");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fw);
			
			writer.write(userDB.toPrettyString());
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Deck findDeckFromServer(String deckName) {
		for (Deck d: deckList) {
			if (d.checkId(deckName)) {
				return d;
			}
		}
		return null;
	}
	
	public ArrayList<String> getDeckIds() {
		ArrayList<String> result = new ArrayList<>();
		
		for (Deck deck: deckList) {
			result.add(deck.id);
		}
		
		return result;
	}
	
	// get deck information : win, lose, tie, total matches
	public String getDeckInfor(String deckID, String type) {
		
		switch (type) {
			case TOTAL:
			case CUBE:
				return userDB.at("/" + deckID + type).asText();
			case WIN:
			case LOSE:
			case TIE:
				int total = userDB.at("/" + deckID + TOTAL).asInt();
				int count = userDB.at("/" + deckID + type).asInt();
				return String.format("%d (%.2f%%)", count, count / (double)total * 100);
		default:
			break;
		}
		
		return "";
	}
	
	// get deck match logs - incomplete
	public ArrayNode getDeckLog(String deckID) {
		if (!deckID.equals("Total")) {
			return (ArrayNode) userDB.at("/" + deckID + LOG);
		}
		
		System.out.println(deckID);
		ArrayNode result = (ArrayNode) userDB.at("/" + deckID + LOG); 
		
		System.out.println(result.toString());
		System.out.println(result.toString());
		
		ArrayList<String> ids = getDeckIds();
		
		
		for (String id : ids) {
			
		}
		//ArrayNode deckNode = (ArrayNode) deckDB.at("/ServerState/Decks")
		//JsonNode node = deckNode.get(0);
		return null;
	}
}
