<template>
    <el-container id="usa_map">
        <el-main>
            <el-row :gutter="20">
                <el-col :span="15">
                    <div id="map1" style="width: 100%;height: 900px"></div>
                </el-col>
                <el-col :span="9">
                    <div style="width: 100%;height: 300px">
                        <h3>{{this.tableName}}</h3>
                        <el-table
                                :data="tableData"
                                stripe
                                border
                                @row-click="country_click"
                                max-height="241">
                            <el-table-column
                                    prop="name"
                                    label="地点"
                                    width="180">
                            </el-table-column>
                            <el-table-column
                                    prop="cases"
                                    label="感染人数"
                                    width="180">
                            </el-table-column>
                            <el-table-column
                                    prop="deaths"
                                    label="死亡人数">
                            </el-table-column>
                            <el-table-column
                                    prop="date"
                                    label="日期">
                            </el-table-column>
                        </el-table>
                    </div>
                    <div id="line1" style="width: 100%;height: 300px"></div>
                    <div id="line2" style="width: 100%;height: 300px"></div>
                </el-col>
            </el-row>
        </el-main>
        <el-dialog
                :title="dialog_title"
                :visible.sync="dialogVisible"
                width="60%"
                :before-close="handleClose">
            <el-container>
                <el-main>
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <div id="line3" style="width: 100%;height: 300px"></div>
                        </el-col>
                        <el-col :span="12">
                            <div id="line4" style="width: 100%;height: 300px"></div>
                        </el-col>
                    </el-row>
                </el-main>
            </el-container>
        </el-dialog>
    </el-container>


</template>

