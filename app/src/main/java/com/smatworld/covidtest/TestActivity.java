package com.smatworld.covidtest;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private LinearLayout checkboxGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_diagnosis_radiogroup);

        /*checkboxGroup = findViewById(R.id.checkbox_group);
        for (int i=0; i<checkboxGroup.getChildCount(); i++){
            checkboxGroup.getChildAt(i).setOnClickListener(checkboxListener);
        }*/

        radioGroup = findViewById(R.id.radio_group);
        for(int i=0; i< radioGroup.getChildCount(); i++){
            radioGroup.getChildAt(i).setOnClickListener(listener);
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton button = ((RadioButton) v);
            if(button.isChecked())
                Toast.makeText(TestActivity.this, "Selected: " +  button.getText().toString(), Toast.LENGTH_LONG).show();
        }
    };

    // once item is checked, its auto added to an arrayList and once unchecked, its removed
    View.OnClickListener checkboxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = ((CheckBox) v);
            if(checkBox.isChecked())
                Toast.makeText(TestActivity.this, checkBox.getText(), Toast.LENGTH_LONG).show();
        }
    };
}
