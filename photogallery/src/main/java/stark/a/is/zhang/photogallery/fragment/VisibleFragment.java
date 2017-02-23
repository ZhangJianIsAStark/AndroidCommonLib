package stark.a.is.zhang.photogallery.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import stark.a.is.zhang.photogallery.model.EventBusData;

public class VisibleFragment extends Fragment {
    EventBus mEventBus;

    @Override
    public void onStart() {
        super.onStart();

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    @Subscribe
    public void onEventMainThread(EventBusData data) {
        Log.d("ZJTest", "onEventMainThread");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.cancel(data.getId());

        forceUpdate();
    }

    protected void forceUpdate() {}
}