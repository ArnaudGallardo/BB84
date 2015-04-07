import java.io.UnsupportedEncodingException;


public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Tests.tests();
		//Tests.mecha();
		
		//@SuppressWarnings("unused")
		Window fen = new Window();
		
		char lettre = '1';
		int ascii = (int) lettre;
		System.out.println(lettre + " : "+ascii+" : "+Integer.toString(ascii,2));
		Crypt.printBin(Crypt.toBin(lettre));
		Crypt.printBin(Crypt.toBin("123"));
		
		byte[] test = Crypt.toBin(lettre);
		for(int i=0;i<test.length;i++){
			test[i]+=48;
		}
		try {
			String testC = new String(test, "UTF-8");
			int testI = Integer.parseInt(testC,2);
			System.out.println(testI +" : "+ (char) testI);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Testing new one :");
		String depart = "I'm groot !";
		System.out.println("Depart : "+depart);
		byte[] bytes = Crypt.toBin(depart);
		Crypt.printBin(bytes);
		String arrive = Crypt.toStr(bytes);
		System.out.println("Arrive : "+ arrive);
	}

}
