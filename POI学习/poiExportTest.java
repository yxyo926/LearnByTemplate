
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LearnPOIController {
    // 构建路径
    String PATH = "F:\\";
    String sheetheader = "item1,item2,item3,item4,item5";
    String[] sheetheaderList = sheetheader.split(",");

    @Test
    public void workwrite() throws Exception {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        Sheet sheet = workbook.createSheet("PRManagement");
        // 创建第一行
        Row row1 = sheet.createRow(0);// 第一行

        // 设置表格样式
        CellStyle cellStyle = workbook.createCellStyle();
        //  设置水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置背景色
        cellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        Font font = row1.getSheet().getWorkbook().createFont();
//        font.setColor(IndexedColors.WHITE.getIndex());
//        cellStyle.setFont(font);
        //设置自适应列宽
        sheet.setDefaultColumnWidth(0);
        row1.setHeight((short) 500);
        row1.setRowStyle(cellStyle);
        // 创建单元格
        for (int i = 0; i < sheetheaderList.length; i++) {
            Cell cell=row1.createCell(i);
            cell.setCellValue(sheetheaderList[i]);
            cell.setCellStyle(cellStyle);
        }
//        cell1.setCellType(CellType.NUMERIC);
        // 第二行
        Row row2 = sheet.createRow(1);// 第一行
        Cell cell21 = row2.createCell(0);// 第一行的第一列
        cell21.setCellValue("时间");
        Cell cell22 = row2.createCell(1);
        cell22.setCellValue(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        // 获取时分秒字符串
        String fileTime = DateTime.now().toString("yyyyMMddHHmmss");
        // 生成表,IO流,07版本使用xlsx后缀
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "考核成绩表" + fileTime +".xlsx");
        workbook.write(fileOutputStream);
        // 关闭流
        fileOutputStream.close();
        System.out.println("考核成绩表07输出完毕");

    }

    @Test
    public void testWrite03() throws Exception {
        // 创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 创建工作表
        Sheet sheet = workbook.createSheet("考核成绩表");
        // 创建第一行
        Row row1 = sheet.createRow(0);// 第一行
        // 创建单元格
        Cell cell1 = row1.createCell(0);// 第一行的第一列
        cell1.setCellValue("数学");
        Cell cell2 = row1.createCell(1);
        cell2.setCellValue(100);
        for (int i = 0; i < sheetheaderList.length; i++) {
            row1.createCell(i + 2).setCellValue(sheetheaderList[i]);
        }
        // 设置表格样式
        CellStyle cellStyle = workbook.createCellStyle();
        // 设置水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置背景色
        cellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = row1.getSheet().getWorkbook().createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFont(font);
        // 设置自适应列宽
        sheet.setDefaultColumnWidth(0);
        row1.setHeight((short) 500);
        row1.setRowStyle(cellStyle);
        cell2.setCellStyle(cellStyle);
        // cell1.setCellType(CellType.NUMERIC);

        // 第二行
        Row row2 = sheet.createRow(1);// 第一行
        Cell cell21 = row2.createCell(0);// 第一行的第一列
        cell21.setCellValue("时间");
        Cell cell22 = row2.createCell(1);
        cell22.setCellValue(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        // 生成表,IO流,03版本使用xls后缀
        // 获取时分秒字符串
        String fileTime = DateTime.now().toString("yyyyMMddHHmmss");
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "考核成绩表" + fileTime + ".xls");
        workbook.write(fileOutputStream);
        // 关闭流
        fileOutputStream.close();
        System.out.println("考核成绩表03输出完毕");
    }

    @Test
    public void testWrite07() throws Exception {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        Sheet sheet = workbook.createSheet("考核成绩表");
        // 创建第一行
        Row row1 = sheet.createRow(0);// 第一行
        // 创建单元格
        Cell cell1 = row1.createCell(0);// 第一行的第一列
        cell1.setCellValue("语文");
        Cell cell2 = row1.createCell(1);
        cell2.setCellValue(100);
        // 第二行
        Row row2 = sheet.createRow(1);// 第一行
        Cell cell21 = row2.createCell(0);// 第一行的第一列
        cell21.setCellValue("时间");
        Cell cell22 = row2.createCell(1);
        cell22.setCellValue(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        // 生成表,IO流,07版本使用xlsx后缀
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "考核成绩表07.xlsx");
        workbook.write(fileOutputStream);
        // 关闭流
        fileOutputStream.close();
        System.out.println("考核成绩表07输出完毕");

    }

    /**
     * 功能描述: 测试Excel表格设置数据下拉列表
     *
     * @author Jack_Liberty
     * 
     * @date 2021-02-21 16:30
     * 
     */

    @Test
    public void testExcelDataValidation() {
        // 1、创建一个工作簿
        XSSFWorkbook wb = new XSSFWorkbook();
        // 导出 输出流
        FileOutputStream fileOutputStream = null;
        // 表头信息
        String[] headerArrays = { "学号", "姓名", "年龄", "班级", "家庭住址" };
        try {
            // 1、创建一个工作簿
            wb = new XSSFWorkbook();
            // 2、创建一个sheet
            Sheet sheet = wb.createSheet("学生信息");
            // 3、创建表头
            Row row = sheet.createRow(0);

            for (int i = 0; i < headerArrays.length; i++) {
                row.createCell(i).setCellValue(headerArrays[i]);

            }
            // 4、下拉数据
            List<String[]> dropDownLDataList = new ArrayList<>();
            // 模拟数据
            String[] classArrays = { "A001", "A002", "A003", "A004", "A005" };
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                list.add("家庭住址" + i);
            }
            String[] homeAddress = new String[list.size()];
            list.toArray(homeAddress);
            dropDownLDataList.add(classArrays);
            dropDownLDataList.add(homeAddress);
            // 5、生成下拉数据sheet页
            generateDropDownDataSheet(wb, dropDownLDataList);
            // 6、给班级设置数据验证也就是添加下拉列表
            // String classFormula = "dropDownData!$A$1:$A$" + classArrays.length;
            String[] classFormula = classArrays;
            DataValidation classDataValidation = setCellDataValidation(wb, classFormula, 1, 1000, 3, 3);
            sheet.addValidationData(classDataValidation);
            // 7、给家庭住址设置数据验证也就是添加下拉列表
            // String homeFormula = "dropDownData!$B$1:$B$" + homeAddress.length;
            String homeFormula = "dropDownData!$B$1:$B$" + homeAddress.length;
            DataValidation homeDataValidation = setDataValidation(wb, homeFormula, 1, 1000, 4, 4);
            sheet.addValidationData(homeDataValidation);
            // 获取时分秒字符串
            String fileTime = DateTime.now().toString("yyyyMMddHHmmss");
            fileOutputStream = new FileOutputStream(PATH + "考核成绩表" + fileTime + ".xlsx");
            wb.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 功能描述: 创建Excel模板下拉列表值存储工作表并设置值
     *
     * @param wb   工作簿
     * @param list 下拉框数据
     * @author Jack_Liberty
     * @date 2021-02-21 16:02
     */

    public static Sheet generateDropDownDataSheet(Workbook wb, List<String[]> list) {
        Sheet sheet = wb.createSheet("dropDownData");
        // 遍历下拉数据并添加至该sheet页
        for (int i = 0; i < list.size(); i++) {
            // 设置列宽
            sheet.setDefaultColumnWidth(4000);
            for (int x = 0; x < list.get(i).length; x++) {
                // 第一个下拉框直接创建行和列
                if (i == 0) {
                    Row row = sheet.createRow(x);
                    Cell cell = row.createCell(i);
                    cell.setCellValue(list.get(i)[x]);
                } else {
                    // 获取行数
                    int lastRowNum = sheet.getLastRowNum();
                    // 如果行已存在则直接获取行创建列并添加数据
                    if (x <= lastRowNum) {
                        Row row = sheet.getRow(x);
                        Cell cell = row.createCell(i);
                        cell.setCellValue(list.get(i)[x]);
                    } else {
                        // 创建行、列添加数据
                        // 设置列宽
                        sheet.setDefaultColumnWidth(4000);
                        sheet.createRow(x).createCell(i).setCellValue(list.get(i)[x]);
                    }

                }

            }

        }
        // 设置隐藏sheet页
        wb.setSheetHidden(wb.getSheetIndex("dropDownData"), true);
        return sheet;

    }

    /**
     * 功能描述: 绑定下拉列表数据
     *
     * @param wb       工作簿
     * @param formula  P公式
     * @param firstRow 起始行
     * @param endRow   结束行
     * @param firstCol 起始列
     * @param endCol   结束列
     * @return org.apache.poi.ss.usermodel.DataValidation
     * @author Jack_Liberty
     * @date 2021-02-21 16:17
     *       <p>
     *       <p>
     *       <p>
     *       String formula = "orgInfo!$A$1:$A$59"
     *       <p>
     *       表示orgInfo工作表A列1-59行作为下拉列表来源数据
     */

    public static DataValidation setDataValidation(XSSFWorkbook wb, String formula, int firstRow, int endRow,
            int firstCol, int endCol) {
        // 获取下拉框数据来源sheet
        XSSFSheet sheet = wb.getSheet("dropDownData");
        // 指定设置下拉框的单元格范围
        CellRangeAddressList cellRangeAddress = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 创建数据验证助手
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        // 创建公式列表约束
        DataValidationConstraint constraint = dvHelper.createFormulaListConstraint(formula);
        // 创建验证 并返回
        return dvHelper.createValidation(constraint, cellRangeAddress);
    }

    /**
     * 功能描述: 绑定下拉列表数据
     *
     * @param wb       工作簿
     * @param formula  P公式
     * @param firstRow 起始行
     * @param endRow   结束行
     * @param firstCol 起始列
     * @param endCol   结束列
     * @return org.apache.poi.ss.usermodel.DataValidation
     * @author Jack_Liberty
     * @date 2021-02-21 16:17
     *       <p>
     *       <p>
     *       <p>
     *       String formula = "orgInfo!$A$1:$A$59"
     *       <p>
     *       表示orgInfo工作表A列1-59行作为下拉列表来源数据
     */

    public static DataValidation setCellDataValidation(XSSFWorkbook wb, String[] formula, int firstRow, int endRow,
            int firstCol, int endCol) {
        // 获取下拉框数据来源sheet
        XSSFSheet sheet = wb.getSheet("dropDownData");
        // 指定设置下拉框的单元格范围
        CellRangeAddressList cellRangeAddress = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 创建数据验证助手
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        // 创建序列数据
        DataValidationConstraint constraint = dvHelper.createExplicitListConstraint(formula);
        // 创建验证 并返回
        return dvHelper.createValidation(constraint, cellRangeAddress);
    }
}
