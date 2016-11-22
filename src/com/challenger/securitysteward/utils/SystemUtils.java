package com.challenger.securitysteward.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;

public class SystemUtils {


    @SuppressWarnings("deprecation")
	private static List<ActivityManager.RunningTaskInfo> getTasks(Context context) {
    	ActivityManager mActivityManager  = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(50);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks;
        }
 
        return null;
    }
    
    public static boolean isActivityAlive(String packageName, String activityName, Context context) {
    	List<ActivityManager.RunningTaskInfo> tasks = getTasks(context);
        if (tasks != null) {
        	for(ActivityManager.RunningTaskInfo act : tasks) {
        		//Log.d("TASK",act.baseActivity.getPackageName() + " " + act.baseActivity.getClassName());
        		ComponentName topActivity = act.baseActivity;
        		if (topActivity.getPackageName().equals(packageName) &&
        				topActivity.getShortClassName().equals("." + activityName)) {
        			return true;
        		}
            }
        }

        return false;
    }
    /** 
     * 判断某个服务是否正在运行的方法 
     *  
     * @param mContext 
     * @param serviceName 
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService） 
     * @return true代表正在运行，false代表服务没有正在运行 
     */  
    public static boolean isServiceWork(Context mContext, String serviceName) {  
        boolean isWork = false;  
        ActivityManager myAM = (ActivityManager) mContext  
                .getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
        if (myList.size() <= 0) {  
            return false;  
        }  
        for (int i = 0; i < myList.size(); i++) {  
            String mName = myList.get(i).service.getClassName().toString();  
            if (mName.equals(serviceName)) {  
                isWork = true;  
                break;  
            }  
        }  
        return isWork;  
    }
    
    public static String getMD5String(byte[] bytes) {
    	MessageDigest md5_alg;
		try {
			md5_alg = MessageDigest.getInstance("MD5");
			md5_alg.update(bytes);
			byte[] res = md5_alg.digest();
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < res.length; i++) {
				if (Integer.toHexString(0xff & res[i]).length() == 1) {
					strBuf.append("0").append(Integer.toHexString(0xff & res[i]));
				} else {
					strBuf.append(Integer.toHexString(0xff & res[i]));
				}
			}
			
			String md5 = strBuf.toString();
			return md5;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
    }
}
