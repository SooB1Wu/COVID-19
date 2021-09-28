package com.soob.demo.service;

import com.soob.demo.pojo.ChartDataRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ForeCast {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private String requestPy(MultiValueMap<String,List<Integer>> map){
        RestTemplate restTemplate = new RestTemplate();
        String url="http://127.0.0.1:8000/forecast";
//        String url="http://121.5.169.34:8000/forecast";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, List<Integer>>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
        String resBody=response.getBody().replace(" ","");
        return resBody;
    }

    public void getForeCastData(ChartDataRes chartDataRes) throws ParseException {
        List<Integer> cases=new ArrayList<>(chartDataRes.casesList);
        List<Integer> deaths=new ArrayList<>(chartDataRes.deathsList);
        int[] lengths=new int[2];
        lengths[0]=cases.size();
        lengths[1]=cases.size();

        modifyList(cases,100);
        modifyList(deaths,20);

        System.out.println(cases);
        System.out.println(deaths);

        MultiValueMap<String,List<Integer>> map = new LinkedMultiValueMap<>();
        map.add("cases", cases);
        map.add("deaths", deaths);

        String resBody=requestPy(map);
        List<List<Integer>> formateLists=formateResponse(resBody,lengths);


        formateChartData(formateLists,chartDataRes);

    }

    private void formateChartData(List<List<Integer>> formateLists,ChartDataRes chartDataRes) throws ParseException {
        int size=chartDataRes.casesList.size();

        if(chartDataRes.casesList.get(size-1)<100){
            chartDataRes.forecastCasesList=new ArrayList<>(chartDataRes.casesList);
            chartDataRes.forecastDeathsList=new ArrayList<>(chartDataRes.deathsList);
            chartDataRes.forecastDateList=new ArrayList<>(chartDataRes.dateList);
        }
        else{
            int dis=formateLists.get(0).size()-formateLists.get(1).size();
            while(dis>0){
                formateLists.get(0).remove(formateLists.get(0).size()-1);
                dis--;
            }
            dis=formateLists.get(1).size()-formateLists.get(0).size();
            while(dis>0){
                formateLists.get(1).remove(formateLists.get(1).size()-1);
                dis--;
            }
            chartDataRes.forecastCasesList=new ArrayList<>(formateLists.get(0));
            chartDataRes.forecastDeathsList=new ArrayList<>(formateLists.get(1));
            int size0=chartDataRes.forecastCasesList.size();
            Date forecastDate=format.parse(chartDataRes.dateList.get(size-1));
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(forecastDate);
            rightNow.add(Calendar.DAY_OF_YEAR,30);//日期加30天
            for(int i=size0;i>0;i--){//循环插入前一天的数据
                chartDataRes.forecastDateList.add(0,format.format(rightNow.getTime()));
                rightNow.add(Calendar.DAY_OF_YEAR,-1);//减去一天
            }
        }
    }

    private void modifyList(List<Integer> cases,int ff){
        int i=0;
        int flag=cases.get(cases.size()-1)/ff;
        while(i<cases.size()){
            if(cases.get(i)<flag){
                cases.remove(i);
            }else{
                i++;
            }
        }
    }
    private List<List<Integer>> formateResponse(String json,int[] lengths){
        List<List<Integer>> res=new ArrayList<>();
        for(int i=0;i<2;i++){
            res.add(new ArrayList<>());
        }
        String[] listsStr=json.split("]");
        String[] casesStr=listsStr[0].substring(1).split(",");
        String[] deathsStr=listsStr[1].substring(1).split(",");
//        String[] casesDayStr=listsStr[2].substring(1).split(",");
//        String[] deathsDayStr=listsStr[3].substring(1,listsStr[3].length()).split(",");

        putDataList(casesStr,res.get(0));
        putDataList(deathsStr,res.get(1));
//        putDateList(casesDayStr,res.get(2),lengths[0]-res.get(0).size());
//        putDateList(deathsDayStr,res.get(3),lengths[1]-res.get(1).size());


        for(List<Integer> list:res){
            System.out.println(list);
        }

        return res;
    }

    private void putDataList(String[] strs,List<Integer> list){
        for(int i=0;i<strs.length;i++){
            if(strs[i].equals(null)||strs[i].length()==0)
                continue;
            if(isInteger(strs[i])){
                list.add(Integer.valueOf(strs[i]));
            }
        }
    }

//    private void putDateList(String[] strs,List<Integer> list,int date){
//        System.out.println("============");
//        System.out.println(Arrays.toString(strs));
//        for(int i=0;i<strs.length;i++){
//
//            if(strs[i].equals(null)||strs[i].length()==0)
//                continue;
//            if(strs[i]!=null&&isInteger(strs[i])){
//                list.add(Integer.valueOf(strs[i])+date);
//            }
//        }
//    }

    private boolean isInteger(String str) {

        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");

        return pattern.matcher(str).matches();

    }
}
