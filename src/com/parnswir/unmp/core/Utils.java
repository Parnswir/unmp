package com.parnswir.unmp.core;

import java.util.Locale;

public class Utils {

	public static String getFileExt(String fileName) {       
	     return fileName.toLowerCase(Locale.ENGLISH).substring((fileName.lastIndexOf(".") + 1), fileName.length());
	}
	
}
