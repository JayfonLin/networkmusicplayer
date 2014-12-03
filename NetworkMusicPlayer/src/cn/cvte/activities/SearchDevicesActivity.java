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
import android.content.Intent;
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
	DatagramSocket ds;
	public static final int TCPSERVER_PORT = 6789;
	ListView deviceLV;
	public static List<Map<String, Object>> deviceInfoList;
	public static TCPClient tcpClient;
	Handler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices_list);
		deviceLV = (ListView) findViewById(R.id.listView1);
		setData();
		SimpleAdapter adapter = new SimpleAdapter(this, deviceInfoList, R.layout.device_item,
                new String[]{"ip"},
                new int[]{R.id.ip_tv});
		
		deviceLV.setAdapter(adapter);
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
		Thread thread = new Thread(new UDPServer(SearchDevicesActivity.this));
		thread.start();
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Thread t = new Thread(new BroadcastClient(SearchDevicesActivity.this, mHandler));
				t.start();
			}
		});
		
		ServerSocket serverSocket = null;
		Socket mSocket = null;	
		
		try{
			serverSocket = new ServerSocket(TCPSERVER_PORT);
			System.out.println("welcome to miro...");
			while(true){
				mSocket = serverSocket.accept();
				System.out.println("accept");
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(mSocket.getInputStream()));
				System.out.println(inFromClient.readLine());
				ProcessThread pt = new ProcessThread(mSocket);
                Thread thread2 = new Thread(pt);   
                thread2.start();
				//threadPool.execute(thread);
			}
		} catch (Exception exception){
			exception.printStackTrace();
		} finally {
			/*try{
				miroSocket.close();
				threadPool.shutdown();
			} catch (Exception exception){}*/
		}
		deviceLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String address = deviceInfoList.get(arg2).get("ip").toString();
				tcpClient = new TCPClient(address);
				if (tcpClient == null){
					Toast.makeText(SearchDevicesActivity.this, "该设备连接不上", Toast.LENGTH_SHORT).show();
				}else{
					Intent intent = new Intent(SearchDevicesActivity.this, MusicListActivity.class);
					intent.putExtra("address", address);
					startActivity(intent);
					finish();
				}
			}
		});
	}
	
	void setData(){
		deviceInfoList = new ArrayList<Map<String, Object>>();
		 
        /*Map<String, Object> map = new HashMap<String, Object>();
        map.put("ip", "255.255.255.255");
        deviceInfoList.add(map);*/
 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
