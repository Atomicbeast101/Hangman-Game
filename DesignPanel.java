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
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Polygon;

// Start of DesignPanel class - Added by Vincent
public class DesignPanel extends JPanel {

   // Attributes
   private int wrongGuesses = 0;

   /**
    * paintComponent() method. Called on repaint() method call.
    * @param g Graphics object
    */
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(new BasicStroke(6));
      setBackground(Color.GRAY);
      setSize(300,500);

      //Rope with support pillar
      g2.drawLine(100, 50, 200, 50);
      g2.drawLine(200, 50, 200, 75);
      g2.drawLine(100, 50, 100, 350);
      g2.drawLine(50, 350, 150, 350);

      switch(wrongGuesses) {
         case 1:
            // Face
            g2.drawOval(163, 75, 75, 75);
            break;
         case 2:
            // Face
            g2.drawOval(163, 75, 75, 75);
            //Body
            g2.drawLine(200, 150, 200, 300);
            break;
         case 3:
            // Face
            g2.drawOval(163, 75, 75, 75);
            //Body
            g2.drawLine(200, 150, 200, 300);
            // Left Arm
            g2.drawLine(200, 175, 165, 215);
            break;
         case 4:
            // Face
            g2.drawOval(163, 75, 75, 75);
            //Body
            g2.drawLine(200, 150, 200, 300);
            // Left Arm
            g2.drawLine(200, 175, 165, 215);
            // Right Arm
            g2.drawLine(200, 175, 235, 215);
            break;
         case 5:
            // Face
            g2.drawOval(163, 75, 75, 75);
            //Body
            g2.drawLine(200, 150, 200, 300);
            // Left Arm
            g2.drawLine(200, 175, 165, 215);
            // Right Arm
            g2.drawLine(200, 175, 235, 215);
            // Left Leg
            g2.drawLine(200, 300, 165, 355);
            break;
         case 6:
            // Face
            g2.drawOval(163, 75, 75, 75);
            //Body
            g2.drawLine(200, 150, 200, 300);
            // Left Arm
            g2.drawLine(200, 175, 165, 215);
            // Right Arm
            g2.drawLine(200, 175, 235, 215);
            // Left Leg
            g2.drawLine(200, 300, 165, 355);
            // Right Leg
            g2.drawLine(200, 300, 235, 355);
            break;
         case 7:
            // Face
            g2.drawOval(163, 75, 75, 75);
            //Body
            g2.drawLine(200, 150, 200, 300);
            // Left Arm
            g2.drawLine(200, 175, 165, 215);
            // Right Arm
            g2.drawLine(200, 175, 235, 215);
            // Left Leg
            g2.drawLine(200, 300, 165, 355);
            // Right Leg
            g2.drawLine(200, 300, 235, 355);
            //Left Eye
            g2.drawLine(180, 90, 190, 110);
            g2.drawLine(190, 90, 180, 110);
            break;
         case 8:
            // Face
            g2.drawOval(163, 75, 75, 75);
            //Body
            g2.drawLine(200, 150, 200, 300);
            // Left Arm
            g2.drawLine(200, 175, 165, 215);
            // Right Arm
            g2.drawLine(200, 175, 235, 215);
            // Left Leg
            g2.drawLine(200, 300, 165, 355);
            // Right Leg
            g2.drawLine(200, 300, 235, 355);
            //Left Eye
            g2.drawLine(180, 90, 190, 110);
            g2.drawLine(190, 90, 180, 110);
            //Right Eye
            g2.drawLine(205, 90, 215, 110);
            g2.drawLine(215, 90, 205, 110);
            break;
         case 9:
            // Face
            g2.drawOval(163, 75, 75, 75);
            //Body
            g2.drawLine(200, 150, 200, 300);
            // Left Arm
            g2.drawLine(200, 175, 165, 215);
            // Right Arm
            g2.drawLine(200, 175, 235, 215);
            // Left Leg
            g2.drawLine(200, 300, 165, 355);
            // Right Leg
            g2.drawLine(200, 300, 235, 355);
            //Left Eye
            g2.drawLine(180, 90, 190, 110);
            g2.drawLine(190, 90, 180, 110);
            //Right Eye
            g2.drawLine(205, 90, 215, 110);
            g2.drawLine(215, 90, 205, 110);
            //Mouth
            g2.drawLine(180, 125, 220, 125);
            break;
      }
   }

   //Getting the counter by passing it in
   /**
    * updateDude() method. Called when a player guesses wrong and updates the graphic box.
    * @param _wrongGuesses # of wrong guesses
    */
   public void updateDude(int _wrongGuesses) {
      wrongGuesses = _wrongGuesses;
      repaint();
   }

}
// End of DesignPanel class