<script>

    import * as echarts from 'echarts';
    import 'echarts/theme/dark.js'
    import $ from 'jquery'
    import {request} from "@/network/request"

    export default {
        name: "Home",
        data() {
            return {
                map_data: [
                    {name: "Alabama", value: 12376}, {name: "Alaska", value: 400}, {
                        name: "Arizona",
                        value: 14566
                    }, {name: "Arkansas", value: 4923}, {name: "California", value: 83983}, {
                        name: "Colorado",
                        value: 22454
                    }, {name: "Connecticut", value: 38430}, {name: "Delaware", value: 8037}, {
                        name: "District of Columbia",
                        value: 7434
                    }, {name: "Florida", value: 46936}, {name: "Georgia", value: 37214}, {
                        name: "Hawaii",
                        value: 636
                    }, {name: "Idaho", value: 2479}, {name: "Illinois", value: 98382}, {
                        name: "Indiana",
                        value: 29404
                    }, {name: "Iowa", value: 15296}, {name: "Kansas", value: 8489}, {
                        name: "Kentucky",
                        value: 8271
                    }, {name: "Louisiana", value: 35161}, {name: "Maine", value: 1741}, {
                        name: "Maryland",
                        value: 41664
                    }, {name: "Massachusetts", value: 87925}, {name: "Michigan", value: 52337}, {
                        name: "Minnesota",
                        value: 17038
                    }, {name: "Mississippi", value: 11708}, {name: "Missouri", value: 11372}, {
                        name: "Montana",
                        value: 471
                    }, {name: "Nebraska", value: 10846}, {name: "Nevada", value: 7170}, {
                        name: "New Hampshire",
                        value: 3721
                    }, {name: "New Jersey", value: 149037}, {name: "New Mexico", value: 6192}, {
                        name: "New York",
                        value: 357757
                    }, {name: "North Carolina", value: 19762}, {
                        name: "North Dakota",
                        value: 1995
                    }, {name: "Ohio", value: 28954}, {
                        name: "Oklahoma",
                        value: 5489
                    }, {name: "Oregon", value: 3847}, {name: "Pennsylvania", value: 67404}, {
                        name: "Puerto Rico",
                        value: 2805
                    }, {name: "Rhode Island", value: 12951}, {name: "South Carolina", value: 9056}, {
                        name: "South Dakota",
                        value: 4085
                    }, {name: "Tennessee", value: 18301}, {name: "Texas", value: 51080}, {
                        name: "Utah",
                        value: 7530
                    }, {name: "Vermont", value: 944}, {name: "Virginia", value: 32145}, {
                        name: "Washington",
                        value: 20064
                    }, {name: "West Virginia", value: 1514}, {name: "Wisconsin", value: 13001}, {
                        name: "Wyoming",
                        value: 776
                    }, {name: "Northern Mariana Islands", value: 21}],
                line_show: false,
                city_data: [],
                stateName: '',
                date_list: [],
                cases_list: [],
                deaths_list: [],
                tableData: [],
                dialogVisible:false,
                dialog_title:'',
                tableName:'USA数据排名'
            }
        }, methods: {
            draw_line(id,casesList, dateList, deathsList,chartName) {
                let myChart = echarts.init(document.getElementById(id));
                let option = {
                    title: {
                        text: chartName,
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: ['确诊人数', '死亡人数']
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            dataZoom: {
                                yAxisIndex: 'none'
                            },
                            dataView: {readOnly: false},
                            magicType: {type: ['line', 'bar']},
                            restore: {},
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: dateList
                    },
                    yAxis: {
                        type: 'value',
                        axisLabel: {
                            formatter: '{value} 人'
                        }
                    },
                    series: [
                        {
                            name: '确诊人数',
                            type: 'line',
                            data: casesList,
                        },
                        {
                            name: '死亡人数',
                            type: 'line',
                            data: deathsList,
                        }
                    ]
                };
                myChart.setOption(option);
            },
            usa_map(min,max) {
                let myChart = echarts.init(document.getElementById("map1"), 'dark');
                let that = this
                // 开启加载loading的动画
                myChart.showLoading();
                // jquery读取json文件
                $.get('https://cdn.jsdelivr.net/gh/apache/echarts-website@asf-site/examples/data/asset/geo/USA.json', function (usaJson) {
                    // 隐藏loading的动画
                    myChart.hideLoading();
                    echarts.registerMap('USA', usaJson, {
                        // 把阿拉斯加移到美国主大陆左下方
                        Alaska: {
                            left: -131,
                            top: 25,
                            width: 15
                        },
                        // 夏威夷
                        Hawaii: {
                            left: -110,
                            top: 28,
                            width: 5
                        },
                        // 波多黎各（因为名字有空格，所以写为字符串的形式）
                        'Puerto Rico': {
                            left: -76,
                            top: 26,
                            width: 2
                        }
                    });
                    let option = {
                        title: {
                            text: '美国疫情地图',
                            subtext: 'condition of covid 19-USA',
                            left: 'right'
                        },
                        // 提示框组件
                        tooltip: {
                            position: function (point, params, dom, rect, size) {

                                // 鼠标坐标和提示框位置的参考坐标系是：以外层div的左上角那一点为原点，x轴向右，y轴向下
                                let x = 0; // x坐标位置
                                let y = 0; // y坐标位置

                                let pointX = point[0];
                                let pointY = point[1];

                                // 提示框大小
                                let boxWidth = size.contentSize[0];
                                let boxHeight = size.contentSize[1];

                                // boxWidth > pointX 说明鼠标左边放不下提示框
                                if (boxWidth > pointX) {
                                    x = 5;
                                } else { // 左边放的下
                                    x = pointX - boxWidth;
                                }

                                // boxHeight > pointY 说明鼠标上边放不下提示框
                                if (boxHeight > pointY) {
                                    y = 5;
                                } else { // 上边放得下
                                    y = pointY - boxHeight;
                                }
                                return [x, y];
                            },
                            triggerOn: 'click',
                            enterable: true,
                            trigger: 'item',
                            // 浮层显示的延迟
                            showDelay: 0,
                            // 提示框浮层的移动动画过渡时间
                            transitionDuration: 0.2,
                            // 按要求的格式显示提示框
                            formatter: function (params) {//请求当前选中的州的数据
                                console.log(params)
                                let stateName = params.name
                                let stateValue = params.data.value
                                let html_str = '地点：' + stateName + '<br>'
                                html_str += '感染人数：' + stateValue[0] + '<br>'
                                html_str += '死亡人数：' + stateValue[1] + '<br>'
                                return html_str
                            },

                        },
                        // 可视映射
                        visualMap: {

                            type: 'continuous',
                            left: 'right',
                            min: min,
                            max: max,
                            // 颜色区间
                            inRange: {
                                // color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8',
                                //     '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
                                color: ['#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
                            },
                            // 文本，默认为数值文本
                            text: ['High', 'Low'],
                            // 显示拖拽用的手柄
                            calculable: true,
                            dimension: 0
                        },
                        // 工具盒
                        toolbox: {
                            show: true,
                            //orient: 'vertical',
                            left: 'left',
                            top: 'top',
                            feature: {
                                // 数据视图
                                dataView: {readOnly: false},
                                // 还原
                                restore: {},
                                // 保存为图片
                                saveAsImage: {}
                            }
                        },
                        series: [
                            {
                                name: 'USA PopEstimates',
                                type: 'map',
                                // 开启鼠标缩放和平移漫游
                                roam: true,
                                map: 'USA',
                                // 显示标签
                                emphasis: {
                                    label: {
                                        show: true,
                                    }
                                },
                                // 文本位置修正
                                textFixed: {
                                    Alaska: [20, -20]
                                },
                                data: that.map_data
                            }
                        ]
                    };

                    myChart.setOption(option);

                    myChart.on('click', (params) => {
                        let stateName = params.name
                        that.stateName = stateName
                        that.getStateData(stateName)
                        that.getCountyLastData()
                    });
                });
            },
            async getCountyNames(stateName) {
                const res = await request({
                    url: '/countyNames',
                    method: 'post',
                    data: {
                        country: 'USA',
                        state: stateName
                    }
                })
                this.city_data = res.data

            },
            async getStateData(stateName) {
                this.tableName=stateName+'数据排名'
                const res = await request({
                    url: '/stateData',
                    method: 'post',
                    data: {
                        country: 'USA',
                        state: stateName
                    }
                })
                let casesList = res.data.casesList
                let dateList = res.data.dateList
                let deathsList = res.data.deathsList
                let forecastCases=res.data.forecastCasesList
                let forecastDeaths=res.data.forecastDeathsList
                this.cases_list = casesList
                this.date_list = dateList
                this.deaths_list = deathsList
                let forecastDates=res.data.forecastDateList
                this.draw_line('line1',casesList, dateList, deathsList,'数据统计')
                this.draw_line('line2',forecastCases, forecastDates, forecastDeaths,'数据预测')

            },
            async getStateLastData() {
                const res = await request({
                    url: '/statesByDate',
                    method: 'post',
                    data: {
                        country: 'USA',
                        date: 'last',
                        sortKey: 'cases'
                    }
                })
                let res_data = res.data
                this.map_data = res_data.colorDataList
                this.tableData = res_data.dataList
                console.log(res.data)
                let max=res_data.colorDataList[0].value[0]
                let min=res_data.colorDataList[res_data.colorDataList.length-1].value[0]
                this.usa_map(min,max)
            },
            async getCountyLastData() {
                let stateName = this.stateName
                const res = await request({
                    url: '/countiesByDate',
                    method: 'post',
                    data: {
                        country: 'USA',
                        state: stateName,
                        date: 'last',
                        //date:'2020-5-18',
                        sortKey: 'cases'
                    }
                })
                let res_data = res.data.dataList
                this.tableData = res_data
            },
            async getCountyData(city_name) {
                let stateName = this.stateName
                const res = await request({
                    url: '/countyData',
                    method: 'post',
                    data: {
                        country: 'USA',
                        state: stateName,
                        county: city_name
                    }
                })
                let casesList = res.data.casesList
                let dateList = res.data.dateList
                let deathsList = res.data.deathsList
                let forecastCases=res.data.forecastCasesList
                let forecastDeaths=res.data.forecastDeathsList
                let forecastDates=res.data.forecastDateList
                this.draw_line('line3',casesList, dateList, deathsList,'数据统计')
                this.draw_line('line4',forecastCases, forecastDates, forecastDeaths,'数据预测')
            },
            async getCountryData(countryName) {
                const res = await request({
                    url: '/countryData',
                    method: 'post',
                    data: {
                        country: countryName
                    }
                })
                let casesList = res.data.casesList
                let dateList = res.data.dateList
                let deathsList = res.data.deathsList
                let forecastCases=res.data.forecastCasesList
                let forecastDeaths=res.data.forecastDeathsList
                let forecastDates=res.data.forecastDateList
                this.draw_line('line1',casesList, dateList, deathsList,'数据统计')
                this.draw_line('line2',forecastCases, forecastDates, forecastDeaths,'数据预测')
            },

            handleClose(done) {
                done();
            },
            country_click(row){
                let city_name = row.name
                if(this.stateName === ''){
                    return false
                }
                this.dialogVisible = true
                this.dialog_title = city_name+'疫情统计'
                this.getCountyData(city_name)
            }
        }, mounted() {
            this.getStateLastData()
            this.getCountryData('USA')
        }
    };
</script>

<style>
    #usa_map .el-main {
        display: block;
        flex: 1;
        flex-basis: auto;
        overflow: auto;
        box-sizing: border-box;
        padding: 0px;
    }
</style>
