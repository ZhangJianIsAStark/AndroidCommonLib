package stark.a.is.zhang.criminalintentapp.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import stark.a.is.zhang.criminalintentapp.R;
import stark.a.is.zhang.utils.PictureUtil;

public class CrimePhotoFragment extends AppCompatDialogFragment{
    private static String FILE_PATH = "filePath";

    public static CrimePhotoFragment newInstance(String filePath) {
        Bundle args = new Bundle();
        args.putSerializable(FILE_PATH, filePath);

        CrimePhotoFragment crimePhotoFragment = new CrimePhotoFragment();
        crimePhotoFragment.setArguments(args);
        return crimePhotoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style,theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String filePath = (String)getArguments().getSerializable(FILE_PATH);

        View v = inflater.inflate(R.layout.dialog_crime_picture, null);

        ImageView imageView = (ImageView) v.findViewById(R.id.crime_big_photo);

        Bitmap bitmap = PictureUtil.getScaledBitmap(filePath, getActivity());
        imageView.setImageBitmap(bitmap);

        return v;
    }
}
