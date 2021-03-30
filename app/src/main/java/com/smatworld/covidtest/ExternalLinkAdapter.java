package com.smatworld.covidtest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class ExternalLinkAdapter extends ListAdapter<ExternalLink, ExternalLinkAdapter.ExternalLinkViewHolder> {
    private Context mContext;

    ExternalLinkAdapter(Context mContext) {
        super(ExternalLinkAdapter.DIFF_CALLBACK);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ExternalLinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_external_link, parent, false);
        return new ExternalLinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExternalLinkViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ExternalLinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView linkIcon;
        TextView linkTitle;

        ExternalLinkViewHolder(@NonNull final View itemView) {
            super(itemView);
            linkIcon = itemView.findViewById(R.id.url_image);
            linkTitle = itemView.findViewById(R.id.url_title);
            itemView.setOnClickListener(this);
        }

        void bind(ExternalLink externalLink) {
            Glide.with(mContext).load(externalLink.getLinkIcon()).into(linkIcon);
            linkTitle.setText(externalLink.getLinkTitle());
            Log.i("AppInfo", "bind:ID: " + externalLink.getId());
        }

        @Override
        public void onClick(View v) {
            ExternalLink externalLink = getItem(getAdapterPosition());
            Intent webviewIntent = new Intent(mContext, WebviewActivity.class);
            /*// Launches default browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(externalLink.getURL()));
            mContext.startActivity(browserIntent);*/
            webviewIntent.putExtra("url", externalLink.getURL());
            webviewIntent.putExtra("title", externalLink.getLinkTitle());
            mContext.startActivity(webviewIntent);
        }
    }

    private static final DiffUtil.ItemCallback<ExternalLink> DIFF_CALLBACK = new DiffUtil.ItemCallback<ExternalLink>() {
        @Override
        public boolean areItemsTheSame(@NonNull ExternalLink oldItem, @NonNull ExternalLink newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ExternalLink oldItem, @NonNull ExternalLink newItem) {
            return oldItem.equals(newItem);
        }
    };
}
