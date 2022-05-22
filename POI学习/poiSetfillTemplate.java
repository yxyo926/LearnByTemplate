package com.numberone.project.learn.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LearnPOIexportController {
    // 构建路径
    String PATH = "F:\\";

    // 导出 输出流
    FileOutputStream fileOutputStream = null;

    // private static final Logger logger =
    // LoggerFactory.getLogger(ExcelController.class);
    //
    // @Autowired
    // private CourseManagementService courseManagementService;

    @GetMapping("/download/student/import/template")
    @ApiOperation("下载学员导入模板")
    @Test
    // public void downloadStudentImportTemplate(HttpServletRequest request,
    // HttpServletResponse response, Long schoolId) throws Exception {
    public void downloadStudentImportTemplate() throws Exception {
        try {
            String[] titleArray = { "*学员姓名", "*性别", "*出生日期", "*联系人手机号", "联系人关系", "*报读课程", "*剩余已购课时数", "*购买时课时单价",
                    "剩余赠送课时数", "*有效期至", "模板填写说明" };
            // 1.创建Excel工作薄对象
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 2.创建Excel工作表对象
            HSSFSheet sheet = workbook.createSheet();
            // 3.创建Excel工作表的行
            HSSFRow row = sheet.createRow(0);
            // 默认每列宽度400 sheet.setColumnWidth(0, 3766);
            // //第一个参数表明列id(0为第一列，索引。从0开始),第2个参数表明宽度值
            int defaultColumnWidth = 10;
            for (int i = 0; i < titleArray.length; i++) {

                String title = titleArray[i];
                // 创建列
                HSSFCell cell = row.createCell(i);
                // 设置Excel cell的值
                cell.setCellValue(title);
                // 单元格样式
                HSSFCellStyle cellStyle = workbook.createCellStyle();

                // 设置每列的宽度 汉字是512，数字是256
                sheet.setColumnWidth(i, defaultColumnWidth * 512);
                if ("模板填写说明".equals(title)) {
                    sheet.setColumnWidth(i, 3 * defaultColumnWidth * 512);
                }
                /*
                 * if("*有效期至".equals(title)){
                 * //设置日期格式
                 * // HSSFDataFormat format=workbook.createDataFormat();
                 * // cellStyle.setDataFormat(format.getFormat("yyyy年m月d日"));
                 * CreationHelper createHelper=workbook.getCreationHelper();
                 * // cellStyle.setDataFormat(createHelper.createDataFormat().
                 * getFormat("yyyymmdd hh:mm:ss"));
                 * cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyymmdd")
                 * );
                 * cell.setCellValue(new Date());
                 * }
                 */

                // 文本设置
                HSSFFont font = workbook.createFont();
                font.setFontName("仿宋_GB2312");
                // 粗体显示
                font.setBold(true);
                // font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                // 字体大小
                font.setFontHeightInPoints((short) 12);

                if (!title.equals("联系人关系") && !title.equals("剩余赠送课时数")) {
                    // 字体红色
                    font.setColor(HSSFColor.RED.index);
                    /*
                     * //前景色
                     * cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                     * //设置为1将用前景色填充单元格 即使用setFillForegroundColor的颜色为单元格填充为背景色
                     * cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                     */
                }
                cellStyle.setFont(font);
                // 设置自动换行
                cellStyle.setWrapText(true);
                // 居中
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                // 下边框
                cellStyle.setBorderBottom(BorderStyle.THIN);
                // 左边框
                cellStyle.setBorderLeft(BorderStyle.THIN);
                // 上边框
                cellStyle.setBorderTop(BorderStyle.THIN);
                // 右边框
                cellStyle.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(cellStyle);
            }

            // 性别
            List<String> sexList = new ArrayList<>();
            sexList.add("男");
            sexList.add("女");
            String[] sexArray = sexList.toArray(new String[sexList.size()]);
            selectList(workbook, 1, 1, sexArray);

            // 联系人关系
            List<String> parentContactList = new ArrayList<>();
            // 模拟数据
            String[] classArrays = { "A001", "A002", "A003", "A004", "A005" };

            // ParentEnums[] parentContactEnums = ParentEnums.values();
            // for (ParentEnums parentContactEnum : parentContactEnums) {
            // parentContactList.add(parentContactEnum. getMessage());
            // }
            String[] parentContactArray = parentContactList.toArray(new String[parentContactList.size()]);
            selectList(workbook, 4, 4, parentContactArray);

            // 报读课程
            // List<String> courseList= courseManagementService.getAllNamesList(schoolId);
            // String[] courseArray = courseList.toArray(new String[courseList.size()]);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                list.add("家庭住址" + i);
            }
            String[] courseArray = new String[list.size()];
            list.toArray(courseArray);
            selectList(workbook, 5, 5, courseArray);
            workbook.setSheetName(0, "学员导入");

            // 添加模板填写说明提示内容
            HSSFRow row1 = sheet.createRow(1);
            HSSFCell cell = row1.createCell(titleArray.length - 1);
            // 单元格样式
            HSSFCellStyle cellStyle1 = workbook.createCellStyle();
            // 设置自动换行
            cellStyle1.setWrapText(true);
            // 向左靠齐
            cellStyle1.setAlignment(HorizontalAlignment.LEFT);
            // 向上靠齐
            cellStyle1.setVerticalAlignment(VerticalAlignment.TOP);
            cell.setCellValue(getTips());
            cell.setCellStyle(cellStyle1);
            // 第二行(索引1)开始向下合并40行
            CellRangeAddress region = new CellRangeAddress(1, 33, titleArray.length - 1, titleArray.length - 1);
            sheet.addMergedRegion(region);

            try {
                // 一个流 两个头
                // 文件名称
                String filename = "学员导入模板.xls";
                // response.setContentType("application/ms-excel");
                // response.setCharacterEncoding("UTF-8");
                String encodedFileName = null;
                // 如果是IE,通过URLEncoder对filename进行UTF8编码。而其他的浏览器（firefox、chrome、safari、opera），则要通过字节转换成ISO8859-1。
                // if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                //// encodedFileName = URLEncoder.encode(filename, "UTF-8");
                // } else {
                // encodedFileName = new String(filename.getBytes("UTF-8"), "ISO8859-1");
                // }
                // response.setHeader("Content-Disposition", "attachment; filename=" +
                // encodedFileName);//设置文件头编码方式和文件名
                // OutputStream out = response.getOutputStream();
                // workbook.write(out);
                // workbook.close(file);
                String fileTime = DateTime.now().toString("yyyyMMddHHmmss");
                fileOutputStream = new FileOutputStream(PATH + "考核成绩表" + fileTime + ".xlsx");
                workbook.write(fileOutputStream);
                workbook.close();
                fileOutputStream.close();
            } catch (Exception e) {
                // logger.error("下载学员导入模板报错误",e);
            }
        } catch (Exception e) {
            // logger.error("下载学员导入模板报错误",e);
        }
    }

    /**
     * firstRow 開始行號 根据此项目，默认为2(下标0开始)
     * lastRow 根据此项目，默认为最大65535
     * firstCol 区域中第一个单元格的列号 (下标0开始)
     * lastCol 区域中最后一个单元格的列号
     * strings 下拉内容
     */
    public static void selectList(Workbook workbook, int firstCol, int lastCol, String[] strings) {
        // 超过20行下拉框会出问题， String literals in formulas can't be bigger than 255 characters
        // ASCII 需要else单独处理
        if (strings.length <= 20) {
            Sheet sheet = workbook.getSheetAt(0);
            // 生成下拉列表
            // 只对(x，x)单元格有效
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 65535, firstCol, lastCol);
            // 生成下拉框内容
            DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(strings);
            HSSFDataValidation dataValidation = new HSSFDataValidation(cellRangeAddressList, dvConstraint);
            // 对sheet页生效
            sheet.addValidationData(dataValidation);
        } else {
            // 将下拉框数据放到新的sheet里，然后excle通过新的sheet数据加载下拉框数据
            Sheet hidden = workbook.createSheet("hidden");
            // 创建单元格对象
            Cell cell = null;
            // 遍历我们上面的数组，将数据取出来放到新sheet的单元格中
            for (int i = 0, length = strings.length; i < length; i++) {
                // 取出数组中的每个元素
                String name = strings[i];
                // 根据i创建相应的行对象（说明我们将会把每个元素单独放一行）
                Row row = hidden.createRow(i);
                // 创建每一行中的第一个单元格
                cell = row.createCell(0);
                // 然后将数组中的元素赋值给这个单元格
                cell.setCellValue(name);
            }
            // 创建名称，可被其他单元格引用
            Name namedCell = workbook.createName();
            namedCell.setNameName("hidden");
            // 设置名称引用的公式
            namedCell.setRefersToFormula("hidden" + "!$A$1:$A$" + strings.length);
            // 加载数据,将名称为hidden的sheet中的数据转换为List形式
            DVConstraint constraint = DVConstraint.createFormulaListConstraint("hidden");

            // 设置第一列的3-65534行为下拉列表
            // (3, 65534, 2, 2) ====> (起始行,结束行,起始列,结束列)
            CellRangeAddressList regions = new CellRangeAddressList(1, 65535, firstCol, lastCol);
            // 将设置下拉选的位置和数据的对应关系 绑定到一起
            DataValidation dataValidation = new HSSFDataValidation(regions, constraint);

            // 将第二个sheet设置为隐藏
            workbook.setSheetHidden(1, true);
            // 将数据赋给下拉列表
            workbook.getSheetAt(0).addValidationData(dataValidation);
        }
    }

    private String getTips() {
        StringBuilder tips = new StringBuilder();
        tips.append("\n");
        tips.append("【导入提示】\n");
        tips.append("导入学员数据之前，必须在系统中完成课程创建，本表格「报读课程」会自动读取系统中创建的名称，若无课程，数据将导入失败\n");
        tips.append("\n");
        tips.append("【填写规范】\n");
        tips.append("1、请勿修改顶部字段标题及顺序\n");
        tips.append("2、标*字段,「学员姓名」、「性别」、「出生日期」、「报读课程」、「剩余课时数」和「购买课时单价」为必填项，「报读课程」为下拉筛选项\n");
        tips.append("3 、「剩余已购课时数」/「剩余赠送课时数」/「购买课时单价」只支持输入阿拉伯数字，请勿携带单位“节”或“元”\n");
        tips.append("5、「出生日期」和「有效期至」(课程截止日期)的日期格式支持年月日输入，请按20210121格式输入\n");
        tips.append("6、「购买课时单价」指学员实际购买课时的对应单价（不含赠送课时），如学员购买10课时，赠送x课时，花费1000元，则购买课时单价为100元（系统最多支持小数点后2位）\n");
        tips.append("\n");
        tips.append("【其他注意】\n");
        tips.append(
                "1、若学员报名了多门课程，需填写多条记录，请保持「学员姓名」、「手机号」和「性别」相同；若一个学员在同同一课程下有多个有剩余课时的订单，请填写多条记录，请保持「学员姓名」、「手机号」和「性别」相同；\n");
        tips.append("2、剩余已购课时数不包含赠送课时数，如总剩余课时40（包含5赠送课时），则填写剩余已购课时35，剩余赠送课时5\n");
        return tips.toString();
    }
}
