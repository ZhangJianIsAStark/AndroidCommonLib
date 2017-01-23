package stark.a.is.zhang.criminalintentapp.activity;

import android.support.v4.app.Fragment;

import stark.a.is.zhang.activity.SingleFragmentActivity;
import stark.a.is.zhang.criminalintentapp.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
