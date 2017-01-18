/**
 * Base Activity of all other activity in application
 *
 * The target of this activity is to classify the work in onCreate into three kinds:
 * 1. initialize the variables in activity
 * 2. initialize all the views in activity
 * 3. prepare data that needed by the activity
 **/

package stark.a.is.zhang.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();

        initViews(savedInstanceState);

        prepareData();
    }

    protected abstract void initVariables();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void prepareData();
}
