import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
 
import javax.swing.JPanel;
 
public class Panel extends JPanel {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void paintComponent(Graphics g){           
		Font font = new Font("Courier", Font.BOLD, 12);
		g.setFont(font);
		g.setColor(Color.BLACK);          
		g.drawString("Alice's bit sequence:", 20, (this.getHeight()/2)-6);                
	}               
}