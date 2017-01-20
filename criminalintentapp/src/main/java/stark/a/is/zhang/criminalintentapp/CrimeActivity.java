package stark.a.is.zhang.criminalintentapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

import stark.a.is.zhang.activity.SingleFragmentActivity;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "stark.a.is.zhang.criminalintentapp.crime_id";

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }

    public static Intent newStartIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    public static Intent newReturnIntent(UUID crimeId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    public static UUID getCrimeIdFromIntent(Intent intent) {
        return (UUID)intent.getSerializableExtra(EXTRA_CRIME_ID);
    }
}
