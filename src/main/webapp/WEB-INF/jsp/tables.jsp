<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="zh-CN">
<head>
    <title>ECharts</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">

    <script src="https://code.jquery.com/jquery.js"></script>
    <script src="http://echarts.baidu.com/dist/echarts.min.js"></script>
    <!-- 引入 echarts.js -->
    <%--<script src="../lib/echartsLib/echarts.js"></script>--%>

</head>
<body>
<%--路径--%>
<ol class="breadcrumb">
    <li><a href="/gotoMain.html?u_id=${user.userId}">Home</a></li>
    <li><a href="/showCheckItem.html?p_id=${selected_p_id}">CheckItem</a></li>
    <li class="active">样本运行图</li>
</ol>
<!-- Single button -->
<div class="jumbotron">
    <h1 class="center-block">图表展示</h1>
</div>
<h1>${SDRBD2}</h1>
<h1>${test}</h1>
<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 1000px;height:400px;"></div>
<div id="main2" style="width: 1000px;height:400px;"></div>
<script type="text/javascript">
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('main'));

    // 指定图表的配置项和数据
    var option = {
        title: {
            text: '样本运行图'
        },
        tooltip: {},
        legend: {
            data:[' 样本数据']
        },
        xAxis: {
            data: ${xList}
        },
        yAxis: {
            min:0,
            max:${max*1.2},
            scale:true
        },
        series: [{
            name: '样本数据',
            type: 'line',
            color:"black",
            data: ${SVlaueList},
            markPoint : {
                data : [
                    // 纵轴，默认
                    {type : 'max', name: '最大值',symbol: 'emptyCircle', itemStyle:{normal:{color:'#dc143c',label:{position:'top'}}}},
                    {type : 'min', name: '最小值',symbol: 'emptyCircle', itemStyle:{normal:{color:'#dc143c',label:{position:'bottom'}}}},
                    // 横轴
                    {type : 'max', name: '最大值', valueIndex: 0, symbol: 'emptyCircle', itemStyle:{normal:{color:'#1e90ff',label:{position:'right'}}}},
                    {type : 'min', name: '最小值', valueIndex: 0, symbol: 'emptyCircle', itemStyle:{normal:{color:'#1e90ff',label:{position:'left'}}}}
                ]
            },
            markLine : {
                data : [
                    // 纵轴，默认
                    {type : 'max', name: '最大值', itemStyle:{normal:{color:'#dc143c'}}},
                    {type : 'min', name: '最小值', itemStyle:{normal:{color:'#dc143c'}}},
                    {type : 'average', name : '平均值', itemStyle:{normal:{color:'#dc143c'}}},
                    {
                        name: 'USL',
                        yAxis: ${USL},
                        itemStyle : {
                            normal : {
                                lineStyle:{
                                    color:'yellow',
                                    type:'solid'  //'dotted'虚线 'solid'实线
                                },

                                label:{
                                    show: true,
                                    formatter: '{b} : {c}' ,
                                    color:"red",
                                    position: 'middle',/*****文字显示的位置**********/
                                },
                                labelLine :{show:true}
                            }
                        }
                    },
                    {
                        name: 'LSL',
                        yAxis: ${LSL},
                        itemStyle : {
                            normal : {
                                lineStyle:{
                                    color:'yellow',
                                    type:'solid'  //'dotted'虚线 'solid'实线
                                },

                                label:{
                                    show: true,
                                    formatter: '{b} : {c}' ,
                                    color:"red",
                                    position: 'middle',/*****文字显示的位置**********/
                                },
                                labelLine :{show:true}
                            }
                        }
                    }
                    ,
                    {
                        name: 'tragetValue',
                        yAxis: ${U},
                        itemStyle : {
                            normal : {
                                lineStyle:{
                                    color:'green',
                                    type:'solid'  //'dotted'虚线 'solid'实线
                                },

                                label:{
                                    show: true,
                                    formatter: '{b} : {c}' ,
                                    color:"green",
                                    position: 'middle',/*****文字显示的位置**********/
                                },
                                labelLine :{show:true}
                            }
                        }
                    }
//                    // 横轴
//                    {type : 'max', name: '最大值', valueIndex: 0, itemStyle:{normal:{color:'#1e90ff'}}},
//                    {type : 'min', name: '最小值', valueIndex: 0, itemStyle:{normal:{color:'#1e90ff'}}},
//                    {type : 'average', name : '平均值', valueIndex: 0, itemStyle:{normal:{color:'#1e90ff'}}}
                ]
            }
        }
        ]


    };


    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
</script>
<script type="text/javascript">
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('main2'));

    // 指定图表的配置项和数据
    var option = {
        title: {
            text: '样本极差运行图'
        },
        tooltip: {},
        legend: {
            data:[' 样本极差数据']
        },
        xAxis: {
            data: ${xRlist}
        },
        yAxis: {
            min:0,
            max:${max2*1.5},
            scale:true
        },
        series: [{
            name: '样本极差数据',
            type: 'line',
            color:"black",
            data: ${RList},

            markLine : {
                data : [
                    // 纵轴，默认
//                    {type : 'max', name: '最大值', itemStyle:{normal:{color:'#dc143c'}}},
//                    {type : 'min', name: '最小值', itemStyle:{normal:{color:'#dc143c'}}},
//                    {type : 'average', name : '平均值', itemStyle:{normal:{color:'#dc143c'}}},
                    {
                        name: 'USL',
                        yAxis: ${USL-LSL},
                        itemStyle : {
                            normal : {
                                lineStyle:{
                                    color:'yellow',
                                    type:'solid'  //'dotted'虚线 'solid'实线
                                },

                                label:{
                                    show: true,
                                    formatter: '{b} : {c}' ,
                                    color:"red",
                                    position: 'middle',/*****文字显示的位置**********/
                                },
                                labelLine :{show:true}
                            }
                        }
                    },
                    {
                        name: 'LSL',
                        yAxis: ${LSL-USL},
                        itemStyle : {
                            normal : {
                                lineStyle:{
                                    color:'yellow',
                                    type:'solid'  //'dotted'虚线 'solid'实线
                                },

                                label:{
                                    show: true,
                                    formatter: '{b} : {c}' ,
                                    color:"red",
                                    position: 'middle',/*****文字显示的位置**********/
                                },
                                labelLine :{show:true}
                            }
                        }
                    }
                    ,
                    {
                        name: 'tragetValue',
                        yAxis: ${0},
                        itemStyle : {
                            normal : {
                                lineStyle:{
                                    color:'green',
                                    type:'solid'  //'dotted'虚线 'solid'实线
                                },

                                label:{
                                    show: true,
                                    formatter: '{b} : {c}' ,
                                    color:"green",
                                    position: 'middle',/*****文字显示的位置**********/
                                },
                                labelLine :{show:true}
                            }
                        }
                    }
//                    // 横轴
//                    {type : 'max', name: '最大值', valueIndex: 0, itemStyle:{normal:{color:'#1e90ff'}}},
//                    {type : 'min', name: '最小值', valueIndex: 0, itemStyle:{normal:{color:'#1e90ff'}}},
//                    {type : 'average', name : '平均值', valueIndex: 0, itemStyle:{normal:{color:'#1e90ff'}}}
                ]
            }
        }
        ]


    };


    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
</script>
</body>
</html>