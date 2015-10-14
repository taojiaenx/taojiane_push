package org.ddpush.im.util;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 *Json 转化成类
 * 
 * @author taojiaen
 * 
 */
public class JsonConvertor {
    private static final Logger log = LoggerFactory.getLogger(JsonConvertor.class);

    public static HashMap toHashMap(final Gson gson, final String input, final Type type)throws Exception {
        HashMap ret;
        ret = (HashMap) gson.fromJson(input, type);
        return ret;
    }

    public static Object toObject(Gson gson, final String input, final Type type)throws Exception {
        Object res;
        res = gson.fromJson(input, type);
        return res;
    }
}