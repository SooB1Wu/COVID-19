package com.soob.demo.dao;

import com.soob.demo.pojo.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/*
 * sql接口
 * */
/* 数据库映射接口:县数据 */
@Mapper
@Component
public interface SqlDao {
    /*
    * 建表
    * */
    @Select(
            "CREATE TABLE IF NOT EXISTS `tbl_country`(\n" +
            "`id` INT UNSIGNED AUTO_INCREMENT,\n" +
            "`name` VARCHAR(100) NOT NULL,\n" +
            "PRIMARY KEY ( `id` )\n" +
            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n" +
            "\n"+
            "CREATE TABLE IF NOT EXISTS `tbl_state`(\n" +
            "`id` INT UNSIGNED AUTO_INCREMENT,\n" +
            "`name` VARCHAR(100) NOT NULL,\n" +
            "`country_id` INT UNSIGNED NOT NULL,\n" +
            "constraint countryID FOREIGN KEY(country_id) REFERENCES tbl_country(id),\n" +
            "PRIMARY KEY ( `id` )\n" +
            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n" +
            "\n"+
            "CREATE TABLE IF NOT EXISTS `tbl_county`(\n" +
            "`id` INT UNSIGNED AUTO_INCREMENT,\n" +
            "`name` VARCHAR(100) NOT NULL,\n" +
            "`state_id` INT UNSIGNED NOT NULL,\n" +
            "constraint stateID FOREIGN KEY(state_id) REFERENCES tbl_state(id),\n" +
            "PRIMARY KEY ( `id` )\n" +
            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n" +
            "\n" +
            "CREATE TABLE IF NOT EXISTS `tbl_info`(\n" +
            "`id` INT UNSIGNED AUTO_INCREMENT,\n" +
            "`county_id` INT UNSIGNED NOT NULL,\n" +
            "`date` DATE NOT NULL,\n" +
            "`cases` INT UNSIGNED NOT NULL,\n" +
            "`deaths` INT UNSIGNED NOT NULL,\n" +
            "constraint countyID FOREIGN KEY(county_id) REFERENCES tbl_county(id),\n" +
            "PRIMARY KEY ( `id` )\n" +
            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n")
    void createAllTables();


    /*
    * 查询字段是否存在
    * */
    @Select("SELECT `name` FROM `tbl_country` WHERE `name`=\"${name}\"")
    String containsCountryName(@Param("name")String name);
    @Select("SELECT `name` FROM `tbl_state` " +
            "WHERE `name`=\"${name}\" AND `country_id`=${id}")
    String containsStateName(@Param("name")String name,
                            @Param("id")int id);
    @Select("SELECT `name` FROM `tbl_county` " +
            "WHERE `name`=\"${name}\" AND `state_id`=${id}")
    String containsCountyName(@Param("name")String name,
                           @Param("id")int id);
    @Select("SELECT `id` FROM `tbl_info` " +
            " WHERE `county_id`=${id} AND `date`='${date}'")
    String containsInfo(@Param("id")int id,
                        @Param("date")Date date);

    /*
    * 查询外键ID
    * */
    @Select("SELECT `id` FROM `tbl_country` WHERE `name`=\"${name}\"")
    int selectCountryID(@Param("name")String name);
    @Select({"SELECT `id` FROM `tbl_state` WHERE `name`=\"${name}\" AND `country_id`=${id}"})
    int selectStateID(@Param("name")String name, @Param("id")int id);
    @Select({"SELECT `id` FROM `tbl_county` WHERE `name`=\"${name}\" AND `state_id`=${id}"})
    int selectCountyID(@Param("name")String name,
                      @Param("id")int id);

    /*
    * 存入国家名
    * */
    @Insert("INSERT INTO `tbl_country`(`name`) "+
            "VALUES (\"${name}\");")
    void insertCountryName(@Param("name")String name);
    /*
    * 存入州名数据
    * */
    @Insert("INSERT INTO `tbl_state`(`name`,`country_id`) "+
            "VALUES (\"${name}\",\"${id}\");")
    void insertStateName(@Param("name")String name,
                         @Param("id")int countryId);
    /*
     * 存入县名数据
     * */
    @Insert("INSERT INTO `tbl_county`(`name`,`state_id`) "+
            "VALUES (\"${name}\",\"${id}\");")
    void insertCountyName(@Param("name")String name,
                          @Param("id")int stateId);
    /*
    * 插入疫情数据
    * */
    @Insert("INSERT INTO `tbl_info`(`county_id`,`date`,`cases`,`deaths`) " +
            "VALUES (${id},\"${date}\",${cases},${deaths})")
    void insertInfo(@Param("id")int countryId,
                    @Param("date") Date date,
                    @Param("cases")int cases,
                    @Param("deaths")int deaths);


    /*
     * 数据补全
     * */
    /*
     * 找日期界限
     * */
    @Select("SELECT MAX(date) FROM tbl_info")
    Date getMaxDate();
    @Select("SELECT MIN(date) FROM tbl_info")
    Date getMinDate();



    /*
     * 查询根据国家id查询所有州的id
     * */
    @Select("SELECT `id` FROM tbl_state \n" +
            "WHERE `country_id`=${id}")
    List<Integer> getStateIdList(@Param("id")int id);

    /*
    * 根据州id查询所有县id
    * */
    @Select("SELECT `id` FROM tbl_county \n" +
            "WHERE `state_id`=${stateId}")
    List<Integer> getCountyIdList(@Param("stateId")int stateId);



    /*
     * 查询国家数据
     * */
    @Results({
            @Result(property = "id",column = "county_id"),
            @Result(property = "date", column = "date"),
            @Result(property = "cases", column = "cases"),
            @Result(property = "deaths", column = "deaths"),
    })
    @Select("SELECT id AS county_id,SUM(cases) AS cases,SUM(deaths) AS deaths,date FROM tbl_info\n" +
            " WHERE county_id IN(SELECT id FROM tbl_county \n" +
            "\tWHERE state_id IN(SELECT id FROM tbl_state WHERE country_id=${countryId})) \n" +
            "GROUP BY date ORDER BY date;")
    List<EpidemicData> getCountryEpidemicData(@Param("countryId")int countryId);
    /*
     * 查询州数据
     * */
    @Results({
            @Result(property = "id",column = "county_id"),
            @Result(property = "date", column = "date"),
            @Result(property = "cases", column = "cases"),
            @Result(property = "deaths", column = "deaths"),
    })
    @Select("SELECT id AS county_id, SUM(cases) AS cases,SUM(deaths) AS deaths,date FROM tbl_info\n" +
            " WHERE county_id IN(SELECT id FROM tbl_county WHERE state_id=${stateId}) \n" +
            "GROUP BY date ORDER BY date;")
    List<EpidemicData> getStateEpidemicData(@Param("stateId")int stateId);
    /*
     * 查询县数据
     * */
    @Results({
            @Result(property = "id",column = "county_id"),
            @Result(property = "date", column = "date"),
            @Result(property = "cases", column = "cases"),
            @Result(property = "deaths", column = "deaths"),
    })
    @Select("SELECT * FROM tbl_info WHERE `county_id`=${countyId} ORDER BY date;")
    List<EpidemicData> getEpidemicData(@Param("countyId")int countyId);



    /*
     * 查询某日某县数据
     * */
    @Results({
            @Result(property = "id",column = "county_id"),
            @Result(property = "date", column = "date"),
            @Result(property = "cases", column = "cases"),
            @Result(property = "deaths", column = "deaths"),
    })
    @Select("SELECT * FROM tbl_info WHERE `county_id`=${countyId} AND `date`=\"${date}\"")
    EpidemicData getEpidemicDataByDate(@Param("countyId")int countyId,
                                 @Param("date") String date);



    /*
     * 根据国家名查询国家id
     * */
    @Select("SELECT `id` FROM tbl_country " +
            "WHERE `name`=\"${countryName}\"")
    int getCountryId(@Param("countryName") String countryName);
    /*
    * 根据州名与国家名查询州id
    * */
    @Select("SELECT `id` FROM tbl_state " +
            "WHERE `name`=\"${stateName}\" AND `country_id`=${countryId}")
    int getStateId(@Param("stateName") String stateName,
                   @Param("countryId") int countryId);
    /*
     * 根据县名与州id查询县id
     * */
    @Select("SELECT `id` FROM tbl_county " +
            "WHERE `name`=\"${countyName}\" AND `state_id`=${stateId}")
    int getCountyId(@Param("countyName") String countyName,
                   @Param("stateId") int stateID);

    /*
     * 查询所有县的名字
     * */
    @Select("SELECT `name` FROM tbl_county WHERE `state_id`=${stateId}")
    List<String> getCountyNameList(@Param("stateId") int stateId);

    /*
     * 查询所有州的名字
     * */
    @Select("SELECT `name` FROM tbl_state WHERE `country_id`=${countryId}")
    List<String> getStateNameList(@Param("countryId") int countryId);

    /*
     * 根据县id查询名字
     * */
    @Select("SELECT `name` FROM `${tableName}` WHERE `id`=${id}")
    String getName(@Param("id") int id,
                   @Param("tableName") String tableName);

    /*
    * 查询日期范围
    * */
    @Select("SELECT DISTINCT `date` FROM tbl_info ORDER BY `date`")
    List<String> getDateScope();
}
