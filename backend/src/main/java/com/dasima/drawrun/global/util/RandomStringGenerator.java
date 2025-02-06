package com.dasima.drawrun.global.util;

import java.util.Random;

public class RandomStringGenerator {
    public static String generateRandomNumber() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000; // 100000 ~ 999999 사이의 숫자 생성
        return String.valueOf(number); // 숫자를 String으로 변환하여 반환
    }
}
