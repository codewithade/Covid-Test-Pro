package com.smatworld.covidtest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class DiagnosisMessageAdapter extends ListAdapter<DiagnosisMessage, RecyclerView.ViewHolder> {

    private static final String TAG = "AppInfo";
    private Context mContext;
    private OnItemClickListener mItemClickListener;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_CHECKBOX = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_RADIO = 4;
    static int RADIO_BUTTON_ID = 0;

    DiagnosisMessageAdapter(Context mContext) {
        super(DiagnosisMessageAdapter.DIFF_CALLBACK);
        this.mContext = mContext;
    }

    // creates interface that the calling activity implements
    public interface OnItemClickListener {
        void getCheckedItems(ArrayList<String> itemsChecked, int checkboxResUsedID);

        void getSelectedRadioButton(String buttonOption);
    }

    void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageViewHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED_CHECKBOX:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_diagnosis_checkbox, parent, false);
                return new CheckboxViewHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED_RADIO:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_diagnosis_radiogroup, parent, false);
                return new RadioMessageViewHolder(view);
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DiagnosisMessage message = getItem(position);
        Log.i(TAG, "onBindViewHolder message ID: " + message.getMessageID());
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageViewHolder) holder).bindTo(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).bindTo(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_CHECKBOX:
                ((CheckboxViewHolder) holder).bindTo(message);
                holder.setIsRecyclable(false);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_RADIO:
                ((RadioMessageViewHolder) holder).bindTo(message);
                holder.setIsRecyclable(false);
                break;
            default:
                break;
        }
    }

    // a viewHolder that contains a view that hasATransientState set to TRUE is not recyclable
    // which means a setHasTransientState(true) can be replaced with a isRecyclable(false) statement

    @Override
    public int getItemViewType(int position) {
        DiagnosisMessage message = getItem(position);
        switch (message.getUserID()) {
            case VIEW_TYPE_MESSAGE_SENT:
                return VIEW_TYPE_MESSAGE_SENT;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                return VIEW_TYPE_MESSAGE_RECEIVED;
            case VIEW_TYPE_MESSAGE_RECEIVED_CHECKBOX:
                return VIEW_TYPE_MESSAGE_RECEIVED_CHECKBOX;
            case VIEW_TYPE_MESSAGE_RECEIVED_RADIO:
                return VIEW_TYPE_MESSAGE_RECEIVED_RADIO;
        }
        return 0;
    }

    private class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView nameTextView, timelineTextView, messageTextView;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_message_profile);
            nameTextView = itemView.findViewById(R.id.text_message_name);
            timelineTextView = itemView.findViewById(R.id.received_timeline);
            messageTextView = itemView.findViewById(R.id.received_text_message_body);
        }

        void bindTo(DiagnosisMessage message) {
            Glide.with(mContext).load(message.getImageResource()).into(profileImage);
            nameTextView.setText(message.getUsername());
            timelineTextView.setText(message.getTimeStamp());
            messageTextView.setText(message.getMessage());
        }
    }

    private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timelineTextView;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.sent_text_message_body);
            timelineTextView = itemView.findViewById(R.id.sent_timeline);
        }

        void bindTo(DiagnosisMessage diagnosisMessage) {
            messageTextView.setText(diagnosisMessage.getMessage());
            timelineTextView.setText(diagnosisMessage.getTimeStamp());
        }
    }

    private class RadioMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        Button sendButtonRadio;
        TextView nameTextView, timelineTextView, questionTextView;
        RadioGroup radioGroup;
        LinearLayout layoutRadio;
        String buttonOption = null;

        RadioMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_profile_radio);
            nameTextView = itemView.findViewById(R.id.message_name_radio);
            timelineTextView = itemView.findViewById(R.id.timeline_radio);
            questionTextView = itemView.findViewById(R.id.question_radio);
            radioGroup = itemView.findViewById(R.id.radio_group);
            layoutRadio = itemView.findViewById(R.id.layout_radio);
            sendButtonRadio = itemView.findViewById(R.id.send_button_radio);
        }


        void bindTo(DiagnosisMessage diagnosisMessage) {
            Glide.with(mContext).load(diagnosisMessage.getImageResource()).into(profileImage);
            nameTextView.setText(diagnosisMessage.getUsername());
            timelineTextView.setText(diagnosisMessage.getTimeStamp());
            questionTextView.setText(diagnosisMessage.getQuestion());

            // Setup the parameters for the Layouts
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            String[] buttonOptions = mContext.getResources().getStringArray(R.array.r_yes_or_no_radio);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (String option : buttonOptions) {
                RadioButton button = (RadioButton) inflater.inflate(R.layout.item_radio_button, null);
                button.setText(option);
                radioGroup.addView(button, layoutParams);
            }
            // radioGroup.setHasTransientState(true);

            /*// checks if the views have a parent and
            // removes them before adding the views to a new parent
            if (questionTextView.getParent() != null || radioGroup.getParent() != null) {
                // ((ViewGroup) questionTextView.getParent()).removeView(questionTextView);
                ((ViewGroup) radioGroup.getParent()).removeView(radioGroup);
            }
             layoutRadio.addView(questionTextView, 0, linearLayoutParams);
             layoutRadio.addView(radioGroup, 1, linearLayoutParams);*/

            View.OnClickListener radioListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton radioButton = ((RadioButton) v);
                    String radioButtonText = radioButton.getText().toString();
                    if (radioButton.isChecked())
                        buttonOption = radioButtonText;
                }
            };

            // Append an onclick listener on each checkbox
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setOnClickListener(radioListener);
            }

            // sends the selected button option to the activity implementing this interface
            sendButtonRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (buttonOption == null)
                        Snackbar.make(v, "You have to select an option", Snackbar.LENGTH_SHORT).show();
                    else {
                        // check if listener is null
                        if (mItemClickListener != null) {
                            RADIO_BUTTON_ID++;
                            mItemClickListener.getSelectedRadioButton(buttonOption);
                            for (int i = 0; i < layoutRadio.getChildCount(); i++) {
                                layoutRadio.getChildAt(i).setEnabled(false);
                                layoutRadio.getChildAt(i).setClickable(false);
                                layoutRadio.getChildAt(i).setFocusable(false);
                            }
                        }
                    }
                }
            });
        }

    }

    private static final DiffUtil.ItemCallback<DiagnosisMessage> DIFF_CALLBACK = new DiffUtil.ItemCallback<DiagnosisMessage>() {
        @Override
        public boolean areItemsTheSame(@NonNull DiagnosisMessage oldItem, @NonNull DiagnosisMessage newItem) {
            Log.i(TAG, "areItemsTheSame: ");
            return oldItem.getMessageID() == newItem.getMessageID();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DiagnosisMessage oldItem, @NonNull DiagnosisMessage newItem) {
            Log.i(TAG, "areContentsTheSame: ");
            return oldItem.equals(newItem);
        }
    };

    private class CheckboxViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        Button sendButtonCheckbox;
        TextView nameTextView, timelineTextView, questionTextView;
        LinearLayout layoutCheckbox, linearLayoutCheckboxContainer;
        ArrayList<String> checkedBoxTitles;
        int checkboxResUsedID = 0;

        CheckboxViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_profile_checkbox);
            nameTextView = itemView.findViewById(R.id.message_name_checkbox);
            timelineTextView = itemView.findViewById(R.id.timeline_checkbox);
            questionTextView = itemView.findViewById(R.id.question_checkbox);
            sendButtonCheckbox = itemView.findViewById(R.id.send_button_checkbox);

            layoutCheckbox = itemView.findViewById(R.id.layout_checkbox);
            linearLayoutCheckboxContainer = itemView.findViewById(R.id.linear_layout_checkbox);
        }

        void bindTo(DiagnosisMessage diagnosisMessage) {
            Glide.with(mContext).load(diagnosisMessage.getImageResource()).into(profileImage);
            nameTextView.setText(diagnosisMessage.getUsername());
            timelineTextView.setText(diagnosisMessage.getTimeStamp());
            questionTextView.setText(diagnosisMessage.getQuestion());

            String[] checkboxResToLoad = null;
            // load the specific checkbox resource
            switch (diagnosisMessage.getCheckboxResID()) {
                case DiagnosisMessageActivity.CHECKBOX_PRECAUTION_ID:
                    checkboxResToLoad = mContext.getResources().getStringArray(R.array.c_precautionary_measures_adopted);
                    checkboxResUsedID = 1;
                    break;
                case DiagnosisMessageActivity.CHECKBOX_SYMPTOMS_ID:
                    checkboxResToLoad = mContext.getResources().getStringArray(R.array.c_symptoms_list_statements);
                    checkboxResUsedID = 2;
                    break;
                case DiagnosisMessageActivity.CHECKBOX_DAYS_SYMPTOMS_ID:
                    checkboxResToLoad = mContext.getResources().getStringArray(R.array.c_days_symptoms);
                    checkboxResUsedID = 3;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + diagnosisMessage.getCheckboxResID());
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (String s : checkboxResToLoad) {
                CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.item_checkbox, null);
                checkBox.setText(s);
                layoutCheckbox.addView(checkBox, layoutParams);
            }

            // view will not be recycled until its set to false
            // layoutCheckbox.setHasTransientState(true);

            // checks if the views have a parent and
            // removes them before adding the views to a new parent
            /*if (questionTextView.getParent() != null || layoutCheckbox.getParent() != null) {
                ((ViewGroup) questionTextView.getParent()).removeView(questionTextView);
                ((ViewGroup) layoutCheckbox.getParent()).removeView(layoutCheckbox);
            }
            linearLayoutCheckboxContainer.addView(questionTextView, 0, layoutParams);
            linearLayoutCheckboxContainer.addView(layoutCheckbox, 1, layoutParams);*/

            // stores the titles of each selected checkbox
            checkedBoxTitles = new ArrayList<>();
            View.OnClickListener checkboxListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = ((CheckBox) v);
                    String checkboxText = checkBox.getText().toString();
                    if (!checkBox.isChecked() && checkedBoxTitles.contains(checkboxText))
                        checkedBoxTitles.remove(checkboxText);
                    else
                        checkedBoxTitles.add(checkboxText);
                }
            };

            // Append an onclick listener on each checkbox
            for (int i = 0; i < layoutCheckbox.getChildCount(); i++) {
                layoutCheckbox.getChildAt(i).setOnClickListener(checkboxListener);
            }

            // sends the selected items to the activity implementing this interface
            sendButtonCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkedBoxTitles.size() == 0)
                        Snackbar.make(v, R.string.error_empty_option, Snackbar.LENGTH_SHORT).show();
                        // Toast.makeText(mContext, R.string.error_empty_option, Toast.LENGTH_SHORT).show();
                    else {
                        if (mItemClickListener != null) {
                            mItemClickListener.getCheckedItems(checkedBoxTitles, checkboxResUsedID);
                            for (int i = 0; i < linearLayoutCheckboxContainer.getChildCount(); i++) {
                                linearLayoutCheckboxContainer.getChildAt(i).setClickable(false);
                                linearLayoutCheckboxContainer.getChildAt(i).setFocusable(false);
                                linearLayoutCheckboxContainer.getChildAt(i).setEnabled(false);
                            }
                        }
                    }
                }
            });
        }
    }
}
