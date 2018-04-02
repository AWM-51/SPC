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
    <li class="active">CPk分析图</li>
</ol>
<!-- Single button -->
<div class="jumbotron">
    <h1 class="center-block">图表展示</h1>
</div>
<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 1000px;height:400px;"></div>

<div class="container">
    <div class="row">
        <div class="col-md-3">
            <div class="panel panel-default">
                <!-- Default panel contents -->
                <div class="panel-heading">统计值</div>
                <!-- List group -->
                <ul class="list-group">
                    <li class="list-group-item">整体样本总数：${Num_total}</li>
                    <li class="list-group-item">整体平均值：${avg_total}</li>
                    <li class="list-group-item">整体最大值：${max_total}</li>
                    <li class="list-group-item">整体最小值：${min_total}</li>
                    <li class="list-group-item">子组内样本数：${num_group}</li>
                </ul>
            </div>
        </div>

        <div class="col-md-3">
            <div class="panel panel-default">
                <!-- Default panel contents -->
                <div class="panel-heading">常量</div>
                <!-- List group -->
                <ul class="list-group">
                    <li class="list-group-item">USL：${USL}</li>
                    <li class="list-group-item">LSL：${LSL}</li>
                    <li class="list-group-item">U：${U}</li>
                </ul>
            </div>
        </div>


        <div class="col-md-3">
            <div class="panel panel-default">
                <!-- Default panel contents -->
                <div class="panel-heading">计算值</div>
                <!-- List group -->
                <ul class="list-group">
                    <li class="list-group-item">标准差（整体）：${standardDeviation}</li>
                    <li class="list-group-item">中间值：${middleValue_total}</li>
                    <li class="list-group-item">正三倍标准差：${middleValue_total_AddThreeSD}</li>
                    <li class="list-group-item">负三倍标准差：${middleValue_total_DecreaseThreeSD}</li>

                </ul>
            </div>
        </div>


        <div class="col-md-3">
            <div class="panel panel-default">
                <!-- Default panel contents -->
                <div class="panel-heading">其它值</div>
                <!-- List group -->
                <ul class="list-group">
                    <li class="list-group-item">Ca：${Ca}</li>

                </ul>
            </div>
        </div>
    </div>

    <div class="row">

        <div class="col-md-3">
            <div class="panel panel-default">
                <!-- Default panel contents -->
                <div class="panel-heading">工序能力（整体）</div>
                <!-- List group -->
                <ul class="list-group">
                    <li class="list-group-item">Ppl：${Ppl}</li>
                    <li class="list-group-item">Ppu：${Ppu}</li>
                    <li class="list-group-item">Ppk：${Ppk}</li>

                </ul>
            </div>
        </div>


        <div class="col-md-3">
            <div class="panel panel-default">
                <!-- Default panel contents -->
                <div class="panel-heading">工序能力（组内）</div>
                <!-- List group -->
                <ul class="list-group">
                    <li class="list-group-item">Cpl：${Cpl}</li>
                    <li class="list-group-item">Cpu：${Cpu}</li>
                    <li class="list-group-item">Cpk：${Cpk}</li>
                </ul>
            </div>
        </div>


        <div class="col-md-3">
            <div class="panel panel-default">
                <!-- Default panel contents -->
                <div class="panel-heading">实测性能</div>
                <!-- List group -->
                <ul class="list-group">
                    <li class="list-group-item">PPM < LSL ：${PPM_LSL}</li>
                    <li class="list-group-item">PPM < USL ：${PPM_USL}</li>
                    <li class="list-group-item">PPM ：${PPM}</li>
                </ul>
            </div>
        </div>

        <%--<div class="col-md-3">--%>
            <%--<div class="panel panel-default">--%>
                <%--<!-- Default panel contents -->--%>
                <%--<div class="panel-heading">预期性能（整体）：</div>--%>
                <%--<!-- List group -->--%>
                <%--<ul class="list-group">--%>
                    <%--<li class="list-group-item">Ca：${Ca}</li>--%>
                <%--</ul>--%>
            <%--</div>--%>
        <%--</div>--%>


    </div>


</div>

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
            data:[' 样本数量数据']
        },
        xAxis: {

        },
        yAxis: {
            min:0,
            max:${max*1.2},
            scale:true
        },
        series: [{
            name: 'CPk数据',
            type: 'line',
            color:"black",
            data: ${Y},
        }]


    };


    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
</script>
</body>
</html>