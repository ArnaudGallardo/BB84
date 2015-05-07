import jexxus.common.Delivery;
import jexxus.server.ServerConnection;


public class ServerCore {
	
	private boolean[] connected = {false,false,false}; // Alice, Bob, Eve
	private ServerConnection[] connectedIp = new ServerConnection[3]; // Alice, Bob, Eve
	private int peopleConnected = 0;
	private String message = "";
	private boolean spying = false;
	
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
			System.out.println("Waiting for a message...");
		}
		System.out.println("Message is : "+this.message);
		
		
	}

	public static void askForMessage(ServerConnection conn) {
		String str = new String("o"+"m"+"What is your message :");
		conn.send(str.getBytes(), Delivery.RELIABLE);
	}

	public static void askForSpying(ServerConnection conn) {
		String str = new String("o"+"s"+"Do you want to spy (y/n) :");
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
}
