
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Etape 1 : Création d'un photon non polarisé
		Photon photon = new Photon();
		System.out.println("Photon départ : " + photon.toString());
		
		//Etape 2 : Polarisation du photon aléatoirement
		photon.setPolarization(Polarization.itopolar((int)(Math.random()*4)));
		System.out.println("Photon apres filtre : " + photon.toString());
		
		//Etape 3 : Detection de la polarisation du photon (Bob)
		Filter filter = new Filter(Basis.HORTOGONAL);
		Polarization polarRes = filter.readPolarPhoton(photon);
		System.out.println("Resultat lecture : " + polarRes);
		System.out.println("Modification du photon : " + photon.toString());
	}

}
