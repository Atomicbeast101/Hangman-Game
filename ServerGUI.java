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
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.text.*;

// Start of ServerGUI class
public class ServerGUI extends JFrame {

   /**
    * main() method - Called on program start
    * @param args Arguements used to start program with
    */
   public static void main(String[] args) {
      new ServerGUI();
   }

   /**
    * Default Constructor
    */
   public ServerGUI() {
      setTitle("Hangman Server");
      setSize(700, 500);
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

      JTextArea jtaClients = new JTextArea(20, 10);
      JTextArea jtaStatus = new JTextArea(40, 30);
      HangmanServer hs = new HangmanServer(jtaClients, jtaStatus);

      // List of Clients
      JPanel jpClients = new JPanel(new BorderLayout(10, 10));
         jpClients.add(new JLabel("List of Clients:"), BorderLayout.NORTH);
         jtaClients.setEnabled(false);
         jtaClients.setDisabledTextColor(Color.BLACK);
         jpClients.add(new JScrollPane(jtaClients), BorderLayout.CENTER);
      add(jpClients, BorderLayout.WEST);

      // Server Status
      JPanel jpStatus = new JPanel(new BorderLayout(10, 10));
         jpStatus.add(new JLabel("Server Status:"), BorderLayout.NORTH);
         jtaStatus.setEnabled(false);
         jtaStatus.setDisabledTextColor(Color.BLACK);
         DefaultCaret caret = (DefaultCaret) jtaStatus.getCaret();
         caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
         jpStatus.add(new JScrollPane(jtaStatus), BorderLayout.CENTER);
      add(jpStatus, BorderLayout.CENTER);
      // Game Controls
      JPanel jpControls = new JPanel();
         JButton jbReset = new JButton("Reset Game");
         jbReset.addActionListener(hs);
         jpControls.add(jbReset);
      add(jpControls, BorderLayout.SOUTH);
      setVisible(true);
   }

}
// End of ServerGUI class
