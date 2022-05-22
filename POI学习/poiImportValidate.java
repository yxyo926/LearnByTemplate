import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.business.domain.draft.AcceptanceBill;
import com.business.mapper.draft.AcceptanceBillMapper;
import com.business.param.draft.request.AcceptanceBillListVO;
import com.business.param.draft.request.AcceptanceBillVO;
import com.business.param.draft.request.AcceptanceImportVO;
import com.business.param.draft.response.AcceptanceImportResponseItem;
import com.business.param.draft.response.AcceptanceResponseItem;
import com.general.dto.PageResponse;
import com.general.dto.Result;
import com.general.util.OrderByCheckUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description 承兑汇票Service
 * @Author Luffy
 * @Date 2020-09-10 10:36
 */
@Service
@Slf4j
// public class AcceptanceService {
    public class poiImportValidate {

    @Autowired
    private AcceptanceBillMapper acceptanceBillMapper;

    /**
     * @Description
     * excel中的字段: 汇票号码(30位数字) 出票单位 汇票金额	出票银行	出票日期	到票日期
     * 此功能不添加事务,允许有不可导入的数据出现(如,票号重复)
     * @Author Luffy
     * @Date 2020-09-10 14:47
     */
    public Result<AcceptanceImportResponseItem> importAcceptDraft(AcceptanceImportVO requestVO) {

        AcceptanceImportResponseItem resp = new AcceptanceImportResponseItem();

        //1.获取文件
        File file = new File(requestVO.getAccessUrl());
        if(!file.exists()){
            return Result.buildFail("获取文件失败,未在对应地址匹配到文件!");
        }
        FileInputStream fis = null;
        Workbook workBook = null;
        try {
            fis = new FileInputStream(file);
            //判断文件类型
            if (requestVO.getAccessUrl().toLowerCase().endsWith(".xls")) {
                // 使用 HSSFWorkbook 解析xls
                workBook = new HSSFWorkbook(fis, true);
            } else if (requestVO.getAccessUrl().toLowerCase().endsWith(".xlsx")) {
                // 使用 XSSFWorkbook 解析xlsx
                workBook = new XSSFWorkbook(fis);
            } else {
                return Result.buildFail("文件格式错误");
            }
            //2.获取excel数据存入map;
            List<Map<Integer, List<String>>> formatList = checkExcelFormat(workBook);
            //总的有效数据条数totalRows(不含首行中文列名),总的字段列数totalCols
            int totalRows =  Integer.valueOf(formatList.get(1).get(0).get(1));
            int totalCols =  Integer.valueOf(formatList.get(1).get(0).get(2));
            List<String> formatFailReasonList = formatList.get(1).get(1);

            //3.解析,根据业务处理数据文件
            String userLogin = requestVO.getUserPartyVO().userLoginId();
            List<Map<Integer, List<String>>> dealList = dealImportedAccept(formatList,userLogin,totalCols);
            int dealSuccNum = Integer.valueOf(dealList.get(0).get(0).get(0));
            List<String> dealFailReasonList = dealList.get(0).get(1);

            //反馈信息合并(成功,失败条数)
            resp.setSuccNum(dealSuccNum);
            resp.setFailNum(totalRows-dealSuccNum);
            //反馈信息合并(反馈信息list)
            formatFailReasonList.addAll(dealFailReasonList);
            resp.setFailReasonList(formatFailReasonList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally{ //关流
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(workBook != null){
                try {
                    workBook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.buildSuccess(resp);
    }

    /**
     * @Description 获取excel的数据存入为Map<Integer, List<String>>/list.get(0)
     * 反馈数据也Map<Integer, List<String>>/list.get(1)
     * @Author Luffy
     * @Date 2020-09-10 15:50
     */
    public List<Map<Integer, List<String>>> checkExcelFormat(Workbook workBook){
        //1.获取第一个sheet
        Sheet sheet = workBook.getSheetAt(0);
        List<Map<Integer, List<String>>> mapList = new ArrayList<>();

        //表单map
        Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        //反馈信息map
        Map<Integer, List<String>> respMsgMap = new HashMap<Integer, List<String>>();
        List<String> respMsgList = new ArrayList<>();
        //数据总行数
        int totalRows = sheet.getPhysicalNumberOfRows();
        //成功执行条数
        int succNum = 0;
        //票据号码
        String billId = "";

        //定义各字段位置 0:票据号码 1:出票单位	2:汇票金额 3:出票银行 4:出票日期 5:到票日期 6:保证金金额	7:敞口金额
        int c0=0;int c1=1;int c2=2;int c3=3;int c4=4;int c5=5;int c6=6;int c7=7;
        Map<Integer, String> ms = new HashMap<>();
        ms.put(c0,"票据号码");ms.put(c1,"出票单位");ms.put(c2,"汇票金额");ms.put(c3,"出票银行");
        ms.put(c4,"出票日期");ms.put(c5,"到票日期");ms.put(c6,"保证金金额");ms.put(c7,"敞口金额");

        //(文件读取的)总的标题列的数量
        int totalCols = sheet.getRow(0).getPhysicalNumberOfCells();
        if(totalCols!=ms.size()) {
            respMsgList.add("请检查文件,标题列数量不对");
        }else {
            for(Row row : sheet){
                map.put(succNum, new ArrayList<String>());

                //实际excel表中描述的行数
                int excelRowNum = row.getRowNum()+1;
                //异常标志
                Boolean failedFlag = false;
                //2.校验中文标题(标题描述,格式,非空,防止错位),标题错误->不可导入任何数据
                if (row.getRowNum()==0) {
                    //非空及格式校验
                    for(int i=0;i<totalCols;i++) {
                        if(row.getCell(i).getCellType()!=CellType.STRING.getCode() || (row.getCell(i).getCellType()==CellType.BLANK.getCode())) {
                            respMsgList.add("第"+i+"列标题格式不对,应当为非空的字符串类型");
                            failedFlag = true;
                        }
                    }
                    //文字描述校验
                    for (Map.Entry<Integer, String> entry : ms.entrySet()) {
                        if (!ms.get(entry.getKey()).equals(row.getCell(entry.getKey()).getRichStringCellValue().getString())) {
                            respMsgList.add("第"+(entry.getKey()+1)+"列标题应当为 "+ms.get(entry.getKey()));
                            failedFlag = true;
                        }
                    }
                    if(failedFlag) {
                        break;
                    }
                }
                //3.校验数据(格式,必填性),长度
                else {
                    //票据号码
                    if(checkBlank(row,c0)) {
                        respMsgList.add("第"+excelRowNum+"行,"+ms.get(c0)+"不能为空");
                        failedFlag = true;
                    }else {
                        if(!checkStr(row,c0)) {
                            respMsgList.add("第"+excelRowNum+"行,"+ms.get(c0)+"格式不对,应当为字符串类型");
                            failedFlag = true;
                        }else {
                            if (row.getCell(c0).getRichStringCellValue().getString().length()!=8) {
                                respMsgList.add("第"+excelRowNum+"行,"+ms.get(c0)+"长度应为8位");
                                failedFlag = true;
                            }
                            map.get(succNum).add(row.getCell(c0).getRichStringCellValue().getString());
                            billId = row.getCell(c0).getRichStringCellValue().getString();
                        }
                    }
                    //出票单位
                    if(checkBlank(row,c1)) {
                        respMsgList.add("第"+excelRowNum+"行,票号[" + billId +"]"+ms.get(c1)+"不能为空");
                        failedFlag = true;
                    }else {
                        if(!checkStr(row,c1)) {
                            respMsgList.add("第"+excelRowNum+"行,"+ms.get(c1)+"格式不对,应当为字符串类型");
                            failedFlag = true;
                        }else {
                            if (row.getCell(c1).getRichStringCellValue().getString().length()>64) {
                                respMsgList.add("第"+excelRowNum+"行,"+ms.get(c1)+"长度不超过64位字符");
                                failedFlag = true;
                            }
                            map.get(succNum).add(row.getCell(c1).getRichStringCellValue().getString());
                        }
                    }
                    //汇票金额
                    if(checkBlank(row,c2)) {
                        respMsgList.add("第"+excelRowNum+"行,"+ms.get(c2)+"不能为空");
                        failedFlag = true;
                    }else {
                        if(!checkNumber(row,c2)) {
                            respMsgList.add("第"+excelRowNum+"行,"+ms.get(c2)+"格式不对,应当为数字类型");
                            failedFlag = true;
                        }else {
                            if (row.getCell(c2).getNumericCellValue()<=0 || row.getCell(c2).getNumericCellValue()>=100000000) {
                                respMsgList.add("第"+excelRowNum+"行,"+ms.get(c2)+"取值范围应在0-1亿");
                                failedFlag = true;
                            }
                            map.get(succNum).add(String.valueOf(row.getCell(c2).getNumericCellValue()));
                        }
                    }
                    //出票银行
                    if(checkBlank(row,c3)) {
                        respMsgList.add("第"+excelRowNum+"行,"+ms.get(c3)+"不能为空");
                        failedFlag = true;
                    }else {
                        if(!checkStr(row,c3)) {
                            respMsgList.add("第"+excelRowNum+"行,"+ms.get(c3)+"格式不对,应当为字符串类型");
                            failedFlag = true;
                        }else {
                            if (row.getCell(c3).getRichStringCellValue().getString().length()>64) {
                                respMsgList.add("第"+excelRowNum+"行,"+ms.get(c3)+"长度不超过64位字符");
                                failedFlag = true;
                            }
                            map.get(succNum).add(row.getCell(c3).getRichStringCellValue().getString());
                        }
                    }
                    //出票日期
                    if(checkBlank(row,c4)) {
                        respMsgList.add("第"+excelRowNum+"行,"+ms.get(c4)+"不能为空");
                        failedFlag = true;
                    }else {
                        if(checkDate(row,c4)) {
                            map.get(succNum).add(String.valueOf(row.getCell(c4).getDateCellValue()));
                        }else {
                            respMsgList.add("第"+excelRowNum+"行,"+ms.get(c4)+"格式不对,应当为(yyyy-MM-dd)日期类型");
                            failedFlag = true;
                        }
                    }
                    //到票日期
                    if(checkBlank(row,c5)) {
                        respMsgList.add("第"+excelRowNum+"行,"+ms.get(c5)+"不能为空");
                        failedFlag = true;
                    }else {
                        if(checkDate(row,c5)) {
                            map.get(succNum).add(String.valueOf(row.getCell(c5).getDateCellValue()));
                        }else {
                            respMsgList.add("第"+excelRowNum+"行,"+ms.get(c5)+"格式不对,应当为(yyyy-MM-dd)日期类型");
                            failedFlag = true;
                        }
                    }
                    //保证金金额
                    if(!checkBlank(row,c6)) {
                        if(!checkNumber(row,c6)) {
                            respMsgList.add("第"+excelRowNum+"行,"+ms.get(c6)+"格式不对,应当为数字类型");
                            failedFlag = true;
                        }else {
                            if (row.getCell(c6).getNumericCellValue()<=0 || row.getCell(c6).getNumericCellValue()>100000000) {
                                respMsgList.add("第"+excelRowNum+"行,"+ms.get(c6)+"范围大于0,不超过1亿");
                                failedFlag = true;
                            }
                            map.get(succNum).add(String.valueOf(row.getCell(c6).getNumericCellValue()));
                        }
                    }
                    //敞口金额
                    if(!checkBlank(row,c7)) {
                        if(!checkNumber(row,c7)) {
                            respMsgList.add("第"+excelRowNum+"行,"+ms.get(c7)+"格式不对,应当为数字类型");
                            failedFlag = true;
                        }else {
                            if (row.getCell(c7).getNumericCellValue()<=0 || row.getCell(c7).getNumericCellValue()>=100000000) {
                                respMsgList.add("第"+excelRowNum+"行,"+ms.get(c7)+"取值范围应在0-1亿");
                                failedFlag = true;
                            }
                            map.get(succNum).add(String.valueOf(row.getCell(c7).getNumericCellValue()));
                        }
                    }
                    //日期检验(出票日期<=当前日期<到票日期)
                    if (row.getCell(c4).getDateCellValue().compareTo(new Date())>0) {
                        respMsgList.add("第"+excelRowNum+"行,出票日期不应超过当前日期");
                        failedFlag = true;
                    }
                    if (row.getCell(c4).getDateCellValue().compareTo(row.getCell(c5).getDateCellValue())>=0) {
                        respMsgList.add("第"+excelRowNum+"行,到票日期应当在出票日期之后");
                        failedFlag = true;
                    }
                    if(!failedFlag) {
                        succNum++;
                    }
                }
            }
        }

        //组装反馈信息
        List<String> respNumMsgList = new ArrayList<>();
        respNumMsgList.add(String.valueOf(succNum));
        //非标题总行数
        respNumMsgList.add(String.valueOf(totalRows-1));
        respNumMsgList.add(String.valueOf(totalCols));
        //成功,列条数
        respMsgMap.put(0,respNumMsgList);
        //反馈信息
        respMsgMap.put(1,respMsgList);

        //存入(数据mapList,反馈信息mapList)
        mapList.add(map);
        mapList.add(respMsgMap);
        return mapList;
    }

    /**
     * @Description 解析,根据业务处理数据(存入db,反馈成功失败数据消息)
     * @Author Luffy
     * 字段: 汇票号码,出票单位,汇票金额,出票银行,出票日期,到票日期
     * @notice 模板里将日期为日期类型(默认也是如此),日期支持常规日期格式以及日期的数值型
     * @Date 2020-09-10 17:30
     */
    private List<Map<Integer, List<String>>> dealImportedAccept(List<Map<Integer, List<String>>> formatList,String userLogin,int totalCols) throws Exception {
        //sheet数据
        Map<Integer, List<String>> map = formatList.get(0);
        //反馈信息
        List<Map<Integer, List<String>>> repMsgMapList = new ArrayList<>();
        Map<Integer, List<String>> respMsgMap = new HashMap<Integer, List<String>>();
        List<String> respMsgList = new ArrayList<>();

        //1.遍历 Map获取解析结果
        Set<Integer> keys = map.keySet();
        Iterator<Integer> it = keys.iterator();
        int succNum = 0;
        while(it.hasNext()){
            List<String> list = map.get(it.next());
            if(list.size()>0) {
                //2.验存
                QueryWrapper wrapper = new QueryWrapper();
                wrapper.eq("acceptance_bill_id", list.get(0));
                if(this.acceptanceBillMapper.selectCount(wrapper)<=0) {
                    //3.大小不等说明格式有校验失败(continue)的,notice:此种判定需要sheet所有列的字段有值(格式检验,存储规则为List<String>,及时赋空亦为值)
                    //加try,因为可能插入异常,如数据超长等
                    try {
                        if(list.size()==totalCols) {
                            AcceptanceBill acceptanceBill = new AcceptanceBill();
                            //4.依次存入字段(汇票号码,出票单位,汇票金额,出票银行,出票日期,到票日期)
                            acceptanceBill.setAcceptanceBillId(list.get(0));
                            acceptanceBill.setIssueByParty(list.get(1));
                            acceptanceBill.setAmount(new BigDecimal(list.get(2)));
                            acceptanceBill.setIssueByBank(list.get(3));
                            acceptanceBill.setFromDate(transDateStr2Date(list.get(4)));
                            acceptanceBill.setThruDate(transDateStr2Date(list.get(5)));
                            acceptanceBill.setCreatedByUserLogin(userLogin);
                            acceptanceBill.setCreatedStamp(new Date());
                            this.acceptanceBillMapper.insert(acceptanceBill);
                            succNum++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        respMsgList.add("票号["+ list.get(0) + "]的数据格式不对,插入失败\"");
                    }
                }else {
                    respMsgList.add("票号["+ list.get(0) + "]的承兑汇票不可重复添加");
                }
            }
        }

        //组装反馈信息
        List<String> respNumMsgList = new ArrayList<>();
        respNumMsgList.add(String.valueOf(succNum));
        //成功,失败条数
        respMsgMap.put(0,respNumMsgList);
        //反馈信息
        respMsgMap.put(1,respMsgList);

        //存入反馈信息
        repMsgMapList.add(0,respMsgMap);
        return repMsgMapList;
    }

    /**
     * @Description 混合类日期格式(型如:Tue Sep 08 00:00:00 CST 2020)"字符串格式"转换为"日期格式"
     * @Author Luffy
     * @Date 2020-09-11 15:25
     */
    private Date transDateStr2Date(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        //java.util.Date对象
        Date date = (Date) sdf.parse(dateStr);
        return date;
    }
}
/**
     * @Description 单元格格式校验_空
     * @Author Luffy
     * @Date 2020/12/02
     */
    private Boolean checkBlank(Row row,int colNum) {
        return row.getCell(colNum).getCellType()==CellType.BLANK.getCode()?true:false;
    }

    /**
     * @Description 单元格格式校验_字符串
     * @Author Luffy
     * @Date 2020/12/02
     */
    private Boolean checkStr(Row row,int colNum) {
        return row.getCell(colNum).getCellType()==CellType.STRING.getCode()?true:false;
    }

    /**
     * @Description 单元格格式校验_日期
     * @备注 数字和日期在解析里用到的是同一个类型判定
     * @Author Luffy
     * @Date 2020/12/02
     */
    private Boolean checkDate(Row row,int colNum) {
        return row.getCell(colNum).getCellType()==CellType.NUMERIC.getCode()?true:false;
    }

    /**
     * @Description 单元格格式校验_金额(数字)
     * @Author Luffy
     * @Date 2020/12/02
     */
    private Boolean checkNumber(Row row, int colNum) {
        return row.getCell(colNum).getCellType()==CellType.NUMERIC.getCode()?true:false;
    }