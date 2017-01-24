package stark.a.is.zhang.criminalintentapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import stark.a.is.zhang.criminalintentapp.activity.CrimePagerActivity;
import stark.a.is.zhang.criminalintentapp.R;
import stark.a.is.zhang.criminalintentapp.data.Crime;
import stark.a.is.zhang.criminalintentapp.data.CrimeLab;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    private static final int REQUEST_CRIME = 1;
    private HashMap<UUID, Integer> mCrimeIdPosition;

    private static final int ADD_CRIME = 2;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private boolean mSubtitleVisible;

    private SharedPreferences mSharedPref;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCrimeIdPosition = new HashMap<>();

        mSubtitleVisible = mSharedPref.getBoolean(SAVED_SUBTITLE_VISIBLE, false);

        updateUI();

        return v;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity().getApplicationContext());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSharedPref.edit().putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible).apply();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder {
        private Crime mCrime;
        private int mPosition;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCrimeCheckBox;

        CrimeHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = CrimePagerActivity.newStartIntent(getActivity(), mCrime.getId());
                    mCrimeIdPosition.put(mCrime.getId(), mPosition);

                    startActivityForResult(intent, REQUEST_CRIME);
                }
            });

            mTitleTextView = (TextView) itemView.findViewById(
                    R.id.list_item_crime_title_text_view);

            mDateTextView = (TextView) itemView.findViewById(
                    R.id.list_item_crime_date_text_view);

            mSolvedCrimeCheckBox = (CheckBox) itemView.findViewById(
                    R.id.list_item_crime_solved_check_box);
            mSolvedCrimeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mCrime.setSolved(b);
                    CrimeLab.get(getActivity().getApplicationContext()).updateCrime(mCrime);
                }
            });
        }

        void bindCrime(Crime crime, int position) {
            mCrime = crime;
            mPosition = position;

            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDateString());
            mSolvedCrimeCheckBox.setChecked(crime.isSolved());
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            holder.bindCrime(mCrimes.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (REQUEST_CRIME == requestCode) {
            UUID crimeId = CrimePagerActivity.getCrimeIdFromIntent(data);
            if (crimeId == null) {
                updateUI();
            } else if (mCrimeIdPosition.containsKey(crimeId)) {
                mAdapter.notifyItemChanged(mCrimeIdPosition.get(crimeId));
                mCrimeIdPosition.remove(crimeId);
                updateSubtitle();
            }
        } else if (ADD_CRIME == requestCode) {
            updateUI();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity().getApplicationContext()).addCrime(crime);
                Intent intent = CrimePagerActivity.newStartIntent(getActivity(), crime.getId());
                startActivityForResult(intent, ADD_CRIME);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity().getApplicationContext());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(
                R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setSubtitle(subtitle);
        }
    }
}