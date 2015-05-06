package com.example.amado.photogallery;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;


public class PhotoGalleryActivity extends SingleFragmentActivity{

    @Override
    protected android.app.Fragment createFragment() {
        return new PhotoGalleryFragment();
    }

}