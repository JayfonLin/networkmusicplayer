package cn.cvte.activities;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import cn.cvte.music.SimpleMusicPlayerService;
import cn.cvte.network.BroadcastClient;
import cn.cvte.network.ProcessThread;
import cn.cvte.network.TCPClient;
import cn.cvte.network.TCPServer;
import cn.cvte.network.UDPServer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MPApplication extends Application{
	/**
	 * static field
	 */
	public static final int TCPSERVER_PORT = 6789;
	public static boolean online = true;
	public static SimpleMusicPlayerService smpService;
	
	public static ServerSocket serverSocket = null;
	Socket mSocket = null;
	public static UDPServer udpServer;
	public static TCPServer tcpServer;
	
	@Override
	public void onCreate() {
		setupServer();
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		online = false;
		close();
		stopMusicPlayerService();
		super.onTerminate();
	}
	
	private void setupServer(){
		try{
			udpServer = new UDPServer(getApplicationContext());
			System.out.println("udp server setup!");
			Thread thread = new Thread(udpServer);
			thread.start();
			
			serverSocket = new ServerSocket(TCPSERVER_PORT);
			System.out.println("tcp server setup!");
			tcpServer = new TCPServer(serverSocket);
			Thread t = new Thread(tcpServer);
			t.start();
		} catch (Exception exception){
			exception.printStackTrace();
		} finally {
			
		}
	}
	
	private void close(){
		TCPClient.close();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopMusicPlayerService(){
		smpService.stopSelf();
		
	}
}
