package stark.a.is.zhang.activity;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends BaseActivity {
    @Override
    protected void initVariables(){
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(getFragmentId());

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(getFragmentId(), fragment)
                    .commit();
        }
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    protected int getFragmentId() {
        return R.id.fragment_container;
    }

    @Override
    protected void prepareData() {
    }

    protected abstract Fragment createFragment();
}
