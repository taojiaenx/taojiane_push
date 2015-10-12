package org.ddpush.im.util;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 *Json 转化成类
 * 
 * @author taojiaen
 * 
 */
public class JsonConvertor {
    // private static final Logger log = LoggerFactory.getLogger(JsonCovert.class);
    public final static Type HashMapType = new TypeToken<HashMap>() {}.getType();

    public static HashMap toHashMap(final String input) {
        Gson gson = new Gson();
        HashMap ret;
        try {
            ret = (HashMap) gson.fromJson(input, HashMapType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    public static Object toObject(final String input, final Type type) {
        Gson gson = new Gson();
        Object res;
        try {
            res = gson.fromJson(input, type);
        } catch (Exception e) {
        //    e.printStackTrace();
            return null;
        }
        return res;
    }
}