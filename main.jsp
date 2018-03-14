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
<html>
<head>
    <title>论坛</title>
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
    </script>
</head>
<body>
    <form action="<c:url value='/addNewProject.html'/>" method="post" enctype="multipart/form-data" th:method="GET"
    >
        项目名：<input type="text" name="p_name"/>
        备注：<input type="text" name="remarks"/>
        <input type="hidden" name="u_id" value=${user.userId}>
               <input type="submit" value="新增项目" onclick="check2()">
    </form>

<c:if test ="${!empty info}">
    <font color="green"><c:out value="${info}"/></font>
</c:if>
      ${user.userName},您好！您的积分为${user.credits}!!,${user.password}
      ${user.userId}


    <ol start="1">
        <c:forEach var="i" begin="0" end="${projects.size()-1}">
            <input action="<c:url value='/showData.html'/>"  method="post" enctype="multipart/form-data" th:method="GET">
            <li><input type="hidden" name="p_id" value=${projects.get(i).getP_id()}/>
                项目名称：<input type="submit" value="${projects.get(i).getP_name()}"/>
            </li>
            </form>
            创建时间：${projects.get(i).getCreate_time()}

            <form action="<c:url value='/deleteProject.html'/>">
                <input type="hidden" name="project.p_id" value="${projects.get(i).getP_id()}"/>
                <input type="hidden" name="project.p_status" value="${projects.get(i).getP_status()}"/>
                <input type="hidden" name="u_id" value="${user.userId}">
                <input type="submit" value="删除------${projects.get(i)}+${projects.get(i).getP_id()}" name="delete"/>
            </form>



            </c:forEach>
    </ol>



      <form  action="<c:url value='/entryExcel.html'/>"method="post" enctype="multipart/form-data" th:method="GET"
             onsubmit="return check1(this)">
          <input type="file" name="file">
          <input type="submit" value="导入excel" >
      </form>
</body>
</html>