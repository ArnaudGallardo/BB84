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
	private boolean directionPhotonFlux = true; //true = AtoB ; false = BtoA
	
	private int[] mailFlux = new int[10];
	private int sizeMailFlux = 0;
	private int animMailFlux = 0;
	private boolean directionMailFlux = true;
	
	private int[] dataFlux = new int[10];
	private int sizeDataFlux = 0;
	private int animDataFlux = 0;
	private boolean directionDataFlux = true;
	
	private boolean isCoAlice = false;
	private boolean isCoBob = false;
	private boolean isCoEve = false;
	
	
	public void paintComponent(Graphics g){
		Image fiber = null;
		Image p0 = null;
		Image p1 = null;
		Image p2 = null;
		Image p3 = null;
		Image mails = null;
		Image m0 = null;
		Image m1 = null;
		Image data = null;
		Image d0 = null;
		Image d1 = null;
		//Load images
		try {
			fiber = ImageIO.read(new File("img/fiber.png"));
			p0 = ImageIO.read(new File("img/vertical.png"));
			p1 = ImageIO.read(new File("img/horizontal.png"));
			p2 = ImageIO.read(new File("img/slash.png"));
			p3 = ImageIO.read(new File("img/antislash.png"));
			mails = ImageIO.read(new File("img/mails.png"));
			m0 = ImageIO.read(new File("img/mp.png"));
			m1 = ImageIO.read(new File("img/mx.png"));
			data = ImageIO.read(new File("img/data.png"));
			d0 = ImageIO.read(new File("img/p0.png"));
			d1 = ImageIO.read(new File("img/p1.png"));
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
		g.drawImage(mails, (this.getWidth()-623)/2, ((height/3)/2)-(110/2)+menu_size, this);
		g.drawImage(fiber, (this.getWidth()-623)/2, (height-110)/2+menu_size, this);
		g.drawImage(data, (this.getWidth()-623)/2, ((height/3)/2)-(110/2)+(2*height/3)+menu_size, this);
		
		//Draw animation photon
		int x;
		int y = ((height-110)/2)+147;
		if(directionPhotonFlux) {
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
		else {
			for(int i=sizePhotonFlux-1;i>=0;i--) {
				x=((this.getWidth()-600)/2)+60*(11-sizePhotonFlux+i)-60;
				x-=this.getAnimPhotonFlux();
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
		//Draw anim mails
		y = ((height/3)/2)-(110/2)+147;
		if(directionMailFlux) {
			for(int i=0;i<sizeMailFlux;i++) {
				x=((this.getWidth()-600)/2)+60*(sizeMailFlux-i)-60;
				x+=this.getAnimMailFlux();
				if(mailFlux[i]==0)
					g.drawImage(m0, x , y ,56,56, this);
				else 
					g.drawImage(m1,x, y ,56,56, this);
			}
		}
		else {
			for(int i=sizeMailFlux-1;i>=0;i--) {
				x=((this.getWidth()-600)/2)+60*(11-sizeMailFlux+i)-60;
				x-=this.getAnimMailFlux();
				if(mailFlux[i]==0)
					g.drawImage(m0, x , y ,56,56, this);
				else 
					g.drawImage(m1,x, y ,56,56, this);
			}
		}
		//Draw anim data
		y = ((height/3)/2)-(110/2)+(2*height/3)+147;
		if(directionDataFlux) {
			for(int i=0;i<sizeDataFlux;i++) {
				x=((this.getWidth()-600)/2)+60*(sizeDataFlux-i)-60;
				x+=this.getAnimDataFlux();
				if(dataFlux[i]==0)
					g.drawImage(d0, x , y ,56,56, this);
				else 
					g.drawImage(d1,x, y ,56,56, this);
			}
		}
		else {
			for(int i=sizeDataFlux-1;i>=0;i--) {
				x=((this.getWidth()-600)/2)+60*(11-sizeDataFlux+i)-60;
				x-=this.getAnimDataFlux();
				if(dataFlux[i]==0)
					g.drawImage(d0, x , y ,56,56, this);
				else 
					g.drawImage(d1,x, y ,56,56, this);
			}
		}
		
		//Draw connected
	    g.drawString("Alice", 50, 75);
	    g.drawString("Eve", (this.getWidth()/2)-25, 75);
	    g.drawString("Bob", this.getWidth()-50, 75);
		if(isCoAlice)
			g.setColor(Color.green);
		else
			g.setColor(Color.red);
		g.fillOval(25, 60, 15, 15);
		if(isCoBob)
			g.setColor(Color.green);
		else
			g.setColor(Color.red);
	    g.fillOval(this.getWidth()-70, 60, 15, 15);
	    if(isCoEve)
			g.setColor(Color.green);
		else
			g.setColor(Color.red);
	    g.fillOval((this.getWidth()/2)-45, 60, 15, 15);
	    
	    //Signature
	    Font fontSign = new Font("Lato",Font.BOLD,15);
	    g.setFont(fontSign);
		g.setColor(Color.black);
	    g.drawString("Arnaud-Marie GALLARDO     Candice Bentejac", 10, this.getHeight()-10);
	    
	}   
	
	public void setIsCoAlice(boolean b) {
		isCoAlice = b;
	}
	
	public void setIsCoBob(boolean b) {
		isCoBob = b;
	}
	
	public boolean getIsCoAlice() {
		return isCoAlice;
	}
	
	public boolean getIsCoBob() {
		return isCoBob;
	}
	
	public void setIsCoEve(boolean b) {
		isCoEve = b;
	}
	
	public boolean getIsCoEve() {
		return isCoEve;
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
	
	public void setDirectionPhotonFlux(boolean dir) {
		this.directionPhotonFlux = dir;
	}
	
	public boolean getDirectionPhotonFlux() {
		return directionPhotonFlux;
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

	
	public void setMailFlux(int i, int v) {
		this.mailFlux[i] = v;
	}
	
	public int getMailFlux(int i) {
		return this.mailFlux[i];
	}
	
	public void setSizeMailFlux(int v) {
		this.sizeMailFlux = v;
	}
	
	public int getSizeMailFlux() {
		return this.sizeMailFlux;
	}
	
	public void setDirectionMailFlux(boolean dir) {
		this.directionMailFlux = dir;
	}
	
	public boolean getDirectionMailFlux() {
		return directionMailFlux;
	}
	
	public void setAnimMailFlux(int v) {
		this.animMailFlux = v;
	}
	
	public int getAnimMailFlux() {
		return this.animMailFlux;
	}
	
	public void addMail(Filter f) {
		System.out.println(this.getSizeMailFlux()+" | "+f);
		
		if(f.getBasis()==Basis.HORTOGONAL)
			this.setMailFlux(getSizeMailFlux(), 0);
		else 
			this.setMailFlux(getSizeMailFlux(), 1);
		
		if(getSizeMailFlux()>=9) {
			for(int i=0;i<getSizeMailFlux();i++) {
				this.setMailFlux(i, this.getMailFlux(i+1));
			}
		}
		else
			this.setSizeMailFlux(this.getSizeMailFlux()+1);
	}
	
	public void clearMail() {
		setSizeMailFlux(0);
	}
	
	public void setDataFlux(int i, int v) {
		this.dataFlux[i] = v;
	}
	
	public int getDataFlux(int i) {
		return this.dataFlux[i];
	}
	
	public void setSizeDataFlux(int v) {
		this.sizeDataFlux = v;
	}
	
	public int getSizeDataFlux() {
		return this.sizeDataFlux;
	}
	
	public void setDirectionDataFlux(boolean dir) {
		this.directionDataFlux = dir;
	}
	
	public boolean getDirectionDataFlux() {
		return directionDataFlux;
	}
	
	public void setAnimDataFlux(int v) {
		this.animDataFlux = v;
	}
	
	public int getAnimDataFlux() {
		return this.animDataFlux;
	}
	
	public void addData(byte b) {
		System.out.println(this.getSizeDataFlux()+" | "+b);
		
		if(b==0)
			this.setDataFlux(getSizeDataFlux(), 0);
		else 
			this.setDataFlux(getSizeDataFlux(), 1);
		
		if(getSizeDataFlux()>=9) {
			for(int i=0;i<getSizeDataFlux();i++) {
				this.setDataFlux(i, this.getDataFlux(i+1));
			}
		}
		else
			this.setSizeDataFlux(this.getSizeDataFlux()+1);
	}
	
	public void clearData() {
		setSizeDataFlux(0);
	}
}