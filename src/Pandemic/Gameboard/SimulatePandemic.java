package Pandemic.Gameboard;

import java.util.Random;
import Pandemic.player.Player;
import Pandemic.variables.Disease;
import Pandemic.variables.Piece;
import Pandemic.variables.Variables;
import Pandemic.Gameboard.*;
import Pandemic.actions.Action;
import Pandemic.cities.City;

import java.util.ArrayList;

public class SimulatePandemic
{
    public static void main(String[] args)
	{
	  System.out.println("starting new game");
	  SimulatePandemic testgame;
	  Player jesus;
	  Player unabomber;
	  Player jack;
	  Player deadpool;

	  unabomber = new Player("Unabomber","QUARANTINE_SPECIALIST");
	  jack = new Player("Jesus","OPERATIONS_EXPERT");
	  jesus = new Player("Jack the Ripper","SCIENTIST");
	  deadpool = new Player ("Zodiac Killer","MEDIC");
	  Player[] currentPlayers;
	  currentPlayers = new Player[4];
	  currentPlayers[0] = jesus;
	  currentPlayers[1] = unabomber;
	  currentPlayers[2] = jack;
	  currentPlayers[3] = deadpool;
	  
	  
	  
	  testgame = new SimulatePandemic(currentPlayers);
	  
	
	  testgame.playGame();
    }
    
    //------------for storing
    private ArrayList<City>   freezeCities = new ArrayList<City>();
	private  int f_redCube,f_blueCubes,f_yellowCubes,f_blackCubes;
	private  ArrayList<Disease> f_diseases = new ArrayList<Disease>();
	private  ArrayList<City>   f_ResearchStation = new ArrayList<City>();
	private  City f1_location,f2_location,f3_location,f4_location;
	//--------------to return
	
	
    // The Game Board contains all the objects of the game.
    public GameBoard gameBoard;
    
    // An array of the players who are playing
    public Player[] gamePlayers;
    
    // boolean attributes which indicate if the game is won or lost.
    public static boolean gameOver;
    public static boolean gameWon;
    public static boolean gameLost;
    
    //public static ArrayList<Action>[] Suggestions = new ArrayList[4];
    
    /**
     * adjustable features of the game that is set up, including
     * numberColours - number of colours of disease played with. (default 4 colour)
     * requiredForCure- the number of cards which must be discarded to cure a disease (we set 4 instead of 5).
     * infectionRateSetting - the arrary of values needed for the infection rate.  (implenent in GameBoard @increaseEpidemic)
     * maximumOutbreaks - the maximum number of outbreaks that can happen before the game is lost. (8)
     * cardsDrawnInitally - the number of cards drawn between the players at the start of the game.
     * maximumHandSize - The maximum handsize of the players. (doesn't implement in this edition)
     */     

    /*
     * This constructor for a Pandemic game creates a new instance of a GameBoard object called GameBoard 
     * with the given adjustable features of the game.
     * It requires an array of players to be provided.
     */
    public SimulatePandemic(Player[] currentGamePlayers)
    {
        System.out.println("Setting up a new game of pandemic with the below features.");
        System.out.println( 4 + " Colours of disease.");
        System.out.println( 4 + " Cards of the same colour must be discarded at a research station to cure a disease.");
        System.out.println( 4 + " is the maximum number of epidemics.");
        System.out.println("Any more than " + 7 + " outbreaks and the game will be lost");
        System.out.println("The initial infection step will see " + 9 + " cities infected ");
        System.out.println("Between all players a maximum of " + 8 + " cards are drawn at start of the game");
        System.out.println("There are " + 5 + " research stations which can be placed, and " + 1 + 
                            " (min 1) are placed at the start of the game");
        
                                  
        // initialise instance variables
        //for 4 player the initial hand size is 2 cards
        int startingHandSize ;
        
        gameBoard = new GameBoard();
        gamePlayers = currentGamePlayers;
        
        // sets the players to the gameboard
        sitPlayersDown();
        gameBoard.startGame();
        startingHandSize = calcHandSize();
        drawHands(startingHandSize);
        placePieces();
        gameOver = false;        
    }
    
