package cn.cvte.activities;

import java.text.DecimalFormat;

import cn.cvte.music.MPObserver;
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
 
public class PlayerActivity extends Activity implements MPObserver{
	
    
    Handler controlHandler;
    
    Button playButton, stopButton;
    TextView fileView, timeView1, timeView2;
    TextView stateTextView;
    SeekBar sb;
    
    String preFile = "";
    public MusicInfo musicInfo;
    Context context = this;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        findViews();
        setClicks();
        processIntent();
        MPApplication.smpService.registerObserver(this);
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
    		updateFileView(path);
    	}
    }
    
    private void stop(){
    	MPApplication.smpService.stop();
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
    	if (intent.getParcelableExtra("music") instanceof MusicInfo){
    		musicInfo = (MusicInfo) intent.getParcelableExtra("music");
    		if (musicInfo != null){
    			playOrPause(musicInfo.data);
    		}
    	}
    }
    

   @Override
    protected void onDestroy() {
	   MPApplication.smpService.removeObserver(this);
       if (TCPServer.pt != null)
    	   TCPServer.pt.unRegisterHandler(controlHandler);
        super.onDestroy();
    }
    
    private void updateFileView(String path){
    	if (path == null){
    		fileView.setText(getResources().getString(R.string.t_idle));
    		return;
    	}
    	int i = path.lastIndexOf("/");
    	path = path.substring(i+1);
    	fileView.setText(path);
    }

	@Override
	public void update(String path, STATE state, int curPos, int duration) {
        if (state == STATE.IDLE){
            playButton.setText(getResources().getString(R.string.s_play));
            stopButton.setEnabled(false);
            stateTextView.setText(getResources().getString(R.string.t_idle));
            updateFileView(null);
        }else if (state == STATE.PALYING){
            playButton.setText(getResources().getString(R.string.s_pause));
            stopButton.setEnabled(true);
            stateTextView.setText(getResources().getString(R.string.t_playing));
            updateFileView(path);
        }else if (state == STATE.PAUSE){
            playButton.setText(getResources().getString(R.string.s_continue));
            stopButton.setEnabled(true);
            stateTextView.setText(getResources().getString(R.string.t_pause));
            updateFileView(path);
        }else if (state == STATE.STOP){
            playButton.setText(getResources().getString(R.string.s_play));
            stopButton.setEnabled(false);
            stateTextView.setText(getResources().getString(R.string.t_stop));
            updateFileView(path);
        }
        String s1, s2;
        
        int min = (curPos/1000)/60;
        int sec = (curPos/1000)%60;
        s1 = new DecimalFormat("00").format(min);
        s2 = new DecimalFormat("00").format(sec);
        timeView1.setText(s1+":"+s2);

        String s3, s4;
        int min2 = (duration/1000)/60;
        int sec2 = (duration/1000)%60;
        s3 = new DecimalFormat("00").format(min2);
        s4 = new DecimalFormat("00").format(sec2);
        timeView2.setText(" / "+s3+":"+s4);
        if (!preFile.equals(musicInfo.data)){
            preFile = musicInfo.data;
            sb.setMax(duration);
        }
        sb.setProgress(curPos);
	}
        
}
