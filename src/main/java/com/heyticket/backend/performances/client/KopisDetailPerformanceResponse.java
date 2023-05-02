//package com.heyticket.backend.performances.client;
//
//import static com.heyticket.backend.performances.utils.OpenFormatter.convertToBoolean;
//import static com.heyticket.backend.performances.utils.OpenFormatter.convertToDate;
//import static com.heyticket.backend.performances.utils.OpenFormatter.convertToStyUrls;
//
//import com.heyticket.backend.performances.domain.Performance;
//
//public record KopisDetailPerformanceResponse(
//        String mt20id,
//        String mt10id,
//        String prfnm,
//        String prfpdfrom,
//        String prfpdto,
//        String fcltynm,
//        String prfcast,
//        String prfcrew,
//        String prfruntime,
//        String prfage,
//        String entrpsnm,
//        String pcseguidance,
//        String poster,
//        String sty,
//        String genrenm,
//        String prfstate,
//        String openrun,
//        String[] styurls,
//        String dtguidance
//) {
//    public Performance toEntity() {
//        return new Performance(
//                mt20id,
//                mt10id,
//                prfnm,
//                convertToDate(prfpdfrom),
//                convertToDate(prfpdto),
//                fcltynm,
//                prfcast,
//                prfcrew,
//                prfruntime,
//                prfage,
//                entrpsnm,
//                pcseguidance,
//                poster,
//                sty,
//                genrenm,
//                prfstate,
//                convertToBoolean(openrun),
//                 convertToStyUrls(styurls),
//                dtguidance
//        );
//    }
//}
