package cn.cvte.activities;

import java.text.DecimalFormat;

import cn.cvte.music.MusicInfo;
import cn.cvte.music.SimpleMusicPlayerService;
import cn.cvte.music.SimpleMusicPlayerService.STATE;
import cn.cvte.networkmusicplayer.R;

import android.R.menu;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
    SimpleMusicPlayerService smpService;
    Button playButton, exitButton;
    String preFile = "";
    public MusicInfo musicInfo;
    //public static String PATH = null;
    TextView fileView, timeView1, timeView2;
    TextView stateTextView;
    SeekBar sb;
    Context context = this;
    ServiceConnection sc = new ServiceConnection() {
        
        public void onServiceDisconnected(ComponentName name) {
            smpService = null;
        }
        
        public void onServiceConnected(ComponentName name, IBinder service) {
            smpService = ((SimpleMusicPlayerService.SMPlayerBinder)service).getService();
            updateByStatus();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        
        processIntent();
        timeView1 = (TextView) findViewById(R.id.time1);
        timeView2 = (TextView) findViewById(R.id.time2);
        fileView = (TextView) findViewById(R.id.file);
        playButton = (Button) findViewById(R.id.play);
        playButton.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
            	System.out.println("musicinfo.data:"+musicInfo.data);
            	System.out.println("musicInfo.name:"+musicInfo.name);
            	if (smpService == null)
            		System.out.println("smpService is null");
                smpService.playOrPause(musicInfo.data);
                fileView.setText(musicInfo.name);
                updateByStatus();
            }
        });
        exitButton = (Button) findViewById(R.id.btn_exit);
        exitButton.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                Intent intent = new Intent(context, SimpleMusicPlayerService.class);
                stopService(intent);
                finish();
                
            }
        });
        sb = (SeekBar) findViewById(R.id.seekBar1);
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
                    smpService.setCurrentPosition((float)progress/seekBar.getMax());
                    if (progress == seekBar.getMax() && smpService.getState() == STATE.PALYING){
                        smpService.setState(STATE.PAUSE);
                    }
                }
            }
        });
        stateTextView = (TextView)findViewById(R.id.state);
        
        handler.post(r);
    }
    
    private void processIntent(){
    	Intent intent = getIntent();
    	if (intent.getSerializableExtra("music") instanceof MusicInfo){
    		musicInfo = (MusicInfo) intent.getSerializableExtra("music");
    	}else{
    		System.out.println("not musicinfo object");
    	}
    	Intent intent2 = new Intent(context, SimpleMusicPlayerService.class);
        startService(intent2);
        Intent bindent = new Intent(context, SimpleMusicPlayerService.class);
        bindent.putExtra("path", musicInfo.data);
        bindService(bindent, sc, BIND_AUTO_CREATE);
    }
    

   @Override
    protected void onDestroy() {
       handler.removeCallbacks(r);
       unbindService(sc);
        super.onDestroy();
    }

    protected void updateByStatus() {
        if (smpService == null){
            return;
        }
        int pos=smpService.getCurrentPosition();
        
        if (smpService.getState() == STATE.IDLE){
            playButton.setText("²¥·Å");
            stateTextView.setText(getResources().getString(R.string.t_idle));
            fileView.setText("");
        }else if (smpService.getState() == STATE.PALYING){
            playButton.setText("ÔÝÍ£");
            stateTextView.setText(getResources().getString(R.string.t_playing));
        }else if (smpService.getState() == STATE.PAUSE){
            playButton.setText("¼ÌÐø");
            stateTextView.setText(getResources().getString(R.string.t_pause));
        }else{
            playButton.setText("²¥·Å");
            stateTextView.setText(getResources().getString(R.string.t_stop));
            
        }
        if (smpService.getState() != STATE.IDLE){
            fileView.setText(musicInfo.name);
        }
        String s1, s2;
        
        int min = (pos/1000)/60;
        int sec = (pos/1000)%60;
        s1 = new DecimalFormat("00").format(min);
        s2 = new DecimalFormat("00").format(sec);
        timeView1.setText(s1+":"+s2);

        String s3, s4;
        int pos2=smpService.getDuration();
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
