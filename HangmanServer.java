/*
 * [Names]:
 *   REMOVED
 * [Date]:
 *   12/2/2015
 * [What]:
 *   Final Project: Hangman Game
 * [Purpose]:
 *   Typical Hangman game
 * [Caveats]:
 *   None.
 */

// Imports
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

// Start of HangmanServer class
public class HangmanServer implements ConnectionInfo, ActionListener {

   // Attributes
   private ArrayList<String> dftWords = new ArrayList<String>();
   private String crrtWord = "";
   private String progWord = "";
   private ArrayList<String> guesses = new ArrayList<String>();
   private ArrayList<Game> clients = new ArrayList<Game>();
   private boolean gameStatus = false;
   private int currentPlyr = 0;
   private JTextArea jtaClients = null;
   private JTextArea jtaStatus = null;

   /**
    * Default Constructor with 2 parameters.
    * @param _jtaClients JTextArea that holds list of clients in server GUI.
    * @param _jtaStatus JTextArea that holds the server status in server GUI.
    */
   public HangmanServer(JTextArea _jtaClients, JTextArea _jtaStatus) {
      // GUI attributes
      jtaClients = _jtaClients;
      jtaStatus = _jtaStatus;

      // Load up problems from the file and store them to arraylist
      try {
         BufferedReader r = new BufferedReader(new FileReader("problems.txt"));
         String line = "";
         while((line = r.readLine()) != null) {
            dftWords.add(line);
         }
         r.close();
      }
      catch(IOException ioex) {
         jtaStatus.append("Error while opening/processing the file! Log:\n");
         ioex.printStackTrace();
      }
      catch(Exception ex) {
         jtaStatus.append("Unexpected error! Log:\n");
         ex.printStackTrace();
      }

      // Start up socket connection
      new AcceptClient().start();
   }

   /**
    * actionPerformed() method - Called on user interact on jframe
    * @param ae Action event information
    */
   public void actionPerformed(ActionEvent ae) {
      if(ae.getActionCommand().equals("Reset Game")) {
         // Reset the game
         setupNewWords();

         // Clear all client's stuff
         jtaStatus.append("[HangmanServer]: Game-> New/Reset game complete!\n");
         for(Game c : clients)
            c.messageClient("RESET " + clients.get(currentPlyr).getPlayerName() + " " + progWord);
         JOptionPane.showMessageDialog(null, "Force reset complete!", "Hangman - Reset Complete", JOptionPane.INFORMATION_MESSAGE);
      }
   }

   // Start of AcceptClient class
   class AcceptClient extends Thread {

      /**
       * run() method - Called through Thread class.
       */
      public void run() {
         jtaStatus.append("[HangmanServer]: Server started up! Waiting for client connections...\n");
         try {
            ServerSocket ss = new ServerSocket(PORT);

            while(true) {
               Socket s = ss.accept();
               jtaStatus.append("[HangmanServer]: Client found! Setting the client up...\n");
               Game g = new Game(s);
               clients.add(g);
               g.start();
            }
         }
         catch(IOException ioex) {
            jtaStatus.append("Error while creating server socket or listening for socket! Log:\n");
            ioex.printStackTrace();
         }
         catch(Exception ex) {
            jtaStatus.append("Unexpected error! Log:\n");
            ex.printStackTrace();
         }
      }

   }
   // End of AcceptClient class

   // Start of Game class
   class Game extends Thread {

      // Attributes
      private Socket s = null;
      private String playerName = "";
      private BufferedReader reader = null;
      private PrintWriter writer = null;

      /**
       * Default constructor with one parameter
       * @param _s Client's socket
       */
      public Game(Socket _s) {
         s = _s;
      }

