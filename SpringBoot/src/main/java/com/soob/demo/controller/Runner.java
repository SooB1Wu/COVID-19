package com.soob.demo.controller;


import com.soob.demo.service.FormatCSV;
import com.soob.demo.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Runner implements ApplicationRunner {

    @Autowired
    FormatCSV formatCSV;

    @Autowired
    SqlService sqlService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Long startTime=System.currentTimeMillis();
        /*读取数据集并且写入mySQL*/
//        formatCSV.readCSV();
        /*数据补全(缺失数据使用前一天的代替)*/
//        formatCSV.completeData();

        System.out.println("Time: "+((Long)System.currentTimeMillis()-startTime));
        System.out.println("WRITE SQL SUCCESS!!!");
        System.out.println("INIT SUCCESS!");
    }
}
