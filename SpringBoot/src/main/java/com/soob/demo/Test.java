package com.soob.demo;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Test {

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        String minStr="2020/4/2";
        String maxStr="2020/5/19";
        Date minDate=dateFormat.parse(minStr);
        Date maxDate=dateFormat.parse(maxStr);

        while(!minDate.after(maxDate)){//按照日期补全数据
            System.out.println(dateFormat.format(minDate));
            minDate=new Date(minDate.getTime()+24*3600*1000);
        }

    }
//    public static List<String> readCSV(String readPath) {
//        String filePath = readPath;
//        List<String> listData = new ArrayList<>();
//        try {
//            filePath = readPath;
//            CsvReader csvReader = new CsvReader(filePath);
//            // 读表头
//            boolean re = csvReader.readHeaders();
//            while (csvReader.readRecord()) {
//                String rawRecord = csvReader.getRawRecord();
//                listData.add(rawRecord);
//            }
//            return listData;
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException("文件未找到");
//        } catch (IOException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//    public static Map<String,DataCSV> formatDatas(List<String> dataList){
//        List<DataCSV> dataCSVList=new ArrayList<>();
//        List<String[]> formatDataList=new ArrayList<>();
//        Map<String,DataCSV> countyMap=new HashMap<>();
//        for(int i=0;i<dataList.size();i++){
//            formatDataList.add(dataList.get(i).split(","));
//        }
//
//        for(int i=0;i<formatDataList.size();i++){
//            /*
//             * formatdata[0]:时间
//             * formatdata[1]:name
//             * formatdata[2]:state(不需要)
//             * formatdata[3]:cases
//             * formatdata[4]:deaths
//             * */
//            String[] formatData=formatDataList.get(i);
//
//            String date=formatData[0];
//            String name=formatData[1];
//            int cases=Integer.valueOf(formatData[3]);
//            int deaths=Integer.valueOf(formatData[4]);
//
//            if(!countyMap.containsKey(formatData[1])){
//                countyMap.put(name,new DataCSV(name));
//            }
//            DataCSV dataCSV=countyMap.get(name);
//            dataCSV.dateList.add(date);
//            dataCSV.casesList.add(cases);
//            dataCSV.deathsList.add(deaths);
//            countyMap.put(name,dataCSV);
//        }
//        return countyMap;
//    }


}



