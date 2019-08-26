package name.yumao.ffxiv.boot.util;

import name.yumao.ffxiv.boot.util.res.Config;
import org.apache.commons.codec.language.bm.Lang;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FFXIVLauncher {
    private static String USERAGENT = "SQEXAuthor/2.0.0(Windows 6.2; ja-jp; 45d19cc985)";

    private static String[] bootFiles =
    {
        "ffxivboot.exe",
        "ffxivboot64.exe",
        "ffxivlauncher.exe",
        "ffxivlauncher64.exe",
        "ffxivupdater.exe",
        "ffxivupdater64.exe",
    };

    public static String getlaunchParams(String user, String pwd, String otp){
        try {
            String lang = Config.getProperty("Language");
            String index = Request.Get("https://ffxiv-login.square-enix.com/oauth/ffxivarr/login/top?lng=" + lang + "&rgn=3&isft=0&issteam=0")
                    .connectTimeout(5000)
                    .socketTimeout(60000)
                    .addHeader("User-Agent", USERAGENT)
                    .execute()
                    .returnContent()
                    .toString();
            Pattern pattern = Pattern.compile("<\\s*input .* name=\"_STORED_\" value=\"(?<stored>.*)\">");
            Matcher matcher = pattern.matcher(index);
            if (matcher.find()) {
                String stored = matcher.group("stored");
                String login = Request.Post("https://ffxiv-login.square-enix.com/oauth/ffxivarr/login/login.send")
                        .connectTimeout(5000)
                        .socketTimeout(60000)
                        .addHeader("User-Agent", USERAGENT)
                        .addHeader("Referer", "https://ffxiv-login.square-enix.com/oauth/ffxivarr/login/top?lng=" + lang + "&rgn=3&isft=0&issteam=0")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .bodyForm(Form.form()
                                .add("_STORED_", stored)
                                .add("sqexid", user)
                                .add("password", pwd)
                                .add("otppw", otp)
                                .build())
                        .execute()
                        .returnContent()
                        .toString();
                pattern = Pattern.compile("window\\.external\\.user\\(\"login=auth,(?<launchParams>.*)\"\\);");
                matcher = pattern.matcher(login);
                if (matcher.find()) {
                    String launchParams = matcher.group("launchParams");
                    return launchParams;
                } else {
                    return "0,err,can not find launchParams";
                }
            } else {
                return "0,err,can not find stored";
            }
        } catch (Exception e){
            return "0,err,launchParams thread exception";
        }
    }

    public static String getSid(String launchParams) {
        String params[] = launchParams.split(",");
        String session = params[2];
        int region = Integer.valueOf(params[6]);

        String gamePath = Config.getProperty("GamePath");
        String lang = Config.getProperty("Language");

        String hash = "";
        File boot = new File(gamePath + File.separator + "boot");
        for(int i = 0; i < bootFiles.length; i++){
            String bootFile = bootFiles[i];
            hash += bootFile + "/" + new File(boot + File.separator + bootFile).length() + "/" + SHA1.getFileSHA1(new File(boot + File.separator + bootFile));
            if (i != bootFiles.length - 1)
                hash += ",";
        }

        String gameVer = "";
        try {
            File gameVerFile = new File(gamePath + File.separator + "game" + File.separator + "ffxivgame.ver");
            if (gameVerFile.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(gameVerFile));
                gameVer = bufferedReader.readLine();
            }
        }catch (Exception e) {}

        try {
            HttpResponse response = Request.Post("https://patch-gamever.ffxiv.com/http/win32/ffxivneo_release_game/" + gameVer + "/" + session)
                    .connectTimeout(5000)
                    .socketTimeout(60000)
                    .addHeader("X-Hash-Check", "enabled")
                    .addHeader("User-Agent", USERAGENT)
                    .addHeader("Referer", "https://ffxiv-login.square-enix.com/oauth/ffxivarr/login/top?lng=" + lang + "&rgn=" + region)
                    .bodyString(hash, ContentType.APPLICATION_FORM_URLENCODED)
                    .execute()
                    .returnResponse();
            String sid =  response.getFirstHeader("X-Patch-Unique-Id").getValue();
            return sid + "," + response.getStatusLine().getStatusCode();
        }catch (Exception e){
            return "0,200";
        }
    }

    public static void launchClient(String launchParams, String sid) {
        String params[] = launchParams.split(",");
        String session = params[2];
        int region = Integer.valueOf(params[6]);
        boolean terms = !params[4].equals("0");
        boolean playable = !params[10].equals("0");
        int maxex = Integer.valueOf(params[14]);

        String gamePath = Config.getProperty("GamePath");
        String lang = Config.getProperty("Language");
        String langCode = Language.getLangCode(lang);
        String dx = Config.getProperty("DXMode");

        String gameVer = "";
        try {
            File gameVerFile = new File(gamePath + File.separator + "game" + File.separator + "ffxivgame.ver");
            if (gameVerFile.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(gameVerFile));
                gameVer = bufferedReader.readLine();
            }
        }catch (Exception e) {}

        String exePath;
        if (dx.equalsIgnoreCase("dx9")) {
            exePath = gamePath + File.separator + "game" + File.separator + "ffxiv.exe";
        } else {
            exePath = gamePath + File.separator + "game" + File.separator + "ffxiv_dx11.exe";
        }

        File exeDirectory = new File(gamePath + File.separator + "boot");
        String[] exeArray = {exePath, "DEV.DataPathType=1", "DEV.MaxEntitledExpansionID=" + maxex, "DEV.TestSID=" + sid, "DEV.UseSqPack=1", "SYS.Region=" + region, "language=" + langCode, "ver=" + gameVer};
        try {
            Process process = Runtime.getRuntime().exec(exeArray, null, exeDirectory);
        }catch (Exception e){}

    }
}


