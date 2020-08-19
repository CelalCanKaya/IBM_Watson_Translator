package com.example.languagetranslator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationModels;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.speech_to_text.v1.websocket.RecognizeCallback;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    StreamPlayer streamPlayer = new StreamPlayer();
    TextToSpeech textToSpeech;
    SpeechToText speechToText;
    LanguageTranslator languageTranslator;
    MicrophoneInputStream capture;
    MicrophoneHelper microphoneHelper;
    boolean isListening = false, isSpeaking = false, isDialogOpenedByFirstFlagButton = false;
    String speechText = "", firstLanguage = "American English", secondLanguage = "German";
    LottieAnimationView loadingAnim;
    Typeface tf;
    ImageButton flagButton, flagButton2, microphoneButton, swapLanguages;
    AlertDialog dialog;
    ListView listView;
    TextView firstLanguageText, secondLanguageText, translatedText, title;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCloudServices();
        findViewsById();
        initializeObjects();
        setOnClickListeners();
    }

    private void findViewsById(){
        flagButton = findViewById(R.id.firstLanguage);
        microphoneButton = findViewById(R.id.microphoneButton);
        loadingAnim = findViewById(R.id.lottie_anim_loading);
        flagButton2 = findViewById(R.id.secondLanguage);
        firstLanguageText = findViewById(R.id.firstLanguageText);
        secondLanguageText = findViewById(R.id.secondLanguageText);
        swapLanguages = findViewById(R.id.swapLanguages);
        translatedText = findViewById(R.id.translatedText);
        title = findViewById(R.id.title);
    }

    private void initializeObjects(){
        tf = Typeface.createFromAsset(getAssets(),"fonts/quicksand.otf");
        microphoneHelper = new MicrophoneHelper(this);
        loadingAnim.setAlpha(0.0f);
        initializeDialogWindow();
        firstLanguageText.setTypeface(tf);
        secondLanguageText.setTypeface(tf);
        translatedText.setTypeface(tf);
        title.setTypeface(tf);
    }

    private void initializeDialogWindow(){
        listView = new ListView(this);
        listView.setBackgroundResource(R.drawable.rounded_rectangle);

        CustomAdapter adapter = new CustomAdapter();
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setView(listView);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }

    private void playSoundEffect(){
        mp = MediaPlayer.create(MainActivity.this, R.raw.microphone_on);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    private void setOnClickListeners(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                if(isDialogOpenedByFirstFlagButton){
                    if (Constants.LANGUAGES[position].equals(secondLanguage) || (Constants.LANGUAGES[position].contains("English") && secondLanguage.contains("English"))){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.error(getApplicationContext(), Constants.LANG_SELECTION_ERROR_MESSAGES[position], Toast.LENGTH_SHORT, true).show();
                            }
                        });
                    }
                    else{
                        flagButton.setBackgroundResource(Constants.FLAGS[position]);
                        firstLanguage = Constants.LANGUAGES[position];
                        firstLanguageText.setText(firstLanguage);
                        dialog.dismiss();
                    }
                }
                else {
                    if (Constants.LANGUAGES[position].equals(firstLanguage) || (Constants.LANGUAGES[position].contains("English") && firstLanguage.contains("English"))){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.error(getApplicationContext(), Constants.LANG_SELECTION_ERROR_MESSAGES[position], Toast.LENGTH_SHORT, true).show();
                            }
                        });
                    }
                    else{
                        flagButton2.setBackgroundResource(Constants.FLAGS[position]);
                        secondLanguage = Constants.LANGUAGES[position];
                        secondLanguageText.setText(secondLanguage);
                        dialog.dismiss();
                    }
                }
            }
        });

        flagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDialogOpenedByFirstFlagButton = true;
                dialog.show();
            }
        });

        flagButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDialogOpenedByFirstFlagButton = false;
                dialog.show();
            }
        });


        microphoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetSpeechTask().execute(firstLanguage);
                if(isSpeaking){
                    microphoneButton.setBackgroundResource(R.drawable.ic_mic_white_24dp);
                    loadingAnim.animate().alpha(0.0f);
                    isSpeaking = false;
                }
                else{
                    playSoundEffect();
                    microphoneButton.setBackgroundResource(R.drawable.ic_mic_off_white_24dp);
                    loadingAnim.animate().alpha(1.0f);
                    isSpeaking = true;
                }
            }
        });

        swapLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = firstLanguage;
                Drawable tempBackground = flagButton.getBackground();
                firstLanguage = secondLanguage;
                secondLanguage = temp;
                firstLanguageText.setText(firstLanguage);
                secondLanguageText.setText(secondLanguage);
                flagButton.setBackground(flagButton2.getBackground());
                flagButton2.setBackground(tempBackground);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void initCloudServices(){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground( Void... voids ) {
                initializeTTS();
                initializeSTT();
                initializeLanguageTranslator();
                return null;
            }
        }.execute();
    }

    private void initializeTTS(){
        IamAuthenticator authenticator = new IamAuthenticator(Constants.TEXT_TO_SPEECH_API_KEY);
        textToSpeech = new TextToSpeech(authenticator);
        textToSpeech.setServiceUrl(Constants.TEXT_TO_SPEECH_URL);
    }

    private void initializeSTT(){
        IamAuthenticator authenticator = new IamAuthenticator(Constants.SPEECH_TO_TEXT_API_KEY);
        speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl(Constants.SPEECH_TO_TEXT_URL);
    }

    private void initializeLanguageTranslator(){
        IamAuthenticator authenticator = new IamAuthenticator(Constants.LANGUAGE_TRANSLATOR_API_KEY);
        languageTranslator = new LanguageTranslator(Constants.LANGUAGE_TRANSLATOR_VERSION, authenticator);
        languageTranslator.setServiceUrl(Constants.LANGUAGE_TRANSLATOR_URL);
    }


    private class GetSpeechTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(final String... params) {
            if(!isListening){
            capture = microphoneHelper.getInputStream(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        speechToText.recognizeUsingWebSocket(new RecognizeOptions.Builder()
                                .audio(capture)
                                .contentType(ContentType.OPUS.toString())
                                .model(Constants.STT_MODELS.get(params[0]))
                                .interimResults(true)
                                .inactivityTimeout(2000)
                                .build(),
                                new MicrophoneRecognizeDelegate());
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }).start();

            isListening = true;
            } else {
                microphoneHelper.closeInputStream();
                isListening = false;
            }

            return "synthesize completed";
        }
    }

    private class TranslateAndSayTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if(speechText.length()>0){
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(speechText)
                        .modelId(Constants.LANG_CODES.get(params[0]) + "-" + Constants.LANG_CODES.get(params[1]))
                        .build();

                String finalTranslation = "";

                if(!Constants.LANG_CODES.get(params[0]).equals("en")){
                    TranslateOptions connectorOptions = new TranslateOptions.Builder()
                            .addText(speechText)
                            .modelId(Constants.LANG_CODES.get(params[0]) + "-en")
                            .build();

                    TranslationResult result = languageTranslator.translate(connectorOptions).execute().getResult();
                    final String translation = result.getTranslations().get(0).getTranslation();

                    TranslateOptions finalTranslateOptions = new TranslateOptions.Builder()
                            .addText(translation)
                            .modelId("en-" + Constants.LANG_CODES.get(params[1]))
                            .build();

                    TranslationResult finalResult = languageTranslator.translate(finalTranslateOptions).execute().getResult();
                    finalTranslation = finalResult.getTranslations().get(0).getTranslation();
                }
                else{
                    System.out.println(Constants.LANG_CODES.get(params[0]) + "-" + Constants.LANG_CODES.get(params[1]));
                    TranslationResult finalResult = languageTranslator.translate(translateOptions).execute().getResult();
                    finalTranslation = finalResult.getTranslations().get(0).getTranslation();
                }


                final String finalTranslation1 = finalTranslation;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        translatedText.setText(finalTranslation1);
                    }
                });

                streamPlayer.playStream(textToSpeech.synthesize(new SynthesizeOptions.Builder()
                        .text(finalTranslation1)
                        .voice(Constants.TTS_VOICES.get(params[1]))
                        .accept(HttpMediaType.AUDIO_WAV)
                        .build()).execute().getResult());
                speechText = "";
            }
            return "Done";
        }
    }

    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback implements RecognizeCallback {
        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            //System.out.println(speechResults);
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                speechText = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
            }
        }

        @Override
        public void onTranscriptionComplete() {
            new TranslateAndSayTask().execute(firstLanguage, secondLanguage);

        }

        @Override
        public void onError(Exception e) {
            try {
                // This is critical to avoid hangs
                // (see https://github.com/watson-developer-cloud/android-sdk/issues/59)
                capture.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onDisconnected() {

        }
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return Constants.FLAGS.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.list_item, null);

            ImageView img = v.findViewById(R.id.flag);
            TextView text = v.findViewById(R.id.lang);

            img.setImageResource(Constants.FLAGS[i]);
            text.setText(Constants.LANGUAGES[i]);
            text.setTypeface(tf);

            return v;
        }
    }

}
