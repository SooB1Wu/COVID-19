package com.soob.demo.pojo;

import java.util.ArrayList;
import java.util.List;

public class ChartDataRes {
    public String name;
    public List<String> dateList=new ArrayList<>();
    public List<Integer> casesList=new ArrayList<>();
    public List<Integer> deathsList=new ArrayList<>();

    public List<Integer> forecastCasesList=new ArrayList<>();
    public List<Integer> forecastDeathsList=new ArrayList<>();
    public List<String> forecastDateList=new ArrayList<>();

    public ChartDataRes(String name) {
        this.name = name;
    }
}
