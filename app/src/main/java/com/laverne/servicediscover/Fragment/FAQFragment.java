package com.laverne.servicediscover.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.laverne.servicediscover.Adapter.FAQAdapter;
import com.laverne.servicediscover.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAQFragment extends Fragment {

    private FloatingActionButton fab;
    private ExpandableListView expandableListView;

    private List<String> listGroup;
    private HashMap<String, List<String>> listItem;

    private FAQAdapter adapter;

    public FAQFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.faq_fragment, container, false);
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        expandableListView = view.findViewById(R.id.expandable_list_view);

        listGroup = new ArrayList<>();
        listItem = new HashMap<>();
        adapter = new FAQAdapter(getActivity(), listGroup, listItem);
        expandableListView.setAdapter(adapter);

        initializeData();
        return view;
    }

    private void initializeData() {
        // add questions
        listGroup.add(getString(R.string.Q1));
        listGroup.add(getString(R.string.Q2));
        listGroup.add(getString(R.string.Q3));
        listGroup.add(getString(R.string.Q4));
        listGroup.add(getString(R.string.Q5));

        // add answers
        List<String> list1 = new ArrayList<>();
        list1.add(getString(R.string.A1));
        List<String> list2 = new ArrayList<>();
        list2.add(getString(R.string.A2));
        List<String> list3 = new ArrayList<>();
        list3.add(getString(R.string.A3));
        List<String> list4 = new ArrayList<>();
        list4.add(getString(R.string.A4));
        List<String> list5 = new ArrayList<>();
        list5.add(getString(R.string.A5));

        listItem.put(listGroup.get(0), list1);
        listItem.put(listGroup.get(1), list2);
        listItem.put(listGroup.get(2), list3);
        listItem.put(listGroup.get(3), list4);
        listItem.put(listGroup.get(4), list5);

        adapter.notifyDataSetChanged();
    }
}
