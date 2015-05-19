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
*/			

public class Benchmark {
	public static void launch() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		write(100, 2, workbook);
		write(100, 3, workbook);
		write(100, 4, workbook);
		write(100, 5, workbook);
		write2n(21, workbook);
		

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
	
	
	public static void write2n(int size_max, HSSFWorkbook workbook)
	{
		StringBuffer name = new StringBuffer();
		name.append("Max characters=");
		name.append(size_max);
		name.append(";security level=2^n");		
		
	    HSSFSheet sheet = workbook.createSheet(name.toString());
	    int[] result;
        
	    // Cell style
        HSSFCellStyle cellStyle = null; // Initialization of cell style
        HSSFFont font = workbook.createFont(); // Creation of the font (to modify its appearence later)
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // Bold
        cellStyle = workbook.createCellStyle(); // Creation of the cell style
        cellStyle.setFont(font); // Application of the cell style
        
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
        
        int cpt = 0; // Counter of times Eve has been detected
        for(int i = 1; i <= size_max; i++) {
        	result = computeVernam(i);
        	if(result[5] == 1)
        		cpt++;
            Row row = sheet.createRow(i);
        	for(int j = 0; j < result.length; j++) {
        		Cell cell = row.createCell(j);
                cell.setCellValue(result[j]);
        	}
        }
                
        // Adds a new row and then the Eve's detection rate
        cpt = (cpt*100) / size_max;
        Row last_row = sheet.createRow(size_max+1); 
        Cell cell = last_row.createCell(5);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cpt + "%");
        
        System.out.println("Page done.");
	}
	
	public static void write(int size_max, int lvl, HSSFWorkbook workbook)
	{
		StringBuffer name = new StringBuffer();
		name.append("Max characters=");
		name.append(size_max);
		name.append(";security=");
		name.append(lvl);
		name.append("n");
		
		
	    HSSFSheet sheet = workbook.createSheet(name.toString());
	    int[] result;
        
        // Cell style
        HSSFCellStyle cellStyle = null; // Initialization of cell style
        HSSFFont font = workbook.createFont(); // Creation of the font (to modify its appearence later)
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // Bold
        cellStyle = workbook.createCellStyle(); // Creation of the cell style
        cellStyle.setFont(font); // Application of the cell style
        
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
        
        int cpt = 0; // Counter of times Eve has been detected
        for(int i = 1; i <= size_max; i++) {
        	result = compute(i, lvl);
        	if(result[5] == 1)
        		cpt++;
            Row row = sheet.createRow(i);
        	for(int j = 0; j < result.length; j++) {
        		Cell cell = row.createCell(j);
                cell.setCellValue(result[j]);
        	}
        }

        // Adds a new row and then the Eve's detection rate
        cpt = (cpt*100) / size_max;
        Row last_row = sheet.createRow(size_max+1); 
        Cell cell = last_row.createCell(5);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cpt + "%");
                
        System.out.println("Page done.");
	}
	
	public static int[] compute(int keySizeMax, int lvl)
	{
		int result[] = new int[6];
		result[0] = keySizeMax; // Number of characters we want to encrypt
		
		
		int oneTimePad = keySizeMax * 8 * lvl;
		result[1] = oneTimePad; // Number of photons that will be send
		
		// Creation of the key, filter scheme and photon scheme associated with Alice
		BytesScheme aliceKey = new BytesScheme(oneTimePad);
		FilterScheme aliceFilters = new FilterScheme(oneTimePad);
		PhotonScheme alicePhotons = new PhotonScheme(oneTimePad, aliceKey, aliceFilters);
		
		// Creation of Eve's filter scheme
		FilterScheme eveFilters = new FilterScheme(oneTimePad);
		result[2] = eveFilters.numberIden(aliceFilters); // Number of photons correctly read by Eve
		
		for(int i = 0; i < oneTimePad; i++)
		{
			eveFilters.getFilter(i).readPolarPhoton(alicePhotons.getPhoton(i)); // Changes photons' polarization when needed
		}
		
		// Creation of Bob's key and filter scheme
		FilterScheme bobFilters = new FilterScheme(oneTimePad);
		BytesScheme bobKey = new BytesScheme(oneTimePad, alicePhotons, bobFilters);
		result[3] = bobFilters.numberIden(aliceFilters); // Number of photons correctly read by Bob
		
		int nbSacrificed = result[3] - (keySizeMax * 8); // Number of photons to sacrifice to obtain a proper key
		
		if(nbSacrificed <= 0) // If there's not bit to sacrifice
		{
			result[4] = 0; // We don't
			result[5] = 0; // Eve is not detected
		}
		else
			result[4] = nbSacrificed;

		int[] comparison = bobKey.arrayWithDiscards(aliceFilters, bobFilters); // Creation of an array containing Bob's bit measurement
		// that Alice has validated, -1 otherwise
		
		if(aliceKey.detection(comparison, result[4]))
			result[5] = 1;
		else
			result[5] = 0;
		
		return result;
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
			numberIden+= eveFilters[i].numberIden(aliceFilters[i]);
			for(int j = 0; j < 8; j++)
			{
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
			numberIden+=bobFilters[i].numberIden(aliceFilters[i]);
			comparison[i] = bobKey[i].arrayWithDiscards(bobFilters[i], aliceFilters[i]);
		}
		 
		result[3] = numberIden;
		
		
		int nbSacrificed = numberIden - (keySizeMax * 8); // Number of photons that must be sacrificed to obtain a correct key
		
		if(nbSacrificed > 0)
			result[4] = nbSacrificed;
		else
		{
			result[4] = 0;
			result[5] = 0;
		}
		
		boolean detected = false;
		for(int i = 0; i < size; i++)
		{
			if(aliceKey[i].detection(comparison[i], nbSacrificed))
			{
				detected = true;
				break;
			}
		}

		if(detected)
			result[5] = 1;
		else
			result[5] = 0;
		return result;
	}

	

	// Returns true is the value i is contained in the array 
	private static boolean isInArray(int i, int[] array) {
		for(int j=0;j<array.length;j++) {
			if (i==array[j])
				return true;
		}
		return false;
	}
	
	// Returns the number of times where the value contained in a1 and a2 at the same index is equal
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
