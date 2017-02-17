package stark.a.is.zhang.photogallery.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import stark.a.is.zhang.utils.HttpUtil;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    private Handler mResponseHandler;
    private ImageCache mImageCache;

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mImageCache = new ImageCache();
    }

    private Handler mRequestHandler;

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new RequestHandler(this);
    }

    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;

    private static class RequestHandler extends Handler {
        private WeakReference<Thread> mThread;

        RequestHandler(Thread thread) {
            mThread = new WeakReference<>(thread);
        }

        @Override
        public void handleMessage(Message msg) {
            ThumbnailDownloader downloader = (ThumbnailDownloader) mThread.get();

            if (msg.what == MESSAGE_DOWNLOAD) {
                downloader.handleDownload(msg.obj);
            } else if (msg.what == MESSAGE_PRELOAD) {
                downloader.handlePreload((String)msg.obj);
            }
        }
    }

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail, int targetId);
    }

    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Integer> mUrlIdMap = new ConcurrentHashMap<>();

    public void queueThumbnail(T target, String url, int targetId) {
        if (url != null) {
            mRequestMap.put(target, url);
            mUrlIdMap.put(url, targetId);

            mRequestHandler.obtainMessage(
                    MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    public void queueThumbnail(String url) {
        if (url != null) {
            mRequestHandler.obtainMessage(MESSAGE_PRELOAD, url).sendToTarget();
        }
    }

    private Boolean mHasQuit = false;

    private void handleDownload(final T target) {
        try {
            final String url = mRequestMap.get(target);

            if (url == null) {
                Log.d(TAG, "url empty");
                return;
            }

            final Bitmap bitmap = downloadBitmap(url);

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mHasQuit || !url.equals(mRequestMap.get(target))) {
                        return;
                    }

                    int tagId = mUrlIdMap.get(url);

                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap, tagId);
                }
            });
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void handlePreload(String url) {
        try {
            downloadBitmap(url);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private Bitmap downloadBitmap(String url) throws IOException {
        Bitmap rst = mImageCache.getBitmap(url);

        if (rst == null) {
            byte[] bitmapBytes = HttpUtil.getUrlBytes(url);
            rst = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            mImageCache.putBitmap(url, rst);
        }

        return rst;
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
}