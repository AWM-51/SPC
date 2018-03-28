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
    <li class="active">CPk趋势图</li>
</ol>
<!-- Single button -->
<div class="jumbotron">
    <h1 class="center-block">图表展示</h1>
</div>
<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 1000px;height:400px;"></div>

<script type="text/javascript">
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('main'));

    // 指定图表的配置项和数据
    var option = {
        title: {
            text: 'CPK运行图'
        },
        tooltip: {},
        legend: {
            data:[' CPK数据']
        },
        xAxis: {
            data: ${xList}
        },
        yAxis: {
            min:0,
            max:${max},
            scale:true
        },
        series: [{
            name: 'CPk数据',
            type: 'line',
            color:"black",
            data: ${CPkList},
//            markPoint : {
//                data : [
//                    // 纵轴，默认
//                    {type : 'max', name: '无缺点考虑降低成本',symbol: 'emptyCircle', itemStyle:{normal:{color:'#dc143c',label:{position:'top'}}}},
//                    {type : 'min', name: '最小值',symbol: 'emptyCircle', itemStyle:{normal:{color:'#dc143c',label:{position:'bottom'}}}},
//                ]
//            },
            markLine : {
                data : [
                    // 纵轴，默认
                    {
                        name: '无缺点考虑降低成本',
                        yAxis: 1.67,
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
                        name: '状态良好维持现状',
                        yAxis: 1.33,
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
                        name: '改进为A级',
                        yAxis: 1.0,
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
                    ,
                    {
                        name: '制程不良较多,必须提升其能力',
                        yAxis: 0.67,
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


                    ,
                    {
                        name: '制程能力较差，考虑整改设计制程',
                        yAxis: 0.67,
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