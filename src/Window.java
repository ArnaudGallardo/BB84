import javax.swing.JFrame;

public class Window extends JFrame {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Window(){             
	    this.setTitle("BB84 Quantum Cryptography Simulator");
	    this.setSize(800, 600);
	    this.setLocationRelativeTo(null);   
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
	    //this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
	    this.setContentPane(new Panel());               
	    this.setVisible(true);
	}       
}