   @SuppressWarnings("Unchecked")
	public void playGame()
    {
        int i,z;
        int turns = 0;
        while (gameOver == false)
        {
            i = gamePlayers.length;   
            while (i > 0 && !gameOver)
            {       
            	/*
            	 * Below we freeze the game to allow other players 
            	 * play our turn to suggest a set of actions.
            	 */
            	i = i -1;     	
            	for (z=0;z<gamePlayers.length;z++) {
            		freeze();
            		if(i!=z) {
            			/*
            			 * Each player has access to the entire gameBoard , his hands
            			 * and the hands of player in command (The player who choose the final moves)
            			 * and don't have knowledge of the cards of other players
            			 */
            			System.out.println("------------------------------------");
            			System.out.println("sug:"+ (z+1) +" " +gamePlayers[z].getPlayerName() + " make suggestion for "+ gamePlayers[i].getPlayerName());
            			System.out.println("------------------------------------");
            			while (gamePlayers[z].getPlayerAction()>0 && !gameOver){
            				gamePlayers[z].makeDecision(gamePlayers[i].getHand(),gamePlayers[i].getPlayerRole());
                			Variables.Suggestions[z]=gamePlayers[z].getSuggestions();
                			checkGameOver();
            			}
            			
            		}
            		unfreeze();
            	} 
            	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    			System.out.println("Time to take the final actions: " + gamePlayers[i].getPlayerName()+ " investigate the comrades suggestions");
    			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                while (gamePlayers[i].getPlayerAction() > 0 && !gameOver){
                
                    //here is where an action should happen
                    // System.out.println("Chance for action here!");
                	/*
                	 * Here  you choose between 2 options
                	 * 1) modify the dummy AI methods 
                	 * 2) Create smart enough new methods to win the game
                	 */
                	
                    gamePlayers[i].makeDecision(null,gamePlayers[i].getPlayerRole());
                    checkGameOver();
               }
                resetAllPlayerAction();
                Variables.Suggestions[i]=gamePlayers[i].getSuggestions();
                //empty all the suggestions array
                for (int i1=0;i1<Variables.Suggestions.length;i1++) {
            	   Variables.Suggestions[i1].clear();
                }
               
                if (!checkGameOver()) 
                {
                   System.out.println(gamePlayers[i].getPlayerName() + " completed 4 actions");
                   gamePlayers[i].drawCard(2);                
                   gameBoard.infectCityPhase(gameBoard.getInfectionRate());

               }
               checkGameOver();
            }
            
            turns++;
            if (!checkGameOver())
            {	
            	System.out.println("================================================");
                System.out.println("Ending turn " + turns + " everybody has had a go.");
                System.out.println("================================================");
            }
            
        }
    }
    
   
   
   private void freeze() {	    
   	  freezeCities  = (ArrayList<City>) gameBoard.getCities().clone();
	  f_redCube         = gameBoard.redCubes;
	  f_blueCubes       = gameBoard.blueCubes;
	  f_yellowCubes     = gameBoard.yellowCubes;
	  f_blackCubes      = gameBoard.blackCubes;
	  f_diseases        = (ArrayList<Disease>) gameBoard.getDiseases().clone();
	  f_ResearchStation = (ArrayList<City>) gameBoard.getResearchCentre().clone();
	  f1_location		= gameBoard.playerPieces[0].getLocation();
	  f2_location		= gameBoard.playerPieces[1].getLocation();
	  f3_location   	= gameBoard.playerPieces[2].getLocation();
	  f4_location   	= gameBoard.playerPieces[3].getLocation();
   
   }
   
   private void unfreeze() {	    
	   	  gameBoard.cities      = freezeCities;
		  gameBoard.redCubes    = f_redCube;
		  gameBoard.blueCubes   = f_blueCubes;  
		  gameBoard.yellowCubes = f_yellowCubes;
		  gameBoard.blackCubes  = f_blackCubes;
		  gameBoard.diseases	= f_diseases;		  
		  gameBoard.playerPieces[0].setLocation(f1_location);
		  gameBoard.playerPieces[1].setLocation(f2_location);
		  gameBoard.playerPieces[2].setLocation(f3_location);
		  gameBoard.playerPieces[3].setLocation(f4_location);
		  Variables.CITY_WITH_RESEARCH_STATION = f_ResearchStation;
	   
	   }
   
