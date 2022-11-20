package io.github.xxyopen.mylearn.generator.utils;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelImport {
    /**
     *
     * @param fileName 文件
     * @param is  文件流
     * @param pattern 返回日期格式
     * @param isExistTitle 是否存在表头
     * @return map
     */
    public Map<Integer, List<List<Object>>>   getExcelRow(String fileName, InputStream is,String pattern, boolean isExistTitle){
        List<List<Object>> list;
        Map<Integer,List<List<Object>>> map = null;
        Workbook workbook = null;
        boolean isExcel03 = fileName.matches("^.+\\.(?i)(xls)$");
        boolean isExcel07 = fileName.matches("^.+\\.(?i)(xlsx)$");
        try {
            if (isExcel03) {
                workbook = new HSSFWorkbook(is);
            }else if (isExcel07) {
                workbook = new XSSFWorkbook(is);
            }else{
                return null;
            }
            int numberOfSheets = workbook.getNumberOfSheets();
            map = new HashMap<Integer,List<List<Object>>>();
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheetAt = workbook.getSheetAt(i);
                int lastRowNum = sheetAt.getLastRowNum();//行数从0开始
                list = new ArrayList<List<Object>>();
                for (int j = 0; j <= lastRowNum; j++) {
                    if (j == 0 && isExistTitle) {
//                        j++;
                        continue;
                    }
                    Row row = sheetAt.getRow(j);
                    short lastCellNum = row.getLastCellNum();
                    List<Object> li = new ArrayList<Object>();
                    for (int k = 0; k < lastCellNum; k++) {
                        li.add(getCellValue(row.getCell(k), pattern));
                    }
                    list.add(li);
                }
                map.put(i, list);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return  map;
    }

    private Object getCellValue(Cell cell,String pattern){
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if("General".equals(cell.getCellStyle().getDataFormatString())){
                    value = df.format(cell.getNumericCellValue());
                }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
                    value = sdf.format(cell.getDateCellValue());
                }else{
                    value = df2.format(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case BLANK:
                value = "";
                break;
            default:
                break;
        }
        return value;
    }
}