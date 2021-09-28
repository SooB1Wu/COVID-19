package com.soob.demo.service;

import com.soob.demo.dao.SqlDao;
import com.soob.demo.pojo.EpidemicData;
import org.jumpmind.symmetric.csv.CsvReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
public class FormatCSV {
    private String dir="src/data/Texas1.csv";
//    private String dir="./Texas1.csv";



    @Autowired
    private SqlDao sqlDao;

    public void readCSV() {
        String filePath=dir;//配置路径

        createTables();//建表

        try {
            CsvReader csvReader = new CsvReader(filePath);
            // 读表头
            boolean re = csvReader.readHeaders();
            while (csvReader.readRecord()) {
                String rawRecord = csvReader.getRawRecord();

                toSQL(rawRecord);//存数据

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("文件未找到");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /*
    * 建表
    * */
    public void createTables(){
        sqlDao.createAllTables();
    }

    /*
    * 数据库写入
    * */
    public void toSQL(String strData) throws ParseException {
        String[] formatData = strData.split(",");
        /*
         * formatdata[0]:时间
         * formatdata[1]:countyName
         * formatdata[2]:state
         * formatdata[3]:cases
         * formatdata[4]:deaths
         * */
        ;//日期转换格式
        Date dateRead=new SimpleDateFormat("yyyy/MM/dd").parse(formatData[0]);
        java.sql.Date date= new java.sql.Date(dateRead.getTime());
        String countyName=formatData[1];
        String stateName=formatData[2];
        int cases=Integer.valueOf(formatData[3]);
        int deaths=Integer.valueOf(formatData[4]);
        /*
        * 插入数据库
        * */
        /*插入国家名*/
        int countryId=insertCountry("USA");
        /*插入州名*/
        int stateId=insertState(countryId,stateName);
        /*插入县名*/
        int countyId=insertCounty(stateId,countyName);
        /*插入疫情数据*/
        insertInfo(countyId,date,cases,deaths);

        System.out.println("写入: "+ Arrays.toString(formatData));
    }

    /*插入国家名
    * 1.查询国家名是否存在
    * 2.插入国家名
    * */
    public int insertCountry(String name){
        if(sqlDao.containsCountryName(name)==null){
            sqlDao.insertCountryName(name);
        }
        return sqlDao.selectCountryID(name);
    }

    /*插入州名
    * 1.查找国家的id
    * 2.用国家id与州名判断是否存在(防止不同国家同名州)
    * 3.插入州名与国家id
    * */
    public int insertState(int countryId,String stateName){
        if(sqlDao.containsStateName(stateName,countryId)==null){
            sqlDao.insertStateName(stateName,countryId);
        }
        return sqlDao.selectStateID(stateName,countryId);
    }

    /*插入县名
    * 1.查找州的id
    * 2.用州id与县名判断是否存在(防止不同州同名县)
    * 3.插入县名与州id
    * */
    public int insertCounty(int stateId,String countyName){
        if(sqlDao.containsCountyName(countyName,stateId)==null){
            sqlDao.insertCountyName(countyName,stateId);
        }
        return sqlDao.selectCountyID(countyName,stateId);
    }

    /*插入疫情信息
    * 1.查找县id
    * 2.用日期,id判断是否重复
    * 3.插入数据(县id,日期,确认数,死亡数)
    * */
    public void insertInfo(int countyId,Date date,int cases,int deaths){
        if(sqlDao.containsInfo(countyId, date)==null){
            sqlDao.insertInfo(countyId,date,cases,deaths);
        }
    }






//    //查询并统计州的总数据,并写入表中
//    public void insertStateData(){
//        List<String> stateNameList=sqlDao.getStateNameList();
//        for(String stateName:stateNameList){
//            //sqlDao.creatStateDataTable(stateName+"_data");
//            List<Date> dateList=sqlDao.getDateList(stateName);
////            System.out.println(dateList);
//            for(Date date:dateList){
//                java.sql.Date dateSql= new java.sql.Date(date.getTime());
//                sqlDao.insertStateData(dateSql,stateName,stateName+"_data");
//            }
//        }
//    }


    /*
    * 思路:如果缺少某一天的数据则继承前一天的(初值为0)
    * */
    public void completeData() throws ParseException {
        int countryId=sqlDao.getCountryId("USA");
        List<Integer> stateIdList=sqlDao.getStateIdList(countryId);
        Date minDate=sqlDao.getMinDate();//最早
        Date maxDate=sqlDao.getMaxDate();//最晚
        for(int stateId:stateIdList){
            List<Integer> countyIdList=sqlDao.getCountyIdList(stateId);
            for(int countyId:countyIdList){
                List<EpidemicData> epidemicList=sqlDao.getEpidemicData(countyId);
                int i=0;
                int lastCase=0;//前一天的确诊
                int lastDeath=0;//前一天的死亡人数
                Date indexDate=minDate;
                EpidemicData epidemicCache=epidemicList.get(0);//取出当前县的最早数据
                Date countyDate=epidemicCache.date;
                /*
                * 补全前面没有的数据,用0补全(其实可以不用往数据库里插入值,因为不会影响后续计算)
                * */
                while(indexDate.before(countyDate)){
                    java.sql.Date date=new java.sql.Date(indexDate.getTime());
                    System.out.println("补零:"+"date:"+date+" id: "+countyId);
                    sqlDao.insertInfo(countyId,date,lastCase,lastDeath);
                    indexDate=new Date(indexDate.getTime()+24*3600*1000);//日期加一天
                }

                while(!indexDate.after(maxDate)){//按照日期补全数据
                    if(i<epidemicList.size()) {
                        epidemicCache = epidemicList.get(i);
                        countyDate = epidemicCache.date;
                        /*
                         * 更新lastCase和lastDeath
                         * */
                        lastCase = epidemicCache.cases;
                        lastDeath = epidemicCache.deaths;
                    }
                    /*
                     * 如果当前日期比countyDate早则说明数据丢失,
                     * 插入字段cases=lastCase,
                     *          deaths=lastDeath,
                     *
                     * */
                    if(!countyDate.equals(indexDate)){
                        //System.out.println("补全数据");
                        java.sql.Date date=new java.sql.Date(indexDate.getTime());
                        System.out.println("补全: "+"date:"+date+" id: "+countyId);
                        sqlDao.insertInfo(countyId,date,lastCase,lastDeath);
                    }
                    else{//
                        i++;
                    }
                    indexDate=new Date(indexDate.getTime()+24*3600*1000);//日期加一天
                }

            }
        }
    }


}
