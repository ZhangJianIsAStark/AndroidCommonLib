package stark.a.is.zhang.photogallery.activity;

import android.support.v4.app.Fragment;
import stark.a.is.zhang.activity.SingleFragmentActivity;
import stark.a.is.zhang.photogallery.fragment.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
