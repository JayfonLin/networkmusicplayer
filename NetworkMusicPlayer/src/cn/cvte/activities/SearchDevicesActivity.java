package cn.cvte.activities;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;

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
		
		Thread thread = new Thread(new UDPServer(ds));
		thread.start();
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Thread t = new Thread(new BroadcastClient(SearchDevicesActivity.this));
				t.start();
			}
		});
		
		ServerSocket miroSocket = null;
		Socket minetSocket = null;
		int serverPort = 6789;
		
		try{
			miroSocket = new ServerSocket(serverPort);
			System.out.println("welcome to miro...");
			while(true){
				minetSocket = miroSocket.accept();
				System.out.println("accept");
				/*ProcessThread pt = new ProcessThread(minetSocket, onlineUserList);
                Thread thread = new Thread(pt);   
				threadPool.execute(thread);*/
			}
		} catch (Exception exception){
			exception.printStackTrace();
		} finally {
			/*try{
				miroSocket.close();
				threadPool.shutdown();
			} catch (Exception exception){}*/
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
