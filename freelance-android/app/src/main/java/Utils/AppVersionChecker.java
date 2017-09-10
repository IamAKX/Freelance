package Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by akash on 25/4/17.
 */

public class AppVersionChecker {

    Context context;
    public String PACKAGE_NAME, VERSION_NAME, VERSION_CODE;

    public AppVersionChecker(Context context) {
        this.context = context;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            VERSION_NAME = packageInfo.versionName;
            VERSION_CODE = String.valueOf(packageInfo.versionCode);
            PACKAGE_NAME = packageInfo.packageName;

            Log.e("checking",VERSION_NAME+"\n"+VERSION_CODE+"\n"+PACKAGE_NAME);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
