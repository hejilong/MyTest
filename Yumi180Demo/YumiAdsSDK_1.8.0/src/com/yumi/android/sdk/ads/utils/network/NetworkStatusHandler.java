package com.yumi.android.sdk.ads.utils.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.location.GpsPermissionLevel;
import com.yumi.android.sdk.ads.utils.location.GpsStatusGetter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public final class NetworkStatusHandler {
	
	private static final String TAG = "NetworkStatusHandler";
	private static final boolean onoff = true;

	// 判断是否有可用的网络连接
	public static boolean isNetWorkAvaliable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			if (info == null) {
				return false;
			} else {
				for (NetworkInfo nf : info) {
					if (nf.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
				return false;
			}
		}
	}

	/**
	 * 当前是否为wifi网络连接
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isWIFIConnected(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		String typeName = null;

		if (networkInfo != null) {
			typeName = networkInfo.getTypeName();
		} else {
			typeName = "null";
		}
		return typeName.trim().equalsIgnoreCase("wifi");
	}

	/**
	 * 获取连接的网络名
	 * 
	 * @param context
	 * @return
	 */
	public static String getConnectedNetName(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		String typeName = "unknown";
		if (networkInfo != null) {
			typeName = networkInfo.getTypeName();
			if (typeName.trim().equalsIgnoreCase("wifi")) {
				typeName = "wifi";
			} else {
				typeName = getConnectionType(context);
			}
		}
		return typeName;
	}

	private static String getConnectionType(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int type = manager.getNetworkType();
		System.err.println(type);
		switch (type) {
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return "2g";
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return "3g";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "4g";
		default:
			return "unknown";
		}
	}
	
	public static WifiInfo getConnectWifiInfo(Context context){
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (manager.isWifiEnabled()) {
			WifiInfo wifi = manager.getConnectionInfo();
			return wifi;
		}
		return null;
	}
	
	public static String getConnectWifiBssid(Context context){
		WifiInfo connectWifiInfo = getConnectWifiInfo(context);
		String bssid = null;
		if (connectWifiInfo != null) {
			bssid =  connectWifiInfo.getBSSID();
		}
		return bssid == null ?  "" :  bssid;
	}
	
	public static String getConnectWifiSsid(Context context){
		WifiInfo connectWifiInfo = getConnectWifiInfo(context);
		String ssid = null;
		if (connectWifiInfo != null) {
			ssid = connectWifiInfo.getSSID();
		}
		return ssid == null ? "" : ssid ;
	}
	
	public static List<ScanResult> getConnectWifiInfoList(Context context){
//		try {
//			WifiManager manager = (WifiManager) context
//					.getSystemService(Context.WIFI_SERVICE);
//			List<ScanResult> wifiList = null;
//			if (manager != null && manager.isWifiEnabled()) {
//				wifiList = manager.getScanResults();
//			}
//			return wifiList;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
	    return new ArrayList<ScanResult>();
	}
	
	
	@SuppressLint("Assert")
	public static String getConnectWifiInfoList(Context context, int getSize){
//		assert getSize > 0;
//		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		if (manager.isWifiEnabled()) {
//			try {
//				JSONObject json = new JSONObject();
//				JSONArray wifi = new JSONArray();
//				List<ScanResult> scanResults = manager.getScanResults();
//				if (NullCheckUtils.isNotEmptyCollection(scanResults)) {
//					int size = scanResults.size() < getSize ? scanResults.size() : getSize;  
//					for (int i = 0; i < size; i++) {
//						ScanResult scanResult = scanResults.get(i);
//						JSONObject result = new JSONObject();
//						result.put("BSSID", scanResult.BSSID);
//						result.put("SSID", scanResult.SSID);
//						result.put("level", scanResult.level);
//						result.put("frequency", scanResult.frequency);
//						result.put("capabilities", scanResult.capabilities);
//						wifi.put(result);
//					}
//				}
//				json.put("wifi", wifi);
//				return json.toString();
//			} catch (JSONException e) {
//				ZplayDebug.e(TAG, "", e, onoff);
//				
//			}
//		}
		return "";
	}
	
	public static String getConnectCellID(Context context){
		try {
			if (GpsStatusGetter.getManifestGpsPermissionLevel(context).equals(
					GpsPermissionLevel.LEVEL_OFF)) {
				return "";
			}
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String cid = null;
			if (manager != null) {
				CellLocation cellLocation = manager.getCellLocation();
				if (cellLocation != null
						&& cellLocation instanceof CdmaCellLocation) {
					cid = ((CdmaCellLocation) cellLocation).getBaseStationId()
							+ "";
				}
				if (cellLocation != null
						&& cellLocation instanceof GsmCellLocation) {
					cid = ((GsmCellLocation) cellLocation).getCid() + "";
				}
			}
			return cid == null ? "" : cid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static GsmCellLocation getGsmLoc(Context context){
		if (GpsStatusGetter.getManifestGpsPermissionLevel(context).equals(GpsPermissionLevel.LEVEL_OFF)) {
			return null;
		}
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		CellLocation cellLocation = tm.getCellLocation();
		if (cellLocation instanceof GsmCellLocation) {
			return (GsmCellLocation)cellLocation;
		}
		return null;
	}
	
	
	public static int getGsmCid(Context context){
		int cid = 0;
		GsmCellLocation loc = getGsmLoc(context);
		if (loc != null) {
			cid = loc.getCid();
		}
		return cid;
	}

	public static int getGsmLac(Context context){
		int lac = 0;
		GsmCellLocation loc = getGsmLoc(context);
		if (loc != null) {
			lac = loc.getLac();
		}
		return lac;
	}
	
	
	@SuppressLint("Assert")
	public static String getConnectCellInfoList(Context context, int getSize){
		assert getSize > 0;
		if (GpsStatusGetter.getManifestGpsPermissionLevel(context).equals(GpsPermissionLevel.LEVEL_OFF)) {
			return "";
		}
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			JSONObject cell = new JSONObject();
			CellLocation cellLocation = manager.getCellLocation();
			if (cellLocation != null && cellLocation instanceof CdmaCellLocation) {
				JSONObject cdma = new JSONObject();
				cdma.put("BSID", ((CdmaCellLocation) cellLocation).getBaseStationId());
				cdma.put("NID", ((CdmaCellLocation) cellLocation).getNetworkId());
				cdma.put("SID", ((CdmaCellLocation) cellLocation).getSystemId());
				cell.put("cdma", cdma);
			}
			if (cellLocation != null && cellLocation instanceof GsmCellLocation) {
				JSONObject gsm = new JSONObject();
				gsm.put("CID", ((GsmCellLocation) cellLocation).getCid());
				gsm.put("LAC", ((GsmCellLocation) cellLocation).getLac());
				gsm.put("PSC", ((GsmCellLocation) cellLocation).getPsc());
				cell.put("gsm", gsm);
			}
			List<NeighboringCellInfo> neighboringCellInfos = manager.getNeighboringCellInfo();
			if (NullCheckUtils.isNotEmptyCollection(neighboringCellInfos)) {
				int size = neighboringCellInfos.size() < getSize ? neighboringCellInfos.size() : getSize;
				JSONArray ncs = new JSONArray();
				for (int i = 0; i < size; i ++) {
					JSONObject nc = new JSONObject();
					NeighboringCellInfo neighboringCellInfo = neighboringCellInfos.get(i);
					nc.put("CID", neighboringCellInfo.getCid());
					nc.put("LAC", neighboringCellInfo.getLac());
					nc.put("PSC", neighboringCellInfo.getPsc());
					nc.put("RSSI", neighboringCellInfo.getRssi());
					ncs.put(nc);
				}
				cell.put("neighbor", ncs);
			}
			return cell.toString();
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return "";
	}

}
