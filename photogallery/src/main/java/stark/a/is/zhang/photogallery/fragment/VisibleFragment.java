package stark.a.is.zhang.photogallery.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;

import stark.a.is.zhang.photogallery.service.JobPollService;
import stark.a.is.zhang.photogallery.service.PollService;

public class VisibleFragment extends Fragment {
    Handler mMainHandler = new Handler();

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter;
        if (Build.VERSION.SDK_INT >= 21) {
            filter = new IntentFilter(JobPollService.ACTION_SHOW_NOTIFICATION);
            getActivity().registerReceiver(
                    mOnShowNotification, filter, JobPollService.PERM_PRIVATE, null);
        } else {
            filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
            getActivity().registerReceiver(
                    mOnShowNotification, filter, PollService.PERM_PRIVATE, null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    forceUpdate();
                }
            });

            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    protected void forceUpdate() {}
}