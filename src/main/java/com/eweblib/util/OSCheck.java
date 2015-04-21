package com.eweblib.util;

public  class OSCheck {

	public enum OSType {

		WINDOWS, WINDOWS_PHONE, ANDROID, IOS, IPHONE, IPAD, MAC, SYMBIAN, BLACKBERRY

	}

	public static OSType checkOS(String userAgent) {
		
		if(EweblibUtil.isEmpty(userAgent)){
			
			return null;
		}

		if (userAgent.contains("Windows Phone")) {

			return OSType.WINDOWS_PHONE;
		} else if (userAgent.contains("Windows NT")) {

			return OSType.WINDOWS;
		} else if (userAgent.contains("BlackBerry")) {

			return OSType.BLACKBERRY;
		} else if (userAgent.contains("Symbian")) {

			return OSType.SYMBIAN;
		} else if (userAgent.contains("Android")) {

			return OSType.ANDROID;
		} else if (userAgent.contains("iPad")) {

			return OSType.IPAD;
		} else if (userAgent.contains("iPhone")) {

			return OSType.IPHONE;
		} else if (userAgent.contains("Macintosh")) {

			return OSType.MAC;
		}

		return null;

	}
}
