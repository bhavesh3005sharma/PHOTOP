package com.example.bestphotocollections.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.bestphotocollections.Fragments.ConnectionSubFrag.ConnectionSubFragment;
import com.example.bestphotocollections.Fragments.HomeSubFragment.HomeSubFragment;
import com.example.bestphotocollections.MessagingActivities.ChatActivity.ChatActivity;
import com.example.bestphotocollections.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainHomeFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    OnItemClickListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        setHasOptionsMenu(true);

        BottomNavigationView navigation =  view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(new HomeSubFragment());
        }

        String check=null;
        if (getArguments()!=null)
         check= getArguments().getString("openUploadPhotoFrag");
        if(check!=null && check.equals("true")) {
            loadFragment(new UploadPhotoFragment());
            navigation.setSelectedItemId(R.id.Add_Photo);
        }

        return  view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                loadFragment(new HomeSubFragment());
                return true;
            case R.id.connections:
                loadFragment(new ConnectionSubFragment());
                return true;
            case R.id.Add_Photo:
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser!=null) {
                    if (currentUser.getDisplayName().equals(""))
                        Toast.makeText(getContext(), "Please Update Your Profile First.", Toast.LENGTH_SHORT).show();
                    else
                        loadFragment(new UploadPhotoFragment());
                }
                return true;
            case R.id.earnings:
                loadFragment(new EarningSubFragment());
                return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu,menu);
        MenuItem search = menu.findItem(R.id.search_bar);
        MenuItem message = menu.findItem(R.id.message);

        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search Here!");
        if(mListener!=null) {
            mListener.searchHome(searchView);
        }

        message.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getContext(), ChatActivity.class));
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public interface OnItemClickListener {

        void searchHome(SearchView searchView);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
