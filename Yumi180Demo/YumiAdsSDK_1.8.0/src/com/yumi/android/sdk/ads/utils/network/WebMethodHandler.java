package com.yumi.android.sdk.ads.utils.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.content.Context;


public final class WebMethodHandler {
	
	private final static String TAG = "WebMethodHandler";
	private final static boolean onoff = true;
	
	
	// request/response成功
	public final static int OK = 0;
	// 失败
	public final static int ERROR = 1;
//	private static Header[] headers;
	
	
	/**
	 * post方式访问网络
	 * 
	 * <p>
	 * 参数已键值对的形式传递
	 * 
	 * @param context
	 * @param url
	 * @param keys
	 * @param values
	 * @param charsetName
	 * @return
	 */
	public static ResultObject accessWebByPost(Context context, String url,
			String[] keys, String[] values, String charset, Map<String, String> headers) {
		// 因为post方式不能输出url，所以这里使用get形式的url进行输出
//		String tempUrl = url;
//		tempUrl += ParamsBuilder.buildGetParams(keys, values);
//		try {
//			ZplayDebug.V(TAG, "url " + 
//					URLDecoder.decode(tempUrl, APPConfig.getDefaultCharset()));
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}

		int resultCode = ERROR;
		InputStream resultStream = null;
		String errorMsg = null;
		ResultObject result = null;

