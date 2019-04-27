package com.bubblestudios.bubble;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements CardsFragment.OnFragmentInteractionListener, UserProfileFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private ImageView profileIcon, logo;
    private Uri photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar = getSupportActionBar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.getUid();

        photoUrl = user.getPhotoUrl();
        profileIcon = findViewById(R.id.profile_icon);
        logo = findViewById(R.id.bubble_logo);
        Glide.with(profileIcon).load(photoUrl).into(profileIcon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        viewPager = findViewById(R.id.main_viewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch(i) {
                    case 0:
                        actionBar.setDisplayHomeAsUpEnabled(false);
                        profileIcon.setVisibility(View.VISIBLE);
                        logo.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        profileIcon.setVisibility(View.GONE);
                        logo.setVisibility(View.GONE);
                        break;
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        int pageNum = (viewPager.getCurrentItem());
        if ( pageNum== 1) {
            menu.findItem(R.id.menu_search).setVisible(true);

        }
        else {
            menu.findItem(R.id.menu_search).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_settings_button:
                //settings menu
                return true;
            case R.id.menu_upload_snippet_button:
                Intent intent = new Intent(this, UploadSnippetActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_upload_artist_button:
                Intent intent2 = new Intent(this, UploadArtistActivity.class);
                startActivity(intent2);
                return true;
            case R.id.menu_logout_button:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CardsFragment.newInstance();
                case 1:
                    return UserProfileFragment.newInstance();
                default:
                    return null;
            }
        }
    }


}
