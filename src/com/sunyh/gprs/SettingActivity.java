package com.sunyh.gprs;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gfan.sdk.statitistics.GFAgent;

public class SettingActivity extends Activity {

	protected void onResume() {
		super.onResume();
		if (AD.GF)
			GFAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();

		if (AD.GF)
			GFAgent.onPause(this);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting);
		final MyApp app = (MyApp) this.getApplication();
		app.prop = PropertiesUtil.loadConfig();

		TextView textView = (TextView) findViewById(R.id.dayGPRSMAX);
		textView.setText(app.getProp("dayGPRSMax"));

		textView = (TextView) findViewById(R.id.dayWifiMax);
		textView.setText(app.getProp("dayWifiMax"));

		textView = (TextView) findViewById(R.id.monthGPRSMAX);
		textView.setText(app.getProp("monthGPRSMax"));

		textView = (TextView) findViewById(R.id.monthWifiMax);
		textView.setText(app.getProp("monthWifiMax"));

		CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox1);
		checkBox.setChecked(Boolean.valueOf(app.getProp("checkbox1")));

		Button bOK = (Button) findViewById(R.id.ok);
		bOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SettingActivity.this,
						MainActivity.class);
				startActivity(intent);

				TextView textView = (TextView) findViewById(R.id.dayGPRSMAX);
				app.putProp("dayGPRSMax", textView.getText());

				textView = (TextView) findViewById(R.id.dayWifiMax);
				app.putProp("dayWifiMax", textView.getText());

				textView = (TextView) findViewById(R.id.monthGPRSMAX);
				app.putProp("monthGPRSMax", textView.getText());
				textView = (TextView) findViewById(R.id.monthWifiMax);
				app.putProp("monthWifiMax", textView.getText());

				CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox1);
				app.putProp("checkbox1", checkBox.isChecked());

				PropertiesUtil.saveConfig(app.prop);

				Warning.warns1 = Warning.initWarning();
				Warning.warns2 = Warning.initWarning();
			}
		});

		Button bCancel = (Button) findViewById(R.id.cancel);
		bCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SettingActivity.this,
						MainActivity.class);
				startActivity(intent);
				SettingActivity.this.onDestroy();
			}
		});

		if (AD.youmi) {
			AdManager.init(this, "198762f972d578e2", "119ca3646511d528", 30,
					false);
			LinearLayout adViewLayout = (LinearLayout) findViewById(R.id.adViewLayout3);
			adViewLayout.addView(new AdView(this), new LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}
}