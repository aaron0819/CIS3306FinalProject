package snake;

/**@author Chris Hume
 *  Thanks to zetcode.com for the multiple 2D game tutorials. Learning to create and understand this is thanks to them.
 */

import java.awt.EventQueue;
import javax.swing.JFrame;


public class Main extends JFrame {

    public Main() {

        add(new Board());        
        
        setTitle("Snake");
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {                
                JFrame snk = new Main();
                snk.setVisible(true);                
            }
        });
    }
}