<jsp:useBean id="user" scope="session" type="com.wj.domain.User"/>
<%--
  Created by IntelliJ IDEA.
  User: ${USER}
  Date: ${DATE}
  Time: ${TIME}
  To change this template use File | Settings | File Templates....
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="zh-CN">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 Shiv 和 Respond.js 用于让 IE8 支持 HTML5元素和媒体查询 -->
    <!-- 注意： 如果通过 file://  引入 Respond.js 文件，则该文件无法起效果 -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
    <title>SPC系统</title>
    <script language="javascript">
        function check1(f){
            if( f.file.value == '' ){
                alert('请先上传文件')
                return false;
            }
        }
        function check2(){
            if(document.getElementsByName('p_name').value.length==0
                ){
                alert('输入为空！');
                document.getElementsByName('p_name').focus();
                return false;
            }
            else if (document.getElementsByName('remarks').value.length==0){
                alert('输入为空！');
                document.getElementsByName('remarks').focus();
                return false;
            }
        }
        function check3(){
            if(document.getElementsByName('p_name').value.length==0
            ){
                alert('输入为空！');
                document.getElementsByName('p_name').focus();
                return false;
            }
            else if (document.getElementsByName('remarks').value.length==0){
                alert('输入为空！');
                document.getElementsByName('remarks').focus();
                return false;
            }
        }
    </script>
</head>
<%--巨幕布--%>
<div class="jumbotron">
    <h1>SPC</h1>
    <p>
        <c:if test ="${!empty info}">

       <font color="green"> <c:out value="${info}"/></font>
        </c:if>
        ${user.userName},您好！您的积分为${user.credits}!!
    </p>
</div>

<body>
<ol class="breadcrumb">
    <li class="active">Home</li>
</ol>
<%--<div class="col-md-3"></div>--%>
<div class="col-md-3"></div>
<div class="col-md-6">
    <%--项目--%>

        <div class="list-group">
            <c:forEach var="i" begin="0" end="${projects.size()-1}">
            <a href="/showCheckItem.html?p_id=${projects.get(i).getP_id()}&u_id=${user.userId}" class="list-group-item active">
                <h4 class="list-group-item-heading">
                     ${projects.get(i).getP_name()}
                </h4>
            </a>
                <a href="/showCheckItem.html?p_id=${projects.get(i).getP_id()}" class="list-group-item">
                    <h4 class="list-group-item-heading">
                        创建时间：
                    </h4>
                    <p class="list-group-item-text">
                            ${projects.get(i).getCreate_time()}
                    </p>
                </a>
                <a href="/deleteProject.html?project.p_id=${projects.get(i).getP_id()}&project.p_status=${projects.get(i).getP_status()}
                &u_id=${user.userId}" class="list-group-item">删除</a>
            </c:forEach>
        </div>
    <form action="<c:url value='/addNewProject.html'/>" method="post" enctype="multipart/form-data" th:method="GET"
    >
        项目名：<input type="text" name="p_name"/>
        备注：<input type="text" name="remarks"/>
        <input type="hidden" name="u_id" value=${user.userId}>
        <input type="submit" value="新增项目" onclick="check2()">
    </form>


