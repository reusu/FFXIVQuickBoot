package name.yumao.ffxiv.boot.util.res;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

public class Config {
	
	private static HashMap<String,String> resourceMap;
	  
	public static void setConfigResource(String fileName) {
		setConfigResource("DefaultConfig", fileName);
	}

	public static void setConfigResource(String configName, String fileName) {
		if(resourceMap==null) resourceMap = new HashMap<String,String>();
		resourceMap.put(configName, fileName);
		ConfigResource configResource = new ConfigResource();
		try {
			configResource.loadConfig(fileName);
			if (configResource.getProperty("trans") != null && configResource.getProperty("trans").length() > 0) {
				String trans = configResource.getProperty("trans");
				for (String transver : trans.split("[|]")) {
					String url = "http://ffxiv.chn.teemo.name/trans/" + transver + ".properties";
					InputStream transStream = new URL(url).openStream();
					Properties transProps = new Properties();
					transProps.load(transStream);
					for (String transKey : transProps.stringPropertyNames()) {
						configResource.setProperty(transKey, transProps.getProperty(transKey));
					}
				}
			}
			if (!configName.equals("DefaultConfig") && Config.getProperty("TransMode") != null && !Config.getProperty("TransMode").equals("0")){
				String url = "http://ffxiv.chn.teemo.name/trans/ex.properties";
				InputStream transStream = new URL(url).openStream();
				Properties transProps = new Properties();
				transProps.load(transStream);
				for (String transKey : transProps.stringPropertyNames()) {
					configResource.setProperty(transKey, transProps.getProperty(transKey));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Resource.remove(configName);
		Resource.put(configName, configResource);
	}

	public static ConfigResource getConfigResource() {
		return getConfigResource("DefaultConfig");
	}

	public static ConfigResource getConfigResource(String configName) {
		ConfigResource configResource = (ConfigResource) Resource.get(configName);
		return configResource;
	}
	
	public static String getProperty(String name) {
		return getProperty("DefaultConfig", name);
	}

	public static String getProperty(String configName, String name) {
		ConfigResource configResource = (ConfigResource) Resource.get(configName);
		return configResource.getProperty(name);
	}

	public static void setProperty(String name, String value) {
		setProperty("DefaultConfig", name, value);
	}

	public static void setProperty(String configName, String name, String value) {
		ConfigResource configResource = (ConfigResource) Resource.get(configName);
		configResource.setProperty(name, value);
	}

	public static void saveProperty() {
		saveProperty("DefaultConfig");
	}

	public static void saveProperty(String configName) {
		ConfigResource configResource = (ConfigResource) Resource.get(configName);
		try {
			configResource.saveProperty();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void reloadConfig() {
		try {
			if (resourceMap != null) {
				Iterator<Entry<String, String>> resMapIter = resourceMap.entrySet().iterator();
				while(resMapIter.hasNext()){
					Entry entry = resMapIter.next();
					String configName = (String)entry.getKey();
					String fileName = (String)entry.getValue();
					Resource.remove(configName);
					ConfigResource configResource = new ConfigResource();
					try {
						configResource.loadConfig(fileName);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Resource.put(configName, configResource);
				}
			} else {
				getConfigResource().loadConfig();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		Config.setConfigResource("transtable", "conf" + File.separator + "transtable.properties");
	}
}
