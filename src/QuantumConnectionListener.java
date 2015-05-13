import jexxus.common.Connection;
import jexxus.common.ConnectionListener;
import jexxus.common.Delivery;
import jexxus.server.ServerConnection;

public class QuantumConnectionListener implements ConnectionListener{

	
	private ServerCore sc = new ServerCore();
	
	public ServerCore getSc() {
		return sc;
	}
	
	public void connectionBroken(Connection broken, boolean forced){
		sc.decPeopleConnected();
		if(broken.toString()==sc.getConnectedIp(0).getIP()) {
			System.out.println("Alice lost: "+broken);
			sc.setConnected(false,0);
			sc.setConnectedIp(null,0);
			Window.disconnectAlice();
		}
		else if(broken.toString()==sc.getConnectedIp(1).getIP()) {
			System.out.println("Bob lost: "+broken);
			sc.setConnected(false,1);
			sc.setConnectedIp(null,1);
			Window.disconnectBob();
		}
		else if(broken.toString()==sc.getConnectedIp(2).getIP()) {
			System.out.println("Eve lost: "+broken);
			sc.setConnected(false,2);
			sc.setConnectedIp(null,2);
			//Window.disconnectEve();
		}
	}
	
	public void receive(byte[] data, Connection from){
		String msg = new String(data);
		System.out.println("Received message: "+new String(data));
		String type = msg.substring(0,1);
    	if(type.equals("r")) {
    		String subtype = msg.substring(1,2);
    		if(subtype.equals("m"))
    			sc.setMessage(msg.substring(2));
    		if(subtype.equals("s"))
    			sc.setSpying(msg.substring(2));
    	}
    	
		/*if(from.toString()==sc.getConnectedIp(0).getIP()) {
			Window.dataFluxAtoB(bs);
		}
		else if(from.toString()==sc.getConnectedIp(1).getIP()) {
			Window.dataFluxBtoA(bs);
		}*/
		from.send(new String("Recu\n").getBytes(), Delivery.RELIABLE);
	}
	
	public void clientConnected(ServerConnection conn){
		sc.incPeopleConnected();
		if(!sc.getConnected(0)) {
			System.out.println("Alice Connected: "+conn.getIP());
			conn.send(new String("ALICE").getBytes(), Delivery.RELIABLE);
			sc.setConnected(true,0);
			sc.setConnectedIp(conn,0);
			Window.connectAlice();
		}
		else if(!sc.getConnected(1)) {
			System.out.println("Bob Connected: "+conn.getIP());
			conn.send(new String("BOB").getBytes(), Delivery.RELIABLE);
			sc.setConnected(true,1);
			sc.setConnectedIp(conn,1);
			Window.connectBob();
		}
		else if(!sc.getConnected(2)) {
			System.out.println("Eve Connected: "+conn.getIP());
			conn.send(new String("EVE").getBytes(), Delivery.RELIABLE);
			sc.setConnected(true,2);
			sc.setConnectedIp(conn,2);
			Window.connectEve();
		}
	}
	
}