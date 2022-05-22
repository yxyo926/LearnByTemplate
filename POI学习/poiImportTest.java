@Test
    public void testCellType() throws Exception {

//获取文件流
        FileInputStream fis = new FileInputStream(PATH +"testData.xlsx");

//创建一个工作簿。使用 excel能操作的这边他都可以操作
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

//获取标题内容
        Row rowTitle = sheet.getRow(0);
        if (rowTitle != null) {
//得到一行有多少列有数据
            int cellCount = rowTitle.getPhysicalNumberOfCells();
            for (int cellNum = 0; cellNum < cellCount; cellNum++) {
                Cell cell = rowTitle.getCell(cellNum);
                if (cell != null) {
                    int cellType = cell.getCellType();
                    String cellValue = cell.getStringCellValue();
                    System.out.print(cellValue + "|");
                }
            }
            System.out.println();
        }

//获取表中的内容
//获取表中有多少行有数据
        int rowCount = sheet.getPhysicalNumberOfRows();
        for (int rowNum = 1; rowNum < rowCount; rowNum++) {
            Row rowData = sheet.getRow(rowNum);
            if (rowData != null) {
//读取列
                int cellCount = rowTitle.getPhysicalNumberOfCells();
                for (int cellNum = 0; cellNum < cellCount; cellNum++) {
                    System.out.println("[" + (rowNum + 1) + "-" + (cellNum + 1) + "]");

                    Cell cell = rowData.getCell(cellNum);
//匹配列的数据类型
                    if (cell != null) {
                        int cellType = cell.getCellType();
                        String cellValue = "";


                        switch (cellType) {
                            case HSSFCell.CELL_TYPE_STRING://字符
                                System.out.print("【 String】");
                                cellValue = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN://布尔
                                System.out.print("【 BOOLEAN】");
                                cellValue = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case HSSFCell.CELL_TYPE_BLANK://空
                                System.out.print("【 BLANK】");
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC://数字(日期、普通数字)
                                System.out.print("【 NUMERIC】");
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 日期
                                    System.out.print("--【日期】");
                                    Date date = cell.getDateCellValue();
                                    cellValue = new DateTime(date).toString("yyyy-MM-dd");
                                } else {
//不是日期格式，防止数字过长！
                                    System.out.print("--【转换为字符串输出】");
                                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                    cellValue = cell.toString();
                                }
                                break;
                            case HSSFCell.CELL_TYPE_ERROR://错误
                                System.out.print("【 数据类型错误】");
                                break;
                        }
                        System.out.println(cellValue);
                    }
                }
            }
        }
//关闭流
        fis.close();
    }