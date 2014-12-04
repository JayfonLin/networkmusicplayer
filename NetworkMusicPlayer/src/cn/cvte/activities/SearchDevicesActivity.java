package cn.cvte.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import cn.cvte.network.BroadcastClient;
import cn.cvte.network.ProcessThread;
import cn.cvte.network.TCPClient;
import cn.cvte.network.UDPServer;
import cn.cvte.networkmusicplayer.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SearchDevicesActivity extends Activity {
	
	/**
	 * static field
	 */
	public static List<Map<String, Object>> deviceInfoList;
	
	DatagramSocket ds;
	ListView deviceLV;
	Handler mHandler;
	Button btn;
	Button myDeviceBtn;
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices_list);
		deviceLV = (ListView) findViewById(R.id.listView1);
		setData();
		initViews();
		setupService();
		setClick();
		
	}
	private void initViews(){
		SimpleAdapter adapter = new SimpleAdapter(this, deviceInfoList, R.layout.device_item,
                new String[]{"ip"},
                new int[]{R.id.ip_tv});
		deviceLV.setAdapter(adapter);
		btn = (Button)findViewById(R.id.button1);
		myDeviceBtn = (Button) findViewById(R.id.button2);
		dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
	}
	private void setupService(){
		mHandler = new Handler(){
			@Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case 0:
	            	((SimpleAdapter)deviceLV.getAdapter()).notifyDataSetChanged();
	            	break;
	            }
			}
		};
		MPApplication.udpServer.setHandler(mHandler);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Thread t = new Thread(new BroadcastClient(SearchDevicesActivity.this));
				t.start();
			}
		});
		myDeviceBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				jumpToMusicList(null);
			}
		});
	}
	private void setData(){
		deviceInfoList = new ArrayList<Map<String, Object>>();
	}
	
	private void setClick(){
		deviceLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String address = deviceInfoList.get(arg2).get("ip").toString();
				ConnectTask task = new ConnectTask();
				task.execute(address);
				
			}
		});
	}
	
	private void jumpToMusicList(String address){
		Intent intent = new Intent(SearchDevicesActivity.this, MusicListActivity.class);
		if (address != null)
			intent.putExtra("address", address);
		startActivity(intent);
		finish();
	}
	
	class ConnectTask extends AsyncTask<String, Integer, String>
	{
		
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			if (TCPClient.clientSocket == null){
				Toast.makeText(SearchDevicesActivity.this, "该设备连接不上", Toast.LENGTH_SHORT).show();
			}else{
				jumpToMusicList(result);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... arg0) {
			new TCPClient(arg0[0]);
			return arg0[0];
			
		}
		
	}

}
