package stark.a.is.zhang.sunset;

import android.support.v4.app.Fragment;

import stark.a.is.zhang.activity.SingleFragmentActivity;

public class SunsetActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return SunSetFragment.newInstance();
    }
}
