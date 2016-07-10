package ru.krasview.kvlib.indep;

public class HeaderAccount {
	static public  boolean hideHeader = false;

	public static boolean hideHeader(){
		return HeaderAccount.hideHeader;
	}

	public static void hh(){
		HeaderAccount.hideHeader = true;
	}

	public static void shh(){
		HeaderAccount.hideHeader = false;
	}
}
