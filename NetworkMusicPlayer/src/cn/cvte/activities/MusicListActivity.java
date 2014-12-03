package cn.cvte.activities;

import java.net.Socket;

import cn.cvte.music.MusicFile;
import cn.cvte.music.MusicListAdapter;
import cn.cvte.network.TCPClient;
import cn.cvte.networkmusicplayer.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MusicListActivity extends Activity{
	ListView audioList;
	ProgressDialog dialog;
	MusicListAdapter adapter;
	Button btn;
	TextView tv;
	String deviceName = "我的设备";
	boolean myDev = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		Intent intent = getIntent();
		if (intent != null){
			myDev = false;
			deviceName = intent.getStringExtra("address");
			
		}
		audioList = (ListView) findViewById(R.id.listView1);
		btn = (Button) findViewById(R.id.button2);
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		tv = (TextView) findViewById(R.id.textView1);
		tv.setText(deviceName);
		if (myDev){
			LoadMusicTask task = new LoadMusicTask();
			task.execute(this);
		}else{
			
		}
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MusicListActivity.this, SearchDevicesActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	class LoadMusicTask extends AsyncTask<Context, Integer, Void>
	{
		@Override
		protected Void doInBackground(Context... params) {
			// TODO Auto-generated method stub
			MusicFile mf = new MusicFile(params[0]);
			mf.load();
			return null;
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter = new MusicListAdapter(MusicListActivity.this, MusicFile.musicInfoList);
			audioList.setAdapter(adapter);
			dialog.dismiss();
			//dialog.hide();
			super.onPostExecute(result);
		}

		
	}
}
