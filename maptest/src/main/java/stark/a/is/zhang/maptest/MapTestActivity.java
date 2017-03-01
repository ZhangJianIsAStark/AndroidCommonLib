package stark.a.is.zhang.maptest;

import android.support.v4.app.Fragment;

import com.baidu.mapapi.SDKInitializer;

import stark.a.is.zhang.activity.SingleFragmentActivity;

public class MapTestActivity extends SingleFragmentActivity {
    @Override
    protected void initVariables(){
        SDKInitializer.initialize(getApplicationContext());
    }

    @Override
    protected Fragment createFragment() {
        return MapTestFragment.newInstance();
    }
}
