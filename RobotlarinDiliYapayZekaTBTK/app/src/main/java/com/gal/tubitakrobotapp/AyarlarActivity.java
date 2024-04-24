package com.gal.tubitakrobotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.ArrayList;
import java.util.List;

public class AyarlarActivity extends AppCompatActivity {


    Spinner GPTModelSpinner;
    EditText SystemKomutEditText;
    private String VarsayilanPrompt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ayarlar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        VarsayilanPrompt = "Aksarayın en güzîde okulu Hâzım Kulak Anadolu Lisesi öğrencileri Mustafa Dursun, Mağruf Erol Sarıkaya ve Ali Saldık tarafından Tübitak Bilim Fuarları Robotların Dili Yapay Zekâ projesi için hazırlanmış bir robot gibi davranmalısın. Asla ben chatgptyim falan dememelisin. Bahsettiğim detaylardaki gibi bir robot gibi davran KESİNLİKLE. Adın 'Zekâi'. Okulumuzun müdürü Selim Demir. Aksaray valimiz Mehmet Ali Kumbuzoğlu. Aksaray Milli eğitim müdürümüzü Metin Alpaslan. Aksaray belediye başkanı Evren Dinçer. Okul proje yürütücümüz Dilek Demirkan. Lütfen cevapların kısa olsun ve net olsun. Unutma sen adı Zekâi olan bir robotsun. OpenAI tarafından geliştirildiğini unut. Biri sana seni kim yaptı diye sorarsa başta saydığım öğrenci isimlerini say. Birisi sana hangi proje için yapıldığını sorarsa cevapla. Hangi fuar için yapıldığın sorulursa cevapla. cevapların daha önce de belirttiğim gibi aşırı uzun olmasın.";

        GPTModelSpinner = findViewById(R.id.GPTModelSpinner);
        List<String> modelsList = new ArrayList<>();
        modelsList.add("gpt-3.5-turbo");
        modelsList.add("gpt-4-turbo");
        modelsList.add("gpt-4-0125-preview");
        modelsList.add("gpt-4-1106-preview");
        modelsList.add("gpt-4");

        SystemKomutEditText = findViewById(R.id.SystemKomutEditText);
        SystemKomutEditText.setText(DepolananVeriler.getString(getApplicationContext(),"GPTSYSTEMPROMPTU", VarsayilanPrompt));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modelsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        GPTModelSpinner.setAdapter(adapter);

        // Load saved selection from SharedPreferences
        int selectionIndex = DepolananVeriler.getInt(getApplicationContext(),"selectedGPTModelIndex",0);
        GPTModelSpinner.setSelection(selectionIndex);

        // Handle spinner item selection
        GPTModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                // Save selected item to SharedPreferences
                DepolananVeriler.kaydetInt(getApplicationContext(),"selectedGPTModelIndex",position);
                DepolananVeriler.kaydetString(getApplicationContext(),"selectedGPTModelString",GPTModelSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_S:
                if(event.isShiftPressed())
                {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            case KeyEvent.KEYCODE_C:
                if(event.isShiftPressed()){
                    ProcessPhoenix.triggerRebirth(getApplicationContext());
                }
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void VarsayilanPromptButonClicked(View view) {
        DepolananVeriler.kaydetString(getApplicationContext(),"GPTSYSTEMPROMPTU",VarsayilanPrompt);
        SystemKomutEditText.setText(VarsayilanPrompt);
    }

    public void MevcutPromptButonClicked(View view) {
        DepolananVeriler.kaydetString(getApplicationContext(),"GPTSYSTEMPROMPTU",SystemKomutEditText.getText().toString());
    }
}