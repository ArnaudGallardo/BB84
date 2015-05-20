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
		while(peopleConnected<3) { // Waits for everyone to be connected
			System.out.println(peopleConnected);
			if(peopleConnected!=tmp) {
				if(peopleConnected==1)	// Connects Alice in the first place
					System.out.println("Waiting for Bob and Eve");
				if(peopleConnected==2)	// Then Bob
					System.out.println("Waiting for Eve");
				tmp=peopleConnected;	// Then Eve
			}
		}
		sendMessage("========= ALICE =========",connectedIp[0]);
		sendMessage("========= BOB =========",connectedIp[1]);
		sendMessage("========= EVE =========",connectedIp[2]);
		System.out.println("Everyone is connected!");
		
		System.out.println("Ask Alice for message:"); // Once everyone's connected, Alice needs to send her message
		askForMessage(getConnectedIp(0));
		while(this.message.equals("")) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Waiting for a response..."); // Waits for Alice's message
		}
		System.out.println("Message is: "+this.message);
		
		System.out.println("Ask Eve for spying:"); // Does Eve want to spy?
		askForSpying(getConnectedIp(2),false);
		
		while(this.spying.equals("")) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Waiting for a response..."); // Waits for Eve's wish to spy
		}
		System.out.println("Response is: "+this.spying); // Yes (y) or No (n) (no other answer accepted)
		
		
		boolean isSpying = this.spying.equals("y"); // True if Eve answered "y", false otherwise
				
		byte[] binMessage = Crypt.toBin(this.message); // Transforms Alice's into binary digits
		
		int oneTimePad = binMessage.length*2; // Number of photons that will be sent
		
		// Alice
		BytesScheme aliceKeyBin = new BytesScheme(oneTimePad);
		FilterScheme aliceKeyFilt = new FilterScheme(oneTimePad);
		PhotonScheme aliceKeyPhoton = new PhotonScheme(oneTimePad, aliceKeyBin, aliceKeyFilt);
		sendMessage("Sending photons", connectedIp[0]);
		Window.photonFluxAtoB(aliceKeyPhoton);
		
		// Eve
		if(isSpying) {
			sendMessage("You are spying on the conversation!", connectedIp[2]);
			FilterScheme eveHackFilter = new FilterScheme(oneTimePad);
			@SuppressWarnings("unused")
			BytesScheme eveHackBin = new BytesScheme(oneTimePad, aliceKeyPhoton, eveHackFilter);
		}
		
		// Bob
		sendMessage("Receiving photons from Alice...", connectedIp[1]);
		FilterScheme bobKeyFilt = new FilterScheme(oneTimePad);
		sendMessage("Photons received", connectedIp[1]);
		BytesScheme bobKeyBin = new BytesScheme(oneTimePad, aliceKeyPhoton, bobKeyFilt);
		sendMessage("Sending filters list to Alice", connectedIp[1]);
		// Mails
		Window.mailFluxBtoA(bobKeyFilt);
		sendMessage("Receiving filters list from Bob...", connectedIp[0]);
		
		// Sacrificing bits to detect Eve
		int[] indexId = bobKeyFilt.indexOfIden(aliceKeyFilt);
		int nbSacrificed = oneTimePad - binMessage.length;
		int[] comparison = bobKeyBin.arrayWithDiscards(aliceKeyFilt, bobKeyFilt);
		boolean detected = aliceKeyBin.detection(comparison, nbSacrificed);
		
		BytesScheme aliceFinalKey = aliceKeyBin.getFinalKey(aliceKeyFilt, bobKeyFilt);
		
		// If Eve isn't detected, the message is sent
		if(!detected) {
			sendMessage("Encrypting message...", connectedIp[0]);
			byte[] cryptedMessage = Crypt.encrypt(binMessage, aliceFinalKey.cleanKeyWithIndex(indexId));
			BytesScheme cryptedBS = new BytesScheme(cryptedMessage);
			sendMessage("Encrypted", connectedIp[0]);
			sendMessage("Sending crypted message to Bob...", connectedIp[0]);
			Window.dataFluxAtoB(cryptedBS);
			
			sendMessage("Receiving crypted message from Alice...", connectedIp[1]);
			byte[] decryptedMessage = Crypt.encrypt(cryptedMessage, aliceFinalKey.cleanKeyWithIndex(indexId));
			String bobMessage =Crypt.toStr(decryptedMessage);
			sendMessage("Message received and decrypted", connectedIp[1]);
			sendMessage("Alice said: "+bobMessage, connectedIp[1]);
			System.out.println(bobMessage);
			sendMessage("Bob received the message", connectedIp[0]);
		}
		// Otherwise, the message isn't sent 
		else {
			sendAllMessage("Eve has been detected!");
			System.out.println("Detected!!");
		}
		
		sendAllMessage("================================"); // Separation line between two "sessions"
		
		this.message = "";
		this.spying = "";
		launchSystem();
	}

	// Sends a message on a client's shell
	private void sendMessage(String message, ServerConnection conn) {
		String tmp = "i" + message;
		conn.send(tmp.getBytes(), Delivery.RELIABLE);
	}
	
	// Sends a message on all of the clients' shells
	private void sendAllMessage(String message) {
		String tmp = "i" + message;
		for(int i=0;i<this.peopleConnected;i++) {
			connectedIp[i].send(tmp.getBytes(), Delivery.RELIABLE);
		}
	}
	
	// Asks Alice for her message
	public static void askForMessage(ServerConnection conn) {
		String str = new String("o"+"m"+"What is your message:");
		conn.send(str.getBytes(), Delivery.RELIABLE);
	}

	// Asks Eve if she wants to spy or not
	public static void askForSpying(ServerConnection conn,boolean error) {
		String str = new String("o"+"s"+"Do you want to spy (y/n):");
		if(error)
			str = str + " Wrong awnser, try again: ";
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
