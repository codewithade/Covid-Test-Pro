package com.smatworld.covidtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import static android.content.Context.MODE_PRIVATE;


public class MainMenuAdapter extends ListAdapter<MainMenu, MainMenuAdapter.MainMenuViewHolder> {

    private Context mContext;

    MainMenuAdapter(Context context) {
        super(MainMenuAdapter.DIFF_CALLBACK);
        mContext = context;
    }

    @NonNull
    @Override
    public MainMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main, parent, false);
        return new MainMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainMenuViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class MainMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppCompatImageView mMenuImage;
        private final TextView mMenuCaption;
        private final TextView mUsagePercent;

        MainMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            mMenuImage = itemView.findViewById(R.id.main_image);
            mMenuCaption = itemView.findViewById(R.id.main_caption);
            mUsagePercent = itemView.findViewById(R.id.usage_stats);
            final AppCompatImageButton menuOverflowButton = itemView.findViewById(R.id.main_overflow_button);

            menuOverflowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, menuOverflowButton);
                    popupMenu.inflate(R.menu.menu_popup);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_details:
                                    Toast.makeText(mContext, getItem(getAdapterPosition()).getDetailTitle(), Toast.LENGTH_LONG).show();
                                    return true;
                                case R.id.menu_help:
                                    Toast.makeText(mContext, "Check the external links menu option", Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });

            mMenuImage.setOnClickListener(this);
            mMenuCaption.setOnClickListener(this);
        }


        void bind(MainMenu menu) {
            Glide.with(mContext).load(menu.getImageResource()).into(mMenuImage);
            mMenuCaption.setText(menu.getMainTitle());
            mUsagePercent.setText(mContext.getResources().getString(R.string.usage_percentage, menu.getUsageCount()));
        }

        @Override
        public void onClick(View v) {
            MainMenu menu = getItem(getAdapterPosition());
            int ID = menu.getId();
            updateUsageStatistics(getAdapterPosition());
            if (ID == 0)
                mContext.startActivity(new Intent(mContext, DiagnosisMessageActivity.class));
            else if (ID == 1)
                mContext.startActivity(new Intent(mContext, StatisticsActivity.class));
            else if (ID == 2)
                mContext.startActivity(new Intent(mContext, UpdateDatabase.class));
            else {
                Intent launchInfoIntent = new Intent(mContext, CovidInfo.class);
                launchInfoIntent.putExtra("fragmentID", ID);
                mContext.startActivity(launchInfoIntent);
            }
        }
    }

    private void updateUsageStatistics(int id) {
        SharedPreferences preferences = mContext.getSharedPreferences(MainActivity.PREF_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int prevValue;
        switch (id){
            case 0:
                prevValue = preferences.getInt(MainActivity.MENU1_KEY, 0);
                editor.putInt(MainActivity.MENU1_KEY, prevValue+1);
                break;
            case 1:
                prevValue = preferences.getInt(MainActivity.MENU2_KEY, 0);
                editor.putInt(MainActivity.MENU2_KEY, prevValue+1);
                break;
            case 2:
                prevValue = preferences.getInt(MainActivity.MENU3_KEY, 0);
                editor.putInt(MainActivity.MENU3_KEY, prevValue+1);
                break;
            case 3:
                prevValue = preferences.getInt(MainActivity.MENU4_KEY, 0);
                editor.putInt(MainActivity.MENU4_KEY, prevValue+1);
                break;
            case 4:
                prevValue = preferences.getInt(MainActivity.MENU5_KEY, 0);
                editor.putInt(MainActivity.MENU5_KEY, prevValue+1);
                break;
            case 5:
                prevValue = preferences.getInt(MainActivity.MENU6_KEY, 0);
                editor.putInt(MainActivity.MENU6_KEY, prevValue+1);
                break;
            case 6:
                prevValue = preferences.getInt(MainActivity.MENU7_KEY, 0);
                editor.putInt(MainActivity.MENU7_KEY, prevValue+1);
                break;
            default:
                return;
        }
        int prevTotalUsageCount = preferences.getInt(MainActivity.TOTAL_USAGE_COUNT_KEY, 0);
        editor.putInt(MainActivity.TOTAL_USAGE_COUNT_KEY, prevTotalUsageCount+1);
        editor.apply();
    }

    private static final DiffUtil.ItemCallback<MainMenu> DIFF_CALLBACK = new DiffUtil.ItemCallback<MainMenu>() {
        @Override
        public boolean areItemsTheSame(@NonNull MainMenu oldItem, @NonNull MainMenu newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MainMenu oldItem, @NonNull MainMenu newItem) {
            return oldItem.equals(newItem);
        }
    };
}
