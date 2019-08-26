package name.yumao.ffxiv.boot.util.res;

import java.util.HashMap;

public class Resource {
	private static HashMap<String,Object> objectHashMap;

	public static void put(String objectName, Object object) {
		if (objectHashMap == null) {
			objectHashMap = new HashMap<String,Object>();
		}
		objectHashMap.remove(objectName);
		objectHashMap.put(objectName, object);
	}

	public static Object get(String objectName) {
		if (objectHashMap == null) {
			return null;
		}
		return objectHashMap.get(objectName);
	}

	public static Object remove(String objectName) {
		if (objectHashMap == null) {
			objectHashMap = new HashMap<String,Object>();
		}
		return objectHashMap.remove(objectName);
	}
}
