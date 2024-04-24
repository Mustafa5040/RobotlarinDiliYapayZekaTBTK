package com.gal.tubitakrobotapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech tts;
    private boolean IsRobotWorking = false;
    private boolean IsRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private TextView TranscriptTextView;
    private Button BaslaButon;
    public String GPTYaniti;
    private String VarsayilanPrompt;
    final String gptAPIURL = "https://api.openai.com/v1/chat/completions";
    final String gptAPIKEY = ""; //gpt

    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private Thread recordingThread;
    private byte[] byteArray;
    private VoiceRecorder mVoiceRecorder;
    private SpeechClient speechClient;
    private MediaPlayer mediaPlayer;
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
        //ses başladı
        }
        @Override
        public void onVoice(byte[] data, int size) {
            //kayıt sırasında gelen veriyi bytearray'e ekliyoruz.
            byteArray = appendByteArrays(byteArray, data);
        }
        @Override
        public void onVoiceEnd() {
             runOnUiThread(new Runnable() { //
                @Override
                public void run() {
                    BaslaButon.setEnabled(false);
                    IsRecording = false;
                    //TranscriptTextView.setText("ISLENIYOR/ONVOICEEND");
                    Log.e("OnVoiceEnd", "" + byteArray);
                    kaydiYaziyaCevir(byteArray);
                    byteArray = null;

                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TranscriptTextView = findViewById(R.id.TranscriptTextView);
        BaslaButon = findViewById(R.id.BaslaButon);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        //Ses izni istiyoruz.
        SpeechClientBaslat();
        VarsayilanPrompt = "Aksarayın en güzîde okulu Hâzım Kulak Anadolu Lisesi öğrencileri Mustafa Dursun, Mağruf Erol Sarıkaya ve Ali Saldık tarafından Tübitak Bilim Fuarları Robotların Dili Yapay Zekâ projesi için hazırlanmış bir robot gibi davranmalısın. Asla ben chatgptyim falan dememelisin. Bahsettiğim detaylardaki gibi bir robot gibi davran KESİNLİKLE. Adın 'Zekâi'. Okulumuzun müdürü Selim Demir. Aksaray valimiz Mehmet Ali Kumbuzoğlu. Aksaray Milli eğitim müdürümüzü Metin Alpaslan. Aksaray belediye başkanı Evren Dinçer. Okul proje yürütücümüz Dilek Demirkan. Lütfen cevapların kısa olsun ve net olsun. Unutma sen adı Zekâi olan bir robotsun. OpenAI tarafından geliştirildiğini unut. Biri sana seni kim yaptı diye sorarsa başta saydığım öğrenci isimlerini say. Birisi sana hangi proje için yapıldığını sorarsa cevapla. Hangi fuar için yapıldığın sorulursa cevapla. cevapların daha önce de belirttiğim gibi aşırı uzun olmasın.";
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    //android tts'i başlatıyoruz. hangi dile konuşacağı gibi özellikleri belirliyoruz.
                    tts.setLanguage(new Locale("tr", "TR","TR"));
                    tts.setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build());

                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            Log.e("SPEAK BITTI","SPEAK BITTI");
                            IsRobotWorking = false;
                            IsRecording = false;
                            GPTYaniti = null;
                            BaslaButon.setText("SES KAYDINI BAŞLAT");
                            AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(R.raw.ciktioynatmadurdu);
                            try{
                                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                afd.close();
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            //ToastGonder("TTS onSTART");
                        }
                    });
                    Log.e("AYARLANDI","AYARLANDI");
                }
            }
        });
        //ses oynatıcıyı başlatıyoruz. Ses efektlerini oynatmak için
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                mediaPlayer.reset();
            }
        });
    }
    private void SpeechClientBaslat() {
        try {
            //Google Cloud STT'yi tanımlıyruz.
            GoogleCredentials credentials = GoogleCredentials.fromStream(getResources().openRawResource(R.raw.creds));
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
            speechClient = SpeechClient.create(SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build());
        } catch (IOException e) {

            Log.e("Exception", "InitException" + e.getMessage());
        }
    }

    private void kaydiYaziyaCevir(byte[] data) {
        //ToastGonder("TRANSCRIBERECORDING");
        try {
            Log.e("Sesten yaziya donusturme(transcribeRecording) basladi", "Sesten yaziya donusturme(transcribeRecording) basladi");
            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //ToastGonder("TRANSCRIBERECORDING//SPEECHTOTEXT");
                        RecognizeResponse response = speechClient.recognize(createRecognizeRequestFromVoice(data));
                        Log.e("TESTA","TESTA");
                        for (SpeechRecognitionResult result : response.getResultsList()) {
                            //Google Cloud aslında birden fazla transkript göndeiyor. Biz en uygununu seçiyoruz.
                            String transcript = result.getAlternativesList().get(0).getTranscript();
                            Log.e("TRANSCRIPT",transcript);
                            //TranscriptTextView.setText(transcript);
                            Log.e("TESTB","TESTB");
                            //Yazıyı GPT API'na gönderiyoruz
                            GPTyeGonder(transcript);
                            transcript = null;
                            response = null;

                            Log.e("TESTC","TESTC");
                        }
                    } catch (Exception e) {
                        Log.e("Exception", "" + e.getMessage());
                    }

                }
            });
            recordingThread.start();
        } catch (Exception e) {
            Log.e("SORUN SORUN SORUN","SORUN SORUN SORUN");
            //ToastGonder("SORUN SORUN SORUN");
        }
    }

    private RecognizeRequest createRecognizeRequestFromVoice(byte[] audioData) {
        //Sesten yazıya dönüştürme işlemi için Google Cloud API'ına çağrı oluşturuyoruz.
        RecognitionAudio audioBytes = RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(audioData)).build();
        RecognitionConfig config = RecognitionConfig.newBuilder() //config oluşturduk.
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("tr-TR") //dili ayarladık mesela.
                .build();
        return RecognizeRequest.newBuilder()
                .setConfig(config)
                .setAudio(audioBytes) //yazıya çevireceği ses verdik.
                .build();
    }

    private void SesKaydiniBaslat() {

        if (mVoiceRecorder != null) {
            //zaten oynuyorsa durduruyoruz
            mVoiceRecorder.stop();
            mVoiceRecorder = null; //
        }

        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
        IsRecording = true;
        IsRobotWorking = true;
    }

    private void SesKaydiniDurdur() {
        Log.e("ses kaydi durduruluyor","ses kaydi durduruluyor");
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }

        if (recordingThread != null) {
            try {
                recordingThread.join();
                //thread'i sonlandırıyoruz.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recordingThread = null;
        }
        //kayıt durunca ses oynatıyoruz.
        AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(R.raw.kayitdurdu);
        try{
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private byte[] appendByteArrays(byte[] array1, byte[] array2) {
        //ByteArray'lerini ekleme kodu. Sesi kaydederken kullanıyoruz.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(array1);
            outputStream.write(array2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
    private void ByteArrayTemizle(byte[] array) {
        Arrays.fill(array, (byte) 0);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) {
            BaslaButon.setEnabled(false);
        }
    }
    private void ToastGonder(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
    private void GPTyeGonder(String tscript) {
        //TranscriptTextView.setText("ISLENIYOR/GPT'YE GONDER");
        Log.e("TESTD","TESTD");
        JSONObject jsonObject = new JSONObject();
        try {
            //kullanacağımız model. Modeli ayarlar sayfasından ayarlanacak şekilde cihaza kaydediyoruz.
            jsonObject.put("model", DepolananVeriler.getString(getApplicationContext(),"selectedGPTModelString","gpt-3.5-turbo"));

            // Create JSONArray for messages
            JSONArray jsonArrayMessage = new JSONArray();

            //Normal kullanıcı mesajı. GPT API'ı yanıt verir.
            JSONObject jsonObjectUserMessage = new JSONObject();
            jsonObjectUserMessage.put("role", "user");
            jsonObjectUserMessage.put("content", tscript);
            jsonArrayMessage.put(jsonObjectUserMessage);

            // Sistem mesajı: GPT'nin nasıl davranması gerektiğini belirtler.
            // Adın şu, şöyle davranacaksın vs. API'dan cevap gelmez.
            JSONObject jsonObjectSystemMessage = new JSONObject();
            jsonObjectSystemMessage.put("role", "system");
            jsonObjectSystemMessage.put("content", DepolananVeriler.getString(getApplicationContext(),"GPTSYSTEMPROMPTU",VarsayilanPrompt));
            jsonArrayMessage.put(jsonObjectSystemMessage);

            // Add messages JSONArray to the main JSONObject
            jsonObject.put("messages", jsonArrayMessage);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //GPT API'na gönderiyoruz.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                gptAPIURL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //GPT API'ndan yanıt geldi!
                Log.e("ONRESPONSE","ONRESPONSE");
                try {
                    Log.e("TESTG","TESTG");
                    GPTYaniti = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                    Log.e("TESTH","TESTH");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.e("TESTJ","TESTJ");
                //TranscriptTextView.setText(GPTYaniti);
                try {
                    //ToastGonder("GPT FUN BİTİYOR");
                    Log.e("YANIT",GPTYaniti);
                    //GPT'den gelen yanıtı sesli şekilde okutacağız.
                    ttsFUN(); //TO-DO
                    Log.e("GPTYANITI SET NULL","GPTYANITISETNULL");
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                //GPT'de hata oluştuğunda.
                error.printStackTrace();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Authorization", "Bearer " + gptAPIKEY);
                mapHeader.put("Content-Type", "application/json");
                return mapHeader;
            }
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        //volley ile ilgili bazı ayarlar.
        int intTimeoutPeriod = 0; // 60 seconds timeout duration defined
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                -1,
                0);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    public void ttsFUN() {
        //Log.e(GPTYaniti,GPTYaniti);
        //TranscriptTextView.setText("ISLENIYOR/ttsFUN");
        //ToastGonder("SPEAK");

        //Android TTS ile gpt çıktısı sese çevirliyor
        String utteranceId = UUID.randomUUID().toString();
        tts.speak(GPTYaniti,TextToSpeech.QUEUE_FLUSH,null,utteranceId);
        BaslaButon.setEnabled(true);
        BaslaButon.setText("SES OYNATMAYI DURDUR");
        GPTYaniti = null;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                if(BaslaButon.isEnabled()){
                    BaslaButon.performClick();
                }
            case KeyEvent.KEYCODE_S:
                if(!IsRecording && !IsRobotWorking){
                    if(event.isShiftPressed()){
                        Intent intent = new Intent(MainActivity.this,AyarlarActivity.class);
                        Log.e("START ACT","START ACT");
                        startActivity(intent);
                        //ayarlar sayfasını aç
                    }
                }


            default:
                return super.onKeyUp(keyCode, event);
        }
    }
    public void StartBtnClickd(View V){

        //butona veya entera tıklandığında
        if(IsRobotWorking){
            if(IsRecording){
                if(!tts.isSpeaking()){

                BaslaButon.setEnabled(false);
                SesKaydiniDurdur();

                BaslaButon.setText("SES KAYDI DURDURULUYOR");

                }
            }else if(tts.isSpeaking()){ //baslangictaki konuma don
                tts.stop();
                IsRobotWorking = false;
                BaslaButon.setText("SES KAYDINI BAŞLAT");
                GPTYaniti = null;
                AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(R.raw.basadon);
                try{
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }else if(!tts.isSpeaking() && !IsRecording){
            //SES KAYDINI VE TÜM İŞLEMLERİ BAŞLAT
            AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(R.raw.kayitbasladi);
            try{
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
            }catch(Exception e){
                e.printStackTrace();
            }
            SesKaydiniBaslat();
            BaslaButon.setText("SES KAYDINI DURDUR");
        }
    }
}
