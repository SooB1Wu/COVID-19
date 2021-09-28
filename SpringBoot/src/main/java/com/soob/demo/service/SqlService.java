package com.soob.demo.service;

import com.soob.demo.dao.SqlDao;
import com.soob.demo.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SqlService {
    @Autowired
    private SqlDao sqlDao;

    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");//日期转换格式


    /*获取国家id*/
    private int getCountryId(String countryName){
        return sqlDao.getCountryId(countryName);
    }
    /*获取州id*/
    private int getStateId(String countryName,String stateName){
        int countryId=getCountryId(countryName);
        return sqlDao.getStateId(stateName,countryId);
    }
    /*获取县id */
    private int getCountyId(String countryName,String stateName,String countyName){
        int stateId=getStateId(countryName,stateName);
        return sqlDao.getCountyId(countyName,stateId);
    }

    /*
    * 根据sortKey排序
    * */
    private void listSort(List<EpidemicData> epidemicDataList,String sortKey){
        /*
         * 根据sortKey进行排序
         * */
        if(sortKey.equals("cases")){//按照cases排序
            Collections.sort(epidemicDataList, new Comparator<EpidemicData>() {
                @Override
                public int compare(EpidemicData last, EpidemicData t1) {
                    return t1.cases-last.cases;
                }
            });
        }
        else{//按照deaths排序
            Collections.sort(epidemicDataList, new Comparator<EpidemicData>() {
                @Override
                public int compare(EpidemicData last, EpidemicData t1) {
                    return t1.deaths-last.deaths;
                }
            });
        }
    }

    /*
     * 格式化为ChartDataRES对象(用于渲染折线图)
     * */
    private ChartDataRes getChartDataRes(String Name, List<EpidemicData> epidemicDataList) {
        ChartDataRes dataRES=new ChartDataRes(Name);
        for(EpidemicData epidemicData:epidemicDataList){
            dataRES.dateList.add(dateFormat.format(epidemicData.date));
            dataRES.casesList.add(epidemicData.cases);
            dataRES.deathsList.add(epidemicData.deaths);
        }
        return dataRES;
    }

    /*
     * 格式化为TableDataRES对象(用于渲染列表)
     * */
    private TableDataRes getTableDataRes(List<EpidemicData> epidemicDataList,String tblName){
        List<DataOnDate> dataOnDates=new ArrayList<>();
        List<ColorData> colorDataList=new ArrayList<>();
        for(EpidemicData epidemicData:epidemicDataList){
            ColorData colorData=new ColorData();
            DataOnDate dataOnDate=new DataOnDate();
            dataOnDate.name=sqlDao.getName(epidemicData.id,tblName);
            dataOnDate.date=dateFormat.format(epidemicData.date);
            dataOnDate.cases=epidemicData.cases;
            dataOnDate.deaths=epidemicData.deaths;
            dataOnDates.add(dataOnDate);
            colorData.name=dataOnDate.name;
            int[] value=new int[2];
            value[0]=dataOnDate.cases;
            value[1]=dataOnDate.deaths;
            colorData.value=value;
            colorDataList.add(colorData);
        }
        TableDataRes tableDataRes=new TableDataRes();
        tableDataRes.colorDataList=colorDataList;
        tableDataRes.dataList=dataOnDates;
        return tableDataRes;
    }

    /*
    * 获取州下县名list
    * 1.根据州名查询州id
    * 2.根据州id查询所有对应县名
    * */
    public List<String> getCountyNames(String countryName,String stateName){
        int stateId=getStateId(countryName,stateName);
        return sqlDao.getCountyNameList(stateId);
    }


    /*
     * 获取国家疫情总数据
     *
     * */
    public ChartDataRes getCountryData(String countryName){
        int countryId=getCountryId(countryName);
        /*获取countyList*/
        List<EpidemicData> epidemicDataList=sqlDao.getCountryEpidemicData(countryId);
        /*格式化为DataRES对象*/
        return getChartDataRes(countryName, epidemicDataList);
    }

    /*
     * 获取州疫情总数据
     * 1.获取州id
     * 2.循环查询
     * */
    public ChartDataRes getStateData(String countryName,String stateName){
        int stateId=getStateId(countryName,stateName);
        /*获取countyList*/
        List<EpidemicData> epidemicDataList=sqlDao.getStateEpidemicData(stateId);
        /*格式化为DataRES对象*/
        return getChartDataRes(stateName, epidemicDataList);
    }

    /*
    * 获取县疫情数据
    * 1.获取countyId
    * 2.根据countyId查询县数据
    * */
    public ChartDataRes getCountyData(String countryName,String stateName, String countyName){
        int countyId=getCountyId(countryName,stateName,countyName);
        /*获取countyList*/
        List<EpidemicData> epidemicDataList=sqlDao.getEpidemicData(countyId);
        /*格式化为DataRES对象*/
        return getChartDataRes(countyName, epidemicDataList);
    }



    /*
     * 获取某日每个州的最新数据(当日期为空值时则获取最新的数据)
     * 1.获取国家id
     * 2.根据国家id获取所有州的最新数据
     * */
    public TableDataRes getAllStateDataByDate(String countryName,String date,String sortKey) throws ParseException {
        if(date.equals("last")){//值为"last"则查询最新数据
            date=dateFormat.format(sqlDao.getMaxDate());
        }
        int countryId=getCountryId(countryName);
        List<Integer> stateIdList=sqlDao.getStateIdList(countryId);
        List<EpidemicData> allDataList=new ArrayList<>();
        for(int stateId:stateIdList){
            /*获取该州内所有的县id*/
            List<Integer> countyIdList=sqlDao.getCountyIdList(stateId);
            /*先取第一个县的数据,然后依次相加即为州的总结果*/
            EpidemicData epidemicData=sqlDao.getEpidemicDataByDate(countyIdList.get(0),date);
            for(int i=1;i<countyIdList.size();i++){
                EpidemicData cacheData=sqlDao.getEpidemicDataByDate(countyIdList.get(i),date);
                epidemicData.cases+=cacheData.cases;
                epidemicData.deaths+=cacheData.deaths;
            }
            epidemicData.id=stateId;
            allDataList.add(epidemicData);
        }
        /*排序*/
        listSort(allDataList,sortKey);
        /*格式化*/
        return getTableDataRes(allDataList,"tbl_state");
    }

    /*
    * 获取某日州内每个县的疫情数据(当日期为'last'时则获取最新的数据)
    * 1.获取州id
    * 3.根据州id和date查询每个县的数据
    * */
    public TableDataRes getAllCountyDataByDate(String countryName,String stateName,String date,String sortKey) throws ParseException {
        if(date.equals("last")){//值为"last"则查询最新数据
            date=dateFormat.format(sqlDao.getMaxDate());
        }
        /*获取所有的县id*/
        int stateId=getStateId(countryName,stateName);
        List<Integer> countyIdList=sqlDao.getCountyIdList(stateId);

        /*结果List*/
        List<EpidemicData> epidemicDataList=new ArrayList<>();
        for(int countyId:countyIdList){
            EpidemicData epidemicData=sqlDao.getEpidemicDataByDate(countyId,date);
            epidemicDataList.add(epidemicData);

        }
        /*排序*/
        listSort(epidemicDataList,sortKey);
        /*格式化*/
        return getTableDataRes(epidemicDataList,"tbl_county");
    }

    /*
    * 获取日期范围
    * */
    public List<String> getDateScope(){
        return sqlDao.getDateScope();
    }
}
