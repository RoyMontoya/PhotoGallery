package com.example.amado.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by Amado on 16/06/2015.
 */
public class PhotoPageActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }
}
