package stark.a.is.zhang.draganddraw.activity;

import android.support.v4.app.Fragment;

import stark.a.is.zhang.activity.SingleFragmentActivity;
import stark.a.is.zhang.draganddraw.fragment.DragAndDrawFragment;

public class DragAndDrawActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return DragAndDrawFragment.newInstance();
    }
}
