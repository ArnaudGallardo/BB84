import java.awt.Color;
import java.awt.Font;
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
	private int animPhotonFlux = 0;
	
	public void paintComponent(Graphics g){
		Image fiber = null;
		Image p0 = null;
		Image p1 = null;
		Image p2 = null;
		Image p3 = null;
		//Load images
		try {
			fiber = ImageIO.read(new File("img/fiber.png"));
			p0 = ImageIO.read(new File("img/vertical.png"));
			p1 = ImageIO.read(new File("img/horizontal.png"));
			p2 = ImageIO.read(new File("img/slash.png"));
			p3 = ImageIO.read(new File("img/antislash.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Clear panel
		g.setColor(Color.white);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    //Draw Menu
	    int menu_size = 100;
	    g.setColor(new Color(0,158,224));
	    g.fillRect(0, 0, this.getWidth(), menu_size);
	    g.setColor(new Color(68,58,49));
	    g.fillRect(0, menu_size-10, this.getWidth(), 10);
	    Font font = new Font("Lato",Font.BOLD,20);
	    g.setFont(font);
	    g.drawString("BB84 Quantum Cryptography Protocol", 5, 25);
	    
	    //rgb(68,58,49)
	    
	    int height = this.getHeight()-100;
		g.drawImage(fiber, (this.getWidth()-623)/2, ((height/3)/2)-(110/2)+menu_size, this);
		g.drawImage(fiber, (this.getWidth()-623)/2, (height-110)/2+menu_size, this);
		g.drawImage(fiber, (this.getWidth()-623)/2, ((height/3)/2)-(110/2)+(2*height/3)+menu_size, this);
		
		//Draw animation photon
		int x;
		int y = ((height-110)/2)+147;
		for(int i=0;i<sizePhotonFlux;i++) {
			x=((this.getWidth()-600)/2)+60*(sizePhotonFlux-i)-60;
			x+=this.getAnimPhotonFlux();
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
	
	public void setAnimPhotonFlux(int v) {
		this.animPhotonFlux = v;
	}
	
	public int getAnimPhotonFlux() {
		return this.animPhotonFlux;
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