    public boolean checkGameOver()
    {
        if (checkGameWon())
        {
            for (int i = 0 ; i < 5 ; i++)
            {
                System.out.println(" YOU WIN!!! ");
            }

            gameOver = true;
        }
        checkGameLost();
        return gameOver;
    }
    
    // checks if the game is lost
    public void checkGameLost()
    {
        if (gameBoard.getOutbreakCount()>Variables.MAX_NUMBER_OF_OUTBREAK)
        {
            System.out.println(" game over, too many outbreaks! ");
            gameLost = true;
            gameOver = true; 
            looserPrint(); 
            gamePlayers[0].printStats();
            for(int i=0;i<4;i++) {
            	System.out.print(gamePlayers[i].getPlayerRole()+"'s ");
            	gamePlayers[i].printPlayerStats();
            }
            gameBoard.printStats();
        }
        else if (gameBoard.emptyDeck())
        {
            System.out.println(" That's it game over, no more cards. ");
            gameLost = true;
            gameOver = true;
            looserPrint();
            gamePlayers[0].printStats();
            for(int i=0;i<4;i++) {
            	System.out.print(gamePlayers[i].getPlayerRole()+"'s ");
            	gamePlayers[i].printPlayerStats();
            }
            gameBoard.printStats();
        } 
        //the condition of not have any other cubes to set 
        //is implemented in @GameBoard.checkCubesRemain and @NoMoreCubes
    }
    
    // Prints looser
    public static void looserPrint()
    {
        for (int i = 0 ; i < 5 ; i++)
        {
            System.out.println(" THE CIVILATION IS DEAD!");
        }
        
    }
    
    // sets all players actions back to 4
    public void resetAllPlayerAction()
    {
        int i = gamePlayers.length;
        while (i > 0)
        {
            i--;
            gamePlayers[i].resetPlayerAction();
        }
    }
    
    
    public void placePieces()
    {
    	City temp3 = new City(0,0,0,0,0);
    	for (int x = 0 ; x < gameBoard.cities.size() ; x ++)
        {
        	if (gameBoard.cities.get(x).getName().equals(Variables.atlanta.getName())) {
        		temp3=gameBoard.cities.get(x);
        	}            
        }
        int i = gamePlayers.length;
        while (i > 0)
        {
        	i = i -1;
            // placing pieces
            // System.out.println("Placing a piece for " + gamePlayers[i-1].getPlayerName());
        	
            gameBoard.playerPieces[i] = new Piece(gamePlayers[i], gameBoard, temp3);
            // System.out.println("making sure they know the piece is their piece!");
            gamePlayers[i].setPlayerPiece(gameBoard.playerPieces[i]);            
        }
    }
    
    // calculates initial hand size based on number of players
    public int calcHandSize()
    {
        int i = gamePlayers.length;
        int toReturn=0;
        if (i==2) {toReturn= 4;}
        else if (i==3) {toReturn= 3;}
        else if (i==4) {toReturn= 2;}
        return toReturn;
    }
    
    //draw cards for each player
    public void drawHands(int handSize)
    {
        int i = gamePlayers.length;
        while (i > 0)
        {
        	i = i -1;
            System.out.println("drawing hand for " + gamePlayers[i].getPlayerName());
            gamePlayers[i].drawCard(handSize);            
        }
    }
    
    public boolean checkGameWon()
    {
        boolean isWon=true;
        for (int i = 0 ; i <gameBoard.diseases.size() ; i ++)
        {
            isWon = isWon && gameBoard.diseases.get(i).getCured();
        }
        gameWon= isWon;
        return isWon;
    }
        

    public void sitPlayersDown()
    {
        int i = gamePlayers.length;
        while (i > 0)
        {
        	i = i -1;
            System.out.println(gamePlayers[i].getPlayerName() + " has joined the game");
            gamePlayers[i].setGameBoard(gameBoard);           
        }
    }

    
    
}
