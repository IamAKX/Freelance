package Utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by akash on 19/4/17.
 */

public class CheckServiceStatus {
    private Context context;

    public CheckServiceStatus(Context context) {
        this.context = context;
    }

    public boolean isRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(serviceClass.getName().equals(serviceInfo.service.getClassName()))
                return true;
        }
        return false;
    }
}
