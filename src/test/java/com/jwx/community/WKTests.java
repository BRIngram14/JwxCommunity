package com.jwx.community;


import java.io.IOException;

public class WKTests {
    public static void main(String[] args) {
        String cmd="d:/wkhtmltopdf/bin/wkhtmltoimage  --quality 75 http://www.nowcoder.com d:/Data/wk-images/2.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
