package com.soob.demo.controller;



import com.soob.demo.pojo.*;
import com.soob.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;


@RestController
@Scope("session")
public class DemoTest {

    @Autowired
    SqlService sqlService;

    @Autowired
    ForeCast foreCast;

    /*
    *
    * {"country":"USA","state":"Texas","county":"Brooks"}
    * =>"country":"USA","state":"Texas","county":"Brooks"
    * =>f1=["country":"USA","state":"Texas","county":"Brooks"]
    * =>f2=[["country","USA"],["state","Texas"],["county","Brooks"]]
    * =>["USA","Texas","Brooks"]
    * =>[USA,Texas,Brooks]
    * */
    /*
    * json格式化为String[]
    * */
    private String[] formatJson(String json){
        json=json.substring(1,json.length()-1);
        String[] formatStr=json.split(",");
        String[] res=new String[formatStr.length];
        for(int i=0;i<formatStr.length;i++){
            String s=formatStr[i].split(":")[1];//按冒号分割后取第二项即为结果
            res[i]=s.substring(1,s.length()-1);//去除前后引号
        }
        System.out.println("Format to:"+ Arrays.toString(res));
        return res;
    }

    /*
    * 获取某天每个州的数据排名列表
    * */
    @RequestMapping("/statesByDate")
    @ResponseBody
    /*
    * String countryName,String dateStr,String sortKey
    * */
    public TableDataRes getStatesByDate(@RequestBody String req) throws ParseException {
        System.out.println(req);
        /*
        * [0]:countryName
        * [1]:date(值为last则查询最新数据)
        * [2]:sortKey
        * */
        String[] formatReq=formatJson(req);
        return sqlService.getAllStateDataByDate(formatReq[0],formatReq[1],formatReq[2]);
    }

    /*
     * 获取某天每个县的数据排名列表
     * */
    @RequestMapping("/countiesByDate")
    @ResponseBody
    public TableDataRes getCountiesByDate(@RequestBody String req) throws ParseException {
        System.out.println(req);
        /*
         * [0]:countryName
         * [1]:stateName
         * [2]:date(值为last则查询最新数据)
         * [3]:sortKey
         * */
        String[] formatReq=formatJson(req);
        return sqlService.getAllCountyDataByDate(formatReq[0],
                formatReq[1],formatReq[2],formatReq[3]);
    }


    /*
    * 获取州内所有的县名
    * */
    @RequestMapping("/countyNames")
    @ResponseBody
    public List<String> getCountyNames(@RequestBody String req) {
        System.out.println(req);
        String[] formatReq=formatJson(req);
        /*
         * [0]:countryName
         * [1]:stateName
         * */
        return sqlService.getCountyNames(formatReq[0],formatReq[1]);
    }


    /*
     * 获取州的总数据
     * */
    @RequestMapping("/countryData")
    @ResponseBody
    public ChartDataRes getCountryData(@RequestBody String req) throws ParseException {
        System.out.println(req);
        String[] formatReq=formatJson(req);
        /*
         * [0]:countryName
         * */
        ChartDataRes chartDataRes=sqlService.getCountryData(formatReq[0]);
        foreCast.getForeCastData(chartDataRes);
        return chartDataRes;
    }

    /*
    * 获取州的总数据
    * */
    @RequestMapping("/stateData")
    @ResponseBody
    public ChartDataRes getStateData(@RequestBody String req) throws ParseException {
        System.out.println(req);
        String[] formatReq=formatJson(req);
        /*
         * [0]:countryName
         * [1]:stateName
         * */
        ChartDataRes chartDataRes=sqlService.getStateData(formatReq[0],formatReq[1]);
        foreCast.getForeCastData(chartDataRes);
        return chartDataRes;
    }

    /*
    * 获取县的疫情数据
    * */
    @RequestMapping("/countyData")
    @ResponseBody
    public ChartDataRes getCountyData(@RequestBody String req) throws ParseException {
        System.out.println(req);
        String[] formatReq=formatJson(req);
        /*
         * [0]:countryName
         * [1]:stateName
         * [2]:countyName
         * */
        ChartDataRes chartDataRes=sqlService.getCountyData(formatReq[0],formatReq[1],formatReq[2]);
        foreCast.getForeCastData(chartDataRes);
        return chartDataRes;
    }
    /*
     * 获取日期范围
     * */
    @RequestMapping("/dateScope")
    @ResponseBody
    public List<String> getDateScope() {
        return sqlService.getDateScope();
    }


}
