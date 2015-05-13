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
		boolean isSpying = this.spying.equals("y");
				
		byte[] binMessage = Crypt.toBin(this.message);
		
		//Si jamais, on utilise a nouveau 2^n
		/*byte[][] binMessageTab = new byte[(binMessage.length/8)+1][8];
		for(int i=0;i<(binMessage.length/8)+1;i++) {
			for(int j=0;j<8;j++) {
				if((i*8)+j<binMessage.length)
					binMessageTab[i][j]=binMessage[(i*8)+j];
			}
		
		}*/
		//int oneTimePad = (int) Math.pow(2, binMessageTab[0].length);
		
		int oneTimePad = binMessage.length*2;
		
		//Alice
		BytesScheme aliceKeyBin = new BytesScheme(oneTimePad);
		FilterScheme aliceKeyFilt = new FilterScheme(oneTimePad);
		PhotonScheme aliceKeyPhoton = new PhotonScheme(oneTimePad, aliceKeyBin, aliceKeyFilt);
		sendMessage("Sending photons", connectedIp[0]);
		Window.photonFluxAtoB(aliceKeyPhoton);
		
		//Eve
		if(isSpying) {
			sendMessage("You are spying the conversation !", connectedIp[2]);
			FilterScheme eveHackFilter = new FilterScheme(oneTimePad);
			@SuppressWarnings("unused")
			BytesScheme eveHackBin = new BytesScheme(oneTimePad, aliceKeyPhoton, eveHackFilter);
		}
		
		//Bob
		sendMessage("Receiving data", connectedIp[1]);
		FilterScheme bobKeyFilt = new FilterScheme(oneTimePad);
		BytesScheme bobKeyBin = new BytesScheme(oneTimePad, aliceKeyPhoton, bobKeyFilt);
		sendMessage("Sending filters", connectedIp[1]);
		//Mails
		Window.mailFluxBtoA(bobKeyFilt);
		sendMessage("Receiving filters", connectedIp[0]);
		
		//BytesScheme eveFinalKey = eveHackBin.getFinalKey(aliceKeyFilt, eveHackFilter);
		//Que faire d'Eve ??
		int percentOfKey = 20; 
		int[] indexId = bobKeyFilt.indexOfIden(aliceKeyFilt);
		boolean detected = aliceKeyBin.eveDetected(bobKeyBin, indexId, percentOfKey);
		
		//Mettre en commun finalKey et detection
		BytesScheme aliceFinalKey = aliceKeyBin.getFinalKey(aliceKeyFilt, bobKeyFilt);
		
		
		if(!detected) {
			sendMessage("Encrypting message...", connectedIp[0]);
			byte[] cryptedMessage = Crypt.encrypt(binMessage, aliceFinalKey.cleanKeyWithIndex(indexId));
			BytesScheme cryptedBS = new BytesScheme(cryptedMessage);
			sendMessage("Sending crypted message...", connectedIp[0]);
			Window.dataFluxAtoB(cryptedBS);
			
			sendMessage("Receiving message", connectedIp[1]);
			byte[] decryptedMessage = Crypt.encrypt(cryptedMessage, aliceFinalKey.cleanKeyWithIndex(indexId));
			String bobMessage =Crypt.toStr(decryptedMessage);
			sendMessage("Message received and decrypted", connectedIp[1]);
			sendMessage("Alice said : "+bobMessage, connectedIp[1]);
			System.out.println(bobMessage);
			sendMessage("Bob received the message", connectedIp[0]);
		}
		else {
			sendAllMessage("Eve detected");
			System.out.println("Detected!!");
		}
		
		
		this.message = "";
		this.spying = "";
		launchSystem();
	}

	private void sendMessage(String message, ServerConnection conn) {
		String tmp = "i" + message;
		conn.send(tmp.getBytes(), Delivery.RELIABLE);
	}
	
	private void sendAllMessage(String message) {
		String tmp = "i" + message;
		for(int i=0;i<this.peopleConnected;i++) {
			connectedIp[i].send(tmp.getBytes(), Delivery.RELIABLE);
		}
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
