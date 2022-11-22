package io.github.xxyopen.mylearn.generator.utils;

import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

public class CommonToolUtils {
    @Test
    public void getMondayByWeek(){
        // 截取字符串判断年份
        String  testStr=new String("Q00012202abcd");
        StringUtil.substring(testStr,5,9);
        String year= StringUtils.join("20",StringUtil.substring(testStr,5,7));
        String week= StringUtil.substring(testStr,7,9);
        WeekFields weekFields= WeekFields.ISO;
        LocalDate now = LocalDate.now();
        // 输入你想要的年份和周数
        LocalDate localDate = now.withYear(Integer.parseInt(year)).with(weekFields.weekOfYear(), Long.parseLong(week));
        // 周一（第一天）
        LocalDate localDate1 = localDate.with(weekFields.dayOfWeek(), 1L);
    }
}
