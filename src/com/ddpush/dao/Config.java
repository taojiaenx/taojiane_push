package com.ddpush.dao;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class Config {

    public static ResourceBundle read() throws NullPointerException {
        return ResourceBundle.getBundle("ddpush");
    }

    public static ResourceBundle read(String path) throws NullPointerException {
        return ResourceBundle.getBundle(path);
    }

    public static Map<String, String> convertResourceBundleToMap(ResourceBundle resource) {
        if (resource == null) throw new NullPointerException("no this properties");
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> keys = resource.getKeys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, resource.getString(key));
        }

        return map;
    }

    public static Map<String, String> convertResourceBundleToMap(String filePath) {
        Properties props = new Properties();
        InputStream in = null;
        Map<String, String> ret = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            Enumeration en = props.propertyNames();
            ret = new HashMap<String, String>();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String property = props.getProperty(key);
                ret.put(key, property);
            }
            in.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
