
<%--
  Created by IntelliJ IDEA.
  User: ${USER}
  Date: ${DATE}
  Time: ${TIME}
  To change this template use File | Settings | File Templates....
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>SPC登录</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 Shiv 和 Respond.js 用于让 IE8 支持 HTML5元素和媒体查询 -->
    <!-- 注意： 如果通过 file://  引入 Respond.js 文件，则该文件无法起效果 -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<%--巨幕布--%>
<div class="jumbotron">
        <h1 class="center-block">SPC</h1>
        <p class="center-block">登录</p>
</div>

<c:if test ="${!empty error}">
    <font color="red"><c:out value="${error}"/></font>
</c:if>
<div class="row">
    <div class="col-md-3">
        <a href="/gotoTable.html">table--------------</a>
    </div>
    <div class="col-md-6">
        <form action="<c:url value='/loginCheck.html'/>"method="post">
            用户名：
            <%--<input name="userName"/><br>--%>
            <div class="input-group">
                <span class="input-group-addon" id="sizing-addon2">@</span>
                <input type="text" name="userName" class="form-control" placeholder="Username" aria-describedby="sizing-addon2">
            </div>
            密码：
            <div class="input-group">
                <span class="input-group-addon" id="sizing-addon1">@</span>
                <input type="password" name="password" class="form-control" placeholder="Password" aria-describedby="sizing-addon2">
            </div>
            <%--<input type="password" name="password"><br>--%>
            <input type="submit" class="btn btn-primary btn-lg" value=" 登录"/>
            <input type="reset" value=" 重置"/>

        </form>
    </div>
    <div class="col-md-3"></div>
</div>


<!-- jQuery (Bootstrap 的 JavaScript 插件需要引入 jQuery) -->
<script src="https://code.jquery.com/jquery.js"></script>
<!-- 包括所有已编译的插件 -->
<script src="js/bootstrap.min.js"></script>
</body>
</html>