import jexxus.common.Connection;
import jexxus.common.ConnectionListener;
import jexxus.common.Delivery;
import jexxus.server.ServerConnection;

public class QuantumConnectionListener implements ConnectionListener{

	private boolean[] connected = {false,false,false}; // Alice, Bob, Eve
	private String[] connectedIp = new String[3]; // Alice, Bob, Eve
	
	public void connectionBroken(Connection broken, boolean forced){
		if(broken.toString()==connectedIp[0]) {
			System.out.println("Alice lost: "+broken);
			connected[0]=false;
			connectedIp[0]=null;
			Window.disconnectAlice();
		}
		else if(broken.toString()==connectedIp[1]) {
			System.out.println("Bob lost: "+broken);
			connected[1]=false;
			connectedIp[1]=null;
			Window.disconnectBob();
		}
		else if(broken.toString()==connectedIp[2]) {
			System.out.println("Eve lost: "+broken);
			connected[2]=false;
			connectedIp[2]=null;
			//Window.disconnectEve();
		}
	}
	
	public void receive(byte[] data, Connection from){
		System.out.println("Received message: "+new String(data));
		byte[] b = Crypt.toBin(new String(data));
		BytesScheme bs = new BytesScheme(b);
		System.out.println(bs);
		if(from.toString()==connectedIp[0]) {
			Window.dataFluxAtoB(bs);
		}
		else if(from.toString()==connectedIp[1]) {
			Window.dataFluxBtoA(bs);
		}
		from.send(new String("Recu\n").getBytes(), Delivery.RELIABLE);
	}
	
	public void clientConnected(ServerConnection conn){
		if(!connected[0]) {
			System.out.println("Alice Connected: "+conn.getIP());
			connected[0]=true;
			connectedIp[0]=conn.getIP();
			Window.connectAlice();
		}
		else if(!connected[1]) {
			System.out.println("Bob Connected: "+conn.getIP());
			connected[1]=true;
			connectedIp[1]=conn.getIP();
			Window.connectBob();
		}
		else if(!connected[2]) {
			System.out.println("Eve Connected: "+conn.getIP());
			connected[2]=true;
			connectedIp[2]=conn.getIP();
			//Window.connectEve();
		}
	}
	
}