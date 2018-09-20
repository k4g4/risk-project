import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class RiskFrame extends JFrame implements ActionListener {
	private Container contentPane;
	private JPanel backgroundPanel;
	private JTextField textField; 
	private JTextArea textArea;
	private final static String newline = "\n";
	
	
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
		//textField = new JTextField(30) ;
	    // TextField.setFont(new Font("serif", font.BOLD, 24));
		
		contentPane.add(backgroundPanel, BorderLayout.CENTER);
		//contentPane.add(textField, BorderLayout.PAGE_END);
		pack();
		

	}
	
	public void setTextWindow() {
		textField = new JTextField("",30);
		textField.addActionListener(this);
		
        textArea = new JTextArea(5, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        contentPane.add(textField, BorderLayout.PAGE_END);
	}

	public void actionPerformed(ActionEvent evt) {
		String text = textField.getText();
		textField.setText("Hello world.");
        textField.selectAll();
        textArea.setCaretPosition(textArea.getDocument().getLength());
		//textField.append(text + newline);
		
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
