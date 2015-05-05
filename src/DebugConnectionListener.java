import jexxus.common.Connection;
import jexxus.common.ConnectionListener;
import jexxus.server.ServerConnection;

public class DebugConnectionListener implements ConnectionListener{

  public void connectionBroken(Connection broken, boolean forced){
    System.out.println("Connection lost: "+broken);
  }

  public void receive(byte[] data, Connection from){
    System.out.println("Received message: "+new String(data));
  }

  public void clientConnected(ServerConnection conn){
    System.out.println("Client Connected: "+conn.getIP());
  }

}