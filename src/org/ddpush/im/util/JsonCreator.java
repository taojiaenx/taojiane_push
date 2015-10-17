package org.ddpush.im.util;


import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 把结构体转换成json格式
 * 
 * @author xierong.rosy
 * 
 */

public class JsonCreator {
    // private static final Logger log = LoggerFactory.getLogger(ConvertToJson.class);
    public static String toJsonWithGson(final Map test) {
        Gson gson = new Gson();
        String res;
        try {
            res = gson.toJson(test);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    public static String toJsonWithGson(final Object obj) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String res;
        try {
            res = gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    public static String toJsonWithGson(final Object obj, Type type, Gson gson) {
        String res;
        try {
            res = gson.toJson(obj, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    public static String toJsonWithGson(final List list) {
        Gson gson = new Gson();
        String res;
        try {
            res = gson.toJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    public static String toJsonWithGson(final List list, final Type type) {
        Gson gson = new Gson();
        String res;
        try {
            res = gson.toJson(list, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }
}
