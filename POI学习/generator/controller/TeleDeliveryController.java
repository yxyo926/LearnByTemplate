package io.github.xxyopen.mylearn.generator.controller;

import io.github.xxyopen.mylearn.generator.entitty.TeleDeliveryVO;
import io.github.xxyopen.mylearn.generator.service.TeleDeliveryVOService;
import io.github.xxyopen.mylearn.generator.utils.ExcelExport;
import io.github.xxyopen.mylearn.generator.utils.ExcelImport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/learn")
@RequiredArgsConstructor
public class TeleDeliveryController {

    private final TeleDeliveryVOService teleDeliveryVOService;

    private static final String PATH = "E:\\POI\\";



    /**
     * 小说分类列表查询接口
     */
    @Operation(summary = "小说分类列表查询接口")
    @GetMapping("my/import")
    public Boolean listCategory(
            @Parameter(description = "作品方向", required = true) List<TeleDeliveryVO> workDirection) {

        return teleDeliveryVOService.saveBatch(workDirection);
    }


    public  void excelImport(){
       ExcelImport excelImport = new ExcelImport();
       FileInputStream fis;
       try {
           fis = new FileInputStream(new File(PATH+"testImportData.xlsx"));
           Map<Integer,List<List<Object>>> map = excelImport.getExcelRow("testImportData.xlsx", fis,"yyyy/MM/dd",true);
           if(map!=null) {
               List<List<Object>> lists = map.get(0);
               System.out.println(lists.toString());
               //将获取的参数在导出
               String str[] = new String[]{"商品id","商品名称","商品价格","有效期","商品图片","是否显示"};
               ExcelExport<TeleDeliveryVO> export = new ExcelExport<TeleDeliveryVO>();
               OutputStream os01 = new FileOutputStream(new File(PATH+"testExportData.xlsx"));
               lists.remove(0);
               //System.out.println("objectsList-->>"+objectsList);
               ArrayList<TeleDeliveryVO> teleDeliveryVOS = new ArrayList<TeleDeliveryVO>();
               for (List<Object> tempList: lists) {
                   TeleDeliveryVO teleDeliveryVO = new TeleDeliveryVO();
                   teleDeliveryVO.setDeliveryNoteId(Long.parseLong(tempList.get(0).toString()));
                   teleDeliveryVO.setDeliveryName(tempList.get(0).toString());
                   teleDeliveryVO.setBarcode(tempList.get(0).toString());
                   teleDeliveryVOS.add(teleDeliveryVO);
               }
               export.exportExcel("测试导出工具类",str,teleDeliveryVOS,os01,"yyyy-MM-dd HH:mm:ss");
           }else{
               System.out.println("导入excel文件格式错误");
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

}

