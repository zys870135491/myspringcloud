package com.zys.springcloud.entities;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SystemContext {

    private static ThreadLocal<Map<String,String>> localMap =  new ThreadLocal();
    private static Logger logger = LoggerFactory.getLogger(SystemContext.class);
    private static Integer MAX_CAPACITY = 100;
    private static Integer MAX_SIZE = 1024;


    public SystemContext() {
    }

    public static Map<String, String> getContextMap() {
        return (Map)localMap.get();
    }

    public static void setContextMap(Map<String, String> map) {
        localMap.set(map);
    }

    public static String get(String key) {
        Map<String, String> contextMap = getContextMap();
        return contextMap == null ? null : (String)contextMap.get(key);
    }

    public static String put(String key, String value) {
        if (key == null) {
            logger.error("key: is null, can't put it into the context map");
            return value;
        } else if (key.length() > MAX_SIZE) {
            throw new RuntimeException("key is more than " + MAX_SIZE + ", can't put it into the context map");
        } else if (value != null && value.length() > MAX_SIZE) {
            throw new RuntimeException("value is more than " + MAX_SIZE + ", can't put it into the context map");
        } else {
            Map<String, String> contextMap = getContextMap();
            if (contextMap == null) {
                contextMap = new HashMap(16);
                localMap.set(contextMap);
            }

            if (((Map)contextMap).size() > MAX_CAPACITY) {
                throw new RuntimeException("the context map is full, can't put anything");
            } else if (value == null) {
                ((Map)contextMap).remove(key);
                return null;
            } else {
                String map = (String) ((Map) contextMap).put(key, value);
                return map ;
            }
        }
    }

    public static void remove(String key) {
        if (key == null) {
            logger.error("key: is null, can't remove");
        } else {
            Map<String, String> contextMap = getContextMap();
            if (contextMap != null) {
                contextMap.remove(key);
            }

        }
    }

    public static String getUserId() {
        return get("TM-Header-UserId");
    }

    public static void setUserId(String userId) {
        put("TM-Header-UserId", userId);
    }

    public static String getEnvId() {
        return get("TM-Header-EnvId");
    }

    public static void setEnvId(String envId) {
        put("TM-Header-EnvId", envId);
    }

}
