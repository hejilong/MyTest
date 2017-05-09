package com.yumi.android.sdk.ads.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Environment;
import android.util.Log;

public class ZplayDebug
{
    private static final String TAG = "YumiMobiAds";
    private static final String FORMAT = "[class] : %s , [msg] : %s";
    private static final String LEVELLOCFILE = "//mnt//sdcard//29b2e3aa7596f75d0fda1f1f56183907";
    private static boolean isSave = true;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT2 = "yyyy_MM_dd_HH";
    private static ExecutorService log_pool = null;

    private static final int MAX_VALUE = 500;
    
    public static void setSave(boolean isSave)
    {
        ZplayDebug.isSave = isSave;
    }

    private static String buildLogMsg(String tag, String msg)
    {
        String log = String.format(FORMAT, tag, msg);
        return log;
    }

    public static void w(String tag, String msg, boolean onoff)
    {
    	if (onoff)
		{
    		DebugLevel level = getDebugLevel();
    		if (level == DebugLevel.LEVEL_TECH)
    		{
    			try
    			{
    				Log.w(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
    			} catch (Exception e)
    			{
    				Log.w(TAG, buildLogMsg(tag, msg));
    			}
    			saveLog(tag, msg);
    		}
		}
    }

    public static void e(String tag, String msg, boolean onoff)
    {
    	if (onoff)
		{
    		DebugLevel level = getDebugLevel();
    		if (level == DebugLevel.LEVEL_TECH)
    		{
    			Log.e(TAG, buildLogMsg(tag, msg));
    			saveLog(tag, msg);
    		}
		}
    }
    
    public static void e(String tag, String msg, Throwable tr, boolean onoff)
    {
    	if (onoff)
		{
    		DebugLevel level = getDebugLevel();
    		if (level == DebugLevel.LEVEL_TECH)
    		{
    			Log.e(TAG, buildLogMsg(tag, msg+tr.getMessage()));
    			saveLog(tag, msg+"\n"+Log.getStackTraceString(tr));
    		}
		}
    }

    public static void d(String tag, String msg, boolean onoff)
    {
    	if (onoff)
		{
    		DebugLevel level = getDebugLevel();
    		if (level == DebugLevel.LEVEL_DEBUG || level == DebugLevel.LEVEL_TECH)
    		{
    			try
    			{
    				Log.d(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
    			} catch (Exception e)
    			{
    				Log.d(TAG, buildLogMsg(tag, msg));
    			}
    			saveLog(tag, msg);
    		}
		}
    }

    public static void v(String tag, String msg, boolean onoff)
    {
        if (onoff)
		{
        	DebugLevel level = getDebugLevel();
            if (level == DebugLevel.LEVEL_TECH)
            {
            	try
    			{
                	Log.v(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
    			} catch (Exception e)
    			{
    				Log.v(TAG, buildLogMsg(tag, msg));
    			}
                saveLog(tag, msg);
            }
		}
    }
    
    public static void i(String tag, String msg, boolean onoff)
    {
        if (onoff)
		{
        	DebugLevel level = getDebugLevel();
            if (level == DebugLevel.LEVEL_TECH)
            {
            	try
    			{
                	Log.i(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
    			} catch (Exception e)
    			{
    				Log.i(TAG, buildLogMsg(tag, msg));
    			}
                saveLog(tag, msg);
            }
		}
    }

    private static DebugLevel getDebugLevel()
    {
        DebugLevel level = DebugLevel.LEVEL_PUBLISHER;
        FileInputStream fis = null;
        StringBuffer sb = new StringBuffer("");
        try
        {
            File file = new File(LEVELLOCFILE);
            fis = new FileInputStream(file);
            int c;
            while ((c = fis.read()) != -1)
            {
                sb.append((char) c);
            }
        } catch (FileNotFoundException e)
        {
        } catch (IOException e)
        {
        } finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                }
            } catch (Exception e2)
            {
            }
        }
        if (isNotNull(sb.toString()))
        {
            String trim = sb.toString().trim();
            if (trim.equals("debug"))
            {
                return DebugLevel.LEVEL_DEBUG;
            }
            if (trim.equals("tech"))
            {
                return DebugLevel.LEVEL_TECH;
            }
        }
        return level;
    }

    private static enum DebugLevel
    {
        LEVEL_PUBLISHER, LEVEL_DEBUG, LEVEL_TECH
    }

    public static final boolean isNotNull(String str)
    {
        if (str != null && str.length() > 0)
        {
            return true;
        }
        return false;
    }

    private static SimpleDateFormat format = null;
    private static SimpleDateFormat format_name = null;
    
    private static SimpleDateFormat getFormat()
	{
		if (format == null)
		{
			format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
		}
		return format;
	}
    
	private static SimpleDateFormat getFormat_name()
	{
		if (format_name == null)
		{
			format_name = new SimpleDateFormat(DATE_FORMAT2, Locale.getDefault());
		}
		return format_name;
	}
	
    
    private static void saveLog(final String tag, final String msg)
    {
        if (isSave)
        {
            if (log_pool == null)
            {
                log_pool = Executors.newFixedThreadPool(1);
            }
            Runnable run = new Runnable()
            {
                public void run()
                {
                	String sdcard = Environment.getExternalStorageDirectory().getPath();
                    File dir = new File(sdcard + "/.zplayads/log/");
                    if (!dir.exists())
                    {
                        dir.mkdirs();
                    }
                    
                    File file = new File(dir, getFormat_name().format(new Date())+".log");
                    FileOutputStream fos = null;
                    try
                    {
                        fos = new FileOutputStream(file,true);
                        String date = getFormat().format(new Date());
                        String log = "[" + date + "] " + "TAG:" + tag + " msg:" + msg + "\r\n";
                        byte[] b = log.getBytes("utf-8");
                        fos.write(b);
                        fos.flush();
                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (fos != null)
                        {
                            try
                            {
                                fos.close();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            log_pool.execute(run);
        }
    }

}