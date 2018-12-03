package com.arg.project;
import com.arg.project.AmazonS3Example;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import org.telegram.telegrambots.*;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Game implements Runnable {

    public void run() {

        final JFrame frame = new JFrame("Risk");
        final Start startScreen = new Start();
        frame.add(startScreen);
        startScreen.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int numPlayers = startScreen.selectPlayers(e);
                if (numPlayers != -1) {
                    startScreen.setVisible(false);
                    startScreen.setEnabled(false);
                    initializeGame(frame, numPlayers);
                }
            }

        });

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

    }

    private static void initializeGame(JFrame frame, int numPlayers) {

        final JPanel turnPanel = new JPanel();
        final JLabel turnInfo = new JLabel();

        turnPanel.add(turnInfo);

        final JPanel statusPanel = new JPanel();
        final JPanel cardPanel = new JPanel();
        cardPanel.setPreferredSize(new Dimension(150, 210));
        statusPanel.setPreferredSize(new Dimension(150,0 ));
        

        final JLabel diceLabel = new JLabel(" Your Roll:    Enemy Roll:" ); 
        diceLabel.setPreferredSize(new Dimension(150, 40 ));
        
        final JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(150, 80));
        


        final JLabel[] cardInfo = new JLabel[9];
        for (int i = 0; i < cardInfo.length; i++) {
            cardInfo[i] = new JLabel();
            cardPanel.add(cardInfo[i]);
        }

        Dice diceInfo = new Dice();  
        diceInfo.setPreferredSize(new Dimension(150,180 ));

        final Board board = new Board(turnInfo, cardInfo, diceInfo, numPlayers);
        frame.add(board, BorderLayout.CENTER);

        final JButton use = new JButton("Use");
        use.setPreferredSize(new Dimension(150, 30));
        use.setBackground(Color.PINK);

        use.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.useCards();
            }
        });

        cardPanel.add(use);



        final JButton next = new JButton("Next");
        next.setPreferredSize(new Dimension(150, 50));
        next.setBackground(Color.green);
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.next();
            }
        });

        final JButton undo = new JButton("Undo");
        undo.setPreferredSize(new Dimension(150, 50));
        undo.setBackground(Color.yellow);
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.undo();
            }
        });

        final JButton save = new JButton("Save AWS");
        save.setPreferredSize(new Dimension(150, 50));
        save.setBackground(Color.orange);
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.save();
            }
        });


        statusPanel.add(diceLabel);
        statusPanel.add(diceInfo);
        statusPanel.add(spacer);
        statusPanel.add(cardPanel);

        statusPanel.add(next);
        statusPanel.add(undo);
        statusPanel.add(save);

        frame.add(statusPanel, BorderLayout.EAST);
        frame.add(turnPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
                // Initialize Api Context
        ApiContextInitializer.init();

        //Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            botsApi.registerBot(new MyAmazingBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

}
