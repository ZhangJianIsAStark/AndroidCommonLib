package stark.a.is.zhang.criminalintentapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import stark.a.is.zhang.activity.SingleFragmentActivity;
import stark.a.is.zhang.criminalintentapp.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
