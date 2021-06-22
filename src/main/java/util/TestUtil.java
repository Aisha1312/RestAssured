package util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestUtil extends TestBase {

    public final static int RESPONSE_CODE_200 = 200;
    public final static int RESPONSE_CODE_201 = 201;
    public final static int RESPONSE_CODE_400 = 400;
    public final static int RESPONSE_CODE_401 = 401;
    public final static String sheetName_Products = "postProducts";

    static Workbook book;
    static Sheet sheet;

    public final static String filePath = "src//main//java//testdata//TestData.xlsx";

    public static Object[][] getDataFromSheet(String sheetName) {

        FileInputStream fip = null;

        try {
            fip = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            book = WorkbookFactory.create(fip);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        sheet = book.getSheet(sheetName);
        Object[][] objectValue = new Object[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];

        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            for (int j = 0; j < sheet.getRow(0).getLastCellNum(); j++) {
                objectValue[i][j] = sheet.getRow(i+1).getCell(j).toString();
                System.out.println(objectValue[i][j]);
            }
        }
        return objectValue;

    }


}