		// 没有可用网络连接
		if (!NetworkStatusHandler.isNetWorkAvaliable(context)) {
			resultCode = ERROR;
			errorMsg = "-1";
			result = new ResultObject(resultCode, errorMsg, null);
			ZplayDebug.w(TAG, "No network connection", onoff);
			return result;
		}
		HttpResponse response = null;
		HttpPost post = null;
		try {
			post = new HttpPost(url);
			if (headers != null && headers.size() > 0) {
				Set<Entry<String,String>> entrySet = headers.entrySet();
				for (Entry<String, String> entry : entrySet) {
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(ParamsBuilder
					.buildPostParams(keys, values), NetworkConfig
					.getDefaultCharset());
			post.setEntity(urlEncodedFormEntity);
			response = HttpClientHolder.getInstance().execute(post);
			// 返回码不是200
			errorMsg = "" + response.getStatusLine().getStatusCode();
			if (response.getStatusLine().getStatusCode() != 200) {
				ZplayDebug.e(TAG, response.getStatusLine().getReasonPhrase(), onoff);
				resultCode = ERROR;
				result = new ResultObject(resultCode, errorMsg, null);
				ZplayDebug.v(TAG, "response code："
						+ response.getStatusLine().getStatusCode(), onoff);
				return result;
			}
			// 提交成功&返回数据成功
			resultCode = OK;
			resultStream = getGzipResponseOrNot(response.getEntity().getContent());
			result = new ResultObject(resultCode, errorMsg,
					StreamParser.parseStream(resultStream, charset));
			return result;
		} catch (Exception ex) {
			ZplayDebug.e(TAG, ex.getMessage(), ex, onoff);
			return new ResultObject(ERROR, "504", null);
		} finally {
			if (post != null) {
				post.abort();
			}
			HttpClientHolder.getInstance().getConnectionManager()
					.closeExpiredConnections();
			headers = null;
		}
	}

	/**
	 * post请求网络
	 * 
	 * @param context
	 * @param url
	 * @param value
	 * @return
	 */
	public static ResultObject accessWebByPost(Context context, String url,
			String value, String charset, Map<String, String> headers) {
		// 因为post方式不能输出url，所以这里使用get形式的url进行输出
//		String tempUrl = url;
//		value = value == null ? "" : value;
//		tempUrl += ParamsBuilder.buildGetParams(new String[] { "" },
//				new String[] { value });
//		try {
//			ZplayDebug.V(TAG, "url " +
//					URLDecoder.decode(tempUrl, APPConfig.getDefaultCharset()));
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
		int resultCode = ERROR;
		InputStream resultStream = null;
		String errorMsg = null;
		ResultObject result = null;

		// 没有可用网络连接
		if (!NetworkStatusHandler.isNetWorkAvaliable(context)) {
			resultCode = ERROR;
			errorMsg = "-1";
			result = new ResultObject(resultCode, errorMsg, null);
			ZplayDebug.w(TAG, "No network connection", onoff);
			return result;
		}
		HttpResponse response = null;
		HttpPost post = null;
		try {
			post = new HttpPost(url);
			if (headers != null && headers.size() > 0) {
				Set<Entry<String,String>> entrySet = headers.entrySet();
				for (Entry<String, String> entry : entrySet) {
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}
			post.setEntity(new StringEntity(value, NetworkConfig
					.getDefaultCharset()));
			response = HttpClientHolder.getInstance().execute(post);
			// 返回码不是200
			errorMsg = "" + response.getStatusLine().getStatusCode();
			if (response.getStatusLine().getStatusCode() != 200) {
				ZplayDebug.e(TAG, response.getStatusLine().getReasonPhrase(), onoff);
				resultCode = ERROR;
				result = new ResultObject(resultCode, errorMsg, null);
				ZplayDebug.v(TAG, "response code："
						+ response.getStatusLine().getStatusCode(), onoff);
				return result;
			}
			// 提交成功&返回数据成功
			resultCode = OK;
			resultStream = getGzipResponseOrNot(response.getEntity().getContent());
			result = new ResultObject(resultCode, errorMsg,
					StreamParser.parseStream(resultStream, charset));
			return result;
		} catch (Exception ex) {
			ZplayDebug.e(TAG, ex.getMessage(), ex, onoff);
			return new ResultObject(ERROR, "504", null);
		} finally {
			if (post != null) {
				post.abort();
			}
			HttpClientHolder.getInstance().getConnectionManager()
					.closeExpiredConnections();
			headers = null;
		}
	}

	/**
	 * get方式请求网络
	 * 
	 * @param context
	 * @param url
	 * @param keys
	 * @param values
	 * @return
	 */
	public static ResultObject accessWebByGet(Context context, String url,
			String[] keys, String[] values, String charset, Map<String, String> headers) {

//		ZplayDebug.V(TAG, "request by get ");

		// 因为post方式不能输出url，所以这里使用get形式的url进行输出
		String tempUrl = url;
		tempUrl += ParamsBuilder.buildGetParams(keys, values);
//		try {
//			ZplayDebug.V(TAG, "url " +
//					URLDecoder.decode(tempUrl, APPConfig.getDefaultCharset()));
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}

		int resultCode = ERROR;
		InputStream resultStream = null;
		String errorMsg = null;
		ResultObject result = null;

		// 没有可用网络连接
		if (!NetworkStatusHandler.isNetWorkAvaliable(context)) {
			resultCode = ERROR;
			errorMsg = "-1";
			result = new ResultObject(resultCode, errorMsg, null);
			ZplayDebug.w(TAG, "No network connection", onoff);
			return result;
		}
		HttpResponse response = null;
		HttpGet get = null;
		try {
			 get = new HttpGet(tempUrl);
			if (headers != null && headers.size() > 0) {
				Set<Entry<String,String>> entrySet = headers.entrySet();
				for (Entry<String, String> entry : entrySet) {
					get.setHeader(entry.getKey(), entry.getValue());
				}
			}
			response = HttpClientHolder.getInstance().execute(get);
			// 提交成功&返回数据成功
			resultCode = OK;
			resultStream = getGzipResponseOrNot(response.getEntity().getContent());
			// 返回码不是200
			errorMsg = response.getStatusLine().getStatusCode() + "";
			if (response.getStatusLine().getStatusCode() != 200) {
				ZplayDebug.e(TAG, response.getStatusLine().getReasonPhrase(), onoff);
				resultCode = ERROR;
				result = new ResultObject(resultCode, errorMsg, StreamParser.parseStream(resultStream, charset));
				ZplayDebug.v(TAG, "response code："
						+ response.getStatusLine().getStatusCode(), onoff);
				return result;
			}
			result = new ResultObject(resultCode, errorMsg,
					StreamParser.parseStream(resultStream, charset));
			return result;
		} catch (Exception ex) {
			ZplayDebug.e(TAG, ex.getMessage(), ex, onoff);
			return new ResultObject(ERROR, "504", null);
		} finally {
			if (get != null) {
				get.abort();
			}
			HttpClientHolder.getInstance().getConnectionManager()
					.closeExpiredConnections();
			headers = null;
		}
	}

	private static InputStream getGzipResponseOrNot(InputStream is){
		try {
         BufferedInputStream bis = new BufferedInputStream(is);  
         bis.mark(2);  
         // 取前两个字节  
         byte[] header = new byte[2];  
         int result;
			result = bis.read(header);
			bis.reset();  
			int headerData = getShort(header);  
			// Gzip 流 的前两个字节是 0x1f8b  
			if (result != -1 && headerData == 0x1f8b) {   
				ZplayDebug.i(TAG, "response is by gzip", onoff);
				return new GZIPInputStream(bis);  
			} else {
				return bis;
			}
		} catch (IOException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}  
		return null;
	}
	
	private static int getShort(byte[] data) {  
        return (int)((data[0]<<8) | data[1]&0xFF);  
    } 
	
	
	
	
	/**
	 * 保存着请求网络以后返回的请求：1、状态，2、成功以后返回的流
	 * 
	 * @author laohuai
	 * 
	 */
	public static class ResultObject {
		private int status;
		private String data;
		private String errorMsg;

		public ResultObject(int status, String errorMsg, String data) {
			this.status = status;
			this.data = data;
			this.errorMsg = errorMsg;

		}

		public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}
	
}
