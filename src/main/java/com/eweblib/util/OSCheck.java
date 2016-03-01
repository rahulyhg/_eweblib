package com.eweblib.util;

public class OSCheck {

	public enum OSType {

		WINDOWS, WINDOWS_PHONE, ANDROID, IPHONE, IPAD, MAC, ANDROID_PAD, LINUX

	}

	public static OSType checkOS(String userAgent) {

		if (EweblibUtil.isEmpty(userAgent)) {

			return null;
		}
		userAgent = userAgent.toLowerCase();

		if (userAgent.contains("Windows Phone".toLowerCase())) {

			return OSType.WINDOWS_PHONE;
		} else if (userAgent.contains("Windows NT".toLowerCase()) || userAgent.contains("WinNT".toLowerCase()) || userAgent.contains("Windows".toLowerCase())) {

			return OSType.WINDOWS;
		} else if (userAgent.contains("Android".toLowerCase()) && userAgent.contains("pad")) {

			return OSType.ANDROID_PAD;
		}else if (userAgent.contains("Android".toLowerCase())) {

			return OSType.ANDROID;
		} else if (userAgent.contains("iPad".toLowerCase())) {

			return OSType.IPAD;
		} else if (userAgent.contains("iPhone".toLowerCase())) {

			return OSType.IPHONE;
		} else if (userAgent.contains("Macintosh".toLowerCase())) {

			return OSType.MAC;
		}else if (userAgent.contains("Linux".toLowerCase())) {

			return OSType.LINUX;
		}

		return null;

	}
}
