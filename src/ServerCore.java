import jexxus.common.Delivery;
import jexxus.server.ServerConnection;


public class ServerCore {
	
	private boolean[] connected = {false,false,false}; // Alice, Bob, Eve
	private ServerConnection[] connectedIp = new ServerConnection[3]; // Alice, Bob, Eve
	private int peopleConnected = 0;
	private String message = "";
	private String spying = "";
	
	public ServerCore() {
		
	}
	
	public void launchSystem() {
		System.out.println("Waiting for Alice, Bob and Eve.");
		int tmp = 0;
		while(peopleConnected<3) {
			if(peopleConnected!=tmp) {
				if(peopleConnected==1)
					System.out.println("Waiting for Bob and Eve");
				if(peopleConnected==2)
					System.out.println("Waiting for Eve");
				tmp=peopleConnected;
			}
		}
		System.out.println("Everyone connected !");
		System.out.println("Ask Alice for message :");
		askForMessage(getConnectedIp(0));
		while(this.message.equals("")) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Waiting for a response...");
		}
		System.out.println("Message is : "+this.message);
		
		System.out.println("Ask Eve for spying :");
		askForSpying(getConnectedIp(2),false);
		
		while(this.spying.equals("")) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Waiting for a response...");
		}
		System.out.println("Response is : "+this.spying); //y or n
		
		//CONTINUER ICI
		//Besoin des r�glages, puis sc�nario classique de cryptage + envoi des donn�es
		boolean spying = this.spying=="y";
		
		byte[] binMessage = Crypt.toBin(this.message);
		
		int oneTimePad = (int) Math.pow(2, binMessage.length);
		
		BytesScheme aliceKeyBin = new BytesScheme(oneTimePad);
		FilterScheme aliceKeyFilt = new FilterScheme(oneTimePad);
		PhotonScheme aliceKeyPhoton = new PhotonScheme(oneTimePad, aliceKeyBin, aliceKeyFilt);
		
		
		
		
		
		
	}

	public static void askForMessage(ServerConnection conn) {
		String str = new String("o"+"m"+"What is your message :");
		conn.send(str.getBytes(), Delivery.RELIABLE);
	}

	public static void askForSpying(ServerConnection conn,boolean error) {
		String str = new String("o"+"s"+"Do you want to spy (y/n) :");
		if(error)
			str = str + " Bad awnser, try again : ";
		conn.send(str.getBytes(), Delivery.RELIABLE);
	}

	public void incPeopleConnected() {
		this.peopleConnected++;
	}


	public void decPeopleConnected() {
		this.peopleConnected--;
	}


	public boolean getConnected(int i) {
		return connected[i];
	}


	public void setConnected(boolean connected, int i) {
		this.connected[i] = connected;
	}


	public ServerConnection getConnectedIp(int i) {
		return connectedIp[i];
	}


	public void setConnectedIp(ServerConnection connectedIp, int i) {
		this.connectedIp[i] = connectedIp;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setSpying(String spying) {
		if(spying.equals("y") || spying.equals("n"))
			this.spying = spying;
		else
			askForSpying(getConnectedIp(2),true);
	}
}
