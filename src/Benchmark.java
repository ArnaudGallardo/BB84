import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

public class Benchmark {
	public static void launch() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		test(100,7,100,workbook);
		test(100,13,100,workbook);
		test(100,20,100,workbook);
		test(100,50,100,workbook);
		try {
            FileOutputStream out = 
                    new FileOutputStream(new File("C:\\Users\\Agila\\Documents\\poi-3.11\\MOI\\test1.xls"));
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
	
	public static void test(int size_max, int percentOfVerification, int percentOfAttack, HSSFWorkbook workbook) {
		StringBuffer name = new StringBuffer();
		name.append("max=");
		name.append(size_max);
		name.append(";percentVerif=");
		name.append(percentOfVerification);
		name.append("%;percentAttack=");
		name.append(percentOfAttack);
		
	    HSSFSheet sheet = workbook.createSheet(name.toString());
	    
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
