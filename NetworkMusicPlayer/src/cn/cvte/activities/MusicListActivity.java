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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicListActivity extends Activity{
	
	ListView audioList;
	LinearLayout headerLayout;
	Button playButton, pauseButton, stopButton, nextButton;
	TextView state_tv, file_tv;
	ProgressDialog dialog;
	Handler controlHandler;
	MusicListAdapter adapter;
	Button btn;
	TextView tv;
	String deviceName;
	int playingMusicID = -1;
	
	boolean myDev = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		init();
		findViews();
		loadFile();
		setHandler();
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
		}else{
			headerLayout.setVisibility(View.VISIBLE);
		}
		state_tv = (TextView) findViewById(R.id.state);
		file_tv = (TextView) findViewById(R.id.file);
		playButton = (Button) findViewById(R.id.play);
		pauseButton = (Button) findViewById(R.id.pause);
		stopButton = (Button) findViewById(R.id.stop);
		nextButton = (Button) findViewById(R.id.next);
		btn = (Button) findViewById(R.id.button2);
		tv = (TextView) findViewById(R.id.textView1);
		tv.setText(deviceName);
		
	}
	
	private void setHandler(){
		controlHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 0:
					state_tv.setText(getResources().getString(R.string.t_playing));
					break;
				case 1:
					state_tv.setText(getResources().getString(R.string.t_pause));
					break;
				case 2:
					state_tv.setText(getResources().getString(R.string.t_stop));
					break;
				case 3:
					state_tv.setText(getResources().getString(R.string.t_playing));
					String name = (String) msg.obj;
					int i = name.lastIndexOf("/");
					name = name.substring(i+1);
					file_tv.setText(name);
					break;
				case -1:
					Toast.makeText(MusicListActivity.this, "操作失败！", Toast.LENGTH_SHORT).show();
					break;
				}
			}
			
		};
	}
	
	private void loadFile(){
		if (myDev){
			LoadMusicTask task = new LoadMusicTask();
			task.execute(MusicListActivity.this);
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
					playSpcMusic(info.data);
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
				new Thread(new ControlThread("play", controlHandler)).start();
			}
		});
		pauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new ControlThread("pause", controlHandler)).start();
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				new Thread(new ControlThread("stop", controlHandler)).start();
			}
		});
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path;
				playingMusicID = (playingMusicID+1)%audioList.getAdapter().getCount();
				path = ((MusicInfo)audioList.getAdapter().getItem(playingMusicID)).data;
				playSpcMusic(path);
			}
		});
	}
	
	private void playSpcMusic(String path){
		SelectMusicTask task = new SelectMusicTask(controlHandler);
		task.execute(path);
	}
	class ControlThread implements Runnable
	{
		String command;
		Handler stateHandler;
		public ControlThread(String cm, Handler pHandler){
			command = cm;
			stateHandler = pHandler;
		}
		@Override
		public void run() {
			boolean result = TCPClient.simpleControl(command);
			if (result){
				if ("play".equals(command))
					stateHandler.sendEmptyMessage(0);
				if ("pause".equals(command))
					stateHandler.sendEmptyMessage(1);
				if ("stop".equals(command))
					stateHandler.sendEmptyMessage(2);
			} else{
				stateHandler.sendEmptyMessage(-1);
			}
		}
		
	}
	class SelectMusicTask extends AsyncTask<String, Integer, Boolean>
	{
		Handler mHandler;
		String filePath;
		public SelectMusicTask(Handler pHandler){
			mHandler = pHandler;
		}
		@Override
		protected Boolean doInBackground(String... params) {
			filePath = params[0];
			return TCPClient.selectMusic(filePath);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result.booleanValue()){
				Message msg = new Message();
				msg.what = 3;
				msg.obj = filePath;
				mHandler.sendMessage(msg);
			}else{
				mHandler.sendEmptyMessage(-1);
			}
		}
		
	}
	class LoadMusicTask extends AsyncTask<Context, Integer, Boolean>
	{
		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(Context... params) {
			MusicFile mf = new MusicFile(params[0]);
			return mf.load();
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result.booleanValue()){
				adapter = new MusicListAdapter(MusicListActivity.this, MusicFile.musicInfoList);
				audioList.setAdapter(adapter);
				
			}else {
				Toast.makeText(getApplicationContext(), "SD卡尚未插入", Toast.LENGTH_SHORT).show();
			}
			
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
			System.out.println("result list size:"+result.size());
			/*MusicListAdapter adapter2 = new MusicListAdapter(MusicListActivity.this, result);
			audioList.setAdapter(adapter2);*/
			//adapter.notifyDataSetChanged();
			if (adapter != null){
				adapter.setListData(result);
			}else{
				adapter = new MusicListAdapter(MusicListActivity.this, result);
				audioList.setAdapter(adapter);
			}
			dialog.dismiss();
			super.onPostExecute(result);
		}
	}
}