      /**
       * run() method - Called through Thread class.
       */
      public void run() {
         try {
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

            // Get player's name
            playerName = reader.readLine();

            // Prevents clients from joining in middle of a game
            if(gameStatus) {
               writer.println("WAIT");
               writer.flush();

               jtaStatus.append("[HangmanServer]: Client " + playerName + " tried to join, but was prevented because game is in session.\n");
            }
            else {
               writer.println("PLAY");
               writer.flush();

               jtaStatus.append("[HangmanServer]: Client " + playerName + " is all set up!\n");
               updateClientList();
            }
         }
         catch(IOException ioex) {
            jtaStatus.append("Error while opening up reader/writer stream! Log:\n");
            ioex.printStackTrace();
         }
         catch(Exception ex) {
            jtaStatus.append("Unexpected error! Log:\n");
            ex.printStackTrace();
         }
         try {
            while(true) {
               String[] input = reader.readLine().split(" ");

               switch(input[0]) {
                  case "CHAT":
                     // Manages the chat
                     String msg = "";
                     for(int i = 1; i < input.length; i++)
                        msg += input[i] + " ";
                     jtaStatus.append("[HangmanServer]: Chat-> " + playerName + ": " + msg + "\n");
                     for(Game c : clients)
                        c.messageClient("CHAT CHAT " + playerName + " " + msg);
                     break;
                  case "ONLINE_PLAYERS":
                     String list = "";
                     for(Game c : clients)
                        list += c.getPlayerName() + " ";
                     writer.println("ONLINE_PLAYERS " + list);
                     writer.flush();
                     break;
                  case "GUESS":
                     // Checks the guessing part
                     if(input[1].equals("LETTER")) {
                        String name = input[2];
                        char letter = input[3].charAt(0);

                        // Notify all players what current player guessed
                        jtaStatus.append("[HangmanServer]: Game-> " + playerName + " guessed " + letter + "...\n");
                        for(Game c : clients)
                           c.messageClient("CHAT GAME " + " Player " + name + " guessed " + letter);

                        // Adds guessed letter to array
                        guesses.add("" + letter);

                        // Change current player
                        currentPlyr++;
                        if(currentPlyr == clients.size())
                           currentPlyr = 0;

                        // Check guess
                        if(checkGuess(letter)) {
                           // Send changes to the clients
                           makeChangesToProgressWord(letter);
                           if(progWord.contains("_")) {
                              String guessList = "";
                              for(String g : guesses)
                                 guessList += g + ",";
                              guessList = guessList.replaceAll(" ", "%");
                              for(Game c : clients)
                                 c.messageClient("GUESS RIGHT NO " + name + " " + clients.get(currentPlyr).getPlayerName() + " " + progWord + " " + guessList);
                           }
                           else {
                              gameStatus = false;
                              String guessList = "";
                              for(String g : guesses)
                                 guessList += g + ",";
                              guessList = guessList.replaceAll(" ", "%");
                              for(Game c : clients)
                                 c.messageClient("GUESS RIGHT YES " + name + " " + progWord + " " + guessList);
                           }

                           // Notify all players that current player guessed right
                           jtaStatus.append("[HangmanServer]: Game-> " + playerName + " guessed right!\n");
                           for(Game c : clients)
                              c.messageClient("CHAT GAME " + " Player " + name + " guessed right! # of " + letter + ": " + getNumOfLetters(letter));
                        }
                        else {
                           // Send changes to the clients
                           String guessList = "";
                           for(String g : guesses)
                              guessList += g + ",";
                           guessList = guessList.replaceAll(" ", "%");
                           for(Game c : clients)
                              c.messageClient("GUESS WRONG NO " + name + " " + clients.get(currentPlyr).getPlayerName() + " " + guessList);

                           // Notify all players that current player guessed wrong
                           jtaStatus.append("[HangmanServer]: Game-> " + playerName + " guessed wrong!\n");
                           for(Game c : clients)
                              c.messageClient("CHAT GAME " + " Player " + name + " guessed wrong!");
                        }
                     }
                     else if(input[1].equals("WORD")) {
                        String name = input[2];
                        String words = "";
                        for(int i = 3; i < input.length - 1; i++)
                           words += input[i] + " ";
                        words += input[input.length - 1];

                        // Notify all players what current player guessed
                        jtaStatus.append("[HangmanServer]: Game-> " + playerName + " guessed " + words + "...\n");
                        for(Game c : clients)
                           c.messageClient("CHAT GAME " + " Player " + name + " guessed " + words);

                        // Adds guessed letter to array
                        guesses.add(words);

                        // Check guess
                        if(checkGuess(words)) {
                           gameStatus = false;
                           // Send changes to the clients
                           String cWord = crrtWord.replaceAll(" ", "%");
                           String guessList = "";
                           for(String g : guesses)
                              guessList += g + ",";
                           guessList = guessList.replaceAll(" ", "%");
                           for(Game c : clients)
                              c.messageClient("GUESS RIGHT YES " + name + " " + cWord + " " + guessList);

                           // Notify all players that current player guessed right
                           jtaStatus.append("[HangmanServer]: Game-> " + playerName + " guessed right!\n");
                           for(Game c : clients)
                              c.messageClient("CHAT GAME " + " Player " + name + " guessed right!");
                        }
                        else {
                           // Send changes to the clients
                           String guessList = "";
                           for(String g : guesses)
                              guessList += g + ",";
                           guessList = guessList.replaceAll(" ", "%");
                           for(Game c : clients)
                              c.messageClient("GUESS WRONG NO " + name + " " + clients.get(currentPlyr).getPlayerName() + " " + guessList);

                           // Notify all players that current player guessed wrong
                           jtaStatus.append("[HangmanServer]: Game-> " + playerName + " guessed wrong!\n");
                           for(Game c : clients)
                              c.messageClient("CHAT GAME " + " Player " + name + " guessed wrong!");
                        }
                     }
                     break;
                  case "RESET":
                     // Reset the game
                     setupNewWords();

                     // Clear all client's stuff
                     jtaStatus.append("[HangmanServer]: Game-> New/Reset game complete!\n");
                     for(Game c : clients)
                        c.messageClient("RESET " + clients.get(currentPlyr).getPlayerName() + " " + progWord);
                     break;
               }
            }
         }
         catch(IOException ioex) {
            clients.remove(this);
            notifyAllClients(this);
            updateClientList();
            jtaStatus.append("[HangmanServer]: Client " + playerName + " has left the game!\n");
         }
         catch(Exception ex) {
            clients.remove(this);
            notifyAllClients(this);
            updateClientList();
            jtaStatus.append("[HangmanServer]: Client " + playerName + " has left the game!\n");
         }
      }

