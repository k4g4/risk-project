import javax.swing.*;

public class Risk {

	static class RiskFrame extends JFrame {

		public RiskFrame() {
			super();
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
		
		public void showText() {
			this.add(new JLabel("Test"));
	    }
		
		public void render() {
			this.pack();
			this.setVisible(true);
		}
  }

	public static void main(String[] args) {
		RiskFrame riskFrame = new RiskFrame();
		riskFrame.showText();
		riskFrame.render();
		
  }
}
