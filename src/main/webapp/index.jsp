<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>

<html>
<head>
    <title>My title</title>
    <link href="./resources/main.css" rel="stylesheet" type="text/css">
    <link href="./resources/bootstrap.min.css" rel="stylesheet" type="text/css">
</head>
<body>
<form method=post action="" id="form">
    <input type="hidden" name="type" value="postForm" />
    Enter your name: <input type=text name=username size=20><br>
    <textarea form="form" placeholder="Enter text here... " maxlength="140" name=text></textarea><br>
    <input type=submit>
</form>

<table>
<tr>
            <td>Name:</td>
            <td>Text:</td>
            <td>Date:</td>
<tr>
<c:forEach items="${posts}" var="post">
    <tr>
        <td>${post.username}</td>
        <td>${post.text}</td>
        <td>${post.date}</td>
    </tr>
</c:forEach>
</table>

<form method=post>

    <c:forEach var="i" begin="0" end="${(postNumber-1)/5}">
       <input type=submit name=page value=${i+1} class="btn btn-link">
    </c:forEach>

    <input type="hidden" name="type" value="pageForm" />
</form>

</body>
</html>