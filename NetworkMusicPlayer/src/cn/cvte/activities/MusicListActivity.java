package cn.cvte.activities;

import java.net.Socket;
import java.util.List;

import cn.cvte.music.MusicFile;
import cn.cvte.music.MusicInfo;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MusicListActivity extends Activity{
	
	ListView audioList;
	LinearLayout headerLayout;
	Button playButton, pauseButton, stopButton, nextButton;
	TextView state_tv, file_tv;
	ProgressDialog dialog;
	MusicListAdapter adapter;
	Button btn;
	TextView tv;
	String deviceName;
	boolean myDev = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		init();
		findViews();
		loadFile();
		setClick();
	}
	private void init(){
		Intent intent = getIntent();
		
		deviceName = intent.getStringExtra("address");
		if (deviceName != null){
			myDev = false;
		}else
			deviceName = "我的设备";
		dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
	}
	
	private void findViews(){
		audioList = (ListView) findViewById(R.id.listView1);
		headerLayout = (LinearLayout) findViewById(R.id.header_layout);
		if (myDev){
			headerLayout.setVisibility(View.GONE);
		}
		playButton = (Button) findViewById(R.id.play);
		pauseButton = (Button) findViewById(R.id.pause);
		stopButton = (Button) findViewById(R.id.stop);
		nextButton = (Button) findViewById(R.id.next);
		btn = (Button) findViewById(R.id.button2);
		tv = (TextView) findViewById(R.id.textView1);
		tv.setText(deviceName);
		
	}
	
	private void loadFile(){
		if (myDev){
			LoadMusicTask task = new LoadMusicTask();
			task.execute(this);
		}else{
			GetNetworkMusic gnm = new GetNetworkMusic();
			gnm.execute();
		}
	}
	
	private void setClick(){
		audioList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MusicInfo info = (MusicInfo) audioList.getAdapter().getItem(arg2);
				if (myDev){
					Intent intent = new Intent(MusicListActivity.this, PlayerActivity.class);
					Bundle b = new Bundle();
					b.putSerializable("music", info);
					intent.putExtras(b);
					startActivity(intent);
				}else{
					
				}
			}
		});
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MusicListActivity.this, SearchDevicesActivity.class);
				startActivity(intent);
				finish();
			}
		});
		playButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new ControlThread("play")).start();
			}
		});
		pauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new ControlThread("pause")).start();
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				new Thread(new ControlThread("stop")).start();
			}
		});
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	class ControlThread implements Runnable
	{
		String command;
		public ControlThread(String cm){
			command = cm;
		}
		@Override
		public void run() {
			TCPClient.simpleControl(command);
		}
		
	}
	class SelectMusicTask extends AsyncTask<String, Integer, Boolean>
	{
		
		@Override
		protected Boolean doInBackground(String... params) {
			return TCPClient.selectMusic(params[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result.booleanValue()){
				//TODO
			}
		}
		
	}
	class LoadMusicTask extends AsyncTask<Context, Integer, Void>
	{
		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Context... params) {
			MusicFile mf = new MusicFile(params[0]);
			mf.load();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			adapter = new MusicListAdapter(MusicListActivity.this, MusicFile.musicInfoList);
			audioList.setAdapter(adapter);
			dialog.dismiss();
			super.onPostExecute(result);
		}
	}
	
	class GetNetworkMusic extends AsyncTask<String, Integer, List<MusicInfo>>
	{
		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}
		@Override
		protected List<MusicInfo> doInBackground(String... arg0) {
			return TCPClient.getMusicList();
		}
		@Override
		protected void onPostExecute(List<MusicInfo> result) {
			adapter = new MusicListAdapter(MusicListActivity.this, result);
			audioList.setAdapter(adapter);
			dialog.dismiss();
			super.onPostExecute(result);
		}
	}
}
