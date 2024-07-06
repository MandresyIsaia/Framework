package util;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.*;
import java.lang.reflect.Parameter;
import annotation.*;
import javax.servlet.http.HttpServletRequest;
public class Util {
    public static Object[] getParameterValues(HttpServletRequest req,Method method,Class<Param> paramAnnotationClass,Class<ParamObjet> paramObjectAnnotationClass)throws Exception{
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = new Object[parameters.length];
        for (int i = 0;i<parameterValues.length ;i++ ) {
            String paramName ="";
            if(parameters[i].isAnnotationPresent(paramAnnotationClass)){
                paramName = parameters[i].getAnnotation(paramAnnotationClass).name();
                String paramValue = req.getParameter(paramName);
                parameterValues[i]=Util.convertParameterValue(paramValue,parameters[i].getType());

            }else if(parameters[i].isAnnotationPresent(paramObjectAnnotationClass)){
                String objName = parameters[i].getAnnotation(paramObjectAnnotationClass).name();
                try{
                    Object paramObjectInstance = parameters[i].getType().getDeclaredConstructor().newInstance();
                    Field[] fields = parameters[i].getType().getDeclaredFields();
                    for (Field field :fields ) {
                        String fieldName="";
                        if(field.isAnnotationPresent(AttributAnnotation.class)){
                            fieldName = field.getAnnotation(AttributAnnotation.class).name();
                        }else{
                            fieldName = field.getName();
                        }
                        String paramValue = req.getParameter(objName+"."+fieldName);
                        System.out.println(fieldName);
                        field.setAccessible(true);
                        field.set(paramObjectInstance,Util.convertParameterValue(paramValue,field.getType()));
                    }
                    parameterValues[i]=paramObjectInstance;
                }catch(Exception e){
                    throw new Exception(e.getMessage());
                }
            }
            else{
                System.out.println("tsisy annotation");
                paramName = parameters[i].getName();
                String paramValue = req.getParameter(paramName);

                if(paramValue == null){
                    parameterValues[i]=null;    
                }else{
                    parameterValues[i]=Util.convertParameterValue(paramValue,parameters[i].getType());    
                }
                
                // throw new Exception("ETU002462 tsisy annotation");
            }
        }
        return parameterValues;
    }
    public static List<String> getAllClassesSelonAnnotation(String packageToScan,Class<?>annotation) throws Exception{
        List<String> controllerNames = new ArrayList<>();
        try {
            
            String path = Thread.currentThread().getContextClassLoader().getResource(packageToScan.replace('.', '/')).getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            File packageDir = new File(decodedPath);

            File[] files = packageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        String className = packageToScan + "." + file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(annotation.asSubclass(java.lang.annotation.Annotation.class))) {
                            controllerNames.add(clazz.getName());
                        }
                    }
                }
            }
           
        } catch (IOException e) {
            throw new Exception("Package introuvable");
        }
       
        return controllerNames;
    }
    public static HashMap<String,Mapping> getAllMethods(List<String>controllers) throws Exception{
        HashMap<String,Mapping> hm=new HashMap<>();
        try {
            for (String c : controllers) {
                Class<?>clazz=Class.forName(c);
                Method[]methods=clazz.getDeclaredMethods();
                for (Method m : methods) {
                    if (m.isAnnotationPresent(Get.class)) {
                        Get getAnnotation= m.getAnnotation(Get.class);
                        String lien = getAnnotation.url();
                        for(String key:hm.keySet()){
                            if(lien.equals(key))
                                 throw new Exception("Duplicate url : "+getAnnotation.url());
                            }
                                    
                    hm.put(lien,new Mapping(clazz.getName(),m.getName()));
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return hm;
    }
    public static Object convertParameterValue(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        } else if (type == short.class || type == Short.class) {
            return Short.parseShort(value);
        } else if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value);
        } else if (type == char.class || type == Character.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("Invalid character value: " + value);
            }
            return value.charAt(0);
        }
        return null;
    }

   
}
