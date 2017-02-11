package stark.a.is.zhang.criminalintentapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;
import java.util.UUID;

import stark.a.is.zhang.activity.BaseActivity;
import stark.a.is.zhang.criminalintentapp.R;
import stark.a.is.zhang.criminalintentapp.data.Crime;
import stark.a.is.zhang.criminalintentapp.data.CrimeLab;
import stark.a.is.zhang.criminalintentapp.fragment.CrimeFragment;

public class CrimePagerActivity extends BaseActivity
        implements CrimeFragment.Callbacks {
    private static final String EXTRA_CRIME_ID =
            "stark.a.is.zhang.criminalintentapp.crime_id";

    private List<Crime> mCrimes;

    @Override
    protected void initVariables() {
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_crime_pager);

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        mCrimes = CrimeLab.get(this.getApplicationContext()).getCrimes();
        FragmentManager fm = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        for (int i = 0; i < mCrimes.size(); ++i) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    protected void prepareData() {
    }

    public static Intent newStartIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    public void onCrimeUpdated() {
    }
}