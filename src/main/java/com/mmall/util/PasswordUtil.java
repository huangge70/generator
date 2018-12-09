package com.mmall.util;

import java.util.Date;
import java.util.Random;

public class PasswordUtil {
    //定义生成用户密码可以使用的字符集
    public final static String[] work={
            "a", "b", "c", "d", "e", "f", "g",
            "h", "j", "k", "m", "n",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "M", "N",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };
    public final static String[] num = {
            "2", "3", "4", "5", "6", "7", "8", "9"
    };
    public static String randomPassword(){
        StringBuffer stringBuffer=new StringBuffer();
        Random random = new Random(new Date().getTime());
        boolean flag=false;
        //默认密码长度为8，9，10位
        int length=random.nextInt(3)+8;
        for(int i=0;i<length;i++){
            if(flag){
                stringBuffer.append(num[random.nextInt(num.length)]);
            }else {
                stringBuffer.append(work[random.nextInt(work.length)]);
            }
            flag=!flag;
        }
        return stringBuffer.toString();
    }
    //验证生成密码的方法
    public static void main(String[] args){
        System.out.print(randomPassword());
    }
}
