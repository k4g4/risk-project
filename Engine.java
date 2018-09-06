import java.util.*;

public class Engine {
	private RiskFrame riskFrame;
	private ArrayList<Territory> territories;
	
	public Engine(String title, String bg, int width, int height) {
		riskFrame = new RiskFrame(title, width, height);
		territories = new ArrayList<Territory>();
		riskFrame.setBackground(bg);
		addTerritory("Test", 50, 50);
		riskFrame.render();
	}
	
	public ArrayList<Territory> getTerritories() {
		return territories;
	}
	
	public void addTerritory(String title, int x, int y) {
		riskFrame.addTerritoryButton("Test", x, y);
		territories.add(new Territory(title));
	}
}
