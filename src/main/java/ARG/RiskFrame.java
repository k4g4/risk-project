import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class RiskFrame extends JFrame {
	private Container contentPane;
	private JPanel backgroundPanel;
	private JTextField textField; 
	private JTextArea textArea;
	private JScrollPane scrollPane;

	
	public RiskFrame(String title, int width, int height) {
		super(title);
		setDefaultCloseOperation(RiskFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(width, height));
		setResizable(false);
		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
	}
	
	public void setBackground(BufferedImage bg) {
		backgroundPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
			}
		};
		backgroundPanel.setLayout(null);
		contentPane.add(backgroundPanel, BorderLayout.CENTER);
		pack();
	}
	
	public void setTextWindow() {
        textArea = new JTextArea(5, 0);
        textArea.setFont(new Font("serif", Font.BOLD, 24));
        textArea.addKeyListener(new KeyListener() {
        	public void keyTyped(KeyEvent evt) {
        		if (evt.getKeyCode() != KeyEvent.VK_ENTER) return;
        		String text = textArea.getText();
        		textArea.setText("You said: " + text);
        		textArea.selectAll();
        		textArea.setCaretPosition(textArea.getDocument().getLength());
        	}
			public void keyPressed(KeyEvent arg0) {	}
			public void keyReleased(KeyEvent e) { }
        });
        scrollPane = new JScrollPane(textArea);
        contentPane.add(scrollPane, BorderLayout.PAGE_END);
        pack();
	}
	
	public void addTerritoryButton(String label, int x, int y) {
		JButton territoryButton = new JButton(label);
		territoryButton.setBounds(
				(int)(backgroundPanel.getWidth() * ((float)x/100)),
				(int)(backgroundPanel.getHeight() * ((float)y/100)),
				label.length() * 8 + 50,
				30);
		backgroundPanel.add(territoryButton);
	}
	
	public void render() {
		pack();
		setVisible(true);
	}
}
