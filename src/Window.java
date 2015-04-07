import javax.swing.JFrame;

public class Window extends JFrame {

	private Panel pan = new Panel();
	
	private static final long serialVersionUID = 1L;

	public Window(){             
	    this.setTitle("BB84 Quantum Cryptography Simulator");
	    this.setSize(800, 600);
	    this.setLocationRelativeTo(null);   
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
	    //this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
	    this.setContentPane(pan);               
	    this.setVisible(true);
	    start_anim();
	}    
	
	private void start_anim() {
		Photon rand = new Photon();
		for(int i=0;i<13;i++) {
			rand.setPolarization(Polarization.random());
			pan.addPhoton(rand);
			pan.repaint();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pan.clearPhoton();
		pan.repaint();
	}
}