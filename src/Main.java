import jexxus.server.Server;


public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Tests.tests();
		//Tests.mecha();
		//Tests.crypt();
		
		//@SuppressWarnings("unused")
		//Window fen = new Window();
		Benchmark.launch();
		//Tests.testEve();
		//Tests.random();
		
		//Tests.encrypt();
		//Tests.bytesClean();
		
		
		QuantumConnectionListener qcl = new QuantumConnectionListener();
		Server server = new Server(qcl, 15652, true);
		server.startServer();
		@SuppressWarnings("unused")
		Window fen = new Window();
		qcl.getSc().launchSystem();
	}

}
