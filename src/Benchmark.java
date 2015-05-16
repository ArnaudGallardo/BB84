import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

/*
 * TODO: - Statistiques avec le chiffre de Vernam : on code 2^n photons, n �tant le nombre de caract�res du messages � crypter
 * 		 	-> Un caract�re est cod� sur 8 octets : donc g�n�ration totale de photons = 2^(n*8)
 * 		 - A la fin, le nombre de bits de la cl� doit �tre �gal au nombre de caract�res du message
 * 			-> La cl� aura une longueur de n*8 bits
 * 		 - On doit donc sacrifier k - n*8 bits, k �tant la longueur de la cl� obtenue � l'issue de la comparaison des filtres
 * 
 * Notes :
 * 			1�re colonne : Nombre de qbits envoy�s
 * 			2�me colonne : Nombre de qbits correctement d�chiffr�s par Eve
 * 			3�me colonne : Nombre de qbits correctement d�chiffr�s par Bob
 * 			4�me colonne : Nombre de qbits sacrifi�s
 * 			5�me colonne : D�tection (ou pas) d'Eve
 */			



public class Benchmark {
	public static void launch() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		write(3, workbook);
		test(100,7,100,workbook);
		test(100,13,100,workbook);
		test(100,20,100,workbook);
		test(100,50,100,workbook);
		
		try {
			JFileChooser dialogue = new JFileChooser(new File("."));
			File excelFile = null;
			dialogue.setAcceptAllFileFilterUsed(false);
			dialogue.addChoosableFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return "Excel files (*.xls)";
				}
				
