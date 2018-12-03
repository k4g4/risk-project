package com.arg.project;
import com.arg.project.MyAmazingBot;
import com.arg.project.AmazonS3Example;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.awt.Color;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JOptionPane;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.telegrambots.*;
 //import org.telegram.telegrambots.api.methods.send.SendMessage;
// import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
 //import org.telegram.telegrambots.meta.api.objects.Update;
// import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


@SuppressWarnings("serial")
public class Board extends JPanel{
	static int currency = 0;
	static int capturedTerritories = 0;
    private static List<Set<Country>> continents;
    private static final int[] continentBonuses = {5, 2, 5, 3, 7, 2};

    static Country[] countries;
    public static final int BOARD_WIDTH = 1350;
    public static final int BOARD_HEIGHT = 900;
    //setBackground(new Color(1.0f,1.0f,1.0f,0.5f));

    public static List<String> amazonlogtext = new ArrayList<String>();
    public static Color player1 = new Color(0.0f, 0.0f, 1.0f, 0.5f);
    public static Color player2 = new Color(1.0f, 0.0f, 0.0f, 0.5f);
    public static Color player3 = new Color(1.0f, 1.0f, 0.0f, 0.5f);
    public static Color player4 = new Color(0.0f, 1.0f, 0.0f, 0.5f);
    public static Color player5 = new Color(0.0f, 1.0f, 1.0f, 0.5f);
    public static Color player6 = new Color(1.0f, 1.0f, 1.0f, 0.5f);

    public static final Color[] colors = {player1, player2, player3, player4, player5, player6};

    private static int turn = 0;

    private static int troopsToPlace;
    private static Country selectedCountry;
    private static Country selectedSecondCountry;
    private final JLabel turnInfo;
    private final JLabel[] cardInfo;
    private final Dice diceInfo;
    private static Player[] players;

    enum Mode {
        UseCardMode, InitialPlacingMode, PlacingMode, AttackFromMode, AttackToMode,
        KeepAttackingMode, NewCountryMode, FortifyFromMode, FortifyToMode,
        KeepFortifyingMode, GameOverMode;
    }

    private static Mode mode = Mode.InitialPlacingMode;

