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
	
	private void photonFluxAtoB(PhotonScheme ps) {
		for(int i=0;i<ps.getSize();i++) {
			pan.addPhoton(ps.getPhoton(i));
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
	}
	
	private void photonFluxBtoA(PhotonScheme ps) {
		pan.setDirectionPhotonFlux(false);
		photonFluxAtoB(ps);
		pan.setDirectionPhotonFlux(true);
	}
		
	private void mailFluxAtoB(FilterScheme fs) {
		for(int i=0;i<fs.getSize();i++) {
			pan.addMail(fs.getFilter(i));
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
	}
	
	private void mailFluxBtoA(FilterScheme fs) {
		pan.setDirectionMailFlux(false);
		mailFluxAtoB(fs);
		pan.setDirectionMailFlux(true);
	}
	
	private void dataFluxAtoB(BytesScheme bs) {
		for(int i=0;i<bs.getSize();i++) {
			pan.addData(bs.getByte(i));
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
	}
	
	private void dataFluxBtoA(BytesScheme bs) {
		pan.setDirectionDataFlux(false);
		dataFluxAtoB(bs);
		pan.setDirectionDataFlux(true);
	}
	
	private void start_anim() {
		BytesScheme bs = new BytesScheme(6);
		FilterScheme fs = new FilterScheme(6);
		PhotonScheme ps = new PhotonScheme(6, bs, fs);
		photonFluxAtoB(ps);
		photonFluxBtoA(ps);
		mailFluxAtoB(fs);
		mailFluxBtoA(fs);
		dataFluxAtoB(bs);
		dataFluxBtoA(bs);
		pan.repaint();
	}
}