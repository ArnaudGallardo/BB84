import javax.swing.JFrame;

public class Window extends JFrame {

	private Panel pan = new Panel();
	
	private static final long serialVersionUID = 1L;

	public Window(){             
	    this.setTitle("BB84 Quantum Cryptography Simulator");
	    this.setSize(800, 700);
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
				//Animation
				pan.setAnimPhotonFlux(0);
				for(int a=0;a<60;a++) {
					pan.setAnimPhotonFlux(pan.getAnimPhotonFlux()+1);
					pan.repaint();
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pan.clearPhoton();
		byte b = 0;
		for(int i=0;i<13;i++) {
			b = (byte)(Math.random()*2);
			pan.addMail(b);
			pan.repaint();
			try {
				//Animation
				pan.setAnimMailFlux(0);
				for(int a=0;a<60;a++) {
					pan.setAnimMailFlux(pan.getAnimMailFlux()+1);
					pan.repaint();
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pan.clearMail();
		b = 0;
		for(int i=0;i<13;i++) {
			b = (byte)(Math.random()*2);
			pan.addData(b);
			pan.repaint();
			try {
				//Animation
				pan.setAnimDataFlux(0);
				for(int a=0;a<60;a++) {
					pan.setAnimDataFlux(pan.getAnimDataFlux()+1);
					pan.repaint();
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pan.clearData();
		pan.repaint();
	}
}