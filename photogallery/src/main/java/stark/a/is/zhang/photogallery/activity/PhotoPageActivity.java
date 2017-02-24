package stark.a.is.zhang.photogallery.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import stark.a.is.zhang.activity.SingleFragmentActivity;
import stark.a.is.zhang.photogallery.fragment.PhotoPageFragment;

public class PhotoPageActivity extends SingleFragmentActivity {
    Fragment mFragment;

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        WebView webView = ((PhotoPageFragment)mFragment).getWebView();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
