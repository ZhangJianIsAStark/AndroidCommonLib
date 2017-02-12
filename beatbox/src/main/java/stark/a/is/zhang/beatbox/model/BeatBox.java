package stark.a.is.zhang.beatbox.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBoxTest";

    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private AssetManager mAssetManager;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        mAssetManager = context.getAssets();

        if (Build.VERSION.SDK_INT > 21) {
            SoundPool. Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(MAX_SOUNDS);

            AudioAttributes.Builder attrBuild = new AudioAttributes.Builder();
            attrBuild.setLegacyStreamType(AudioManager.STREAM_MUSIC);

            builder.setAudioAttributes(attrBuild.build());

            mSoundPool = builder.build();
        } else {
            mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }

        loadSounds();
    }

    private void loadSounds() {
        String[] soundNames;

        try {
            soundNames = mAssetManager.list(SOUNDS_FOLDER);

            if (soundNames != null) {
                for (String filename : soundNames) {
                    Sound sound = new Sound(SOUNDS_FOLDER + "/" + filename);
                    mSounds.add(sound);
                }
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Could not load sounds", ioe);
        }
    }

    public void soundPoolPreload() throws IOException {
        for (Sound sound : mSounds) {
            AssetFileDescriptor afd = mAssetManager.openFd(sound.getAssetPath());

            int soundId = mSoundPool.load(afd, 1);
            sound.setSoundId(soundId);

            sound.setBeLoaded(true);
        }
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }

        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release() {
        mSoundPool.release();
    }
}
