package stark.a.is.zhang.beatbox.model;

public class Sound {
    private String mAssetPath;
    private String mName;
    private Integer mSoundId;
    private boolean mBeLoaded;

    public Sound(String assetPath) {
        mAssetPath = assetPath;

        String[] components = assetPath.split("/");
        String fileName = components[components.length-1];
        mName = fileName.replace(".wav", "");
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public String getName() {
        return mName;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }

    public boolean isBeLoaded() {
        return mBeLoaded;
    }

    public void setBeLoaded(boolean beLoaded) {
        mBeLoaded = beLoaded;
    }
}


