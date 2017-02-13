package stark.a.is.zhang.nerdlauncher;

import android.support.v4.app.Fragment;

import stark.a.is.zhang.activity.SingleFragmentActivity;

public class NerdLauncherActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }
}
