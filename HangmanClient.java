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
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Start of HangmanClient class
public class HangmanClient implements ActionListener {

   // Attributes
   private BufferedReader reader = null;
   private PrintWriter writer = null;
   private boolean gameStatus = false;
   private String currentPlyr = "";
   private int numOfGuesses = 0;
   private String name = "";
   private String host = "";
   private ArrayList<String> guesses = new ArrayList<String>();

   private JLabel jlCurrentPlyr = null;
   private JLabel jlLetters = null;
   private JLabel jlWrongGuesses = null;
   private JTextField jtfGuessWrd = null;
   private JTextArea jtaChat = null;
   private JTextField jtfChatIn = null;
   private int wrongGuess = 0;
   private DesignPanel dp = null;

   /**
    * Two-paramenter Constructor
    * @param _host address used to connect to the socket
    * @param _name string text to show who you are
    * @param _dp DesignPanel for hangman designing
    * @param _jlCurrentPlyr JLabel to show current player
    * @param _jlLetters JLabel for word process
    * @param _jlWrongGuesses JLabel to show guess history
    * @param _jtfGuessWrd JTextField for guessing word
    * @param _jtaChat JTextArea for chat history
    * @param _jtfChatIn JTextField for user input for chat
    */
   public HangmanClient(String _host, String _name, DesignPanel _dp, JLabel _jlCurrentPlyr, JLabel _jlLetters, JLabel _jlWrongGuesses, JTextField _jtfGuessWrd, JTextArea _jtaChat, JTextField _jtfChatIn) {
      host = _host;
      name = _name;
      jlCurrentPlyr = _jlCurrentPlyr;
      jlLetters = _jlLetters;
      jlWrongGuesses = _jlWrongGuesses;
      jtfGuessWrd = _jtfGuessWrd;
      jtaChat = _jtaChat;
      jtfChatIn = _jtfChatIn;
      dp = _dp;

      ComServer cs = new ComServer();
      cs.start();
   }

   /**
    * actionPerformed() method - Called on user interact on jframe
    * @param ae Action event information
    */
   public void actionPerformed(ActionEvent ae) {
      switch(ae.getActionCommand()) {
         case "Reset":
            // Resets the game
            writer.println("RESET");
            writer.flush();
            break;
         case "Send":
            // Chat manager
            String msg = jtfChatIn.getText();
            if(msg.equals(""))
               JOptionPane.showMessageDialog(null, "You cannot send an empty chat message!", "Hangman - Chat Error", JOptionPane.ERROR_MESSAGE);
            else {
               writer.println("CHAT " + msg);
               writer.flush();
               jtfChatIn.setText("");
               jtfChatIn.grabFocus();
            }
            break;
         case "Players Online":
            writer.println("ONLINE_PLAYERS");
            writer.flush();
            break;
         case "Guess Word":
            // Guessing word manager
            if(gameStatus) {
               if(name.equals(currentPlyr)) {
                  if(numOfGuesses == 0) {
                     // Add to prevent empty inputs.
                     if(!jtfGuessWrd.getText().equals("") && !jtfGuessWrd.getText().equals(" ")) {
                        if(guessedBefore(jtfGuessWrd.getText()))
                           JOptionPane.showMessageDialog(null, "Those word(s) are already guessed!", "Hangman - Already Guessed", JOptionPane.ERROR_MESSAGE);
                        else {
                           writer.println("GUESS WORD " + name + " " + jtfGuessWrd.getText().toUpperCase());
                           writer.flush();
                           numOfGuesses++;
                           jtfGuessWrd.setText("");
                        }
                     }
                     else
                        JOptionPane.showMessageDialog(null, "You can't send an empty guess! Please fill it in and try again!", "Hangman - Empty Guess Input", JOptionPane.ERROR_MESSAGE);
                  }
                  else
                     JOptionPane.showMessageDialog(null, "You can't do another word guess! You only had one chance!", "Hangman - Ran Out of Guesses", JOptionPane.ERROR_MESSAGE);
               }
               else
                  JOptionPane.showMessageDialog(null, "It's not your turn! Please wait till it's your turn to guess!", "Hangman - Not Your Turn", JOptionPane.ERROR_MESSAGE);
            }
            else
               JOptionPane.showMessageDialog(null, "Can't keep playing! Game already finished!\nPlease start a new game under Game->Reset!", "Hangman - Game Already Ended", JOptionPane.ERROR_MESSAGE);
            break;
         default:
            // Guessing letter manager
            if(gameStatus) {
               if(name.equals(currentPlyr)) {
                  char letter = ae.getActionCommand().charAt(0);
                  if(guessedBefore(letter))
                     JOptionPane.showMessageDialog(null, "That letter has already been guessed!", "Hangman - Already Guessed", JOptionPane.ERROR_MESSAGE);
                  else {
                     writer.println("GUESS LETTER " + name + " " + letter);
                     writer.flush();
                  }
               }
               else
                  JOptionPane.showMessageDialog(null, "It's not your turn! Please wait till it's your turn to guess!", "Hangman - Not Your Turn", JOptionPane.ERROR_MESSAGE);
            }
            else
               JOptionPane.showMessageDialog(null, "Can't keep playing! Game already finished!\nPlease start a new game under Game->Reset!", "Hangman - Game already ended", JOptionPane.ERROR_MESSAGE);
            break;
      }
   }

