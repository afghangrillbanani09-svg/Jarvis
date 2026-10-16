package com.jarvis.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.widget.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import java.text.DateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private TextView status, answer;
    private EditText input;
    private TextToSpeech tts;
    private SpeechRecognizer recognizer;
    private final int MIC_REQUEST = 701;
    private boolean ttsReady = false;

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private TextView text(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); return t;
    }
    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        getWindow().setStatusBarColor(Color.rgb(5,15,29));
        getWindow().setNavigationBarColor(Color.rgb(5,15,29));
        buildUi();
        tts = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                ttsReady = true;
                tts.setLanguage(Locale.getDefault());
            }
        });
        showAnswer("JARVIS online. Tap TALK or type a command. Wake-word detection is not enabled in this starter build.");
    }
    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_HORIZONTAL); page.setPadding(dp(22),dp(24),dp(22),dp(20));
        page.setBackgroundColor(Color.rgb(5,15,29));
        TextView brand=text("J.A.R.V.I.S",14,Color.rgb(113,218,255)); brand.setLetterSpacing(.28f);
        page.addView(brand,new LinearLayout.LayoutParams(-1,dp(35)));
        status=text("SYSTEM READY · MAXIMUM PROJECT STARTER",11,Color.rgb(160,190,213));
        page.addView(status,new LinearLayout.LayoutParams(-1,dp(28)));
        TextView orb=text("✦",66,Color.rgb(190,245,255)); orb.setTypeface(Typeface.DEFAULT_BOLD);
        orb.setBackgroundColor(Color.rgb(10,47,77));
        LinearLayout.LayoutParams op=new LinearLayout.LayoutParams(dp(164),dp(164)); op.setMargins(0,dp(24),0,dp(18)); page.addView(orb,op);
        page.addView(text("Hello, I'm Jarvis.",26,Color.WHITE),new LinearLayout.LayoutParams(-1,dp(44)));
        page.addView(text("Bangla + English • Android assistant",13,Color.rgb(160,190,213)),new LinearLayout.LayoutParams(-1,dp(32)));
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(16),dp(16),dp(16),dp(16)); card.setBackgroundColor(Color.rgb(12,29,47));
        TextView label=text("JARVIS RESPONSE",11,Color.rgb(113,218,255)); label.setGravity(Gravity.LEFT); card.addView(label);
        answer=new TextView(this); answer.setTextColor(Color.WHITE); answer.setTextSize(16); answer.setPadding(0,dp(12),0,dp(10)); card.addView(answer);
        page.addView(card,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        Button talk=new Button(this); talk.setText("🎙 TALK"); talk.setOnClickListener(v->listen());
        Button stop=new Button(this); stop.setText("STOP VOICE"); stop.setOnClickListener(v->{if(recognizer!=null)recognizer.cancel();if(tts!=null)tts.stop();status.setText("VOICE STOPPED");});
        row.addView(talk,new LinearLayout.LayoutParams(0,dp(58),1)); row.addView(stop,new LinearLayout.LayoutParams(0,dp(58),1)); page.addView(row);
        input=new EditText(this); input.setSingleLine(true); input.setHint("বাংলা বা English command লিখুন"); input.setHintTextColor(Color.GRAY); input.setTextColor(Color.WHITE);
        page.addView(input,new LinearLayout.LayoutParams(-1,dp(54)));
        Button send=new Button(this); send.setText("SEND COMMAND"); send.setOnClickListener(v->{String s=input.getText().toString();input.setText("");handle(s);});
        page.addView(send,new LinearLayout.LayoutParams(-1,dp(52)));
        TextView examples=text("Try: time • date • open YouTube • search weather • help",12,Color.rgb(160,190,213)); examples.setPadding(0,dp(14),0,dp(8)); page.addView(examples);
        TextView warning=text("Wake word, AI chat API, and deep phone automation require extra setup. This starter does not listen continuously.",11,Color.rgb(115,145,168)); page.addView(warning);
        scroll.addView(page); setContentView(scroll);
    }
    private void showAnswer(String s) {
        answer.setText(s); status.setText("SYSTEM READY · RESPONSE GENERATED");
        if(ttsReady && tts!=null) {
            Locale lang = s.matches("(?s).*[\u0980-\u09FF].*") ? new Locale("bn","BD") : Locale.US;
            if(tts.isLanguageAvailable(lang)>=TextToSpeech.LANG_AVAILABLE) tts.setLanguage(lang);
            tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"jarvis-reply");
        }
    }
    private void listen() {
        if(!SpeechRecognizer.isRecognitionAvailable(this)){showAnswer("Voice recognition unavailable. Type a command instead.");return;}
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},MIC_REQUEST);return;}
        try {
            if(recognizer!=null)recognizer.destroy();
            recognizer=SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener(){
                public void onReadyForSpeech(Bundle p){status.setText("LISTENING · SPEAK NOW");}
                public void onBeginningOfSpeech(){} public void onRmsChanged(float r){} public void onBufferReceived(byte[] b){}
                public void onEndOfSpeech(){status.setText("PROCESSING…");}
                public void onError(int e){status.setText("SYSTEM READY");answer.setText("Could not hear clearly. Try again or type a command.");}
                public void onResults(Bundle b){ArrayList<String> a=b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);if(a!=null&&!a.isEmpty())handle(a.get(0));}
                public void onPartialResults(Bundle b){} public void onEvent(int t,Bundle p){}
            });
            Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"bn-BD");
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"bn-BD");
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak in Bangla or English");
            recognizer.startListening(i);
        } catch(Exception e){showAnswer("Could not start microphone. Check permission.");}
    }
    @Override public void onRequestPermissionsResult(int r,String[] p,int[] g){
        super.onRequestPermissionsResult(r,p,g);
        if(r==MIC_REQUEST&&g.length>0&&g[0]==PackageManager.PERMISSION_GRANTED)listen();
        else showAnswer("Microphone permission is required for voice commands.");
    }
    private void handle(String raw) {
        if(raw==null||raw.trim().isEmpty())return;
        String c=raw.trim(),s=c.toLowerCase(Locale.ROOT);
        if(s.matches("^(hi|hello|hey)( jarvis)?[.! ]*$")||s.contains("হ্যালো")||s.contains("হাই")){showAnswer("Hello! আমি অনলাইনে আছি। কী সাহায্য করতে পারি?");return;}
        if(s.contains("time")||s.contains("কটা বাজে")||s.contains("সময়")){showAnswer("এখন সময় "+DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date())+".");return;}
        if(s.contains("date")||s.contains("today")||s.contains("আজ কত তারিখ")){showAnswer("আজ "+DateFormat.getDateInstance(DateFormat.FULL,new Locale("bn","BD")).format(new Date())+".");return;}
        if(s.contains("open youtube")||s.contains("ইউটিউব খোলো")||s.contains("youtube kholo")){showAnswer("Opening YouTube.");open("https://m.youtube.com");return;}
        if(s.contains("open google")||s.contains("গুগল খোলো")||s.contains("google kholo")){showAnswer("Opening Google.");open("https://www.google.com");return;}
        String q=null;
        if(s.startsWith("search for "))q=c.substring(11).trim();
        else if(s.startsWith("search "))q=c.substring(7).trim();
        else if(s.startsWith("google "))q=c.substring(7).trim();
        else if(s.startsWith("সার্চ "))q=c.substring(5).trim();
        if(q!=null&&!q.isEmpty()){showAnswer("Searching Google for "+q);open("https://www.google.com/search?q="+Uri.encode(q));return;}
        if(s.contains("help")||s.contains("সাহায্য")){showAnswer("আমি সময়-তারিখ বলতে, YouTube/Google খুলতে এবং ওয়েবে সার্চ করতে পারি। AI chat এখনো সংযুক্ত নয়।");return;}
        showAnswer("আমি শুনেছি: "+c+". এখন আমি সময়, তারিখ, YouTube/Google এবং ওয়েব সার্চ করতে পারি। AI service এখনো যুক্ত করা হয়নি।");
    }
    private void open(String url){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}catch(Exception e){answer.setText("Could not open link.");}}
    @Override protected void onDestroy(){if(recognizer!=null)recognizer.destroy();if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
