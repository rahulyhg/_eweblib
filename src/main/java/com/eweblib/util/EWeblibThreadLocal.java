package com.eweblib.util;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.eweblib.bean.BaseEntity;

public class EWeblibThreadLocal {

	// public static final String LOG="log";
	public static final Map<Thread, Map<String, Object>> threadMap = Collections.synchronizedMap(new HashMap<Thread, Map<String, Object>>());

	public static Object get(String key) {
		Thread curThread = Thread.currentThread();
		Map<String, Object> map = getMapResult(curThread);
		return map.get(key);
	}

	private static Map<String, Object> getMapResult(Thread curThread) {
		Map<String, Object> o = threadMap.get(curThread);
		if (o == null && !threadMap.containsKey(curThread)) {
			o = initialValue();
			threadMap.put(curThread, o);
		}
		return o;
	}

	public static void set(String key, Object newValue) {
		Thread currentThread = Thread.currentThread();
		currentThread.setName(String.valueOf(new Date().getTime()));
		Map<String, Object> map = getMapResult(currentThread);
		map.put(key, newValue);
		threadMap.put(currentThread, map);
	}

	public static void remove(String key) {
		Thread currentThread = Thread.currentThread();
		Map<String, Object> map = getMapResult(currentThread);
		map.remove(key);
		threadMap.put(currentThread, map);
	}

	public static Map<String, Object> initialValue() {
		return new HashMap<String, Object>();
	}

	public static void removeAll() {
		Thread curThread = Thread.currentThread();
		threadMap.remove(curThread);
	}

	public static String getCurrentUserId() {
		Object id = EWeblibThreadLocal.get(BaseEntity.ID);
		if (id != null) {
			return id.toString();
		}

		return null;
	}

}
