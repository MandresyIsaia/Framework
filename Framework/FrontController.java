package controller;

import util.*;
import util.Mapping;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.*;
import javax.servlet.http.HttpSession;
import annotation.*;
import model.*;
import com.google.gson.Gson;

public class FrontController extends HttpServlet {
    private List<String>controllers;
    private HashMap<String, Mapping> map;

    @Override
    public void init() throws ServletException {
        try {
            String packageName = this.getInitParameter("package_name");
            System.out.println(packageName);
            controllers = Util.getAllClassesSelonAnnotation(packageName,ControllerAnnotation.class);
            map = Util.getAllMethods(controllers);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Initialization failed: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        System.out.println(controllers.size());
        // for (int i = 0;i<controllers.size() ;i++ ) {
        //         System.out.println(controllers.get(i));
        // }
        // for (Map.Entry<String,Mapping> entry:map.entrySet()) {
        //     String key = entry.getKey();
        //     System.out.println(key);
        // }
        PrintWriter out = res.getWriter();

        String url = req.getRequestURI();
       
        boolean urlExists = false;

        if (map.containsKey(url)) {
            urlExists = true;
            Mapping mapping = map.get(url);

            try {
                Class<?> c = Class.forName(mapping.getClassName());
                Method[]methods=c.getDeclaredMethods();
                Method m = null;
                for (Method method : methods) {
                    if (method.getName().equals(mapping.getMethodeName())) {
                        m = method;
                    }
                }
                if(m==null)
                    throw new ServletException("No such method "+mapping.getMethodeName()+" in class "+mapping.getClassName());
                
                Object instance = c.getDeclaredConstructor().newInstance();
                Field[] fields = c.getDeclaredFields();
                for (Field field :fields ) {
                    if(field.getType()==MySession.class){
                        System.out.println("MySession class");
                        field.setAccessible(true);
                        field.set(instance,new MySession(req.getSession()));
                    }
                }
                Object result;
                
                Object[] paramValues = Util.getParameterValues(req,m,Param.class,ParamObjet.class);
                for (int i = 0;i<paramValues.length ;i++ ) {
                    Type parameterType = m.getParameters()[i].getParameterizedType();
                    if(paramValues[i]==null && parameterType.getTypeName().equals(MySession.class.getTypeName())){
                        System.out.println(i);
                        MySession session = new MySession(req.getSession());
                        paramValues[i]=session;
                    }
                }
                result = m.invoke(instance, paramValues);
                
                if(m.isAnnotationPresent(ResponseBody.class)){
                    res.setContentType("text/json");
                    if(!(result instanceof ModelView)){
                        Gson gson = new Gson();
                        String jsonResponse = gson.toJson(result);
                        out.println(jsonResponse);
                        out.flush();
                    }else{
                        ModelView mv = (ModelView) result;
                        HashMap<String, Object> data = mv.getData();
                        Gson gson = new Gson();
                        String jsonResponse = gson.toJson(data);
                        out.println(jsonResponse);
                        out.flush();                       
                    }
                }
                else{
                    if (result instanceof ModelView) {
                        ModelView mv = (ModelView) result;
                        String jspPath = mv.getUrl();
                        ServletContext context = getServletContext();
                        String realPath = context.getRealPath(jspPath);

                        if (realPath == null || !new File(realPath).exists()) {
                            throw new ServletException("The JSP page " + jspPath + " does not exist.");
                        }

                        HashMap<String, Object> data = mv.getData();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }

                        RequestDispatcher dispatch = req.getRequestDispatcher(jspPath);
                        dispatch.forward(req, res);
                    } else if (result instanceof String) {
                        out.println(result.toString());
                    } else {
                        throw new ServletException("Unknown return type: " + result.getClass().getSimpleName());
                    }
                }
                    
            } catch (Exception e) {
                e.printStackTrace();
                out.println("Error: " + e.getMessage());
                RequestDispatcher dispatch = req.getRequestDispatcher("/erreur.jsp");
                req.setAttribute("erreur", e.getMessage());
                dispatch.forward(req, res);
            }
        }

        if (!urlExists) {
            out.println("No method is associated with the URL: " + url);
        }
    }
}
