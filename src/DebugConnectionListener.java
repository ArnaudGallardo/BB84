import jexxus.common.Connection;
import jexxus.common.ConnectionListener;
import jexxus.server.ServerConnection;

public class DebugConnectionListener implements ConnectionListener{

	// Tells that the connection between the server and the clients has been lost
  public void connectionBroken(Connection broken, boolean forced){
    System.out.println("Connection lost: "+ broken);
  }

  // Tells that the client has received a message
  public void receive(byte[] data, Connection from){
    System.out.println("Received message: "+ new String(data));
  }
  
  // Tells that the client is connected
  public void clientConnected(ServerConnection conn){
    System.out.println("Client Connected: "+ conn.getIP());
  }

}