package interfaceApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.ContentInfo;

/**
 * Servlet implementation class getContent
 */
@WebServlet("/Content")
public class Content extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Content() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-type", "text/html;charset=UTF-8");

        String searchK = request.getParameter("SearchK");
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String temp = "";
        try {
            temp = new ContentInfo().getTask(searchK, pageIndex, pageSize, startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().print(temp);
    }

    /**
     * 读取配置文件
     * 
     * @project File
     * @package model
     * @file GetFileUrl.java
     * 
     * @param key
     *            配置文件中 key值
     * @return
     *
     */
    private String getConfig(String key) {
        String value = "";
        try {
            Properties pro = new Properties();
            pro.load(new FileInputStream("SYJJConfig.properties"));
            value = pro.getProperty(key);
        } catch (Exception e) {
            value = "";
        }
        return value;
    }
}
