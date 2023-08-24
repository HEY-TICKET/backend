package com.heyticket.backend.module.language;

import java.util.List;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;

public class MorphologicalAnalyzer {

    private static Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);

    public static List<String> getNouns(String sentence) {
        KomoranResult komoranResult = komoran.analyze(sentence);
        return komoranResult.getNouns();
    }
}
