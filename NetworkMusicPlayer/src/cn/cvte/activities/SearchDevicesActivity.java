package cn.cvte.activities;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import cn.cvte.network.BroadcastClient;
import cn.cvte.network.UDPServer;
import cn.cvte.networkmusicplayer.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SearchDevicesActivity extends Activity {
	DatagramSocket ds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices_list);
		try {
			ds = new DatagramSocket(30000);
			//ds.setBroadcast(true);
			ds.setSoTimeout(0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception");
			e.printStackTrace();
		}
		final DatagramSocket socket;
		try {
			socket = new DatagramSocket(9998);
			socket.setBroadcast(true);
			socket.setSoTimeout(0);
			Thread thread = new Thread(new UDPServer(ds));
			thread.start();
			Button btn = (Button)findViewById(R.id.button1);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Thread t = new Thread(new BroadcastClient(socket, SearchDevicesActivity.this));
					t.start();
				}
			});
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