				@Override
				public boolean accept(File arg0) {
					if(arg0.isDirectory()) {
						return true;
					} else {
						return arg0.getName().toLowerCase().endsWith(".xls");
					}
				}
			});
			if (dialogue.showSaveDialog(null)== 
				    JFileChooser.APPROVE_OPTION) {
				    excelFile = dialogue.getSelectedFile();
				    if (!excelFile.getName().toLowerCase().endsWith(".xls")) {
				    	 excelFile = new File(excelFile.getAbsolutePath()+".xls");
				    }
		            //FileOutputStream out = 
		                    //new FileOutputStream(new File("C:\\Users\\Candice\\Documents\\testQuantique.xls"));
		            FileOutputStream out = 
		                    new FileOutputStream(excelFile);
		            workbook.write(out);
		            workbook.close();
		            out.close();
		            System.out.println("Excel written successfully..");
		            System.out.println("Closing.");
			}
			else
				System.out.println("No file chosen !");
             
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	public static void write(int size_max, HSSFWorkbook workbook)
	{
		StringBuffer name = new StringBuffer();
		name.append("max characters=");
		name.append(size_max);
		
		
	    HSSFSheet sheet = workbook.createSheet(name.toString());
	    int[] result;
        
        // Style de cellules
        HSSFCellStyle cellStyle = null; // Pour style des cellules
        HSSFFont font = workbook.createFont(); // Cr�ation de la "police" pour pouvoir faire la mise en forme
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // Mise en gras
        cellStyle = workbook.createCellStyle(); // On initialise le style des cellules
        cellStyle.setFont(font);
        
        Row firstLine = sheet.createRow(0);
        
        
        String[] s = {"Length of the message", "Number of Qbits sent by Alice", "Number of Qbits correctly read by Eve", 
        		"Number of Qbits correctly read by Bob", "Number of sacrificed photons", "Eve's detection"};
        for(int i = 0; i < 6; i++)
        {
        	Cell cell = firstLine.createCell(i);   	
        	cell.setCellValue(s[i]);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(i);
        }
        
        for(int i=1; i<=size_max;i++) {
        	result = computeVernam(i);
            Row row = sheet.createRow(i);
        	for(int j=0;j<result.length;j++) {
        		Cell cell = row.createCell(j);
                cell.setCellValue(result[j]);
        	}
        }
                
        System.out.println("Page done.");
	}

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
        HSSFFont font = workbook.createFont(); // Cr�ation de la "police" pour pouvoir faire la mise en forme
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // Mise en gras
        cellStyle = workbook.createCellStyle(); // On initialise le style des cellules
        cellStyle.setFont(font);
        
        Row firstLine = sheet.createRow(0);
        
        
        String[] s = {"Number of Qbits sent by Alice", "Number of Qbits correctly read by Eve", 
        		"Number of Qbits correctly read by Bob", "Part of the key known by Eve", 
        		"Part of sacrificed photons", "Eve's detection"};
        for(int i = 0; i < 6; i++)
        {
        	Cell cell = firstLine.createCell(i);   	
        	cell.setCellValue(s[i]);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(i);
        }
        
        for(int i=0; i<=size_max;i++) {
        	result = compute(48+i*8, percentOfVerification, percentOfAttack);
            Row row = sheet.createRow(i+1);
        	for(int j=0;j<result.length;j++) {
        		Cell cell = row.createCell(j);
                cell.setCellValue(result[j]);
        	}
        }
                
        System.out.println("Page done.");
	}

	
	
	// Make statistics using a message containing from 1*8 to keySizeMax*8 (1 characters = 8 bytes) characters with the creation of 2^n photons, n being the number of characters
	public static int[] computeVernam(int keySizeMax)
	{
		int[] result = new int[6]; // Initialization of the array containing the results
		result[0] = keySizeMax;
		
		//int qBitsEve = 0; // Initialization of the qBits number Eve reads properly
		int oneTimePad = (int) Math.pow(2, keySizeMax*8); // Length = 2^(n*8) bytes (number of sent photons)
		result[1] = oneTimePad; // Sent photons
		
		// Alice creates a chain of polarized photons
		BytesScheme aliceKey = new BytesScheme(oneTimePad);
		FilterScheme aliceFilters = new FilterScheme(oneTimePad);
		PhotonScheme alicePhotons = new PhotonScheme(oneTimePad, aliceKey, aliceFilters);
		
		// Eve uses a chain of filters to try to read the photons
		FilterScheme eveFilters = new FilterScheme(oneTimePad);
		int[] indexIden = aliceFilters.indexOfIden(eveFilters);
		result[2] = indexIden.length; // Number of qBits correctly read by Eve
		for(int i = 0; i < aliceFilters.getSize(); i++)
		{
			eveFilters.getFilter(i).readPolarPhoton(alicePhotons.getPhoton(i));
		}
		
		
		// Bob receives the photons chain and tries to read it
		FilterScheme bobFilters = new FilterScheme(oneTimePad);
		BytesScheme bobKey = new BytesScheme(oneTimePad, alicePhotons, bobFilters);
		
		// Verification of Bob's filters
		indexIden = aliceFilters.indexOfIden(bobFilters); // Index of the identical filters
		int length = indexIden.length; // Number of qBits correctly read by Bob
		result[3] = length;
		
		int nbSacrificed = length - (keySizeMax*8);
		result[4] = nbSacrificed; // Number of qBits to sacrifice 
		
		int cpt = 0; // Counter of the number of sacrificed photons
		
		int[] indexFinal = new int[length]; // Used to determine which bits have been sacrificed
		
		boolean detected = false; // Is Eve detected ? 
		
		while(cpt <= nbSacrificed && !detected)
		{
			int random = (int) (Math.random() * length); // Random index to sacrifice randomly a bit
			while(indexFinal[random] == -1) // Checks we're not trying to sacrifice an already sacrificed bit
			{
				random = (int) (Math.random() * length);
			}
			
			if(aliceKey.getByte(indexIden[random]) == (bobKey.getByte(indexIden[random]))) // If the bit values are equals: nothing special
			{
				cpt++;
				indexFinal[random] = -1;
			}
			else // Eve is detected (or there was an error due to the fiber's noise)
				detected = true;			
		}
		
		if(detected == true)
			result[5] = 1;
		else
			result[5] = 0;
	
		return result;
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
			photonAlice = psAlice.getPhoton(random).clone(); //On r�cup�re (sans modifier) le photon
			polarEve = filtersEve.getFilter(random).readPolarPhoton(psAlice.getPhoton(random)); //On r�cup�re pour Eve (en Modifiant) le photon
			photonEve.setPolarization(polarEve); //On simule un photon pour Eve
			if(photonAlice.equals(photonEve)) { //On regarde si ils sont �gaux
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
