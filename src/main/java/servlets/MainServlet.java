package servlets;

import database.PostgreSQLManager;
import entities.Post;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by numash on 27.01.2017.
 */

@WebServlet("/main")
public class MainServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
                PostgreSQLManager pm = new PostgreSQLManager();
                //pm.createMasterTable();
                //pm.createPartitionFunction();

                String username = request.getParameter("username");
                String text = request.getParameter("text");

                if ((username != null && !username.isEmpty()) && (text != null && !text.isEmpty())) {
                    pm.insertIntoPosts(username, text);
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //TODO change!!
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        List<Post> posts = null;
        int page = request.getParameter("page") == null ? 1 : Integer.parseInt(request.getParameter("page"));
        int postNumber = 0;

        try {
            PostgreSQLManager pm = new PostgreSQLManager();

            postNumber = pm.countPosts();
            posts = pm.selectPosts(page);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("posts", posts);
        request.setAttribute("postNumber", postNumber);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp?page=" + page);
        dispatcher.forward(request, response);
    }
}
