package com.laverne.servicediscover.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.laverne.servicediscover.R;

public class FAQFragment extends Fragment {

    private FloatingActionButton fab;

    public FAQFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.faq_fragment, container, false);
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return view;
    }
}
