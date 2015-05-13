import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

/*
 * TODO: - Statistiques avec le chiffre de Vernam : on code 2^n photons, n étant le nombre de caractères du messages à crypter
 * 		 	-> Un caractère est codé sur 8 octets : donc génération totale de photons = 2^(n*8)
 * 		 - A la fin, le nombre de bits de la clé doit être égal au nombre de caractères du message
 * 			-> La clé aura une longueur de n*8 bits
 * 		 - On doit donc sacrifier k - n*8 bits, k étant la longueur de la clé obtenue à l'issue de la comparaison des filtres
 * 
 * Notes :
 * 			1ère colonne : Nombre de qbits envoyés
 * 			2ème colonne : Nombre de qbits correctement déchiffrés par Eve
 * 			3ème colonne : Nombre de qbits correctement déchiffrés par Bob
 * 			-> A insérer : Nombre de qbits de la clé après comparaison des filtres
 * 			4ème colonne : Pourcentage de la clé connue par Eve
 * 			5ème colonne : Pourcentage de qbits sacrifiés
 * 			6ème colonne : Détection (ou pas) d'Eve
 */			



public class Benchmark {
	public static void launch() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		test(100,7,100,workbook);
		test(100,13,100,workbook);
		test(100,20,100,workbook);
		test(100,50,100,workbook);
		try {
            FileOutputStream out = 
                    new FileOutputStream(new File("C:\\Users\\Candice\\Documents\\testQuantique.xls"));
            workbook.write(out);
            workbook.close();
            out.close();
            System.out.println("Excel written successfully..");
            System.out.println("Closing.");
             
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@SuppressWarnings("deprecation")
	public static void test(int size_max, int percentOfVerification, int percentOfAttack, HSSFWorkbook workbook) {
		StringBuffer name = new StringBuffer();
		name.append("max=");
		name.append(size_max);
		name.append(";percentVerif=");
		name.append(percentOfVerification);
		name.append("%;percentAttack=");
		name.append(percentOfAttack);
		
	    HSSFSheet sheet = workbook.createSheet(name.toString());
	    /*
        //MOI
        int[] result;
        for(int i=0; i<size_max;i++) {
        	result = compute(48+i*8,percentOfVerification,percentOfAttack);
            Row row = sheet.createRow(i);
        	for(int j=0;j<result.length;j++) {
        		Cell cell = row.createCell(j);
                cell.setCellValue(result[j]);
        	}
        }
        //FIN MOI
         */
        
        int[] result;
        
        // Style de cellules
        HSSFCellStyle cellStyle = null; // Pour style des cellules
        HSSFFont font = workbook.createFont(); // Création de la "police" pour pouvoir faire la mise en forme
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // Mise en gras
        cellStyle = workbook.createCellStyle(); // On initialise le style des cellules
        cellStyle.setFont(font);
        
        Row firstLine = sheet.createRow(0);
        
        
        String[] s = {"Number of Qbits sent by Alice", "Number of Qbits correctly read by Eve", 
        		"Number of Qbits correctly read by Bob", "Part of the key Eve knows (in %)", 
        		"Part of the key that is sacrificed (in %)", "Eve's detection"};
        for(int i = 0; i < 6; i++)
        {
        	Cell cell = firstLine.createCell(i);   	
        	cell.setCellValue(s[i]);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(i);
        }
        
        for(int i=1; i<=size_max;i++) {
        	result = compute(48+i*8,percentOfVerification,percentOfAttack);
            Row row = sheet.createRow(i);
        	for(int j=0;j<result.length;j++) {
        		Cell cell = row.createCell(j);
                cell.setCellValue(result[j]);
        	}
        }
        
        // Pour Vernam
        /* size_max = nombre max de caractères
         * On part d'un minimum de 6 caractères
         * 
         */
        
        System.out.println("Page done.");
	}
	
	public static int[] compute(int key_size, int percentOfKey, int percentOfAttack) {
		int[] result = new int[6];
		int nb_correct_eve = 0;
		FilterScheme filtersAlice = new FilterScheme(key_size);
		BytesScheme bytesAlice = new BytesScheme(key_size);
		PhotonScheme psAlice = new PhotonScheme(key_size, bytesAlice, filtersAlice);
		Photon photonAlice = new Photon();
		Photon photonEve = new Photon();
		int[] indexGoodReadingOfEve = new int[key_size];
		Polarization polarEve;
		FilterScheme filtersEve = new FilterScheme(key_size);
		int[] readByEve = new int[key_size];
		int nbToRead = percentOfAttack*key_size/100;
		for(int i=0; i<nbToRead;i++) {  //Pour chaque photons
			int random = (int)(Math.random()*key_size);
			while(isInArray(random,readByEve)) {
				random = (int)(Math.random()*key_size);
			}
			photonAlice = psAlice.getPhoton(random).clone(); //On récupère (sans modifier) le photon
			polarEve = filtersEve.getFilter(random).readPolarPhoton(psAlice.getPhoton(random)); //On récupère pour Eve (en Modifiant) le photon
			photonEve.setPolarization(polarEve); //On simule un photon pour Eve
			if(photonAlice.equals(photonEve)) { //On regarde si ils sont égaux
				indexGoodReadingOfEve[nb_correct_eve]=random;
				nb_correct_eve++;
			}
		}
		
		FilterScheme filtersBob = new FilterScheme(key_size);
		BytesScheme bytesBob = new BytesScheme(key_size, psAlice, filtersBob);
		int[] goodsAliceBob = filtersBob.indexOfIden(filtersAlice);
		boolean eveDetected = bytesBob.eveDetected(bytesAlice, goodsAliceBob, percentOfKey);
		int eveDetectedInt = 0;
		if(eveDetected)
			eveDetectedInt = 1;
		result[5] = eveDetectedInt;
		int[] goodsBobEve = filtersBob.indexOfIden(filtersEve);
		int knownEve = compareArray(goodsAliceBob, goodsBobEve);
		result[0] = key_size;
		result[1] = nb_correct_eve;
		result[2] = goodsAliceBob.length;
		double tmp = (knownEve*100)/goodsAliceBob.length;
		result[3] = (int)tmp;
		tmp = (goodsAliceBob.length*100)/key_size;
		result[4] = (int)tmp;
		return result;
	}
	
	private static boolean isInArray(int i, int[] array) {
		for(int j=0;j<array.length;j++) {
			if (i==array[j])
				return true;
		}
		return false;
	}
	
	public static int compareArray(int[] a1, int[] a2) {
		//MUST BE THE SAME SIZE!
		int result=0;
		for(int i=0;i<a1.length;i++) {
			for(int j=0;j<a2.length;j++) {
				if(a2[j]==a1[i]) {
					result++;
					break;
				}
			}
		}
		return result;
	}
}