    public Board(final JLabel turnInfo, final JLabel[] cardInfo, Dice diceInfo, int numPlayers) {

        this.turnInfo = turnInfo;
        this.cardInfo = cardInfo;
        this.diceInfo = diceInfo;

        initializeCountries();
        initializeContinents();
        initializePlayers(numPlayers);
        initialCountryOwners(numPlayers);
        Player.initialDeck();
        initialTroopsToPlace();
        setBackground(Color.black);
        setCardLabels();


        turnInfo.setText(getStringForMode());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point mouse = e.getPoint();

                switch (mode) {
                case UseCardMode:
                    break;
                case InitialPlacingMode:
                    placeSoldier(mouse);
                    break;
                case PlacingMode:
                    placeSoldier(mouse);
                    break;
                case AttackFromMode:
                    selectOwnerCountry(mouse);
                    break;
                case AttackToMode:
                    selectEnemyCountry(mouse);
                    break;
                case KeepAttackingMode:
                    keepAttacking(mouse);
                    break;
                case NewCountryMode:
                    placeSoldierNewCountry(mouse);
                    break;
                case FortifyFromMode:
                    selectOwnerCountry(mouse);
                    break;
                case FortifyToMode:
                    selectFortify(mouse);
                    break;
                case KeepFortifyingMode:
                    if (selectedSecondCountry.inBounds(mouse)) {
                        fortify();
                    }
                    break;
                case GameOverMode:
                    break;
                }
                setCardLabels();
                turnInfo.setText(getStringForMode());
                repaint();
            }

        });

    }
    int[] totalCaptured;
    int numbPlayers = 0;
    private void initializePlayers(int numPlayers) {
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player();
        }
        totalCaptured = new int[numPlayers];
        numbPlayers = numPlayers;
    }


    /* creates the ArrayList of Set<Country> that represents continents
     * necessary for checking continent bonuses
     */
    private void initializeContinents() {

        continents = new ArrayList<Set<Country>>();
        for (int i = 0; i < 6; i++) {
            continents.add(i, new TreeSet<Country>());
        }
        // North America
        Set<Country> thisContinent = continents.get(0);
        for (int i = 0; i < 9; i++) {
            thisContinent.add(countries[i]);
        }

        // South America
        thisContinent = continents.get(1);
        for (int i = 9; i < 13; i++) {
            thisContinent.add(countries[i]);
        }

        // Europe
        thisContinent = continents.get(2);
        for (int i = 13; i < 20; i++) {
            thisContinent.add(countries[i]);
        }

        // Africa
        thisContinent = continents.get(3);
        for (int i = 20; i < 26; i++) {
            thisContinent.add(countries[i]);
        }

        // Asia
        thisContinent = continents.get(4);
        for (int i = 26; i < 38; i++) {
            thisContinent.add(countries[i]);
        }

        // Australia
        thisContinent = continents.get(5);
        for (int i = 38; i < 42; i++) {
            thisContinent.add(countries[i]);
        }
    }

   
    private void initializeCountries() {
        countries = new Country[42];
        countries[0] = new Country("Alaska", 85, 210, 50, 50);
        countries[1] = new Country("Alberta", 220, 280, 50, 50);
        countries[2] = new Country("Central America", 280, 500, 50, 50);
        countries[3] = new Country("Eastern United States", 220, 365, 50, 50);
        countries[4] = new Country("Greenland", 480, 120, 50, 50);
        countries[5] = new Country("Northwest Territory", 180, 210, 50, 50);
        countries[6] = new Country("Ontario", 300, 280, 50, 50);
        countries[7] = new Country("Quebec", 370, 280, 50, 50);
        countries[8] = new Country("Western United States", 300, 385, 50, 50);
        countries[9] = new Country("Venezuela", 370, 550, 50, 50);
        countries[10] = new Country("Brazil", 450, 625, 50, 50);
        countries[11] = new Country("Peru", 370, 625, 50, 50);
        countries[12] = new Country("Argentina", 385, 750, 50, 50);
        countries[13] = new Country("Great Britain", 610, 285, 50, 50);
        countries[14] = new Country("Iceland", 550, 213, 50, 50);
        countries[15] = new Country("Northern Europe", 670, 298, 50, 50);
        countries[16] = new Country("Scandinavia",680, 210, 50, 50);
        countries[17] = new Country("Ukraine", 760, 270, 50, 50);
        countries[18] = new Country("Southern Europe", 690, 355, 50, 50);
        countries[19] = new Country("Western Europe", 610, 360, 50, 50);
        countries[20] = new Country("Madagascar", 785, 670, 50, 50);
        countries[21] = new Country("Egypt", 700, 440, 50, 50);
        countries[22] = new Country("North Africa", 625, 490, 50, 50);
        countries[23] = new Country("East Africa", 750, 540, 50, 50);
        countries[24] = new Country("Congo", 695, 580, 50, 50);
        countries[25] = new Country("South Africa", 710, 670, 50, 50);
        countries[26] = new Country("Middle East", 780, 420, 50, 50);
        countries[27] = new Country("Afghanistan", 860, 340, 50, 50);
        countries[28] = new Country("Ural", 860, 250, 50, 50);
        countries[29] = new Country("India", 890, 460, 50, 50);
        countries[30] = new Country("China", 960, 400, 50, 50);
        countries[31] = new Country("Siberia", 960, 200, 50, 50);
        countries[32] = new Country("Siam", 970, 500, 50, 50);
        countries[33] = new Country("Mongolia", 1020, 340, 50, 50);
        countries[34] = new Country("Irkutsk", 1020, 280, 50, 50);
        countries[35] = new Country("Yakutsk", 1060, 200, 50, 50);
        countries[36] = new Country("Kamchatka", 1175, 200, 50, 50);
        countries[37] = new Country("Japan", 1115, 385, 50, 50);
        countries[38] = new Country("Indonesia", 1020, 580, 50, 50);
        countries[39] = new Country("Western Australia", 1075, 700, 50, 50);
        countries[40] = new Country("Eastern Australia", 1140,700, 50, 50);
        countries[41] = new Country("New Guinea", 1140, 600, 50, 50);

        countries[0].adjacentCountries = new TreeSet<Country>();
        countries[0].adjacentCountries.add(countries[1]);
        countries[0].adjacentCountries.add(countries[5]);
        countries[0].adjacentCountries.add(countries[36]);

        countries[1].adjacentCountries = new TreeSet<Country>();
        countries[1].adjacentCountries.add(countries[5]);
        countries[1].adjacentCountries.add(countries[6]);
        countries[1].adjacentCountries.add(countries[8]);

        countries[2].adjacentCountries = new TreeSet<Country>();
        countries[2].adjacentCountries.add(countries[3]);
        countries[2].adjacentCountries.add(countries[8]);
        countries[2].adjacentCountries.add(countries[9]);

        countries[3].adjacentCountries = new TreeSet<Country>();
        countries[3].adjacentCountries.add(countries[6]);
        countries[3].adjacentCountries.add(countries[7]);
        countries[3].adjacentCountries.add(countries[8]);

        countries[4].adjacentCountries = new TreeSet<Country>();
        countries[4].adjacentCountries.add(countries[5]);
        countries[4].adjacentCountries.add(countries[6]);
        countries[4].adjacentCountries.add(countries[7]);
        countries[4].adjacentCountries.add(countries[14]);

        countries[5].adjacentCountries = new TreeSet<Country>();
        countries[5].adjacentCountries.add(countries[6]);

        countries[6].adjacentCountries = new TreeSet<Country>();
        countries[6].adjacentCountries.add(countries[7]);
        countries[6].adjacentCountries.add(countries[8]);

        countries[7].adjacentCountries = new TreeSet<Country>();

        countries[8].adjacentCountries = new TreeSet<Country>();

        countries[9].adjacentCountries = new TreeSet<Country>();
        countries[9].adjacentCountries.add(countries[10]);
        countries[9].adjacentCountries.add(countries[11]);

        countries[10].adjacentCountries = new TreeSet<Country>();
        countries[10].adjacentCountries.add(countries[11]);
        countries[10].adjacentCountries.add(countries[12]);
        countries[10].adjacentCountries.add(countries[22]);

        countries[11].adjacentCountries = new TreeSet<Country>();
        countries[11].adjacentCountries.add(countries[12]);

        countries[12].adjacentCountries = new TreeSet<Country>();

        countries[13].adjacentCountries = new TreeSet<Country>();
        countries[13].adjacentCountries.add(countries[14]);
        countries[13].adjacentCountries.add(countries[15]);
        countries[13].adjacentCountries.add(countries[16]);
        countries[13].adjacentCountries.add(countries[19]);

        countries[14].adjacentCountries = new TreeSet<Country>();

        countries[15].adjacentCountries = new TreeSet<Country>();
        countries[15].adjacentCountries.add(countries[16]);
        countries[15].adjacentCountries.add(countries[17]);
        countries[15].adjacentCountries.add(countries[18]);
        countries[15].adjacentCountries.add(countries[19]);

        countries[16].adjacentCountries = new TreeSet<Country>();
        countries[16].adjacentCountries.add(countries[17]);

        countries[17].adjacentCountries = new TreeSet<Country>();
        countries[17].adjacentCountries.add(countries[18]);
        countries[17].adjacentCountries.add(countries[26]);
        countries[17].adjacentCountries.add(countries[27]);
        countries[17].adjacentCountries.add(countries[28]);

        countries[18].adjacentCountries = new TreeSet<Country>();
        countries[18].adjacentCountries.add(countries[19]);
        countries[18].adjacentCountries.add(countries[21]);
        countries[18].adjacentCountries.add(countries[22]);
        countries[18].adjacentCountries.add(countries[26]);

        countries[19].adjacentCountries = new TreeSet<Country>();
        countries[19].adjacentCountries.add(countries[22]);

        countries[20].adjacentCountries = new TreeSet<Country>();
        countries[20].adjacentCountries.add(countries[23]);
        countries[20].adjacentCountries.add(countries[25]);

        countries[21].adjacentCountries = new TreeSet<Country>();
        countries[21].adjacentCountries.add(countries[22]);
        countries[21].adjacentCountries.add(countries[23]);
        countries[21].adjacentCountries.add(countries[26]);

        countries[22].adjacentCountries = new TreeSet<Country>();
        countries[22].adjacentCountries.add(countries[23]);
        countries[22].adjacentCountries.add(countries[24]);

        countries[23].adjacentCountries = new TreeSet<Country>();
        countries[23].adjacentCountries.add(countries[24]);
        countries[23].adjacentCountries.add(countries[25]);
        countries[23].adjacentCountries.add(countries[26]);

        countries[24].adjacentCountries = new TreeSet<Country>();
        countries[24].adjacentCountries.add(countries[25]);

        countries[25].adjacentCountries = new TreeSet<Country>();

        countries[26].adjacentCountries = new TreeSet<Country>();
        countries[26].adjacentCountries.add(countries[27]);
        countries[26].adjacentCountries.add(countries[29]);

        countries[27].adjacentCountries = new TreeSet<Country>();
        countries[27].adjacentCountries.add(countries[28]);
        countries[27].adjacentCountries.add(countries[29]);
        countries[27].adjacentCountries.add(countries[30]);

        countries[28].adjacentCountries = new TreeSet<Country>();
        countries[28].adjacentCountries.add(countries[30]);
        countries[28].adjacentCountries.add(countries[31]);

        countries[29].adjacentCountries = new TreeSet<Country>();
        countries[29].adjacentCountries.add(countries[30]);
        countries[29].adjacentCountries.add(countries[32]);

        countries[30].adjacentCountries = new TreeSet<Country>();
        countries[30].adjacentCountries.add(countries[31]);
        countries[30].adjacentCountries.add(countries[32]);
        countries[30].adjacentCountries.add(countries[33]);

        countries[31].adjacentCountries = new TreeSet<Country>();
        countries[31].adjacentCountries.add(countries[33]);
        countries[31].adjacentCountries.add(countries[34]);
        countries[31].adjacentCountries.add(countries[35]);

        countries[32].adjacentCountries = new TreeSet<Country>();
        countries[32].adjacentCountries.add(countries[38]);

        countries[33].adjacentCountries = new TreeSet<Country>();
        countries[33].adjacentCountries.add(countries[34]);
        countries[33].adjacentCountries.add(countries[36]);
        countries[33].adjacentCountries.add(countries[37]);

        countries[34].adjacentCountries = new TreeSet<Country>();
        countries[34].adjacentCountries.add(countries[35]);
        countries[34].adjacentCountries.add(countries[36]);

        countries[35].adjacentCountries = new TreeSet<Country>();
        countries[35].adjacentCountries.add(countries[36]);

        countries[36].adjacentCountries = new TreeSet<Country>();
        countries[36].adjacentCountries.add(countries[37]);

        countries[37].adjacentCountries = new TreeSet<Country>();

        countries[38].adjacentCountries = new TreeSet<Country>();
        countries[38].adjacentCountries.add(countries[39]);
        countries[38].adjacentCountries.add(countries[41]);

        countries[39].adjacentCountries = new TreeSet<Country>();
        countries[39].adjacentCountries.add(countries[40]);
        countries[39].adjacentCountries.add(countries[41]);

        countries[40].adjacentCountries = new TreeSet<Country>();
        countries[40].adjacentCountries.add(countries[41]);

        countries[41].adjacentCountries = new TreeSet<Country>();

        for (int i = 0; i < countries.length; i++) {
            for (Country c : countries[i].adjacentCountries) {
                c.adjacentCountries.add(countries[i]);
            }
        }

    }

    /* creates a shuffled array of countries
     */
    private Country[] shuffleCountries() {
        Country[] shuffledCountries = countries.clone();
        for (int i = 0; i < countries.length; i++) {
            int j = i + (int) ((countries.length - i) * Math.random());
            Country temp = shuffledCountries[i];
            shuffledCountries[i] = shuffledCountries[j];
            shuffledCountries[j] = temp;
        }
        return shuffledCountries;
    }

    /* iterates through a shuffled array of countries to randomly
     * assign owners to countries
     * @param numPlayers the number of players
     */
    private static Timer timer;
    private void launchSomeTimer() {
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
            	System.out.println("Going to next player");
            	nextPlayer();

            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 30000);

    }

    public static void resetSomeTimer() {
    	System.out.println("Resettig timer");
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
            	nextPlayer();
            }
        };
        timer.cancel();
        timer = new Timer();
        timer.schedule(timerTask,30000);


    }


    private void initialCountryOwners(int numPlayers) {
        int playerID = 0;
        Country[] shuffledCountries = shuffleCountries();
        for (int i = 0; i < countries.length; i++) {
            players[playerID].countriesOwned.add(shuffledCountries[i]);
            playerID = (playerID + 1) % numPlayers;
        }
        launchSomeTimer();
    }



    /* draws the connecting lines for countries that are adjacent
     * but not visibly so
     */
    private void drawLines(Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setStroke(new java.awt.BasicStroke(5));

    }

    /* ends the game if only one player is remaining
     */
    private void checkWin() {
        int numDead = 0;
        for (Player p : players) {
            if (p.dead) {
                numDead++;
            }
        }
        if (numDead == players.length - 1) {
            mode = Mode.GameOverMode;
            turnInfo.setText(getStringForMode());
            repaint();
            for(int i=1;i<numbPlayers+1;i++)
            {
            	TwitterFactory factory = new TwitterFactory();
        		Twitter twitter = factory.getInstance();
        		twitter.setOAuthConsumer("ctGAc5sGxzYS0hbrWUgzTT5Ww","SL1kayThYFJTvc6PjXIQRpFqgzDgvFlCrgyQIFCEPyTKsaSVGy");
        		AccessToken accessToken = new AccessToken("1058188356302057473-jRNLejEhgWpWmGbj4Ux6kde1PsxdqK","YEp6sBeLAc5z1zID7mg8aSXX8xhE9vd36iV1GXcimPpXo");
        		twitter.setOAuthAccessToken(accessToken);
        		try {
        			Status status = twitter.updateStatus("Player " + i +  " captured " + totalCaptured[i-1] + " territories total.") ;
              //String faketext = "testing";
              String texttotelegram = "Player " + i +  " captured " + totalCaptured[i-1] + " territories total.";
              MyAmazingBot.sendSampleText(texttotelegram);
            } catch (TwitterException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }
    }

    /* places soldier in a country provided the current player owns the country
     * moves on to next mode after all soldiers have been placed
     * @param: mouse for the mouse click location
     */




    private void placeSoldier(Point mouse) {
    	resetSomeTimer();


        for (Country c : players[turn].countriesOwned) {
            if (c.inBounds(mouse)) {
                c.numSoldiers++;
                troopsToPlace--;
            }
        }
        if (troopsToPlace == 0) {
            if (mode == Mode.InitialPlacingMode){
                turn++;
                if (turn == players.length) {
                    turn = 0;
                    updateTroopsToPlace();
                    nextMode();
                } else {
                    initialTroopsToPlace();
                }
            } else {
                nextMode();
            }
        }
    }

    /* calculates the initial troops for a player to place
     */
    private void initialTroopsToPlace() {

        int countriesOwned = players[turn].countriesOwned.size();
        troopsToPlace = 40 - countriesOwned - (players.length - 2) * 5;
    }
    /* return true if current player owns the continent, false otherwise
     * @param continent index for continent
     */
    private static boolean continentOwned(int continent) {
        for (Country c : continents.get(continent)) {
            if (!players[turn].countriesOwned.contains(c)) {
                return false;
            }
        }
        return true;
    }

    /* calculates the number of troops a player can place at
     * the beginning of his/her turn
     */
    private static void updateTroopsToPlace() {

        int countryBonus = players[turn].countriesOwned.size() / 3;
        troopsToPlace = Math.max(3, countryBonus);

        for (int i = 0; i < Board.continentBonuses.length; i++) {
            if (continentOwned(i)) {
                troopsToPlace += continentBonuses[i];
            }
        }
    }


    /* selects a country and stores it given that the current player owns it
     * @param mouse for the mouse click location
     */
    private void selectOwnerCountry(Point mouse) {
        for (Country c : players[turn].countriesOwned) {
            if (c.inBounds(mouse) && c.numSoldiers > 1) {
                selectedCountry = c;
                nextMode();
            }
        }
    }

    /* selects a country and stores it given that the current player does not own it
     * @param mouse for the mouse click location
     */
    private void selectEnemyCountry(Point mouse) {
    	resetSomeTimer();

        // unselect the country to attack from
        if (selectedCountry.inBounds(mouse)) {
            selectedCountry = null;
            mode = Mode.AttackFromMode;
            return;
        }
        for (Country c : selectedCountry.adjacentCountries) {
            if (c.inBounds(mouse) && !players[turn].countriesOwned.contains(c)) {
                selectedSecondCountry = c;
                attack(selectedCountry, selectedSecondCountry);
                checkOutcome();
                if (mode == Mode.AttackToMode) {
                    nextMode();
                }
            }
        }
    }

    /* sorts an array using insertion sort
     */
    private void insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] > arr[j - 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                }
            }
        }
    }

    /* returns a random int from 1-6
     */
    private int roll() {
        return (int) Math.ceil(6 * Math.random());
    }

    /* simulates the dice rolling for an attack
     * number of dice is dependent on available soldiers
     * @param own for the attacking country
     * @param enemy for the defending country
     */
     Country acountry;
    private void attack(Country own, Country enemy) {
    	resetSomeTimer();
        int[] atkDice = new int[3];
        int[] defDice = new int[2];
        acountry = own;

        for (int i = 0; i < Math.min(atkDice.length, own.numSoldiers - 1); i++) {
            atkDice[i] = roll();
        }

        for (int i = 0; i < Math.min(defDice.length, enemy.numSoldiers); i++) {
            defDice[i] = roll();
        }

        insertSort(atkDice);
        insertSort(defDice);

        if (atkDice[0] > defDice[0]) {
            enemy.numSoldiers--;
        } else {
            own.numSoldiers--;
        }
        if (atkDice[1] != 0 && defDice[1] != 0) {
            if (atkDice[1] > defDice[1] && atkDice[1] != 0 && defDice[1] != 0) {
                enemy.numSoldiers--;
            } else {
                own.numSoldiers--;
            }
        }

        diceInfo.dice[0].update(atkDice[0]);
        diceInfo.dice[1].update(defDice[0]);
        diceInfo.dice[2].update(atkDice[1]);
        diceInfo.dice[3].update(defDice[1]);
        diceInfo.dice[4].update(atkDice[2]);
        diceInfo.repaint();
    }

    /* checks the number of available soldiers to see if a battle is over
     */
    private void checkOutcome() {
        if (selectedCountry.numSoldiers == 1) {
            selectedCountry = null;
            selectedSecondCountry = null;
            mode = Mode.AttackFromMode;
            return;
        }
        if (selectedSecondCountry.numSoldiers == 0) {
            if (!Player.wonCardAlready) {
                players[turn].winCard();
                Player.wonCardAlready = true;
            }

            mode = Mode.NewCountryMode;
            conquer();
        }
    }

    private void keepAttacking(Point mouse) {
    	resetSomeTimer();

        // unselect the country to attack from
        if (selectedCountry.inBounds(mouse)) {
            selectedCountry = null;
            selectedSecondCountry = null;
            mode = Mode.AttackFromMode;
            return;
        }

        if (selectedSecondCountry.inBounds(mouse)) {
            attack(selectedCountry, selectedSecondCountry);
            checkOutcome();
        }

    }

    /* takes all the troops remaining after a conquest and allow them to be placed
     */
    private void conquer() {
    	resetSomeTimer();
    	capturedTerritories++;
        Player enemy = null;
        for (Player p : players) {
            if (p.countriesOwned.contains(selectedSecondCountry)) {
                enemy = p;
            }
        }
        enemy.countriesOwned.remove(selectedSecondCountry);
        players[turn].countriesOwned.add(selectedSecondCountry);

        if (enemy.countriesOwned.isEmpty()) {
            enemy.dead = true;
            for (int i = 0; i < enemy.cards.length; i++) {
                players[turn].cards[i] += enemy.cards[i];
            }
        }
        totalCaptured[turn]++;
        checkWin();
        selectedSecondCountry.numSoldiers = 1;
        troopsToPlace = selectedCountry.numSoldiers - 2;
        selectedCountry.numSoldiers = 1;

        // deal with edge case where there are no remaining soldiers right after a conquest
        if (troopsToPlace == 0) {
            selectedCountry = null;
            selectedSecondCountry = null;
            nextMode();
        }


        if(turn==0)
            JOptionPane.showMessageDialog(null,
            	    "Player 2, you are being attacked.");
        if(turn==1)
           	JOptionPane.showMessageDialog(null,
               	    "Player 1, you are being attacked.");
    }

    /* place a soldier in a newly conquered country
     * if there are no more soldiers, move on to the next mode
     * @param mouse for the mouse click location
     */
    private void placeSoldierNewCountry(Point mouse) {
    	resetSomeTimer();

        if (selectedCountry.inBounds(mouse)) {
            troopsToPlace--;
            selectedCountry.numSoldiers++;
        }
        if (selectedSecondCountry.inBounds(mouse)) {
            troopsToPlace--;
            selectedSecondCountry.numSoldiers++;
        }

        if (troopsToPlace == 0) {
            selectedCountry = null;
            selectedSecondCountry = null;
            nextMode();
        }
    }

    /* fortifies a soldier from one country to another given that they are adjacent
     * @param mouse for the mouse click location
     */
    private void selectFortify(Point mouse) {
    	resetSomeTimer();
        if (selectedCountry.inBounds(mouse)) {
            selectedCountry = null;
            mode = Mode.FortifyFromMode;
            return;
        }

        for (Country c : selectedCountry.adjacentCountries) {
            if (c.inBounds(mouse) && players[turn].countriesOwned.contains(c)) {
                selectedSecondCountry = c;
                fortify();
                nextMode();
            }
        }
    }

    /* fortifies a soldier from one country to another
     * if there are no more soldiers available, move on to next mode
     */
    private void fortify() {
    	resetSomeTimer();
        selectedCountry.numSoldiers--;
        selectedSecondCountry.numSoldiers++;

        // immediately switch to next mode if no longer possible to fortify
        if (selectedCountry.numSoldiers == 1) {
            nextMode();
        }
    }

    /* returns a String that contains information
     * on the game state
     */
    public String getStringForMode() {

        String init = "Player " + (turn + 1) + ": ";
        //String asdf1 = init = "Player " + (turn + 1) + ": ";
        //amazonlogtext.add(asdf1);
        switch(mode) {
        case UseCardMode:
            if (players[turn].fullHand()) {
                String asdf2 = init + "You have a full hand. You must use a set.";
                amazonlogtext.add(asdf2);
                return init + "You have a full hand. You must use a set.";
            }
            String asdf3 = init + "Would you like to use your cards?";
            amazonlogtext.add(asdf3);
            return init + "Would you like to use your cards?";
        case InitialPlacingMode:
            String asdf4 = init + "Welcome to Risk! Place troops: " + troopsToPlace + " remaining";
            amazonlogtext.add(asdf4);
            //System.out.print
            return init + "Welcome to Risk! Place troops: " + troopsToPlace + " remaining";
        case PlacingMode:
            String asdf5 = init + "Place troops: " + troopsToPlace + " remaining";
            amazonlogtext.add(asdf5);
            return init + "Place troops: " + troopsToPlace + " remaining";
        case AttackFromMode:
            String asdf6 = init + "Choose country to attack from: ___ -> ___";
            amazonlogtext.add(asdf6);
            return init + "Choose country to attack from: ___ -> ___";
        case AttackToMode:
            String asdf7 = init + "Choose country to attack: " + selectedCountry.getName() + " -> ___";
            amazonlogtext.add(asdf7);
            return init + "Choose country to attack: " + selectedCountry.getName() + " -> ___";
        case KeepAttackingMode:
            String asdf8 = init + "Keep Attacking? " + selectedCountry.getName() +
                    " -> " + selectedSecondCountry.getName();
            amazonlogtext.add(asdf8);
            return init + "Keep Attacking? " + selectedCountry.getName() +
                    " -> " + selectedSecondCountry.getName();
        case NewCountryMode:
            String asdf9 = init + "You successfully conquered " + selectedSecondCountry.getName() +
                    "! Add troops to your new or old country: " + troopsToPlace + " remaining";
            amazonlogtext.add(asdf9);
            return init + "You successfully conquered " + selectedSecondCountry.getName() +
                    "! Add troops to your new or old country: " + troopsToPlace + " remaining";
        case FortifyFromMode:
            String asdf10 = init + "Choose country to fortify from: ___ -> ___";
            amazonlogtext.add(asdf10);
            return init + "Choose country to fortify from: ___ -> ___";
        case FortifyToMode:
            String asdf11 = init + "Choose country to fortify: " + selectedCountry.getName() + " -> ___";
            amazonlogtext.add(asdf11);
            return init + "Choose country to fortify: " + selectedCountry.getName() + " -> ___";
        case KeepFortifyingMode:
            String asdf12 = init + "Continue to fortify " + selectedCountry.getName() + " -> " +
            selectedSecondCountry.getName() + "?";
            amazonlogtext.add(asdf12);
            return init + "Continue to fortify " + selectedCountry.getName() + " -> " +
            selectedSecondCountry.getName() + "?";
        case GameOverMode:
            String asdf13 = init + "You won!!!";
            amazonlogtext.add(asdf13);
            return init + "You won!!!";
        default:
            return "did you just break this game why";
        }
    }

    /* Allows the player to move on to the next phase of the game
     * This function is used by the Next button
     */


    public void save(){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        try{
            // Create new file
            //String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            //String a = current date;
            //String content = "This is the content to write into create file";
            String path = timeStamp + "_risklog.txt";
            File file = new File(path);

            // If file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            // Write in file
            //bw.write(content);
            //bw.write(out,amazonlogtext,Charset.defaultCharset());

            int listsize = amazonlogtext.size(); 
            for (int a = 0; a < listsize; a++) {
            //a.amazonlogtext.add(asdf10); 
            //bw.write("/n") 
            String sample = amazonlogtext.get(a) + "\r\n";
            bw.write(sample);
            //bw.write("\n"); 

            }

            // Close connection
            bw.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        System.out.println(amazonlogtext);
        AmazonS3Example.amazonConnect(timeStamp);
        System.out.println("Game log saved");

         /*
        String clientRegion = "us-east-2"; //String clientRegion = "*** Client region ***";
        String bucketName = "risk-project"; //String bucketName = "*** Bucket name ***";
        String keyName = "AKIAJ673ISJSSODR5YSA";  //String keyName = "*** Key name ***"; AKIAJ673ISJSSODR5YSA AKIAIYPP5T2RBC4HDICA
        String filePath = "risklog.txt";
        
        File file = new File(filePath);
        long contentLength = file.length();
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB. 

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                    .withRegion(clientRegion)
                                    .withCredentials(new ProfileCredentialsProvider())
                                    .build();
                       
            // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
            // then, after each individual part has been uploaded, pass the list of ETags to 
            // the request to complete the upload.
            List<PartETag> partETags = new ArrayList<PartETag>();

            // Initiate the multipart upload.
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
            InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

            // Upload the file parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Because the last part could be less than 5 MB, adjust the part size as needed.
                partSize = Math.min(partSize, (contentLength - filePosition));

                // Create the request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(keyName)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                // Upload the part and add the response's ETag to our list.
                UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());

                filePosition += partSize;
            }

            // Complete the multipart upload.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, keyName,
                    initResponse.getUploadId(), partETags);
            s3Client.completeMultipartUpload(compRequest);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }        



            
        try {
            //Whatever the file path is.
            File statText = new File("statsTest.txt");
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            Writer w = new BufferedWriter(osw);
            w.write("POTATO!!!");
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest.txt");
        } 
        */

    }
    public void undo(){





      acountry.numSoldiers++;
      players[turn].cards[4]=players[turn].cards[4]-1;

      System.out.println("did the undo button register");

    }
    public void next()
    { 



    


        System.out.println("playercommandboardjava");
        String samples = "end";
        System.out.println(samples);
        System.out.println(MyAmazingBot.playercommand);
        System.out.println("playercommandboardjava");
        String testing = MyAmazingBot.playercommand;
        if(testing == samples){
            nextPlayer();
            System.out.println("nextPlayer()");
            MyAmazingBot.playercommand = null;
        }
        else {

        switch(mode) {
        case UseCardMode:
            if (!players[turn].fullHand()) {
                nextMode();
            }
            break;
        case InitialPlacingMode:
            break;
        case PlacingMode:
            break;
        case AttackFromMode:
            mode = Mode.FortifyFromMode;
            selectedCountry = null;
            break;
        case AttackToMode:
            mode = Mode.FortifyFromMode;
            selectedCountry = null;
            selectedSecondCountry = null;
            break;
        case KeepAttackingMode:
            mode = Mode.AttackFromMode;
            selectedCountry = null;
            selectedSecondCountry = null;
            break;
        case NewCountryMode:
            break;
        case FortifyFromMode:
            nextPlayer();
            break;
        case FortifyToMode:
            nextPlayer();
            break;
        case KeepFortifyingMode:
            nextPlayer();
            break;
        case GameOverMode:
            break;
        }
        setCardLabels();
        turnInfo.setText(getStringForMode());
        repaint();
        }
    }

    /* increments the turn to the next living player and resets all of the
     * board state information to the current player
     */
    protected static void nextPlayer() {
    	resetSomeTimer();
        selectedCountry = null;
        selectedSecondCountry = null;
        Player.wonCardAlready = false;
        TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer("ctGAc5sGxzYS0hbrWUgzTT5Ww","SL1kayThYFJTvc6PjXIQRpFqgzDgvFlCrgyQIFCEPyTKsaSVGy");
		AccessToken accessToken = new AccessToken("1058188356302057473-jRNLejEhgWpWmGbj4Ux6kde1PsxdqK","YEp6sBeLAc5z1zID7mg8aSXX8xhE9vd36iV1GXcimPpXo");
		twitter.setOAuthAccessToken(accessToken);
		try {

			Status status = twitter.updateStatus("Player " + (turn + 1) + " captured " + capturedTerritories + " territories this turn.") ;
      String sendingaction ="Player " + (turn + 1) + " captured " + capturedTerritories + " territories this turn." ;
      MyAmazingBot.sendSampleText(sendingaction);
    } catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        turn = (turn + 1) % players.length;
        while (players[turn].dead) {
            turn = (turn + 1) % players.length;
        }
        if (players[turn].hasSet()) {
            mode = Mode.UseCardMode;
        } else {
            mode = Mode.PlacingMode;
        }
        updateTroopsToPlace();

        capturedTerritories=0;
        players[turn].cards[4] = players[turn].cards[4]+1;
    }

    /* iterates the mode to the next
     * on the end of a turn, iterates to next player
     */

    private void nextMode() {
        System.out.println("playercommandboardjava");
        String samples = "end";
        System.out.println(samples);
        System.out.println(MyAmazingBot.playercommand);
        System.out.println("playercommandboardjava");
        String testing = MyAmazingBot.playercommand;
        if(testing == samples){
            nextPlayer();
            MyAmazingBot.playercommand = null;
        }
        else{


        switch(mode) {
        case UseCardMode:
            mode = Mode.PlacingMode;
            break;
        case InitialPlacingMode:
            mode = Mode.PlacingMode;
            break;
        case PlacingMode:
            mode = Mode.AttackFromMode;
            break;
        case AttackFromMode:
            mode = Mode.AttackToMode;
            break;
        case AttackToMode:
            mode = Mode.KeepAttackingMode;
            break;
        case KeepAttackingMode:
            mode = Mode.NewCountryMode;
            break;
        case NewCountryMode:
            mode = Mode.AttackFromMode;
            break;
        case FortifyFromMode:
            mode = Mode.FortifyToMode;
            break;
        case FortifyToMode:
            mode = Mode.KeepFortifyingMode;
            break;
        case KeepFortifyingMode:
            nextPlayer();
            break;
        case GameOverMode:
            break;
        }
        }
    }

    /* Uses a set of the player's cards and updates the number of troops they
     * can place accordingly
     * This function is used by the Use button
     */
    public void useCards() {
        if (mode != Mode.UseCardMode) {
            return;
        }
        int bonus = Player.cardBonus();
        troopsToPlace += bonus;
        turnInfo.setText("You just traded in a set for " + bonus + " soldiers!");
        players[turn].useSet();
        setCardLabels();
        if (!players[turn].hasSet()) {
            nextMode();
        }

    }

    /* updates the text displaying the card status
     */
    public void setCardLabels() {
        String[] cardLabels = players[turn].StringOfCards();
        for (int i = 0; i < cardLabels.length; i++) {
            cardInfo[i].setText(cardLabels[i]);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	g.setColor(new Color(255, 255, 255));
        //g.finalize();
        //g.setColor(new Color(255, 255, 255));
       // final BufferedImage image = ImageIO.read(new File("mapbackground.PNG"));

        //super.paintComponent(g);
        super.paintComponent(g);
        //g.drawImage(img, 0, 0, this);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        Image picture = Toolkit.getDefaultToolkit().getImage("mapbackground.PNG");
        g.drawImage(picture, 10, 10, this);
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            for(Country c : player.countriesOwned) {
                g.setColor(colors[i]);

                if (c == selectedCountry || c == selectedSecondCountry) {
                    c.highlight();
                } else {
                    c.unhighlight();
                }
                c.draw(g);
            }
        }
        drawLines(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

}
