package interfaceApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class getContent
 */
@WebServlet("/getContent")
public class getContent extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public getContent() {
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

        String oid = request.getParameter("id");
        String pageIndex = request.getParameter("pageIndex");

        String temp = "";
        try {
            temp = new Model.ContentInfo().getContentInfo(oid, Integer.parseInt(pageIndex));
        } catch (Exception e) {
            temp = "";
        }
        response.getWriter().write(temp);
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
