package com.yumi.android.sdk.ads.publish.enumbean;

public enum LayerErrorCode {

	/**
	 *  <p> Code failed.
	 */
	CODE_FAILED("0", "failed"),
	
	/**
	 *  <p> Code success.
	 */
	CODE_SUCCESS("1", "success"),
	
	/**
	 *<p> The error of SDK internal.
	 */
	ERROR_INTERNAL("3", "error_internal"),
	
	/**
	 * <p> The error of error network . Maybe you need VPN or something of kind.
	 */
	ERROR_NETWORK_ERROR("4", "error_network"),
	
	/**
	 * <p> The request success but there is no ad return.
	 */
	ERROR_NO_FILL("5", "error_nofill"),
	
	/**
	 * <p> Invalid request.
	 */
	ERROR_INVALID("6", "error_invalid"),
	
	/**
	 *  <p> Invalid network.
	 */
	ERROR_INVALID_NETWORK("7" ,"no_network"),
	
	/**
	 *  <p> Request has non response over limit time.
	 */
	ERROR_NON_RESPONSE("8", "no_callback_response"),
	
	/**
	 *  <p> Progressive failures times reach the limit.
	 */
	ERROR_OVER_RETRY_LIMIT("9", "reach max retry times"),
	
	/**
	 *  <p> Incentive media get reward reach the limit times per day.
	 */
	ERROR_OVER_INCENTIVED_LIMIT("10", "reach max incentived_times");
	
	
	private String code ;
	private String msg;
	private LayerErrorCode(String code, String msg){
		this.code = code;
		this.msg = msg;
	}
	
	/**
	 *  Get the ERROR code String.
	 * @return code String
	 */
	public String getCode(){
		return code;
	}
	
	/**
	 *  Get the ERROR message 
	 * @return error message.
	 */
	public String getMsg(){
		return msg;
	}
}

