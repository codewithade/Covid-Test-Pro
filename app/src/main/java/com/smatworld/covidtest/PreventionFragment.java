package com.smatworld.covidtest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;

public class PreventionFragment extends Fragment {
    private Context mContext;
    private PDFView mPdfView;
    private final String PDF_ASSET_NAME = "covid.pdf";
    private ViewFlipper mViewFlipper;

    public PreventionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prevention, container, false);
        mPdfView = view.findViewById(R.id.pdf_view);
        mViewFlipper = view.findViewById(R.id.prevention_flipper);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPdfView.fromAsset(PDF_ASSET_NAME)
                .defaultPage(0)
                .enableAntialiasing(true)
                .password(null)
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(mContext, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        Toast.makeText(mContext, "Error on page $page", Toast.LENGTH_SHORT).show();
                    }
                })
                .load();
    }
}
