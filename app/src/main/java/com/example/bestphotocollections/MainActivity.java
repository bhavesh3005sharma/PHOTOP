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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bestphotocollections.AuthenticaionActivities.LOGIN_ACTIVITY;
import com.example.bestphotocollections.Fragments.MainHomeFragment;
import com.example.bestphotocollections.Fragments.DownloadFragment.DownloadsFragment;
import com.example.bestphotocollections.Fragments.MyUploadsFragment.MyUploadsFragment;
import com.example.bestphotocollections.Profile.Activities.EditProfileActivity.EditProfileActivity;
import com.example.bestphotocollections.Profile.Activities.ShowProfile.ShowProfileActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    CircularImageView profileIcon;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        profileIcon = findViewById(R.id.profileImage);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("PHOTOP BEST PHOTOS");
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
        if (currentUser.getPhotoUrl()!=null)
        Picasso.get().load(currentUser.getPhotoUrl()).placeholder(R.drawable.ic_profile).into(profileIcon);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.getDisplayName().isEmpty())
                    startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
                else
                openProfileActivity();
            }
        });
    }

    private void openProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ShowProfileActivity.class);
        intent.putExtra("uid",currentUser.getUid());
        intent.putExtra("name",currentUser.getDisplayName());
        if (currentUser.getPhotoUrl()!=null)
            intent.putExtra("uri",currentUser.getPhotoUrl());
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.HOME:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MainHomeFragment()).commit();
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
                                SharedPreferences sharedPreferencesProfileData = getSharedPreferences("shared_pref_connections_list",MODE_PRIVATE);
                                SharedPreferences.Editor editorProfile =sharedPreferencesProfileData.edit();
                                editorProfile.clear();
                                editorProfile.apply();

                                SharedPreferences sharedPreferencesDownloads = getSharedPreferences(String.valueOf(R.string.sharedPrefDownloads),MODE_PRIVATE);
                                SharedPreferences.Editor editorDownloads =sharedPreferencesDownloads.edit();
                                editorDownloads.clear();
                                editorDownloads.apply();

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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
        }
    }
}
