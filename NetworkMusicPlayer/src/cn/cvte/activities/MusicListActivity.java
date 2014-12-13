package cn.cvte.activities;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cn.cvte.music.MusicFile;
import cn.cvte.music.MusicInfo;
import cn.cvte.music.MusicListAdapter;
import cn.cvte.music.SimpleMusicPlayerService;
import cn.cvte.music.SimpleMusicPlayerService.STATE;
import cn.cvte.network.TCPClient;
import cn.cvte.networkmusicplayer.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
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
	LinearLayout controlLayout;
	Button playButton, pauseButton, stopButton, nextButton;
	TextView state_tv, file_tv;
	ProgressDialog dialog;
	Handler controlHandler;
	MusicListAdapter adapter;
	Button btn;
	TextView tv;
	String deviceName;
	int playingMusicID = -1;
	Context context = this;
	boolean myDev = true;
	ServiceConnection sc = new ServiceConnection() {
        
        public void onServiceDisconnected(ComponentName name) {
            MPApplication.smpService = null;
        }
        
        public void onServiceConnected(ComponentName name, IBinder service) {
        	MPApplication.smpService = ((SimpleMusicPlayerService.SMPlayerBinder)service).getService();
            System.out.println("onServiceConnected");
        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		processIntent();
		init();
		findViews();
		loadFile();
		setHandler();
		setClick();
	}
	
	@Override
	protected void onDestroy() {
		unbindService(sc);
		if (TCPClient.getInstance() != null)
			TCPClient.getInstance().removeHandler(controlHandler);
		super.onDestroy();
	}


	private void processIntent(){
    	Intent intent2 = new Intent(getApplicationContext(), SimpleMusicPlayerService.class);
        startService(intent2);
        Intent bindent = new Intent(getApplicationContext(), SimpleMusicPlayerService.class);
        bindService(bindent, sc, BIND_AUTO_CREATE);
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
		controlLayout = (LinearLayout) findViewById(R.id.header_layout);
		if (myDev){
			controlLayout.setVisibility(View.GONE);
			
		}else{
			controlLayout.setVisibility(View.VISIBLE);
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
					if (simpleResult(msg))
						state_tv.setText(getResources().getString(R.string.t_playing));
					else failop();
					break;
				case 1:
					if (simpleResult(msg))
						state_tv.setText(getResources().getString(R.string.t_pause));
					else failop();
					break;
				case 2:
					if (simpleResult(msg))
						state_tv.setText(getResources().getString(R.string.t_stop));
					else failop();
					break;
				case 3:
					if (simpleResult(msg)){
						state_tv.setText(getResources().getString(R.string.t_playing));
						String name = msg.getData().getString("path");
						int i = name.lastIndexOf("/");
						name = name.substring(i+1);
						file_tv.setText(name);
					}else failop();
					break;
				case 4:
					Bundle b = msg.getData();
					ArrayList<MusicInfo> list = b.getParcelableArrayList("musicList");
					MPApplication.smpService.setMusicList(list);
					if (adapter != null){
						adapter.setListData(list);
					}else{
						adapter = new MusicListAdapter(MusicListActivity.this, list);
						audioList.setAdapter(adapter);
					}
					break;
				case 5:
					setState(msg);
					break;
				case -1:
					Toast.makeText(MusicListActivity.this, "操作失败！", Toast.LENGTH_SHORT).show();
					break;
				}
			}
			
		};
		if (TCPClient.getInstance() != null)
			TCPClient.getInstance().setHandler(controlHandler);
	}

	private void setState(Message msg){
		Bundle b2 = msg.getData();
		String result = (String) msg.obj;
		if ("success".equals(result)){
			String path = b2.getString("path");
			String stateStr = b2.getString("state");
			STATE state = STATE.stringToState(stateStr);
			switch(state){
			case IDLE:
				state_tv.setText(getResources().getString(R.string.t_idle));
				break;
			case PALYING:
				state_tv.setText(getResources().getString(R.string.t_playing));
				break;
			case PAUSE:
				state_tv.setText(getResources().getString(R.string.t_pause));
				break;
			case STOP:
				state_tv.setText(getResources().getString(R.string.t_stop));
				break;
			}
			
			int i = path.lastIndexOf("/");
			String name = path.substring(i+1);
			file_tv.setText(name);
		}
	}
	private boolean simpleResult(Message msg){
		String str = (String) msg.obj;
		return "success".equals(str);
	}
	private void failop(){
		Toast.makeText(MusicListActivity.this, "操作失败！", Toast.LENGTH_SHORT).show();
	}
	
	private void loadFile(){
		if (myDev){
			LoadMusicTask task = new LoadMusicTask();
			task.execute(MusicListActivity.this);
		}else{
			TCPClient.getInstance().getMusicList();
			TCPClient.getInstance().getState();
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
					b.putParcelable("music", info);
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
				TCPClient.getInstance().write("play\n");
			}
		});
		pauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TCPClient.getInstance().write("pause\n");
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				TCPClient.getInstance().write("stop\n");
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
		TCPClient.getInstance().selectMusic(path);
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
				MPApplication.smpService.setMusicList(MusicFile.musicInfoList);
			}else {
				Toast.makeText(getApplicationContext(), "SD卡尚未插入", Toast.LENGTH_SHORT).show();
			}
			
			dialog.dismiss();
			super.onPostExecute(result);
		}
	}

}
