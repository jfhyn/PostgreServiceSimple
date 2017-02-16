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
        <nav class="navbar navbar-fixed-top header">
        <div class="header_content">
             <div class="">Microblog.</div>
        </div>
        </nav>

        <div class="content" style="text-align:center">
            <form method=post action="" id="form" class="form">
                    <input type="hidden" name="type" value="postForm"/>
                    <input type=text name=username placeholder="Enter your name here... " maxlength="20" class="username" required>
                    <textarea form="form" type="text" name=text placeholder="Enter text here... " maxlength="140" rows="5" class="post_text" required></textarea>
                    <input type=submit class="btn btn-default send_button" value="Send">
            </form>

            <c:forEach items="${posts}" var="post">
                <div class="post">
                    <div class="post_header">
                        <div class="name">User ${post.username}</div>
                        <div class="date">${post.date}</div>
                    </div>
                    <div class="post_body">
                        <div class="text">${post.text}</div>
                    </div>
                </div>
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