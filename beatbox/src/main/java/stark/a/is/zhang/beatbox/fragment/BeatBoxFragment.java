package stark.a.is.zhang.beatbox.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import stark.a.is.zhang.beatbox.R;
import stark.a.is.zhang.beatbox.model.BeatBox;
import stark.a.is.zhang.beatbox.model.Sound;

public class BeatBoxFragment extends Fragment{
    private BeatBox mBeatBox;

    private Handler mMainHandler;

    private HandlerThread mHandlerThread;

    private SoundAdapter mSoundAdapter;

    private class LocalHandler extends Handler {
        final static int PRELOAD_SOUND = 0;

        LocalHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PRELOAD_SOUND:
                    try {
                        mBeatBox.soundPoolPreload();
                        mMainHandler.sendEmptyMessage(MainHandler.SOUND_LOADED);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private static class MainHandler extends Handler {
        final static int SOUND_LOADED = 0;

        private WeakReference<Fragment> mRefs;

        MainHandler(Fragment fragment) {
            mRefs = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SOUND_LOADED:
                    if (((BeatBoxFragment) mRefs.get()).mSoundAdapter != null) {
                        ((BeatBoxFragment) mRefs.get()).mSoundAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mBeatBox = new BeatBox(getActivity());

        mMainHandler = new MainHandler(this);

        mHandlerThread = new HandlerThread("BeatBoxFragment");
        mHandlerThread.start();
        LocalHandler loadHandler = new LocalHandler(mHandlerThread.getLooper());

        loadHandler.sendEmptyMessage(LocalHandler.PRELOAD_SOUND);
    }

    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beat_box, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(
                R.id.fragment_beat_box_recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mSoundAdapter = new SoundAdapter(mBeatBox.getSounds());
        recyclerView.setAdapter(mSoundAdapter);

        return view;
    }

    private class SoundHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private Button mButton;
        private Sound mSound;

        SoundHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.list_item_sound, container, false));

            mButton = (Button)itemView.findViewById(R.id.list_item_sound_button);
            mButton.setOnClickListener(this);
        }

        void bindSound(Sound sound) {
            mSound = sound;
            mButton.setText(mSound.getName());
            mButton.setEnabled(mSound.isBeLoaded());
        }

        @Override
        public void onClick(View v) {
            mBeatBox.play(mSound);
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {
        private List<Sound> mSounds;

        SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SoundHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            Sound sound = mSounds.get(position);
            holder.bindSound(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();

        if (Build.VERSION.SDK_INT > 18) {
            mHandlerThread.quitSafely();
        } else {
            mHandlerThread.quit();
        }
        mHandlerThread = null;
    }
}