</div>
<%--&lt;%&ndash;<div class="col-md-3"></div>&ndash;%&gt;--%>
<%--<div class="col-md-3">--%>
    <%--<c:if test="${empty checkItems}">  <h1>为选择项目或请在该项目中添加检查属性</h1></c:if>--%>
    <%--<c:if test="${not empty checkItems}">--%>
    <%--<div class="list-group">--%>

        <%--<c:forEach var="i" begin="0" end="${checkItems.size()-1}">--%>
            <%--<a href="/showDataTable.html?c_id=${checkItems.get(i).getC_id()}&p_id=${selected_p_id}" class="list-group-item active">--%>
                <%--<c:if test="${checkItems.get(i).getC_status() == 1}">--%>
                <%--<h4 class="list-group-item-heading" style="color: red">--%>
                    <%--</c:if>--%>
                    <%--<c:if test="${checkItems.get(i).getC_status() != 1}">--%>
                        <%--<h4 class="list-group-item-heading">--%>
                            <%--</c:if>--%>
                        <%--${checkItems.get(i).getC_name()}--%>
                <%--</h4>--%>
            <%--</a>--%>
            <%--<a href="/deleteChtekItem.html?p_id=${selected_p_id}--%>
                <%--&c_id=${checkItems.get(i).getC_id()}" class="list-group-item">删除</a>--%>
        <%--</c:forEach>--%>
    <%--</div>--%>
    <%--</c:if>--%>


    <%--<c:if test="${not empty selected_p_id}">--%>
    <%--<form action="<c:url value='/addNewCheckItem.html'/>"  enctype="multipart/form-data" th:method="GET"--%>
    <%-->--%>
        <%--项目名：<input type="text" name="c_name"/>--%>
        <%--备注：<input type="text" name="c_remarks"/>--%>
        <%--<input type="hidden" name="p_id" value=${selected_p_id}>--%>
        <%--<input type="submit" value="新增测试属性 to ${selected_p_id}" onclick="check2()">--%>
    <%--</form>--%>
    <%--</c:if>--%>
<%--</div>--%>


<%--<div class="col-md-6">--%>

    <%--&lt;%&ndash;表格&ndash;%&gt;--%>
        <%--<c:if test="${empty sampleDataList or empty showedCheckItem}">  <h1>还未请求数据</h1></c:if>--%>
        <%--<c:if test="${not empty sampleDataList and not empty showedCheckItem}">--%>
        <%--<table class="table table-hover">--%>
            <%--<!-- On rows -->--%>
            <%--<tr class="active">序号</tr>--%>
            <%--<tr class="success">属性</tr>--%>
            <%--<tr class="warning"> 抽检时间</tr>--%>
            <%--<tr class="danger">数据1</tr>--%>
            <%--<tr class="info">数据2</tr>--%>
            <%--<tr class="danger">数据3</tr>--%>
            <%--<tr class="info">数据4</tr>--%>
            <%--<tr class="danger">数据5</tr>--%>


            <%--<!-- On cells (`td` or `th`) -->--%>
            <%--<c:forEach var="i" begin="0" end="${sampleDataList.size()-1}">--%>
            <%--<tr>--%>
                <%--<td class="active">${i+1}</td>--%>
                <%--<td class="success">${showedCheckItem.getC_name()}</td>--%>
                <%--<td class="warning">${sampleDataList.get(i).get(0).getObtain_time()}</td>--%>
                <%--<c:forEach var="j" begin="0" end="${sampleDataList.get(i).size()-1}">--%>
                    <%--<td class="info">${sampleDataList.get(i).get(j).getValue()}</td>--%>
                <%--</c:forEach>--%>
            <%--</tr>--%>
            <%--</c:forEach>--%>
        <%--</table>--%>


        <%--</c:if>--%>
        <%--<form  action="<c:url value='/entryExcel.html'/>"method="post" enctype="multipart/form-data" th:method="GET"--%>
               <%--onsubmit="return check1(this)">--%>
            <%--<input type="hidden" name='c_id' value=${selected_c_id}>--%>
            <%--<input type="hidden" name='p_id' value=${selected_p_id}>--%>
            <%--<input type="file" name="file">--%>
            <%--<input type="submit" value="导入excel" >--%>
        <%--</form>--%>

<%--</div>--%>

<div class="col-md-3"></div>


    <!-- jQuery (Bootstrap 的 JavaScript 插件需要引入 jQuery) -->
    <script src="https://code.jquery.com/jquery.js"></script>
    <!-- 包括所有已编译的插件 -->
    <script src="js/bootstrap.min.js"></script>
</body>
</html>