      /**
       * getName() method. Returns client's player name.
       * @return name Client's name.
       */
      public String getPlayerName() {
         return playerName;
      }

      /**
       * messageClient() method. Sends a message to the client.
       * @param msg Message to client.
       */
      public void messageClient(String msg) {
         writer.println(msg);
         writer.flush();
      }

   }
   // End of Game class

   /**
    * setupNewWords() method. Sets up a game with new word(s).
    */
   private void setupNewWords() {
      crrtWord = dftWords.get((int)(Math.random() * dftWords.size()));
      crrtWord = crrtWord.toUpperCase();
      progWord = "";
      for(char c : crrtWord.toCharArray()) {
         if(c == ' ')
            progWord += "%";
         else
            progWord += "_";
      }
      guesses.clear();
      gameStatus = true;
      currentPlyr = 0;
   }

   /**
    * notifyAllClients() method. Called when a client leaves the game.
    * @param client Client who left the game
    */
   private void notifyAllClients(Game client) {
      for(Game c : clients)
         c.messageClient("=[" + client.getPlayerName() + " has left the game]=");
   }

   /**
    * checkGuess() method. Called when user clicks on letter button.
    * @param cc Letter that client chose to check with.
    * @return Returns true/false on user's guess of letter.
    */
   private boolean checkGuess(char cc) {
      for(char c : crrtWord.toCharArray())
         if(c == cc)
            return true;
      return false;
   }

   /**
    * makeChangesToProgressWord() method. Changes the progWord string.
    * @param letter Letter to add to progWord variable.
    */
   private void makeChangesToProgressWord(char letter) {
      char[] cWord = crrtWord.toCharArray();
      char[] pWord = progWord.toCharArray();
      for(int i = 0; i < cWord.length; i++) {
         if(cWord[i] == letter) {
            pWord[i] = letter;
         }
      }
      progWord = "";
      for(char p : pWord)
         progWord += "" + p;
   }

   /**
    * getNumOfLetters() method. Called to get number of times in a letter.
    * @param letter Letter to count the # of.
    * @return num Number of times the letter is in a string.
    */
   private int getNumOfLetters(char letter) {
      int count = 0;
      for(char c : crrtWord.toCharArray())
         if(c == letter)
            count++;
      return count;
   }

   /**
    * checkGuess() method. Called when user enters the guess word(s).
    * @param word Guessed word that client sent to check with.
    * @return Returns true/false on user's word guess.
    */
   private boolean checkGuess(String word) {
      if(crrtWord.equalsIgnoreCase(word))
         return true;
      return false;
   }

   /**
    * updateClientList() method. Updates the list of online clients in server GUI.
    */
   private void updateClientList() {
      String list = "";
      for(Game c : clients)
         list += c.getPlayerName() + "\n";
      jtaClients.setText(list);
   }

}
// End of HangmanServer class
