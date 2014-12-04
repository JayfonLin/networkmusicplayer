package cn.cvte.activities;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import cn.cvte.network.BroadcastClient;
import cn.cvte.network.ProcessThread;
import cn.cvte.network.TCPClient;
import cn.cvte.network.TCPServer;
import cn.cvte.network.UDPServer;

import android.app.Application;

public class MPApplication extends Application{
	/**
	 * static field
	 */
	public static final int TCPSERVER_PORT = 6789;
	public static boolean online = true;
	static final int UDP_CLIENT_PORT = 9998;
	public static DatagramSocket udpSocket;
	
	public static ServerSocket serverSocket = null;
	Socket mSocket = null;
	public static UDPServer udpServer;
	public static TCPClient tcpClient;
	
	@Override
	public void onCreate() {
		setupServer();
		setupClient();
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		online = false;
		close();
		super.onTerminate();
	}
	
	private void setupServer(){
		try{
			udpServer = new UDPServer(getApplicationContext());
			Thread thread = new Thread(udpServer);
			thread.start();
			
			serverSocket = new ServerSocket(TCPSERVER_PORT);
			System.out.println("tcp server set up!");
			Thread t = new Thread(new TCPServer(serverSocket));
			t.start();
		} catch (Exception exception){
			exception.printStackTrace();
		} finally {
			
		}
	}
	
	private void setupClient(){
		try {
			System.out.println("udp client setup");
			udpSocket = new DatagramSocket(UDP_CLIENT_PORT);
			udpSocket.setBroadcast(true);
			udpSocket.setSoTimeout(0);
		} catch (SocketException e) {
			System.out.println("Do not support broadcast");
			e.printStackTrace();
		}
	}
	
	private void close(){
		udpSocket.close();
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}