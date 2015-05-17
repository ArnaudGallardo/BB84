import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;


/* 
 * Notes :
 * 			1st colum: Length of the original message
 * 			2sd column: Number of qBits sent
 * 			3rd column: Number of qBits correctly read by Eve
 * 			4th column: Number of qBits correctly read by Bob
 * 			5th column: Number of sacrificed qBits
 * 			6th column: Eve detection
 * 			
 */			



public class Benchmark {
	public static void launch() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		write(21, "2^n", workbook);
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
	
	
	public static void write(int size_max, String lvl, HSSFWorkbook workbook)
	{
		StringBuffer name = new StringBuffer();
		name.append("Max characters=");
		name.append(size_max);
		name.append(", security level=");
		name.append(lvl);
		
		
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
		int oneTimePad = (int) Math.pow(2, keySizeMax) * 8; // Length = 2^(n) * 8 bytes (number of sent photons)
		result[1] = oneTimePad; // Sent photons
	
		
		
		int size = (int) Math.pow(2, keySizeMax); // Size of the future arrays of schemes (which will be arrays of size 8 themselves)
		
		// Creation of a BytesScheme array that will contain the byte scheme for the 2^n bits (n = number of character; 1n = 8bits)  
		BytesScheme[] aliceKey = new BytesScheme[size];
		 
		// Creation of a FilterScheme array that will contain the filter scheme for each sequence of 8 photons
		FilterScheme [] aliceFilters = new FilterScheme[size];
		 
		// Creation of a PhotonScheme array that will contain the photon scheme corresponding the sequences of 8 filters and bytes
		PhotonScheme [] alicePhotons = new PhotonScheme[size];
		for(int i = 0; i < size; i++)
		{
			aliceKey[i] = new BytesScheme(8); // The bytes scheme array is filled with bytes scheme of size 8
		 	aliceFilters[i] = new FilterScheme(8); // The filters scheme array is filled with filters scheme of size 8
		 	alicePhotons[i] = new PhotonScheme(8, aliceKey[i], aliceFilters[i]);
		}

		// Creation of a FilterScheme array that will contain Eve's filter schemes
		FilterScheme[] eveFilters = new FilterScheme[size];
		for(int i = 0; i < size; i++)
		{
			eveFilters[i] = new FilterScheme(8);
		}
		 
		int numberIden = 0; // Number of filters that Eve chose properly
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				if(eveFilters[i].getFilter(j).equals(aliceFilters[i].getFilter(j)))
					numberIden++;
				eveFilters[i].getFilter(j).readPolarPhoton(alicePhotons[i].getPhoton(j));
			}
		}
		 
		result[2] = numberIden;
		 
		// Creation of a FilterScheme array that will contain Bob's filters
		FilterScheme[] bobFilters = new FilterScheme[size];
		
		// Creation of a BytesScheme array that will contain Bob's measurement with his own filters
		BytesScheme[] bobKey = new BytesScheme[size];
		for(int i = 0; i < size; i++)
		{
			bobFilters[i] = new FilterScheme(8);
			bobKey[i] = new BytesScheme(8, alicePhotons[i], bobFilters[i]);
		}
		
		numberIden = 0; // Number of filters Bob chose properly
		
		// Copy of Bob's key in an int array of dimension 2 to determine which bits measurement should be kept or not
		// This array will later be used to determine if a photon has already been sacrificed or not
		int[][] comparison = new int[size][8];
		
		
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				if(bobFilters[i].getFilter(j).equals(aliceFilters[i].getFilter(j)))
				{
					if(bobKey[i].getByte(j) == 1)
						comparison[i][j] = 1;
					else
						comparison[i][j] = 0;
					numberIden++;
				}
				else
					comparison[i][j] = -1;
			}
		}
		 
		result[3] = numberIden;
		
		int nbSacrificed = numberIden - (keySizeMax * 8); // Number of photons that must be sacrificed to obtain a correct key
		result[4] = nbSacrificed;
		
		boolean detected = false; // Is Eve detected?
		
		int cpt = 0; // Number of sacrificed photons
		
		
		while(cpt <= nbSacrificed && !detected) // Loop stopped if Eve is detected OR if nbSacrificed photons have been discarded
		{
			int i = (int) (Math.random() * size);
			int j = (int) (Math.random() * 8);
			
			while(comparison[i][j] == -1) // Checks if the coordinates correspond to a photon that has already been sacrificed
										  // Or that has been discarded after the check of filters
			{
				i = (int) (Math.random() * size);
				j = (int) (Math.random() * 8);
			}
			
			if(aliceKey[i].getByte(j) == bobKey[i].getByte(j)) // The two bits are equal: good measurement
			{
				comparison[i][j] = -1;
				cpt++;
			}
			else // The two bits are not equal: we suppose it is because of Eve who has tried to spy on us
			{
				detected = true;
			}
		}
		
		if(detected)
			result[5] = 1;
		else
			result[5] = 0;
		
		return result;
	}

	
	public static int[] compute(int key_size, int percentOfKey, int percentOfAttack) {
		int[] result = new int[6];
		
		int nb_correct_eve = 0; // Number of filters that Eve chooses cor
		FilterScheme filtersAlice = new FilterScheme(key_size);
		BytesScheme bytesAlice = new BytesScheme(key_size);
		PhotonScheme psAlice = new PhotonScheme(key_size, bytesAlice, filtersAlice);
		Photon photonAlice = new Photon();
		Photon photonEve = new Photon();
		int[] indexGoodReadingOfEve = new int[key_size]; // Array that will contain the indexes of filters Eve chose properly
		Polarization polarEve;
		FilterScheme filtersEve = new FilterScheme(key_size); // Eve's filter scheme
		int[] readByEve = new int[key_size]; // Array that will contain the indexes of the photons read by Eve
		int nbToRead = percentOfAttack*key_size/100; // Percent of photons Eve will read
		for(int i=0; i<nbToRead;i++) // For each photons Eve will read 
		{  
			int random = (int)(Math.random()*key_size); // Random index
			
			while(isInArray(random,readByEve)) // If the generated index corresponds to a photon that has already been read
			{
				random = (int)(Math.random()*key_size); // New index randomly generated
			}
			photonAlice = psAlice.getPhoton(random).clone(); // Catches Alice's photon without modifying it
			polarEve = filtersEve.getFilter(random).readPolarPhoton(psAlice.getPhoton(random)); // Catches the polarization of Alice's photon and modifies it when necessary
			photonEve.setPolarization(polarEve); // Simulates a photon for Eve
			if(photonAlice.equals(photonEve)) // Checks if they are equal
			{ 
				indexGoodReadingOfEve[nb_correct_eve]=random; // Adds the index of the read photon in the array
				nb_correct_eve++; // Increases the counter value
			}
		}
		
		FilterScheme filtersBob = new FilterScheme(key_size);
		BytesScheme bytesBob = new BytesScheme(key_size, psAlice, filtersBob);
		
		// Creates an array containing the indexes of the identical filters used by both Alice and Bob
		int[] goodsAliceBob = filtersBob.indexOfIden(filtersAlice);
		
		// Tests if Eve's detected
		boolean eveDetected = bytesBob.eveDetected(bytesAlice, goodsAliceBob, percentOfKey);
		int eveDetectedInt = 0;
		if(eveDetected)
			eveDetectedInt = 1;
		result[5] = eveDetectedInt;
		
		// Creates an array containing the indexes of the identical filters used by both Eve and Bob
		int[] goodsBobEve = filtersBob.indexOfIden(filtersEve); 
		// Computes the number of measurements Eve can keep by comparing the arrays of identical filters indexes
		int knownEve = compareArray(goodsAliceBob, goodsBobEve); 
		
		result[0] = key_size;
		result[1] = nb_correct_eve;
		result[2] = goodsAliceBob.length;
		
		double tmp = (knownEve*100)/goodsAliceBob.length; // Computes the part of the final key Eve knowss
		result[3] = (int)tmp;
		
		tmp = (goodsAliceBob.length*100)/key_size; // Part of the key that has been sacrificed to detect Eve
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
