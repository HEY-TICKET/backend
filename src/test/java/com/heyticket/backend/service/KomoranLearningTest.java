package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled // KOMORAN 학습 테스트
public class KomoranLearningTest {

    @Test
    @DisplayName("KOMORAN 기본 테스트1")
    void komoran_example1() {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        String strToAnalyze = "동해물과 백두산이 마르고 닳도록";

        KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);

        System.out.println(analyzeResultList.getPlainText());

        List<Token> tokenList = analyzeResultList.getTokenList();
        for (Token token : tokenList) {
            System.out.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(), token.getEndIndex(), token.getMorph(), token.getPos());
        }
    }

    @Test
    @DisplayName("KOMORAN 기본 예제 테스트2")
    void komoran_example2() {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        String title = "미래상상마술쇼";

        KomoranResult titleAnalyzeResultList = komoran.analyze(title);

        System.out.println(titleAnalyzeResultList.getPlainText());

        List<Token> tokenList = titleAnalyzeResultList.getTokenList();
        for (Token token : tokenList) {
            if (token.getPos().startsWith("N")) {
                System.out.println(token.getMorph());
            }
        }
    }

    @Test
    @DisplayName("KOMORAN 명사 추출")
    void komoran_getNoun() {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        String title = "미래상상마술쇼";

        KomoranResult komoranResult = komoran.analyze(title);
        List<String> nouns = komoranResult.getNouns();
        for (String noun : nouns) {
            System.out.println("noun = " + noun);
        }
        assertThat(nouns).hasSize(4);
    }
}