   // Start of ComServer class
   class ComServer extends Thread {

      /**
       * run() method - Called through Thread class
       */
      public void run() {
         try {
            // Open socket and streams
            Socket s = new Socket(host, 16789);
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

            // Sends player's name to the server
            writer.println(name);
            writer.flush();

            // Gets reply to see if game is in session or not.
            String reply = reader.readLine();
            if(reply.equals("WAIT")) {
               JOptionPane.showMessageDialog(null, "Game is in session! Please wait till game is done!", "Hangman - Game in Session", JOptionPane.ERROR_MESSAGE);
               System.exit(0);
            }
            else {
               gameStatus = false;
               jlLetters.setText("Please start a new game!");

               String progWrd = "";
               String guessList = "";
               while(true) {
                  String[] input = reader.readLine().split(" ");

                  // Manage server inputs
                  switch(input[0]) {
                     case "CHAT":
                        // Manages the chat system
                        String type = input[1];
                        if(type.equals("CHAT")) {
                           String playerName = input[2];
                           String msg = "";
                           for(int i = 3; i < input.length; i++)
                              msg += input[i] + " ";
                           jtaChat.append(playerName + ": " + msg + "\n");
                        }
                        else if(type.equals("GAME")) {
                           String msg = "";
                           for(int i = 2; i < input.length; i++)
                              msg += input[i] + " ";
                           jtaChat.append("=[" + msg + "]=\n");
                        }
                        break;
                     case "ONLINE_PLAYERS":
                        String message = "";
                        for(int i = 1; i < input.length; i++)
                           message += input[i] + "\n";
                        JOptionPane.showMessageDialog(null, message, "List of Online Players", JOptionPane.INFORMATION_MESSAGE);
                        break;
                     case "GUESS":
                        // Manages the guessing part
                        String rightWrong = input[1];
                        String complete = input[2];

                        if(rightWrong.equals("RIGHT")) {
                           String playerName = input[3];

                           // Checks if user completed the game
                           if(complete.equals("YES")) {
                              progWrd = input[4].replaceAll("%", " ");
                              guessList = input[5].replaceAll("%", " ");
                              guesses.clear();
                              for(String gL : guessList.split(","))
                                 guesses.add(gL);
                              gameStatus = false;

                              // Notify the guesser
                              if(playerName.equals(name))
                                 JOptionPane.showMessageDialog(null, "You guessed right and won the game!", "Hangman - Guess", JOptionPane.INFORMATION_MESSAGE);

                              // Puts info to the GUI
                              String progressWord = "";
                              for(char c : progWrd.toCharArray())
                                 progressWord += "" + c + " ";
                              jlLetters.setText(progressWord);
                              jlWrongGuesses.setText("Guesses: " + guessList);
                           }
                           else {
                              currentPlyr = input[4];
                              progWrd = input[5].replaceAll("%", " ");
                              guessList = input[6].replaceAll("%", " ");
                              guesses.clear();
                              for(String gL : guessList.split(","))
                                 guesses.add(gL);

                              // Puts info to the GUI
                              jlCurrentPlyr.setText("Current player: " + currentPlyr);
                              String progressWord = "";
                              for(char c : progWrd.toCharArray())
                                 progressWord += "" + c + " ";
                              jlLetters.setText(progressWord);
                              jlWrongGuesses.setText("Guesses: " + guessList);

                              // Notify the guesser
                              if(playerName.equals(name))
                                 JOptionPane.showMessageDialog(null, "You guessed right!", "Hangman - Guess", JOptionPane.INFORMATION_MESSAGE);
                           }
                        }
                        else {
                           String playerName = input[3];
                           currentPlyr = input[4];
                           guessList = input[5].replaceAll("%", " ");
                           guesses.clear();
                           for(String gL : guessList.split(","))
                              guesses.add(gL);

                           // Puts info to the GUI
                           jlCurrentPlyr.setText("Current player: " + currentPlyr);
                           String progressWord = "";
                           for(char c : progWrd.toCharArray())
                              progressWord += "" + c + " ";
                           jlLetters.setText(progressWord);
                           jlWrongGuesses.setText("Guesses: " + guessList);

                           // Notify the guesser
                           if(playerName.equals(name))
                              JOptionPane.showMessageDialog(null, "You guessed wrong!", "Hangman - Guess", JOptionPane.INFORMATION_MESSAGE);

                           //Added two lines below to pass the counter in - Vincent - Fixed by Adam
                           wrongGuess++;
                           dp.updateDude(wrongGuess);
                           if(wrongGuess == 9) {
                              JOptionPane.showMessageDialog(null, "Ran out of guesses! Game has ended!", "Hangman - Out Of Guesses", JOptionPane.INFORMATION_MESSAGE);
                              gameStatus = false;
                           }
                        }
                        break;
                     case "RESET":
                        // Resets the client to new words sent by the server
                        currentPlyr = input[1];
                        progWrd = input[2].replaceAll("%", " ");
                        guesses.clear();
                        guessList = "";

                        // Puts info to the GUI
                        jlCurrentPlyr.setText("Current player: " + currentPlyr);
                        String progressWord = "";
                        for(char c : progWrd.toCharArray())
                           progressWord += "" + c + " ";
                        jlLetters.setText(progressWord);
                        jlWrongGuesses.setText("Guesses: " + guessList);
                        wrongGuess = 0;
                        numOfGuesses = 0;
                        dp.updateDude(wrongGuess);
                        gameStatus = true;
                        break;
                  }
               }
            }
         }
         catch(IOException ioex) {
            JOptionPane.showMessageDialog(null, "Unable to communicate with the server Perhaps the input you made is wrong?", "Hangman - Connection Issue", JOptionPane.ERROR_MESSAGE);
            ioex.printStackTrace();
            System.exit(0);
         }
         catch(Exception ex) {
            JOptionPane.showMessageDialog(null, "Unexpected error while trying to communicate with the server!", "Hangman - Connection Issue", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            System.exit(0);
         }
      }

   }
   // End of ComServer class

   /**
    * guessedBefore() method. Checks to make sure user hasn't guessed the word before.
    * @param words Word(s) to check to see if user guessed it before or not.
    * @return guessed Returns true/false if user guessed the word before.
    */
   private boolean guessedBefore(String words) {
      for(String g : guesses)
         if(g.equalsIgnoreCase(words))
            return true;
      return false;
   }

   /**
    * guessedBefore() method. Checks to make sure user hasn't guessed the letter before.
    * @param letter Letter to check to see if user guessed it before or not.
    * @return guessed Returns true/false if user guessed the letter before.
    */
   private boolean guessedBefore(char letter) {
      for(String g : guesses)
         if(g.equalsIgnoreCase("" + letter))
            return true;
      return false;
   }

}
// End of HangmanClient class
