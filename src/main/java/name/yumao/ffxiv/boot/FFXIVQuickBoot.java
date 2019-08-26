package name.yumao.ffxiv.boot;

import name.yumao.ffxiv.boot.swing.ClientLauncherPanel;
import name.yumao.ffxiv.boot.swing.ConfigApplicationPanel;
import name.yumao.ffxiv.boot.util.res.Config;

import java.io.File;

public class FFXIVQuickBoot {
    public static void main(String[] args) throws Exception {
        File confFile = new File("conf" + File.separator + "boot.properties");
        if (!confFile.exists()){
            confFile.createNewFile();
        }
        Config.setConfigResource("conf" + File.separator + "boot.properties");
        String path = Config.getProperty("GamePath");
        if(isFFXIVFloder(path)){
            new ClientLauncherPanel();
        }else{
            new ConfigApplicationPanel();
        }
    }
    private static boolean isFFXIVFloder(String path){
        if (path == null) return false;
        return new File(path + File.separator + "game" + File.separator + "ffxiv.exe").exists();
    }
}
