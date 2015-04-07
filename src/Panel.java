import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
 
@SuppressWarnings("serial")
public class Panel extends JPanel {
	// pos en Y = +47 par rapport a fiber
	
	private int[] photonFlux = new int[10];
	private int sizePhotonFlux = 0;
	
	public void paintComponent(Graphics g){
		Image fiber = null;
		Image p0 = null;
		Image p1 = null;
		Image p2 = null;
		Image p3 = null;
		//Background youpii
		try {
			fiber = ImageIO.read(new File("img/fiber.png"));
			p0 = ImageIO.read(new File("img/vertical.png"));
			p1 = ImageIO.read(new File("img/horizontal.png"));
			p2 = ImageIO.read(new File("img/slash.png"));
			p3 = ImageIO.read(new File("img/antislash.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//EFFACER
		g.setColor(Color.white);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    //BACKGROUND
		g.drawImage(fiber, (this.getWidth()-623)/2, (this.getHeight()-110)/2, this);
		int x;
		int y = ((this.getHeight()-110)/2)+47;
		for(int i=0;i<sizePhotonFlux;i++) {
			x=((this.getWidth()-600)/2)+60*(sizePhotonFlux-i)-30;
			if(photonFlux[i]==0)
				g.drawImage(p0, x , y ,56,56, this);
			else if(photonFlux[i]==1)
				g.drawImage(p1,x, y ,56,56, this);
			else if(photonFlux[i]==2)
				g.drawImage(p2,x, y ,56,56, this);
			else
				g.drawImage(p3,x, y ,56,56, this);
		}
	}   
	
	public void setPhotonFlux(int i, int v) {
		this.photonFlux[i] = v;
	}
	
	public int getPhotonFlux(int i) {
		return this.photonFlux[i];
	}
	
	public void setSizePhotonFlux(int v) {
		this.sizePhotonFlux = v;
	}
	
	public int getSizePhotonFlux() {
		return this.sizePhotonFlux;
	}
	
	public void addPhoton(Photon p) {
		System.out.println(this.getSizePhotonFlux()+" | "+p);
		Polarization po = p.getPolarization();
		if(po==Polarization.VERTICAL)
			this.setPhotonFlux(getSizePhotonFlux(), 0);
		else if(po==Polarization.HORIZONTAL)
			this.setPhotonFlux(getSizePhotonFlux(), 1);
		else if(po==Polarization.SLASH)
			this.setPhotonFlux(getSizePhotonFlux(), 2);
		else
			this.setPhotonFlux(getSizePhotonFlux(), 3);
		
		if(getSizePhotonFlux()>=9) {
			for(int i=0;i<getSizePhotonFlux();i++) {
				this.setPhotonFlux(i, this.getPhotonFlux(i+1));
			}
		}
		else
			this.setSizePhotonFlux(this.getSizePhotonFlux()+1);
	}
	
	public void clearPhoton() {
		setSizePhotonFlux(0);
	}
}