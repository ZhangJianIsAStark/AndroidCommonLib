package stark.a.is.zhang.photogallery.service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Random;

import stark.a.is.zhang.photogallery.R;
import stark.a.is.zhang.photogallery.activity.PhotoGalleryActivity;
import stark.a.is.zhang.photogallery.tool.QueryPreference;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobPollService extends JobService{
    private static String[] RANDOM_WORD = new String[] {
            "大炮", "坦克", "武器", "航母", "飞船", "UFO"
    };

    public static final String ACTION_SHOW_NOTIFICATION =
            "stark.a.is.zhang.photogallery.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE = "stark.a.is.zhang.photogallery.Private";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    @Override
    public boolean onStartJob(JobParameters params) {
        Random r = new Random();
        int wordNum = r.nextInt(RANDOM_WORD.length);

        String lastQuery = QueryPreference.getStoredQuery(this);

        if (!RANDOM_WORD[wordNum].equals(lastQuery)) {
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

        jobFinished(params, false);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);

        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK,null,null);
    }
}
