package stark.a.is.zhang.photogallery.tool;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageCache {
    private LruCache<String, Bitmap> mImageCache;

    ImageCache() {
        //get KB
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 4;

        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public Bitmap getBitmap(String url) {
        return mImageCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        mImageCache.put(url, bitmap);
    }
}
