
public class Tests {
	static int TAILLE = 10;
	public static void mecha() {
		//On commence par créer une clé aléatoire de taille fixe
				BytesScheme aliceBitSeq = new BytesScheme(TAILLE);
				//On fabrique ensuite une séquence aléatoire de filtre polarisant
				FilterScheme aliceFilterScheme = new FilterScheme(TAILLE);
				//La création des photons polarisés se fait à l'aide des deux séquences précédente
				PhotonScheme transmittedPhoton = new PhotonScheme(TAILLE, aliceBitSeq, aliceFilterScheme);
				//On transmet ces photons
				System.out.println(transmittedPhoton);
				
				//Eve essaye de voler les infos
				FilterScheme eveFilterScheme = new FilterScheme(TAILLE);
				//Il lit les photons qui arrivent
				BytesScheme eveBitMeasurements = new BytesScheme(TAILLE, transmittedPhoton, eveFilterScheme);
				System.out.println(eveBitMeasurements);
				BytesScheme keyEve = eveBitMeasurements.getFinalKey(aliceFilterScheme, eveFilterScheme);
				System.out.println("Eve : "+keyEve);
				
				
				//Bob fabrique une séquence aléatoire de filtre polarisant
				FilterScheme bobFilterScheme = new FilterScheme(TAILLE);
				//Il lit les photons qui arrivent
				BytesScheme bobBitMeasurements = new BytesScheme(TAILLE, transmittedPhoton, bobFilterScheme);
				//Il envoi sa séquence de filtre et compare avec Alice pour avoir la clé
				BytesScheme keyBob = bobBitMeasurements.getFinalKey(aliceFilterScheme, bobFilterScheme);
				//Il possède maintenant une clé
				System.out.println("Bob : "+keyBob);
				//Alice fait pareil
				BytesScheme keyAlice = aliceBitSeq.getFinalKey(bobFilterScheme, aliceFilterScheme);
				System.out.println("Alice : "+keyAlice);
	}
	public static void tests() {

		//Etape 1 : Création d'un photon non polarisé
		Photon photon = new Photon();
		System.out.println("Photon départ : " + photon.toString());
		
		//Etape 2 : Polarisation du photon aléatoirement
		photon.setPolarization(Polarization.random());
		System.out.println("Photon apres filtre : " + photon.toString());
		
		//Etape 3 : Detection de la polarisation du photon (Bob)
		Filter filter = new Filter(Basis.HORTOGONAL);
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
}
