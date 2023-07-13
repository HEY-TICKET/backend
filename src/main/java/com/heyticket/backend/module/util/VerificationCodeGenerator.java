package com.heyticket.backend.module.util;

public class VerificationCodeGenerator {

    public static String createCode() {
        StringBuilder code = new StringBuilder();
        //개발용 임시코드
        code.append("0000");
//        Random random = new Random();
//
//        for (int i = 0; i < 8; i++) { // 인증코드 8자리
//            int index = random.nextInt(3); // 0~2 까지 랜덤, random 값에 따라서 아래 switch 문이 실행됨
//            switch (index) {
//                case 0 -> code.append((char) (random.nextInt(26) + 97)); // a~z (ex. 1+97=98 => (char)98 = 'b')
//                case 1 -> code.append((char) (random.nextInt(26) + 65)); // A~Z
//                case 2 -> code.append((random.nextInt(10))); // 0~9
//            }
//        }

        return code.toString();
    }
}
