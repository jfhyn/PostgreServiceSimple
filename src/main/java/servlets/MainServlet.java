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
            pm.createMasterTable();
            pm.createPartitionFunction();
            //pm.insertIntoPosts(request.getParameter("username"), request.getParameter("text"));

            //TODO change!!
            doGet(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        List<Post> posts = null;
        try {
            PostgreSQLManager pm = new PostgreSQLManager();
            posts  = pm.selectAllPosts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("posts", posts);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }
}
