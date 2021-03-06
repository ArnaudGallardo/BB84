import java.io.UnsupportedEncodingException;


public class Tests {
	static int TAILLE = 10;
	public static void random() {
		byte tmp;
		for(int i=0;i<20;i++) {
			tmp = (byte)(Math.random()*2);
			System.out.println(tmp);
		}
	}
	
	// Cyphering test (from a character to an array of bytes)
	public static void encrypt() {
		char a = 'a';
		byte[] aB = Crypt.toBin(a);
		char b = 'b';
		byte[] bB = Crypt.toBin(b);
		
		Crypt.printBin(aB);
		Crypt.printBin(bB);
		
		byte[] en = Crypt.encrypt(aB, bB);
		Crypt.printBin(en);
		
	}
	
	
	public static void bytesClean() {
		BytesScheme bs = new BytesScheme(5);
		int[] index = {0,1,2,-1,-1};
		System.out.println(bs);
		Crypt.printBin(bs.cleanKeyWithIndex(index));
	}
	
	public static void mecha() {
		// Creation of a random key of fixed size
		BytesScheme aliceBitSeq = new BytesScheme(TAILLE);
		// Creation of a random scheme of polarizing filters
		FilterScheme aliceFilterScheme = new FilterScheme(TAILLE);
		// Creation of polarized photons thanks to both of the previous schemes
		PhotonScheme transmittedPhoton = new PhotonScheme(TAILLE, aliceBitSeq, aliceFilterScheme);
		// The photons are transmitted
		System.out.println(transmittedPhoton);
			
		// Eve tries to spy on the conversation
		FilterScheme eveFilterScheme = new FilterScheme(TAILLE);
		// She read the incoming photons
		BytesScheme eveBitMeasurements = new BytesScheme(TAILLE, transmittedPhoton, eveFilterScheme);
		System.out.println(eveBitMeasurements);
		BytesScheme keyEve = eveBitMeasurements.getFinalKey(aliceFilterScheme, eveFilterScheme);
		System.out.println("Eve : "+keyEve);
						
		// Bob creates of random scheme of polarizing filters		
		FilterScheme bobFilterScheme = new FilterScheme(TAILLE);
		// He reads the incoming photons
		BytesScheme bobBitMeasurements = new BytesScheme(TAILLE, transmittedPhoton, bobFilterScheme);
		// He sends his filter scheme to Alice and compares it with her to obtain the key
		BytesScheme keyBob = bobBitMeasurements.getFinalKey(aliceFilterScheme, bobFilterScheme);
		// He has now a key
		System.out.println("Bob : "+keyBob);
		
		// So does Alice
		BytesScheme keyAlice = aliceBitSeq.getFinalKey(bobFilterScheme, aliceFilterScheme);
		System.out.println("Alice : "+keyAlice);
	}
	
	public static void tests() {

		// Step 1: Non-polarized photon creation
		Photon photon = new Photon();
		System.out.println("Photon d�part : " + photon.toString());
		
		// Step 2: Random photon polarization 
		photon.setPolarization(Polarization.random());
		System.out.println("Photon apres filtre : " + photon.toString());
		
		// Step 3: Photon polarization detection (Bob)
		Filter filter = new Filter(Basis.ORTHOGONAL);
		Polarization polarRes = filter.readPolarPhoton(photon);
		System.out.println("Resultat lecture : " + polarRes);
		System.out.println("Modification du photon : " + photon.toString());
		
		FilterScheme p = new FilterScheme(10);
		System.out.println(p);
		FilterScheme p2 = new FilterScheme(10);
		System.out.println(p2);
		if(p.equals(p))
			System.out.println("EQUALS");
		else
			System.out.println("NOT EQUALS");
		int[] iden = p.indexOfIden(p2);
		System.out.print("Index identique : [");
		for(int i=0;i<iden.length-1;i++) {
			System.out.print(iden[i]+", ");
		}
		System.out.println(iden[iden.length-1]+"]");
		
		BytesScheme b = new BytesScheme(10);
		System.out.println(b);
		
		PhotonScheme ph = new PhotonScheme(10, b, p2);
		System.out.println(ph);
	}
	
	// Tests the conversion from string to bytes and the other way round
	public static void crypt() {
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
	
	// Tests Eve's spying and detection
	public static void testEve() {
		int size = 80;
		BytesScheme aliceKey = new BytesScheme(size);
		System.out.println(aliceKey);
		FilterScheme aliceFilter = new FilterScheme(size);
		System.out.println(aliceFilter);
		PhotonScheme photonBeam = new PhotonScheme(size, aliceKey, aliceFilter);
		
		// Eve
		Filter filtre = new Filter(Basis.random());
		for(int i=0;i<size/2;i++) {
			filtre = new Filter(Basis.random());
			filtre.readPolarPhoton(photonBeam.getPhoton(i));
		}
		
		FilterScheme bobFilter = new FilterScheme(size);
		System.out.println(bobFilter);
		BytesScheme bobKey = new BytesScheme(size, photonBeam, bobFilter);
		System.out.println(bobKey);
		BytesScheme finalKey = bobKey.getFinalKey(aliceFilter, bobFilter);
		System.out.println(finalKey);
		
		int nbSacrificed = 20*size/100; // For the test, sacrificing of 20% of the key
		int[] comparison = bobKey.arrayWithDiscards(aliceFilter, bobFilter);
		boolean detected = aliceKey.detection(comparison, nbSacrificed);
		
		System.out.println(detected);
	}
}
