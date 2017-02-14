package stark.a.is.zhang.photogallery;

import android.support.v4.app.Fragment;
import stark.a.is.zhang.activity.SingleFragmentActivity;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
