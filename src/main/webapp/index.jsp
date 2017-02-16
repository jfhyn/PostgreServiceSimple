<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>

<html>
    <head>
        <title>Microblog</title>
        <link href="${pageContext.request.contextPath}/libs/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet" type="text/css">
    </head>
    <body>
        <div class="container">
            <form method=post action="" id="form" class="form">
                    <input type="hidden" name="type" value="postForm" />
                    Enter your name: <input type=text name=username size=20 required><br>
                    <input form="form" type="text" placeholder="Enter text here... " maxlength="140" name=text class="post_text" required></input>
                    <input type=submit class="btn btn-default" value="Send">
            </form>

            <c:forEach items="${posts}" var="post">
                <div class="post">
                    <div class="name">${post.username}:</div>
                    <div class="text">${post.text}</div>
                </div><br>
            </c:forEach>

            <form method=post>
                <ul class="pagination pagination-sm">
                    <c:forEach var="i" begin="0" end="${(postNumber-1)/5}">
                       <li><a><input type=submit name=page value=${i+1} class="btn btn-link"></a></li>
                    </c:forEach>
                </ul>
                <input type="hidden" name="type" value="pageForm" />
            </form>
        </div>
    </body>
</html>