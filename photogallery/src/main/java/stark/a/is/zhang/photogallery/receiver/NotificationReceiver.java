package stark.a.is.zhang.photogallery.receiver;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import stark.a.is.zhang.photogallery.service.JobPollService;
import stark.a.is.zhang.photogallery.service.PollService;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() != Activity.RESULT_OK) {
            return;
        }

        int requestCode;
        Notification notification;
        if (Build.VERSION.SDK_INT >= 21) {
            requestCode = intent.getIntExtra(JobPollService.REQUEST_CODE, 0);
            notification = intent.getParcelableExtra(JobPollService.NOTIFICATION);
        } else {
            requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
            notification = intent.getParcelableExtra(PollService.NOTIFICATION);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode, notification);
    }
}
