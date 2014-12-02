package cn.cvte.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import cn.cvte.activities.SearchDevicesActivity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class UDPServer implements Runnable{
	final static int RECEIVE_LENGTH = 1024;
	public final static int LOCAL_PORT = 9998;
	public final static String BROADCAST_STR = "255.255.255.255";
	public static int TTLTime = 1;
	
	DatagramSocket socket;
	public UDPServer(DatagramSocket pSocket){
		System.out.println("hh");
		Log.i("a", "hhh");
		socket = pSocket;
	}
	@Override
	public void run() {
		
		try {
			
			DatagramPacket dp = new DatagramPacket(new byte[RECEIVE_LENGTH], RECEIVE_LENGTH);
			System.out.println("bb");
			socket.receive(dp);
			System.out.println("aa");
			System.out.println(new String(dp.getData()).trim());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
		
		}
		
	}
	
}
