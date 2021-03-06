package stark.a.is.zhang.photogallery.model;

import android.net.Uri;

public class GalleryItem {
    private String mObjURL;
    private String mFromURLHost;
    private String mThumbURL;

    public String getObjURL() {
        return mObjURL;
    }

    public void setObjURL(String objURL) {
        mObjURL = objURL;
    }

    public String getFromURLHost() {
        return mFromURLHost;
    }

    public void setFromURLHost(String fromURL) {
        mFromURLHost = fromURL;
    }

    public String getThumbURL() {
        return mThumbURL;
    }

    public void setThumbURL(String thumbURL) {
        mThumbURL = thumbURL;
    }
}