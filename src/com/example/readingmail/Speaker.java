package com.example.readingmail;

import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

@SuppressLint("NewApi")
public class Speaker implements OnInitListener {
	 
    private TextToSpeech tts;
    
    private boolean isSpeaking = false;
    
    private boolean ready = false;
     
    private boolean allowed = false;
    
    private boolean finish = false;
     
    @SuppressLint("NewApi")
	public Speaker(Context context){
        tts = new TextToSpeech(context, this);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			
			@Override
			public void onStart(String utteranceId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(String utteranceId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDone(String utteranceId) {
				// TODO Auto-generated method stub
				finish = true;
			}
		});
        
    }   
     
    public boolean isAllowed(){
        return allowed;
    }
     
    public void allow(boolean allowed){
        this.allowed = allowed;
    }

	@SuppressLint("NewApi")
	@Override
	public void onInit(int status) {
	    if(status == TextToSpeech.SUCCESS){
	        // Change this to match your
	        // locale
	        tts.setLanguage(Locale.FRANCE);
	        ready = true;
	    }else{
	        ready = false;
	    }
	}
	
	
	
	public void speak(String text){
	     
	    // Speak only if the TTS is ready
	    // and the user has allowed speech
	    
	    if(ready && allowed) {
	        HashMap<String, String> hash = new HashMap<String,String>();
	        hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, 
	                String.valueOf(AudioManager.STREAM_NOTIFICATION));
	        tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
	        
	    }
	}
	
	public void pause(int duration){
	    tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
	}
	
	public boolean callMain()
	{
		if(!tts.isSpeaking())
        {
        	isSpeaking = false;
        }
		else
		{
			isSpeaking = true;
		}
		return isSpeaking;
	}
	
	
	// Free up resources
	public void destroy(){
	    tts.shutdown();
	}
}