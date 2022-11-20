package io.github.xxyopen.mylearn.generator.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

public class ExcelExport<T> {

    /**
     * 利用了JAVA的反射机制，将放置在JAVA集合中并且符合一定条件的数据以EXCEL 的形式输出到指定IO设备上
     * @param title   表格标题名
     * @param headers 表格属性列名数组
     * @param dataSet 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param pattern 如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void exportExcel(String title, String[] headers,
                            Collection<T> dataSet, OutputStream out, String pattern) {
        // 声明一个工作薄，将1000条写入内存，其余在硬盘，以防止内存溢出
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为10个字节
        sheet.setDefaultColumnWidth((short) 10);
        //设置表格头样式
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);//设置背景色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成一个字体
        Font font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 产生表格标题行
        Row row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        // 设置表格内容样式
        CellStyle style2 = workbook.createCellStyle();
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        // 生成另一个字体
        Font font2 = workbook.createFont();
        font.setFontName("仿宋_GB2312");
        font.setColor((short) 64);//黑色
        // 把字体应用到当前的样式
        style2.setFont(font2);
        // 遍历集合数据，产生数据行
        Iterator<T> it = dataSet.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = (T) it.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (short i = 0; i < fields.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(style2);
                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                try {
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    // 根据javabean的方法类型，判断值的类型后进行强制类型转换（具体需要自己根据情况修改）
                    String textValue = null;
                    //javabean里边的get和set方法为Integer时做处理
                    if (value instanceof Integer) {
                        int intValue = (Integer) value;
                        cell.setCellValue(intValue);
                    }
                    else if (value instanceof Double) {
                        double intValue = (Double) value;
                        cell.setCellValue(intValue);
                    }
                    else if (value instanceof Boolean) {
                        boolean bValue = (Boolean) value;
                        textValue = bValue ? "是" : "否";
                        cell.setCellValue(textValue);
                    }
                    else if (value instanceof Date) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        textValue = sdf.format(date);
                        cell.setCellValue(textValue);
                    } else if (value instanceof byte[]) {
                        // 声明一个画图的顶级管理器
                        Drawing drawing= sheet.createDrawingPatriarch();
                        row.setHeightInPoints(60);
                        sheet.setColumnWidth(i, (short) (35.7 * 80));
                        byte[] bsValue = (byte[]) value;
                        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1023, 255, i, index, i+1, index+1);
                        anchor.setAnchorType(ClientAnchor.AnchorType.byId(0));
                        drawing.createPicture(anchor, workbook.addPicture(bsValue, XSSFWorkbook.PICTURE_TYPE_JPEG));
                    } else {
                        if (value != null) {
                            textValue = value.toString();
                        } else {
                            textValue = "";
                        }
                        cell.setCellValue(textValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

