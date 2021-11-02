package it.feio.android.omninotes.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.feio.android.omninotes.models.Region;

public class StrUtil {

    public  static  List<Region> matchPatternStr(String string , String  patternStr){
//        String line = "2016年10月11日我們去哪里玩近期我們近日，次年，同年等等，10月，2019年，3月4日";

//        String  patternStr;
//        Pattern datePattern = Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日|\\d{4}年\\d{1,2}月|\\d{1,2}月\\d{1,2}日|\\d{4}年|\\d{1,2}月|同年|次年|近日|近期");
        Pattern pattern = Pattern.compile(patternStr);

        Matcher matcher = pattern.matcher(string);

//        int dateCount = 0;

//        List<int[]>list=new ArrayList<>();
        List<Region> regions=new ArrayList<>();
        while(matcher.find()) {
//            dateMatcher.regionStart()
//            找到了 开始
//            dateMatcher.regionStart()
//            int[]
//            Region region=new Region(  dateMatcher.regionStart(),  dateMatcher.regionEnd());
            Region region=new Region(  matcher.start(),  matcher.end());
            regions.add(region);
//            String group = dateMatcher.group();
//            System.out.println(dateMatcher.group());

//            ++dateCount;

        }
        return regions;
//————————————————
//        版权声明：本文为CSDN博主「weixin_39999222」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/weixin_39999222/article/details/114782840
    }
}
