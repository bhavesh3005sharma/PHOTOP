package com.example.bestphotocollections.Fragments.DownloadFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bestphotocollections.R;

public class DownloadsFragment extends Fragment implements Contract.view {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.download_fragment,container,false);
        getActivity().setTitle("Your Complete Collections");
        return view;
    }
}
