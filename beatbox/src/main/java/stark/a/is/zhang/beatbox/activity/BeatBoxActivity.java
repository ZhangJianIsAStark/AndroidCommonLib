package stark.a.is.zhang.beatbox.activity;

import android.support.v4.app.Fragment;

import stark.a.is.zhang.activity.SingleFragmentActivity;
import stark.a.is.zhang.beatbox.fragment.BeatBoxFragment;

public class BeatBoxActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return BeatBoxFragment.newInstance();
    }
}