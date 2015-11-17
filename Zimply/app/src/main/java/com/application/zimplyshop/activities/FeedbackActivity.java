package com.application.zimplyshop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.utils.CommonLib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class FeedbackActivity extends BaseActivity{

	int screenWidth;
	int screenHeight;

	private SharedPreferences prefs;

	final Context context = this;

	private TextView feedbackEmailText;
	private final int EMAIL_FEEDBACK = 1500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.feedback_page);

		Display display = getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
		
		feedbackEmailText = (TextView)findViewById(R.id.feedback_email);
		feedbackEmailText.setTextColor(getResources().getColor(R.color.black));
		feedbackEmailText.setText(getFeedbackEmailSpannableText(), TextView.BufferType.SPANNABLE);
		feedbackEmailText.setMovementMethod(LinkMovementMethod.getInstance());
		feedbackEmailText.setPadding(0, screenWidth/20, 0, 0);



		prefs = getSharedPreferences("application_settings", 0);

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		fixSizes();
	}

	private SpannableString getFeedbackEmailSpannableText()
	{

		String feedbackText =  getResources().getString(R.string.feedback_email);
		String email = "android@zimply.in";
		SpannableString ss = new SpannableString(feedbackText);
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View textView) {
				feedbackEmailText.setEnabled(false);
				sendFeedbackEmail();
			}
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
//				ds.setTypeface(CommonLib.getTypeface(getApplicationContext(), CommonLib.Regular));
				ds.setTextSize(getResources().getDimension(R.dimen.size12));
				ds.setColor(getResources().getColor(R.color.z_red_feedback));
			}
		};
		
		if(feedbackText.indexOf(email) > -1)
			ss.setSpan(clickableSpan, feedbackText.indexOf(email), feedbackText.indexOf(email) + email.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return ss;
	}



	private void sendFeedbackEmail()
	{
		Intent i = new Intent(Intent.ACTION_SEND);

		i.setType("application/octet-stream");
		i.putExtra(Intent.EXTRA_EMAIL  , new String [] {"android@zimply.in"});
		i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_email_subject));

		try { 
			final String LogString = new String(  "App Version  : " + CommonLib.VERSION_STRING + "\n"
					+ "Connection   : " + CommonLib.getNetworkState(context) + "_" + CommonLib.getNetworkType(context) + "\n"
					+ "Identifier   : " + prefs.getString("app_id", "") + "\n"
					+ "User Id     	: " + prefs.getInt("uid", 0) + "\n"
					+ "&device=" + android.os.Build.DEVICE);

			FileOutputStream fOut = openFileOutput("log.txt",MODE_WORLD_READABLE);
			File file = getFileStreamPath("log.txt");
			Uri uri = Uri.fromFile(file);
			OutputStreamWriter osw = new OutputStreamWriter(fOut); 
			osw.write(LogString);
			osw.flush();
			osw.close();
			i.putExtra(Intent.EXTRA_STREAM, uri);
		}
		catch(Exception e){}

		try {
			startActivityForResult(Intent.createChooser(i, getResources().getString(R.string.send_mail)),EMAIL_FEEDBACK);
		}catch (android.content.ActivityNotFoundException ex) {
			showToast(getResources().getString(R.string.no_email_clients));

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode ==  EMAIL_FEEDBACK){
			deleteFile("log.txt");
			feedbackEmailText.setEnabled(true);
			//onBackPressed();
		}
	}

	private void fixSizes() {

//		findViewById(R.id.header_button_left).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				goBack(v);
//			}
//		});
//
//		findViewById(R.id.header_button_left).setPadding(screenWidth/20, 0, screenWidth/20, 0);
//		((TextView)findViewById(R.id.header_button_left)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.size16));
//		((TextView)findViewById(R.id.header_button_right_icon)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.size14));
//		findViewById(R.id.header_button_right).getLayoutParams().width = 3* screenWidth/20;
//		findViewById(R.id.header_button_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.header).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 3*screenWidth/20));
		((TextView) findViewById(R.id.header_text)).setText(getResources().getString(R.string.feedback_title));

		findViewById(R.id.submit_button).getLayoutParams().height = screenWidth/10;
		findViewById(R.id.submit_button).setEnabled(false);
		findViewById(R.id.submit_button).setClickable(false);

		findViewById(R.id.feedback_content).getLayoutParams().height = screenHeight/2;
		EditText feedbackContent = (EditText) findViewById(R.id.feedback_content);
		feedbackContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().equals("") || s.toString().trim().length() < 1 )
				{
//					findViewById(R.id.header_button_right).setVisibility(View.INVISIBLE);
					/*findViewById(R.id.submit_button).setEnabled(false);
					findViewById(R.id.submit_button).setClickable(false);
					findViewById(R.id.submit_button).setBackgroundColor(getResources().getColor(R.color.zhl_dark));*/
				}
				else
				{/*
					findViewById(R.id.submit_button).setEnabled(true);
					findViewById(R.id.submit_button).setClickable(true);
					findViewById(R.id.submit_button).setBackgroundDrawable(getResources().getDrawable(R.drawable.greenbuttonfeedback));*/
//					findViewById(R.id.header_button_right).setVisibility(View.VISIBLE);
//					findViewById(R.id.header_button_right).setBackgroundDrawable(getResources().getDrawable(R.drawable.greenbuttonfeedback));
//					findViewById(R.id.header_button_right).setEnabled(true);
//					findViewById(R.id.header_button_right).setClickable(true);
				}
			}
		});

		feedbackContent.setPadding(screenWidth/40, screenWidth/40, screenWidth/40, 0);
		findViewById(R.id.feedback_container).setPadding(screenWidth/20, screenWidth/20, screenWidth/20, screenWidth/20);
	}


	private String getLogString()
	{
		final String LogString = new String(  "App Version  : " + CommonLib.VERSION_STRING + "\n"
				+ "Connection   : " + CommonLib.getNetworkState(this) + "_" + CommonLib.getNetworkType(this) + "\n"
				+ "Identifier   : " + prefs.getString("app_id", "") + "\n"
				+ "User Id     	: " + prefs.getInt("uid", 0) + "\n"
				+ "&device=" + android.os.Build.DEVICE);

		return LogString;
	}

	@Override
	public void onBackPressed() {
		InputMethodManager imm = (InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(findViewById(R.id.feedback_content).getWindowToken(), 0);
		FeedbackActivity.this.finish();
		//overridePendingTransition(R.anim.no_anim, R.anim.slide_out_bottom);
	}



	public void submit(View v) {

		if(!((EditText)findViewById(R.id.feedback_content)).getText().toString().equals(""))
		{
			String message = ((EditText)findViewById(R.id.feedback_content)).getText().toString();
//			new sendFeedback().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message );
		}
	}

	public void proceed(View v){
		if(!((EditText)findViewById(R.id.feedback_content)).getText().toString().equals("") && ((EditText)findViewById(R.id.feedback_content)).getText().toString().trim().length()>0)
		{
			String message = ((EditText)findViewById(R.id.feedback_content)).getText().toString();
//			new sendFeedback().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message );
		}
	}

	
	public void goBack(View view){
		onBackPressed();	
	}

}
