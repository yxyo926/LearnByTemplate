package io.github.xxyopen.mylearn.generator.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonExcelUtil {

    /**
     * 导出excel2007
     *
     * @author wxr
     * @date 2017年9月20日
     * @param object list<entity> 或者 list<map>
     * @param table
     *   public static final String[][] CargoLocationTable = {{"表头名称"}
     * 	,{"列名","货位编号","货位名称","最大容量（立方米）","最大承重（KG）","备注"}
     * 	,{"列对应属性名","code","locationName","volumeMax","bearingMax","remark"}};
     * @param out
     */
    public static void createExcel(Object object,String[][] table,OutputStream out) {
        List<Object> list = (List<Object>) object;

        // 创建一个Excel文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 创建一个工作表
        XSSFSheet sheet = workbook.createSheet("表一");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, table[1].length-1));



        XSSFFont font=workbook.createFont();//表头字体
        font.setFontHeightInPoints((short)14);
//        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);//字体增粗
        font.setFontName("宋体");

        XSSFFont font1=workbook.createFont();//标题字体
        font1.setFontHeightInPoints((short)12);
//        font1.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);//字体增粗
        font1.setFontName("宋体");

        int rowNum = 0;

        // 添加表头行
        XSSFRow hssfRow = sheet.createRow(rowNum);
        rowNum++;

        // 设置单元格格式居中
        XSSFCellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setAlignment(XSSFCellStyle.ALIGN.ALIGN_CENTER);
        cellStyle.setFont(font);

        XSSFCellStyle cellStyle1 = workbook.createCellStyle();
//        cellStyle1.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle1.setFont(font1);

        XSSFCellStyle cellStyle2 = workbook.createCellStyle();
