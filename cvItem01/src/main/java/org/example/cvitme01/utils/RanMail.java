package org.example.cvitme01.utils;

import java.util.Random;

public class RanMail {
    private final Random random=new Random();
    private final StringBuilder result=new StringBuilder();


    public String authCode(){
        for (int i = 0; i < 6; i++) {
            int number = random.nextInt(10);
            result.append(number);
        }
        return String.valueOf(result);
    }
}
