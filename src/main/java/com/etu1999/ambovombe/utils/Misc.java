package com.etu1999.ambovombe.utils;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Misc {

    public static String getGetterName(String fieldName){
        return "get" + Misc.capitalize(fieldName);
    }

    public static String getSetterName(String fieldName){
        return "set" + Misc.capitalize(fieldName);
    }

    public static Method getGetter(Object object, String fieldName) throws NoSuchMethodException, SecurityException{
        String getter = getGetterName(fieldName);
        return object.getClass().getMethod(getter);
    }

    public static Method getSetter(Object object, String fieldName, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException{
        String getter = getSetterName(fieldName);
        return object.getClass().getMethod(getter, parameterTypes);
    }
    
    /**
     * Converts the first character of a given string to uppercase
     * @param str the input string
     * @return the input string with the first character in uppercase
     */
    public static String capitalize(String str){
        return Character.toUpperCase(str.charAt(0))
                + str.substring(1);
    }

    /**
     * return an adapted string for sql syntax
     */
    public static String convertForSql(Object attrb){
        if(attrb == null) return "null";
        Class<?> AttrClass = attrb.getClass();
        return AttrClass == Date.class ? "TO_DATE('"+attrb+"','YYYY-MM-DD')"
                : (AttrClass == Timestamp.class) ? "TO_TIMESTAMP('"+ attrb +"', 'YYYY-MM-DD HH24:MI:SS.FF')"
                : (AttrClass == String.class) || (AttrClass == Time.class) ? "'"+attrb+"'"
                : attrb.toString();
    }

    public static String TabToString(String regex,String... str){
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            val.append(str[i]);
            if(i < str.length - 1)
                val.append(regex);
        }
        return val.toString();
    }

    public static String currentLocation() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource("").getPath();
    }

}
