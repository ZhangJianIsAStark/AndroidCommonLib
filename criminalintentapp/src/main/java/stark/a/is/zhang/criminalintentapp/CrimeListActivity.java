package stark.a.is.zhang.criminalintentapp;

import android.support.v4.app.Fragment;

import stark.a.is.zhang.activity.SingleFragmentActivity;

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