//        cellStyle2.setAlignment(XSSFCellStyle.ALIGN_CENTER);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        int[] cellWeight = new int[table[1].length];

        try{
            //添加标题
            XSSFCell headCell_1 = hssfRow.createCell(0);
            hssfRow.setHeightInPoints(30);//设置行高
            headCell_1.setCellValue(table[0][0]);
            headCell_1.setCellStyle(cellStyle);



            // 添加表头内容
            hssfRow = sheet.createRow(rowNum);
            hssfRow.setHeightInPoints(25);
            rowNum++;
            for(int n=0;n<table[1].length;n++){
                XSSFCell headCell = hssfRow.createCell(n);
                headCell.setCellValue(table[1][n]);
                headCell.setCellStyle(cellStyle1);

                if(cellWeight[n]<table[1][n].getBytes().length*256){
                    cellWeight[n] = table[1][n].getBytes().length*256;
                }
            }

            // 添加数据内容
            for (int i = 0; i < list.size(); i++) {
                hssfRow = sheet.createRow(rowNum);
                rowNum++;
                Object obj = list.get(i);

                if(obj instanceof Map){
                    Map<String,Object> objMap = (Map<String,Object>) obj;
                    for(int m=0;m<table[2].length;m++){
                        String param = table[2][m];
                        // 创建单元格，并设置值
                        XSSFCell cell = hssfRow.createCell(m);
                        if("".equals(param)){
                            cell.setCellValue("");
                        }else if("index".equals(param)){
                            int index = i+1;
                            cell.setCellValue(index);
                        }else{
                            String value = "";
                            Object o = objMap.get(param);
                            if(o instanceof Date){//将日期格式转换成字符型
                                value = (o==null?null:format.format((Date) o));
                            }else{
                                value = (o==null?null:o.toString());
                            }
                            if(value!=null){
                                cell.setCellValue(value);

                                if("remark".equals(param)||param.indexOf("Remark")>0){

                                }else{
                                    if(cellWeight[m]<value.getBytes().length*256){
                                        cellWeight[m] = value.getBytes().length*256;
                                    }
                                }
                            }
                        }
                        cell.setCellStyle(cellStyle2);
                    }
                }else{
                    for(int m=0;m<table[2].length;m++){
                        String param = table[2][m];
                        // 创建单元格，并设置值
                        XSSFCell cell = hssfRow.createCell(m);


                        if("".equals(param)){
                            cell.setCellValue("");
                        }else if("index".equals(param)){
                            int index = i+1;
                            cell.setCellValue(index);
                        }else{
                            // 将属性的首字符大写，方便构造get，set方法
                            String nameCapital = param.substring(0, 1).toUpperCase() + param.substring(1);

                            Method method = obj.getClass().getMethod("get" + nameCapital);

                            String value = "";
                            // 调用getter方法获取属性值
                            Object o = method.invoke(obj);

                            if(o instanceof Date){//将日期格式转换成字符型
                                value = (o==null?null:format.format((Date) o));
                            }else{
                                value = (o==null?null:o.toString());
                            }
                            if(value!=null){
                                cell.setCellValue(value);
                                if("remark".equals(param)||param.indexOf("Remark")>0){
                                }else{
                                    if(cellWeight[m]<value.getBytes().length*256){
                                        cellWeight[m] = value.getBytes().length*256;
                                    }
                                }

                            }
                        }
                        cell.setCellStyle(cellStyle2);
                    }
                }
            }


            for(int i = 0; i < cellWeight.length; i++){//设置最大宽度
                sheet.setColumnWidth(i,cellWeight[i]);
            }

            // 保存Excel文件
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取excel2003
     *
     * @author wxr
     * @date 2017年9月20日
     * @param inputStream
     * @param table
     * @return
     */
    public static List<Map<String,String>> readExcel2003(InputStream inputStream,String[][] table) {
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        HSSFWorkbook workbook = null;

        try {
            // 读取Excel文件
            workbook = new HSSFWorkbook(inputStream);

            HSSFSheet hssfSheet = workbook.getSheetAt(0);
            if (hssfSheet == null) {
                return null;
            }
            //验证标题
            HSSFRow tableName = hssfSheet.getRow(0);
            HSSFCell tableCell = tableName.getCell(0);

            if(!table[0][0].equals(tableCell.getStringCellValue())){
                return null;
            }

            //验证表头
            tableName = hssfSheet.getRow(2);
            for(int i=0;i<table[1].length;i++){
                tableCell = tableName.getCell(i);
                if(!table[1][i].equals(tableCell.getStringCellValue())){
                    return null;
                }
            }

            // 循环行
            for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow == null) {
                    continue;
                }

                // 将单元格中的内容存入集合
                Map<String,String> map = new HashMap<String,String>();

                for(int i=0;i<table[2].length;i++){
                    if("".equals(table[2][i])){
                        continue;
                    }
                    HSSFCell cell = hssfRow.getCell(i);
                    if(cell==null||getValue2003(cell)==null||"".equals(getValue2003(cell))){
                        continue;
                    }else{
                        map.put(table[2][i], getValue2003(cell));
                    }
                }

                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 读取excel2007
     *
     * @author wxr
     * @date 2017年9月20日
     * @param inputStream
     * @param table
     * @return
     */
    public static List<Map<String,String>> readExcel2007(InputStream inputStream,String[][] table) {
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        XSSFWorkbook workbook = null;

        try {
            // 读取Excel文件
            workbook = new XSSFWorkbook(inputStream);

            XSSFSheet hssfSheet = workbook.getSheetAt(0);
            if (hssfSheet == null) {
                return null;
            }
            //验证标题
            XSSFRow tableName = hssfSheet.getRow(0);
            XSSFCell tableCell = tableName.getCell(0);

            if(!table[0][0].equals(tableCell.getStringCellValue())){
                return null;
            }

            //验证表头
            tableName = hssfSheet.getRow(2);
            for(int i=0;i<table[1].length;i++){
                tableCell = tableName.getCell(i);
                if(!table[1][i].equals(tableCell.getStringCellValue())){
                    return null;
                }
            }

            // 循环行
            for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow == null) {
                    continue;
                }
                // 将单元格中的内容存入集合
                Map<String,String> map = new HashMap<String,String>();

                for(int i=0;i<table[2].length;i++){
                    if("".equals(table[2][i])){
                        continue;
                    }
                    XSSFCell cell = hssfRow.getCell(i);
                    if(cell==null||getValue2007(cell)==null||"".equals(getValue2007(cell))){
                        continue;
                    }else{
                        map.put(table[2][i], getValue2007(cell));
                    }
                }

                if(map.size()>0){
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String getValue2003(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == CellType.BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == CellType.NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    private static String getValue2007(XSSFCell hssfCell) {
        if (hssfCell.getCellType() == CellType.BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == CellType.NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
}

