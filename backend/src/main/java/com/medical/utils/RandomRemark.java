package com.medical.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomRemark {

    public static String generateRandomString() {
        // 生成今天的年月日
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());

        // 生成三位随机数
        Random random = new Random();
        int randomNumber = random.nextInt(900) + 100;

        // 拼接字符串
        return "ORD" + datePart + randomNumber;
    }
}    