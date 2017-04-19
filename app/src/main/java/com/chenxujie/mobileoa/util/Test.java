package com.chenxujie.mobileoa.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by minxing on 2017-04-11.
 */

public class Test {
    private static  String phonePat="^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
    private static String emailPat = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"; //定义验证规则

    public static Boolean testEmail(String email) {
        Pattern p = Pattern.compile(emailPat);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }
    public  static Boolean testPhone(String phone){
        Pattern p = Pattern.compile(phonePat);
        Matcher m = p.matcher(phone);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
