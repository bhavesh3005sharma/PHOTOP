package com.example.bestphotocollections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bestphotocollections.AuthenticaionActivities.LOGIN_ACTIVITY;
import com.example.bestphotocollections.Fragments.MainHomeFragment;
import com.example.bestphotocollections.Fragments.DownloadFragment.DownloadsFragment;
import com.example.bestphotocollections.Fragments.MyUploadsFragment.MyUploadsFragment;
import com.example.bestphotocollections.Fragments.ProfileFragment.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        String check = getIntent().getStringExtra("openUploadPhotoFrag");
        Bundle bundle = new Bundle();
        bundle.putString("openUploadPhotoFrag",check);


        if (savedInstanceState == null) {
            Fragment fragment = new MainHomeFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
            navigationView.setCheckedItem(R.id.HOME);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.HOME:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MainHomeFragment()).commit();
                break;
            case R.id.Profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.MyUploads:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyUploadsFragment()).commit();
                break;
            case R.id.Downloads:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DownloadsFragment()).commit();
                break;
            case R.id.Settings:
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.AboutApp:
                Toast.makeText(this, "About the App", Toast.LENGTH_SHORT).show();
                break;
            case R.id.LogOut :
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Do you want to LOGOUT?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoge, int id) {
                                FirebaseAuth.getInstance().signOut();
                                SharedPreferences sharedPreferences = getSharedPreferences("shared_pref_profile_data",MODE_PRIVATE);
                                SharedPreferences.Editor editor =sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(), LOGIN_ACTIVITY.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                builder.create().show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (null != fragment) {
                fragment.onActivityResult(requestCode, resultCode, data);
            } else {
                new ProfileFragment().onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
        }
    }
}
