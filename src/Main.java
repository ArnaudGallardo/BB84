import jexxus.server.Server;


public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length==1) {
			int argInt = new Integer(args[0]);
			
			switch(argInt) {
			case 0:
				Benchmark.launch();
				break;
			case 1:
				QuantumConnectionListener qcl = new QuantumConnectionListener();
				Server server = new Server(qcl, 15652, true);
				server.startServer();
				@SuppressWarnings("unused")
				Window fen = new Window();
				qcl.getSc().launchSystem();
				break;
			case 2:
				Tests.tests();
				Tests.mecha();
				Tests.crypt();
				Tests.testEve();
				Tests.random();
				Tests.encrypt();
				Tests.bytesClean();
				break;
			default:
				howto();
			}
		}
		else
			howto();
	}

	public static void howto() {
		System.out.println("Bad arguments !");
		System.out.println("0 : launch benchmark");
		System.out.println("1 : launch server");
		System.out.println("2 : launch tests");
	}

}
