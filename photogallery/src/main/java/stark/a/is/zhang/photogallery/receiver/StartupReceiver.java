package stark.a.is.zhang.photogallery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import stark.a.is.zhang.photogallery.service.PollService;
import stark.a.is.zhang.photogallery.tool.QueryPreference;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT < 21) {
            boolean isOn = QueryPreference.isAlarmOn(context);
            PollService.setServiceAlarm(context, isOn);
        }
    }
}