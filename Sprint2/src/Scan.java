package utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.Get;

public class Scan {
    public static HashMap<String, Mapping> getAllClassSelonAnnotation(HttpServlet servlet, String packageName,
            Class<?> annotation) throws Exception {
                HashMap<String, Mapping> mappingUrls = new HashMap<>();

        String path = servlet.getClass().getClassLoader().getResource(packageName.replace('.', '/')).getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File packageDir = new File(decodedPath);

        File[] files = packageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    try {
                        Class<?> classe = Class.forName(className);
                        if (classe.isAnnotationPresent(annotation.asSubclass(java.lang.annotation.Annotation.class))) {
                            Method[] methods=classe.getDeclaredMethods();
                            for (Method method : methods) {
                                if (method.isAnnotationPresent(Get.class)) {
                                    Get getAnnotation=method.getAnnotation(Get.class);
                                    mappingUrls.put(getAnnotation.url(),new Mapping(classe.getSimpleName(),method.getName()));
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return mappingUrls;
    }
}
