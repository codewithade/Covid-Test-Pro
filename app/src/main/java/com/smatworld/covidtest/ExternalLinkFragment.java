package com.smatworld.covidtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ExternalLinkFragment extends Fragment {
    private ArrayList<ExternalLink> mExternalLinkList;
    private Context mContext;

    public ExternalLinkFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExternalLink.COUNT = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_external_link, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView externalLinkRecyclerview = view.findViewById(R.id.external_link_recycler_view);
        mExternalLinkList = new ArrayList<>();
        ExternalLinkAdapter externalLinkAdapter = new ExternalLinkAdapter(mContext);
        externalLinkRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        externalLinkRecyclerview.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        externalLinkRecyclerview.setAdapter(externalLinkAdapter);
        externalLinkRecyclerview.hasFixedSize();
        externalLinkAdapter.submitList(mExternalLinkList);

        initData();
    }

    private void initData() {
        TypedArray urlIcons = getResources().obtainTypedArray(R.array.link_icons);
        String[] linkURL = getResources().getStringArray(R.array.link_url);
        String[] linkTitle = getResources().getStringArray(R.array.link_title);

        for (int i=0; i<linkTitle.length; i++)
            mExternalLinkList.add(new ExternalLink(linkURL[i], linkTitle[i], urlIcons.getResourceId(i, 0)));

        urlIcons.recycle();
    }
}
