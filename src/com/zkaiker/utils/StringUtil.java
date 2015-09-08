package com.zkaiker.utils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author cwx
 *
 */
public class StringUtil {

	/**
	 * 检查ssid是否有",如果有则返回去掉"的SSID.
	 * 
	 * @param ssid
	 * @return
	 */
	public static String checkSSID(String ssid) {
		if (isNullOrEmpty(ssid)) {
			return null;
		}
		if (ssid.matches("\\s*\\\"[a-zA-Z0-9_-]+\\\"\\s*")) {
			ssid = ssid.replaceAll("\\s*\\\"", "");
			ssid = ssid.replaceAll("\\s*", "");
			return ssid;
		} else {
			return ssid;
		}
	}

	/**
	 * 检查是否为设备的ssid
	 * 
	 * @param ssid
	 * @return
	 */
	public static boolean isEquipmentSSID(String ssid) {
		return true;
		// return ssid.matches("[Jj][Aa][Dd][Ll]\\w*");
	}

	/**
	 * 判断是否为null或空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}

	/**
	 * 判断是否为null或空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean notNullorEmpty(String str) {
		return str != null && !str.equals("");
	}

	/**
	 * 判断是否为整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInt(String str) {
		try {
			Integer.valueOf(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isLong(String str) {
		try {
			Long.valueOf(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isFloat(String str) {
		try {
			Float.valueOf(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDouble(String str) {
		try {
			Double.valueOf(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断是否是Order status
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isOrderStatus(String str) {
		Pattern pattern = Pattern.compile("[ORPCE]");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 判断是否是邮箱地址
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmail(String str) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 判断是否为IP地址
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isIP(String str) {
		Pattern pattern = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(str); // 以验证127.400.600.2为例
		return matcher.matches();
	}

	/**
	 * 判断是否为时间格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDate(String str) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			sdf.parse(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 是否为小时时间格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isTime(String str) {
		try {
			if (Time.valueOf(str) != null)
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * 判断是否为手机号码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isPhoneNum(String str) {
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(str); // 以验证127.400.600.2为例
		return matcher.matches();
	}

}
