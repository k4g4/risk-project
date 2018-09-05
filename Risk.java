import javax.swing.*;

public class Risk {

  public class RiskFrame extends JFrame {

    public RiskFrame() {
      super();
      super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      super.add(new JLabel("Hello, world!"));
      super.pack();
      super.setVisible(true); 
    }
  }

  public static void main(String[] args) {
    JFrame riskFrame = new RiskFrame();

  }
}
