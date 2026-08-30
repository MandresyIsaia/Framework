package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ModuleLayer.Controller;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;


import annotation.*;
import utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;


public class FrontControlleur extends HttpServlet {

    HashMap<String, Mapping> mappingUrls;

    public void init()throws ServletException{
        try{
            String package_name= this.getInitParameter("package_name");
            mappingUrls=Scan.getAllClassSelonAnnotation(this,package_name,AnnotationController.class);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("Tongasoa");
        String url=request.getRequestURI().toString();
        boolean urlexist=false;

        for(String cle:mappingUrls.keySet()){
            if(cle.equals(request.getRequestURI().toString())){
            out.println("votre url : "+url +" est associé à la méthode : "+mappingUrls.get(cle).getMethodName()+"dans la classe : "+(mappingUrls.get(cle).getClassName()));

                Mapping mapping= mappingUrls.get(url);
                try {
                    Class<?> classes=Class.forName(mapping.getClassName());
                    Method method= classes.getDeclaredMethod(mapping.getMethodName());
                    Object instance=classes.getDeclaredConstructor().newInstance();
                    Object object=method.invoke(instance);
                    out.println(object.toString());
                    urlexist=true;

                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
        }
        if(!urlexist){
            out.print("Aucune méthode n'est associé à ce url");
        }
    }
    
}
