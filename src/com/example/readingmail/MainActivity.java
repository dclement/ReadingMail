package com.example.readingmail;

import java.util.ArrayList;
import java.util.List;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener{

	protected static final int REQUEST_OK = 1;
	protected static final int ANSWER = 2;
	
	private enum ActionTODObyuser {
		  répondre,
		  oui,
		  non,
		  ok;  
		}
	
	private final int CHECK_CODE = 0x1;
	private final int LONG_DURATION = 5000;
	private final int SHORT_DURATION = 1200;
	private boolean reading = false;
	public boolean InstallTTSDone = false;
	public static boolean EtatSpeak = false;
	
	
	private String LastSMSNumber;
	private Speaker speaker;  
	private BroadcastReceiver smsReceiver;
	
    private ToggleButton toggle;
    private OnCheckedChangeListener toggleListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toggle = (ToggleButton)findViewById(R.id.speechToggle); 
//		findViewById(R.id.button1).setOnClickListener(this);
		
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
		
		LayoutParams params = this.getWindow().getAttributes();
		params.flags |= LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().setAttributes(params);
		
		//findViewById(R.id.button2).setOnClickListener(this);
		toggleListener = new OnCheckedChangeListener() {            
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
	            if(isChecked){
	                speaker.allow(true);
	                //speaker.speak(getString(R.string.start_speaking));
	            }else{
	                //speaker.speak(getString(R.string.stop_speaking));
	                speaker.allow(false);   
	            }
			}
	    };      
	    toggle.setOnCheckedChangeListener(toggleListener);
	     
	    checkTTS();
	    initializeSMSReceiver();
	    registerSMSReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void checkTTS(){
	    Intent check = new Intent();
	    check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
	    startActivityForResult(check, CHECK_CODE);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
//			case R.id.button1:
//				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//		        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");
//		       	 try {
//		            startActivityForResult(i, REQUEST_OK);
//		        } catch (Exception e) {
//		       	 	Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
//		        }
//			case R.id.button2:
//				Intent myIntent = new Intent(Intent.ACTION_SENDTO);
//
//				PackageManager pm = getPackageManager();
//				Intent tempIntent = new Intent(Intent.ACTION_SEND);
//				tempIntent.setType("*/*");
//				List<ResolveInfo> resInfo = pm.queryIntentActivities(tempIntent, 0);
//				for (int i1 = 0; i1 < resInfo.size(); i1++) {
//				    ResolveInfo ri = resInfo.get(i1);
//				    if (ri.activityInfo.packageName.contains("android.gm")) {
//				        myIntent.setComponent(new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name));
//				        myIntent.setAction(Intent.ACTION_SEND);
//				        myIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jc.dalle48@gmail.com"});
//				        myIntent.setType("message/rfc822");
//				        myIntent.putExtra(Intent.EXTRA_TEXT, "extra text");
//				        myIntent.putExtra(Intent.EXTRA_SUBJECT, "Extra subject");
//				        myIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("uri://your/uri/string"));
//				    }
//				}
//				startActivity(myIntent);
				
//			    Intent intent = new Intent(Intent.ACTION_SENDTO);
//			    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//			    intent.putExtra(Intent.EXTRA_EMAIL, "jc.dalle48@gmail.com");
//			    intent.putExtra(Intent.EXTRA_SUBJECT, "Test envoi");
//			    if (intent.resolveActivity(getPackageManager()) != null) {
//			        startActivity(intent);
//			    }
				
				
				
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
	        		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        		
	        		((TextView)findViewById(R.id.text1)).setText(thingsYouSaid.get(0));
	        		String action = thingsYouSaid.get(0);
	        		
	        		switch(ActionTODObyuser.valueOf(action))
	        		{
						case ok:
							break;
						case non:
							setReading(false);
							break;
						case oui:
							setReading(true);
							break;
						case répondre:
						    try{
						    	Intent i2 = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
			                    i2.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");
			                    try {
			                    	startActivityForResult(i2, ANSWER);
			                    } catch (Exception e) {
			                    	//Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
			                    }
						    }
						    catch(Exception e){
						        Toast.makeText(this,"Message not sent!", resultCode);
						    }							break;
						default:
							break;
	        		
	        		}
	        }
	        
	        // Body Answer
	        if(requestCode == ANSWER)
	        {
	        	ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        		SmsManager sms = SmsManager.getDefault();
        		String AnswerData = "";

		        sms.sendTextMessage(LastSMSNumber,null, thingsYouSaid.get(0) +  " (Sent from my personal application)",null,null);
	        }
	        
	        if(requestCode == CHECK_CODE){
	            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS || InstallTTSDone){
	                speaker = new Speaker(this);
	                speaker.allow(true);
	                InstallTTSDone = true;
	            }else {
	                Intent install = new Intent();
	                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	                startActivity(install);
	                InstallTTSDone = true;
	            }
	        }
	}
	
	private void initializeSMSReceiver(){
	    smsReceiver = new BroadcastReceiver(){
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	Log.i("Receive SMS " ,"Receive");
	            Bundle bundle = intent.getExtras();
	            if(bundle!=null){
	                Object[] pdus = (Object[])bundle.get("pdus");
	                for(int i=0;i<pdus.length;i++){
	                	EtatSpeak = true;
	                    byte[] pdu = (byte[])pdus[i];
	                    SmsMessage message = SmsMessage.createFromPdu(pdu);
	                    String text = message.getDisplayMessageBody();
	                    LastSMSNumber = message.getOriginatingAddress();
	                    String sender = getContactName(message.getOriginatingAddress());
	                    
	                    speaker.pause(LONG_DURATION);
	                    speaker.speak("Nouveau Message de " + sender + "!?");
	                    speaker.speak(text);
	                    Log.i("Speaker etat ", "speaker.callMain() " + speaker.callMain());
	                    while(speaker.callMain())
	                    {
	                    	Log.i("Still talking", "Speaking");
	                    }
	                    
	                    Intent i1 = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
	                    	i1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");
	                    
	                    try {
	                    	startActivityForResult(i1, REQUEST_OK);
	                    } catch (Exception e) {
	                    	//Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
	                    }
	                    

//	    		       	speaker.pause(LONG_DURATION);
//	    		       	if(getReading())
//	    		       	{
//	    		       		//speaker.pause(SHORT_DURATION);
//	    		       		speaker.speak(text);
//	    		       	}
//	    		       	else
//	    		       	{
//	    		       		speaker.speak("Compris, je ne lie pas ce SMS");
//	    		       	}
	    		      
	    		       	
	                }
	            }
	             
	        }           
	    };      
	}
	
	
	private String getContactName(String phone){
	    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
	    String projection[] = new String[]{ContactsContract.Data.DISPLAY_NAME};
	    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);              
	    if(cursor.moveToFirst()){
	        return cursor.getString(0);
	    }else {
	        return "Numéro inconnu";
	    }
	}
	
	private void registerSMSReceiver() {    
	    IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
	    registerReceiver(smsReceiver, intentFilter);
	}
	
	@Override
	protected void onDestroy() {    
	    super.onDestroy();
	    unregisterReceiver(smsReceiver);
//	    if(speaker.isAllowed())
//	    {
	    	//speaker.destroy();
//	    }
	}

	public boolean getReading() {
		return reading;
	}

	public void setReading(boolean reading) {
		this.reading = reading;
	}
	
	
}
