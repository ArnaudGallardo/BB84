import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


public class BenchmarkGraph extends Benchmark {
	// Create a block a cells that can directly be used to create a graph
	public static void writeGraph(HSSFWorkbook workbook) {
				
	    HSSFSheet sheet = workbook.createSheet("Graphs");
	    
        // Cell style
        HSSFCellStyle cellStyle = null; // Initialization of cell style
        HSSFFont font = workbook.createFont(); // Creation of the font (to modify its appearence later)
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // Bold
        cellStyle = workbook.createCellStyle(); // Creation of the cell style
        cellStyle.setFont(font); // Application of the cell style
        
        Row firstLine = sheet.createRow(0);
        
        String[] s = {"Security Level", "Detection rate", "Usable key rate"};
        for(int i = 0; i < 3; i++)
        {
        	Cell cell = firstLine.createCell(i);   	
        	cell.setCellValue(s[i]);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(i);
        }
        
        String[] s2 = {"2n", "3n", "4n","5n"};
        for(int i = 0; i < 4; i++)
        {
        	Row line = sheet.createRow(i+1);
        	Cell cell = line.createCell(0);   	
        	cell.setCellValue(s2[i]);
        	cell = line.createCell(1);   	
        	cell.setCellFormula("'Max characters=100;security="+s2[i]+"'!H1");
        	
        }
	}
}
