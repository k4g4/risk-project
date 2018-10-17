import java.util.*;

public class Territory {
	private HashMap<String, Territory> adjacentTerritories;
	private String name;
	
	public Territory(String name) {
		adjacentTerritories = new HashMap<String, Territory>();
		this.name = name;
	}
	
	public HashMap<String, Territory> getAdjacentTerritories() { return adjacentTerritories; }
	
	public String getName() { return name; }
}
