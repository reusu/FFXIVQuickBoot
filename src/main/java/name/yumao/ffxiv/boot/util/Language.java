package name.yumao.ffxiv.boot.util;

public class Language {
    public static String getLang(String lang){
        switch(lang){
//            case "日语":
//                return "ja";
            case "英语":
                return "en-gb";
            case "德语":
                return "de";
            case "法语":
                return "fr";
            default:
                return "ja";
        }
    }
    public static String getLangCode(String lang){
        switch(lang){
//            case "ja":
//                return "0";
            case "en-gb":
                return "1";
            case "de":
                return "2";
            case "fr":
                return "3";
            default:
                return "0";
        }
    }
}
