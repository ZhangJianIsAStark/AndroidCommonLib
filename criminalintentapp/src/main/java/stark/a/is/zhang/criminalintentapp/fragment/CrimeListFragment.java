package stark.a.is.zhang.criminalintentapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCrimeIdPosition = new HashMap<>();

        updateUI();

        return v;
    }

    private void updateUI() {
        if (mAdapter == null) {
            CrimeLab crimeLab = CrimeLab.get(getActivity().getApplicationContext());
            mAdapter = new CrimeAdapter(crimeLab.getCrimes());
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder {
        private Crime mCrime;
        private int mPosition;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCrimeCheckBox;

        public CrimeHolder(View itemView) {
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
        }

        public void bindCrime(Crime crime, int position) {
            mCrime = crime;
            mPosition = position;

            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDateString());
            mSolvedCrimeCheckBox.setChecked(crime.isSolved());
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (REQUEST_CRIME == requestCode) {
            UUID crimeId = CrimePagerActivity.getCrimeIdFromIntent(data);
            if (mCrimeIdPosition.containsKey(crimeId)) {
                mAdapter.notifyItemChanged(mCrimeIdPosition.get(crimeId));
                mCrimeIdPosition.remove(crimeId);
            }
        }
    }
}