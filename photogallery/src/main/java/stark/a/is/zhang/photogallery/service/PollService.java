package stark.a.is.zhang.photogallery.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

import stark.a.is.zhang.photogallery.R;
import stark.a.is.zhang.photogallery.activity.PhotoGalleryActivity;
import stark.a.is.zhang.photogallery.tool.QueryPreference;

public class PollService extends IntentService {
    private static final String TAG = "PollService";

    private static final long POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    private static String[] RANDOM_WORD = new String[] {
            "极简", "汽车", "飞机", "清新", "风景", "星系"
    };

    public static final String ACTION_SHOW_NOTIFICATION =
            "stark.a.is.zhang.photogallery.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE = "stark.a.is.zhang.photogallery.Private";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        Random r = new Random();
        int wordNum = r.nextInt(RANDOM_WORD.length);

        String lastQuery = QueryPreference.getStoredQuery(this);

        if (RANDOM_WORD[wordNum].equals(lastQuery)) {
            return;
        }

        Log.d(TAG, "wordNum: " + wordNum);

        QueryPreference.setStoredQuery(this, RANDOM_WORD[wordNum]);

        Resources res = getResources();
        Intent i = PhotoGalleryActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(res.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(res.getString(R.string.new_pictures_title))
                .setContentText(res.getString(R.string.new_pictures_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        showBackgroundNotification(0, notification);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return (cm.getActiveNetworkInfo() != null)
                && (cm.getActiveNetworkInfo().isConnected());
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        QueryPreference.setAlarmOn(context, isOn);
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);

        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK,null,null);
    }
}
