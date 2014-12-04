package cn.cvte.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cn.cvte.activities.MPApplication;

public class TCPServer implements Runnable{
	ServerSocket serverSocket;
	static Socket mSocket;
	public static ProcessThread pt;
	public TCPServer(ServerSocket socket){
		serverSocket = socket;
	}
	@Override
	public void run() {
		try {
			while(MPApplication.online){
				mSocket = serverSocket.accept();
				System.out.println("accept");
				if (mSocket != null){
					mSocket.close();
					mSocket = null;
				}
				pt = new ProcessThread(mSocket);
	            Thread thread2 = new Thread(pt);   
	            thread2.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				mSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
	}

}
