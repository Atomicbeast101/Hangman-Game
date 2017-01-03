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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Polygon;
import javax.swing.text.*;

// Start of HangmanGUI class
public class HangmanGUI extends JFrame implements ConnectionInfo {

   /**
    * main() method - Called on program start
    * @param args Arguements used to start program with
    */
   public static void main(String[] args) {
      new HangmanGUI();
   }

   /**
    * Default Constructor
    */
   public HangmanGUI() {
      // Show splash first
      new SplashSection();

      // Ask user for host and player name
      HangmanClient hc = null;
      DesignPanel dp = null;

      // Create containers that needs to be accessed in HangmanClient
      JLabel jlCurrentPlyr = new JLabel("Current Player: USERNAME", JLabel.CENTER);
      JLabel jlLetters = new JLabel("Please start a new game!", JLabel.CENTER);
      JLabel jlWrongGuesses = new JLabel("Wrong guesses: ", JLabel.LEFT);
      JTextField jtfGuessWrd = new JTextField(20);
      JTextArea jtaChat = new JTextArea(10, 10);
      JTextField jtfChatIn = new JTextField(25);

      JPanel jpLogin = new JPanel(new GridLayout(0,1));
         jpLogin.add(new JLabel("Enter your host and name to log in", JLabel.CENTER));
         JPanel jpHost = new JPanel();
            jpHost.add(new JLabel("Host: "));
            JTextField jtfHost = new JTextField(20);
            jtfHost.setText(HOST);
            jpHost.add(jtfHost);
         jpLogin.add(jpHost);
         JPanel jpName = new JPanel();
            jpName.add(new JLabel("Name: "));
            JTextField jtfName = new JTextField(20);
            jpName.add(jtfName);
            jpLogin.add(jpName);
         jpLogin.add(jpName);
      int connect = JOptionPane.showConfirmDialog(null, jpLogin, "Hangman - Host & Player Name", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      String name = "";
      if(connect == JOptionPane.OK_OPTION) {
         String host = jtfHost.getText();
         name = jtfName.getText();
         if(!name.contains(" ")) {
            dp = new DesignPanel();
            if(!host.equals("") && !name.equals(""))
               hc = new HangmanClient(host, name, dp, jlCurrentPlyr, jlLetters, jlWrongGuesses, jtfGuessWrd, jtaChat, jtfChatIn);
            else {
               JOptionPane.showMessageDialog(null, "There cannot be any empty values! Please try again!", "Hangman - Invalid input", JOptionPane.ERROR_MESSAGE);
               System.exit(0);
            }
         }
         else {
            JOptionPane.showMessageDialog(null, "Your name cannot contain any spaces! Please try again!", "Hangman - Invalid input", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
         }
      }
      else
         System.exit(0);

      setTitle("Hangman - " + name);
      setSize(800, 500);
      setResizable(false);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setLayout(new BorderLayout(10, 10));
      try {
         Image img = ImageIO.read(getClass().getResource("icon.png"));
         setIconImage(img);
      }
      catch(IOException ioex) {
         System.out.println("Unable to set program icon! Log:");
         ioex.printStackTrace();
      }

      // Menu
      JMenuBar jmb = new JMenuBar();
      setJMenuBar(jmb);
         JMenu jmGame = new JMenu("Game");
         jmGame.setMnemonic('G');
         jmb.add(jmGame);
            JMenuItem jmiPlayers = new JMenuItem("Players Online");
            jmiPlayers.setMnemonic('P');
            jmiPlayers.addActionListener(hc);
            jmGame.add(jmiPlayers);
            JMenuItem jmiReset = new JMenuItem("Reset");
            jmiReset.setMnemonic('R');
            jmiReset.addActionListener(hc);
            jmGame.add(jmiReset);
            JMenuItem jmiExit = new JMenuItem("Exit");
            jmiExit.setMnemonic('E');
            jmiExit.addActionListener(new ActionListener() {

               /**
                * actionPerformed() method. Called on 'Exit' click.
                * @param ae Object that holds the action event information
                */
               public void actionPerformed(ActionEvent ae) {
                  System.exit(0);
               }

            });
            jmGame.add(jmiExit);

         JMenu jmHelp = new JMenu("Help");
         jmHelp.setMnemonic('H');
         jmb.add(jmHelp);
            JMenuItem jmiAbout = new JMenuItem("About");
            jmiAbout.setMnemonic('A');
            jmiAbout.addActionListener(new ActionListener() {

               /**
                * actionPerformed() method. Called on 'About' click.
                * @param ae Object that holds the action event information
                */
               public void actionPerformed(ActionEvent ae) {
                  JOptionPane.showMessageDialog(null, "Version: 1.0\nDeveloped by:\n- Adam Brodack\n- Becca Dingman\n- Louis Roman", "Hangman - About", JOptionPane.INFORMATION_MESSAGE);
               }

            });
            jmHelp.add(jmiAbout);
            JMenuItem jmiRules = new JMenuItem("Rules");
            jmiRules.setMnemonic('R');
            jmiRules.addActionListener(new ActionListener() {

               /**
                * actionPerformed() method. Called on 'Rules' click.
                * @param ae Object that holds the action event information
                */
               public void actionPerformed(ActionEvent ae) {
                  JOptionPane.showMessageDialog(null, "1) One chance of guessing the word(s)\n2) Each player gets a turn to guess a letter/word(s)\n3) Once all players run out of word(s) guesses,\nyou are still able to win via guessing letters\n4) Remember to have fun!", "Hangman - Rules", JOptionPane.INFORMATION_MESSAGE);
               }

            });
            jmHelp.add(jmiRules);



      // Designer
      add(dp, BorderLayout.WEST); //Added by Vincent; to add the design

      // Controls
      JPanel jpControls = new JPanel(new GridLayout(0, 1, 5, 5));
         JPanel jpStatus = new JPanel(new GridLayout(0, 1));
            // Current Player
            jpStatus.add(jlCurrentPlyr);
            // Guessing Letters Status
            jlLetters.setFont(new Font(jlLetters.getFont().getName(), Font.PLAIN, 25));
            jpStatus.add(jlLetters);
            // Wrong guesses
            jpStatus.add(jlWrongGuesses);
         jpControls.add(jpStatus);
         JPanel jpBttns = new JPanel(new GridLayout(0, 1, 0, 0));
            // Letter buttons
            JPanel jpBttnsOne = new JPanel();
               for(String l : Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I")) {
                  JButton jb = new JButton(l);
                  jb.setPreferredSize(new Dimension(48, 30));
                  jb.addActionListener(hc);
                  jpBttnsOne.add(jb);
               }
            jpBttns.add(jpBttnsOne);
            JPanel jpBttnsTwo = new JPanel();
               for(String l : Arrays.asList("J", "K", "L", "M", "N", "O", "P", "Q", "R")) {
                  JButton jb = new JButton(l);
                  jb.setPreferredSize(new Dimension(48, 30));
                  jb.addActionListener(hc);
                  jpBttnsTwo.add(jb);
               }
            jpBttns.add(jpBttnsTwo);
            JPanel jpBttnsThree = new JPanel();
               for(String l : Arrays.asList("S", "T", "U", "V", "W", "X", "Y", "Z")) {
                  JButton jb = new JButton(l);
                  jb.setPreferredSize(new Dimension(48, 30));
                  jb.addActionListener(hc);
                  jpBttnsThree.add(jb);
               }
            jpBttns.add(jpBttnsThree);
            // Guessing Input
            JPanel jpGuess = new JPanel();
               jpGuess.add(jtfGuessWrd);
               JButton jbGuessWrd = new JButton("Guess Word");
               jbGuessWrd.addActionListener(hc);
               jpGuess.add(jbGuessWrd);
            jpBttns.add(jpGuess);
         jpControls.add(jpBttns);

         JPanel jpChat = new JPanel(new GridLayout(0, 1, 5, 5));
            // Chat area
            jtaChat.setEnabled(false);
            jtaChat.setDisabledTextColor(Color.BLACK);
            jtaChat.setLineWrap(true);
            jpChat.add(new JScrollPane(jtaChat));
            DefaultCaret caret = (DefaultCaret) jtaChat.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            // Chat Input
            JPanel jpChatInput = new JPanel();
               jpChatInput.add(jtfChatIn);
               jtfChatIn.setActionCommand("Send");
               jtfChatIn.addActionListener(hc);
               JButton jbChat = new JButton("Send");
               jbChat.addActionListener(hc);
               jpChatInput.add(jbChat);
            jpChat.add(jpChatInput);
         jpControls.add(jpChat);
      add(jpControls, BorderLayout.CENTER);

      add(jpControls, BorderLayout.EAST); //Made small change by add the jpControls to frame - Vincent

      setVisible(true);
   }

   // Start of SplashSection class
   class SplashSection extends JWindow
   {
      /**
       * Default Constructor
       */
      public SplashSection() {
         JPanel content = (JPanel)getContentPane();
         setOpacity(0.95f);

         int width = 800;
         int height = 500;
         Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
         setBounds(((screen.width - width) / 2), ((screen.height - height) / 2), width, height);

         JLabel label = new JLabel(new ImageIcon(getClass().getResource("splash.png")));
         content.add(label, BorderLayout.CENTER);

         setVisible(true);

         try {
            Thread.sleep(5000);
         }
         catch(InterruptedException iex) {
            JOptionPane.showMessageDialog(null, "Error while displaying splash image!\nClosing program...", "Hangman - Splash Error", JOptionPane.ERROR_MESSAGE);
            iex.printStackTrace();
            System.exit(0);
         }

         setVisible(false);
      }
   }
   // End of SplashSection class
}
// End of HangmanGUI class
