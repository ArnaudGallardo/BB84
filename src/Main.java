import jexxus.server.Server;


public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Tests.tests();
		//Tests.mecha();
		//Tests.crypt();
		
		//@SuppressWarnings("unused")
		//Window fen = new Window();
		//Benchmark.launch();
		//Tests.testEve();
		
		Server server = new Server(new QuantumConnectionListener(), 15652, true);
		server.startServer();
		@SuppressWarnings("unused")
		Window fen = new Window();
	}

}
