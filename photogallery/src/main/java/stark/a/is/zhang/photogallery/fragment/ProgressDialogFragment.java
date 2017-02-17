package stark.a.is.zhang.photogallery.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        return new ProgressDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage("Loading Pictures")
                .show();
    }
}
