package stark.a.is.zhang.criminalintentapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import stark.a.is.zhang.activity.SingleFragmentActivity;
import stark.a.is.zhang.criminalintentapp.R;
import stark.a.is.zhang.criminalintentapp.data.Crime;
import stark.a.is.zhang.criminalintentapp.fragment.CrimeFragment;
import stark.a.is.zhang.criminalintentapp.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) != null) {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, newDetail)
                    .commit();
        } else {
            Intent intent = CrimePagerActivity.newStartIntent(this, crime.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onCrimeUpdated() {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
