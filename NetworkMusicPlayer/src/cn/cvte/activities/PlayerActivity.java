package cn.cvte.activities;

import java.text.DecimalFormat;

import cn.cvte.music.MusicInfo;
import cn.cvte.music.SimpleMusicPlayerService;
import cn.cvte.music.SimpleMusicPlayerService.STATE;
import cn.cvte.network.TCPServer;
import cn.cvte.networkmusicplayer.R;

import android.R.menu;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
 
public class PlayerActivity extends Activity {
	
    Handler handler = new Handler();
    Handler controlHandler;
    
    Button playButton, stopButton;
    TextView fileView, timeView1, timeView2;
    TextView stateTextView;
    SeekBar sb;
    
    String preFile = "";
    public MusicInfo musicInfo;
    Context context = this;
    ServiceConnection sc = new ServiceConnection() {
        
        public void onServiceDisconnected(ComponentName name) {
            MPApplication.smpService = null;
        }
        
        public void onServiceConnected(ComponentName name, IBinder service) {
        	MPApplication.smpService = ((SimpleMusicPlayerService.SMPlayerBinder)service).getService();
            System.out.println("onServiceConnected");
            if (musicInfo != null){
            	MPApplication.smpService.playOrPause(musicInfo.data);
            }
            
        	updateByStatus();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        findViews();
        setClicks();
        
        processIntent();
        handler.post(r);
    }
    
    private void findViews(){
    	timeView1 = (TextView) findViewById(R.id.time1);
        timeView2 = (TextView) findViewById(R.id.time2);
        fileView = (TextView) findViewById(R.id.file);
        playButton = (Button) findViewById(R.id.play);
        stopButton = (Button) findViewById(R.id.stop);
        sb = (SeekBar) findViewById(R.id.seekBar1);
        stateTextView = (TextView)findViewById(R.id.state);
    }
    
    private void setClicks(){
    	playButton.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
            	playOrPause(null);
            }
        });
        stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stop();
			}
		});
    	setHandler();
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                
            }
            
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                
            }
            
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                if  (fromUser){
                	MPApplication.smpService.setCurrentPosition((float)progress/seekBar.getMax());
                    if (progress == seekBar.getMax() && 
                    		MPApplication.smpService.getState() == STATE.PALYING){
                    	MPApplication.smpService.setState(STATE.PAUSE);
                    }
                }
            }
        });
    }
    
    private void playOrPause(String path){
    	MPApplication.smpService.playOrPause(path);
    	if (path != null){
    		int i = path.lastIndexOf("/");
    		if (i != -1){
    			path = path.substring(i+1);
    			fileView.setText(path);
    		}
    	}
        updateByStatus();
    }
    
    private void stop(){
    	MPApplication.smpService.stop();
    	updateByStatus();
    }
    
    private void setHandler(){
    	controlHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 0:
					String path = (String) msg.obj;
					playOrPause(path);
					break;
				case 1:
					playOrPause(null);
					break;
				case 2:
					stop();
					break;
				}
			}
    		
    	};
    	if (TCPServer.pt != null){
    		TCPServer.pt.setHandler(controlHandler);
    	}
    }
    
    private void processIntent(){
    	Intent intent = getIntent();
    	if (intent.getSerializableExtra("music") instanceof MusicInfo){
    		musicInfo = (MusicInfo) intent.getSerializableExtra("music");
    	}
    	Intent intent2 = new Intent(context, SimpleMusicPlayerService.class);
        startService(intent2);
        Intent bindent = new Intent(context, SimpleMusicPlayerService.class);
        bindService(bindent, sc, BIND_AUTO_CREATE);
    }
    

   @Override
    protected void onDestroy() {
       handler.removeCallbacks(r);
       if (TCPServer.pt != null)
    	   TCPServer.pt.unRegisterHandler(controlHandler);
       unbindService(sc);
        super.onDestroy();
    }

    protected void updateByStatus() {
        if (MPApplication.smpService == null){
            return;
        }
        int pos=MPApplication.smpService.getCurrentPosition();
        
        if (MPApplication.smpService.getState() == STATE.IDLE){
            playButton.setText("²¥·Å");
            stopButton.setEnabled(false);
            stateTextView.setText(getResources().getString(R.string.t_idle));
            fileView.setText("");
        }else if (MPApplication.smpService.getState() == STATE.PALYING){
            playButton.setText("ÔÝÍ£");
            stopButton.setEnabled(true);
            stateTextView.setText(getResources().getString(R.string.t_playing));
        }else if (MPApplication.smpService.getState() == STATE.PAUSE){
            playButton.setText("¼ÌÐø");
            stopButton.setEnabled(true);
            stateTextView.setText(getResources().getString(R.string.t_pause));
        }else if (MPApplication.smpService.getState() == STATE.STOP){
            playButton.setText("²¥·Å");
            stopButton.setEnabled(false);
            stateTextView.setText(getResources().getString(R.string.t_stop));
        }
        if (MPApplication.smpService.getState() != STATE.IDLE){
            fileView.setText(musicInfo.name);
        }
        String s1, s2;
        
        int min = (pos/1000)/60;
        int sec = (pos/1000)%60;
        s1 = new DecimalFormat("00").format(min);
        s2 = new DecimalFormat("00").format(sec);
        timeView1.setText(s1+":"+s2);

        String s3, s4;
        int pos2=MPApplication.smpService.getDuration();
        int min2 = (pos2/1000)/60;
        int sec2 = (pos2/1000)%60;
        s3 = new DecimalFormat("00").format(min2);
        s4 = new DecimalFormat("00").format(sec2);
        timeView2.setText(" / "+s3+":"+s4);
        if (!preFile.equals(musicInfo.data)){
            preFile = musicInfo.data;
            sb.setMax(pos2);
        }
        sb.setProgress(pos);
    }
    
    private Runnable r = new Runnable() {
            
            public void run() {
                updateByStatus();
                handler.postDelayed(r, 1000);
            }
    };
        
}
