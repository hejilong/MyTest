package com.yumi.android.sdk.ads.publish;

public class YumiCheckPermission {

    private static boolean CheckPermission = false;
    
//  public static void runInDebugMode(){
//      YumiDebug.DEBUG = true;
//  }
    
    public static void runInCheckPermission(boolean flag){
        YumiCheckPermission.CheckPermission = flag;
    }
    
    public static boolean isCheckPermission(){
        return CheckPermission;
    }
}
