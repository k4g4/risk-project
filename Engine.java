import java.util.*;

public class Engine {
	private ArrayList<Territory> territories;
	
	public Engine() {
		this.territories = new ArrayList<Territory>();
		this.territories.add(new Territory());
	}
	
	public Engine(ArrayList<Territory> territories) {
		this.territories = territories;
	}
	
	public ArrayList<Territory> getTerritories() {
		return this.territories;
	}
}
