package utils;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {

    private Workbook workbook;
    private Sheet sheet;

    public ExcelReader(String filePath) {
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            this.workbook = WorkbookFactory.create(fis);
            this.sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getData(int row, int col) {
        try {
            return sheet.getRow(row).getCell(col).getStringCellValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeWorkbook() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
