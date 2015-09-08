package com.zkaiker.utils;

import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * 硬件工具类
 * 
 * @author cwx
 *
 */
public class DeviceUtils {

	/**
	 * 获取机器的唯一ID
	 * 
	 * @return
	 */
	public static String getDeviceId(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(((long) tmDevice.hashCode() << 32) | tmSerial.hashCode(),
				androidId.hashCode());
		String uniqueId = deviceUuid.toString().replace("-", "");
		return uniqueId;
	}

	/**
	 * 连接已保存的wifi
	 * 
	 * @param context
	 */
	public static void connectSavedWifi(final Context context, Handler handler) {
		SharedPreferences sp = context.getSharedPreferences("wifi_cache", Context.MODE_PRIVATE);
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> list = wm.getConfiguredNetworks();
		String wifiSSID = sp.getString("WIFI_SSID", "");
		String wifiPassword = sp.getString("WIFI_PASSWORD", "");
		WifiConfiguration configuration = null;
		for (WifiConfiguration item : list) {
			wifiSSID = StringUtil.checkSSID(wifiSSID);
			if (wifiSSID.equals(StringUtil.checkSSID(item.SSID))) {
				configuration = item;
			}
		}
		if (configuration != null) {
			if (wm.enableNetwork(configuration.networkId, true)) {
				return;
			}
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, "连接用户wifi错误！请手动连接", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 保存当前wifi
	 * 
	 * @param context
	 */
	public static void saveCurrentWifi(Context context, String wifiPassword) {
		WifiInfo info = getWifiInfo(context);
		String wifiSSID = info.getSSID();
		wifiSSID = StringUtil.checkSSID(wifiSSID);
		SharedPreferences sp = context.getSharedPreferences("wifi_cache", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("WIFI_SSID", wifiSSID);
		editor.putString("WIFI_PASSWORD", wifiPassword);
		editor.commit();
	}

	/**
	 * WIFI网络开关
	 */
	public static void toggleWiFi(Context context, boolean enabled) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (enabled && !wm.isWifiEnabled()) {
			wm.setWifiEnabled(enabled);
		}
		if (!enabled && wm.isWifiEnabled()) {
			wm.setWifiEnabled(enabled);
		}
	}

	/**
	 * 返回当前系统版本
	 * 
	 * @return
	 */
	public static int GetSystemVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 获取当前连接wifi 的对象
	 * 
	 * @param context
	 * @return
	 */
	public static WifiInfo getWifiInfo(final Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifi.getConnectionInfo();
	}

	/**
	 * 无密码wifi
	 */
	public static final int WIFI_TYPE_NO_PASSWORD = 0;
	/**
	 * wep加密wifi
	 */
	public static final int WIFI_TYPE_WEP_PASSWORD = 1;
	/**
	 * wpa加密wifi
	 */
	public static final int WIFI_TYPE_WPA_PASSWORD = 2;

	public static final int getPriority(Context context) {
		SharedPreferences sp = context.getSharedPreferences("wifi_cache", Context.MODE_PRIVATE);
		int n = sp.getInt("LAST_PRIORITY", 5);
		n++;
		Editor editor = sp.edit();
		editor.putInt("LAST_PRIORITY", n);
		editor.commit();
		return n;
	}

	/**
	 * 连接上指定的wifi(非异步执行)
	 * 
	 * @param context
	 * @param wifiSSID
	 * @param wifiPassword
	 * @param type
	 */
	public static boolean connectToTargetWifi(final Context context, String wifiSSID,
			final String wifiPassword, final int type) {
		wifiSSID = StringUtil.checkSSID(wifiSSID);
		System.out.println("开始连接wifi" + wifiSSID);
		long mainTime = System.currentTimeMillis();
		int mainTimeOut = 180000;
		try {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiConfiguration wifiConfiguration = new WifiConfiguration();
			wifiConfiguration.allowedAuthAlgorithms.clear();
			wifiConfiguration.allowedGroupCiphers.clear();
			wifiConfiguration.allowedKeyManagement.clear();
			wifiConfiguration.allowedPairwiseCiphers.clear();
			wifiConfiguration.allowedProtocols.clear();
			wifiConfiguration.priority = getPriority(context);
			wifiConfiguration.SSID = "\"" + wifiSSID + "\"";
			switch (type) {
			case WIFI_TYPE_NO_PASSWORD:
				// wifiConfiguration.wepKeys[0] = "";
				wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				// wifiConfiguration.wepTxKeyIndex = 0;
				break;
			case WIFI_TYPE_WEP_PASSWORD:
				wifiConfiguration.hiddenSSID = true;
				wifiConfiguration.wepKeys[0] = "\"" + wifiPassword + "\"";
				wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
				wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
				wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
				wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
				wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
				wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				wifiConfiguration.wepTxKeyIndex = 0;
				break;
			case WIFI_TYPE_WPA_PASSWORD:
				wifiConfiguration.preSharedKey = "\"" + wifiPassword + "\"";
				wifiConfiguration.hiddenSSID = true;
				wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
				wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
				wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
				wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
				wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
				wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
				break;
			}
			// 如果已存在该wifi,则先删除之前wifi
			WifiConfiguration temp = IsExsits(context, wifiSSID);
			if (temp != null) {
				wifi.removeNetwork(temp.networkId);
			}
			int id = wifi.addNetwork(wifiConfiguration);
			long timeEnable = System.currentTimeMillis();
			while (!wifi.enableNetwork(id, true)) {
				if (System.currentTimeMillis() - mainTime >= mainTimeOut) {
					return false;
				}
				if (System.currentTimeMillis() - timeEnable >= 20000) { // 设置超时
					System.out.println(id);
					System.out.println("enable 错误");
					wifi.removeNetwork(id);
					return false;
				}
			}
			android.net.NetworkInfo wifiInfo;
			long time = System.currentTimeMillis();
			boolean bl = true;
			while (bl) {
				// 获取系统广播,了解wifi状态
				if (System.currentTimeMillis() - mainTime >= mainTimeOut) {
					return false;
				}
				ConnectivityManager connMgr = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (wifiInfo.getState() == NetworkInfo.State.DISCONNECTED
						|| wifiInfo.getState() == NetworkInfo.State.CONNECTING) {
					// 如果没有重新启用wifi则死循环,知道wifi重新启用为止
					bl = false;
					time = System.currentTimeMillis();
				}
			}
			do {
				if (System.currentTimeMillis() - mainTime >= mainTimeOut) {
					return false;
				}
				// 获取系统广播,了解wifi状态
				ConnectivityManager connMgr = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (wifiInfo.getState() == NetworkInfo.State.CONNECTING) {
					time = System.currentTimeMillis();
				}
				if (System.currentTimeMillis() - time >= 20000) { // 设置超时
					System.out.println("wifi 错误" + wifiInfo.getState().toString());
					return false;
				}
			} while (!wifiInfo.isConnected());
			wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			while (!wifi.getConnectionInfo().getSSID().equals(wifiSSID)
					& !wifi.getConnectionInfo().getSSID().equals("\"" + wifiSSID + "\"")) {
				if (System.currentTimeMillis() - mainTime >= mainTimeOut) {
					return false;
				}
				System.out.println(wifi.getConnectionInfo().getSSID());
				System.out.println("wifi ssid 错误");
				wifi.startScan();
			}
			System.out.println("连接wifi" + wifiSSID + "成功");
			return true;
		} catch (Exception er) {
			er.printStackTrace();
			return false;
		}
	}

	/**
	 * 是否存在该ssid,是的话返回该WifiConfiguration,否则为空
	 * 
	 * @param context
	 * @param SSID
	 * @return
	 */
	private static WifiConfiguration IsExsits(Context context, String SSID) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> existingConfigs = wifi.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	/**
	 * 获取当前wifi的ssid列表
	 * 
	 * @param context
	 * @return
	 */
	public static void getWifiSSIDList(final Context context, final OnGetWifiListListener lis) {
		if (lis == null) {
			return;
		}
		final Handler handler = new Handler();
		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				while (!wifi.isWifiEnabled()) {
					DeviceUtils.toggleWiFi(context, true);
				}
				while (true) {
					if (wifi.startScan()) {
						final List<ScanResult> scanResult = wifi.getScanResults();
						if (scanResult.size() > 0) {
							handler.post(new Runnable() {

								@Override
								public void run() {
									if (lis != null) {
										lis.onGetWifiList(scanResult);
									}
								}
							});
							break;
						}
					}
				}
			}
		};
		thread.start();
	}

	public interface OnGetWifiListListener {
		public void onGetWifiList(List<ScanResult> list);
	}
}
