package com.stringee.app.activity;

import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.stringee.app.R.anim;
import com.stringee.app.R.id;

public class BaseActivity extends AppCompatActivity implements OnClickListener {

    @Override
    public void onClick(View view) {

    }

    public void addFragment(Fragment fragment, String tag) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(anim.slide_in_right, anim.slide_out_right, anim.slide_in_right, anim.slide_out_right);
        fragmentTransaction.replace(id.v_fragment, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commitAllowingStateLoss();
        supportFragmentManager.executePendingTransactions();
    }

    public void removeFragment(String tag) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitAllowingStateLoss();
            supportFragmentManager.executePendingTransactions();
        }
    }
}
