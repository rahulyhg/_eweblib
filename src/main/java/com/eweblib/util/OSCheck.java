package com.eweblib.util;

public  class OSCheck {

	public enum OSType {

		WINDOWS, WINDOWS_PHONE, ANDROID, IOS, IPHONE, IPAD, MAC, SYMBIAN, BLACKBERRY

	}

	public static OSType checkOS(String userAgent) {
		userAgent = userAgent.toLowerCase();
		
		if(EweblibUtil.isEmpty(userAgent)){
			
			return null;
		}

		if (userAgent.contains("windows Phone".toLowerCase())) {

			return OSType.WINDOWS_PHONE;
		} else if (userAgent.contains("Windows NT".toLowerCase())) {

			return OSType.WINDOWS;
		} else if (userAgent.contains("BlackBerry".toLowerCase())) {

			return OSType.BLACKBERRY;
		} else if (userAgent.contains("Symbian".toLowerCase())) {

			return OSType.SYMBIAN;
		} else if (userAgent.contains("Android".toLowerCase())) {

			return OSType.ANDROID;
		} else if (userAgent.contains("iPad".toLowerCase())) {

			return OSType.IPAD;
		} else if (userAgent.contains("iPhone".toLowerCase())) {

			return OSType.IPHONE;
		} else if (userAgent.contains("Macintosh".toLowerCase())) {

			return OSType.MAC;
		}

		return null;

	}
}
