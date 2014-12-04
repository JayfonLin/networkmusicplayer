package cn.cvte.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cvte.activities.MPApplication;
import cn.cvte.activities.SearchDevicesActivity;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class UDPServer implements Runnable{
	final static int RECEIVE_LENGTH = 1024;
	public final static int SERVER_PORT = 30000;
	public static int TTLTime = 1;
	Handler mHandler;
	DatagramSocket socket;
	
	Context mContext;
	public UDPServer(Context context){
		mContext = context;
		try {
			socket = new DatagramSocket(SERVER_PORT);
			socket.setSoTimeout(0);
			
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	
	public void setHandler(Handler pHandler){
		mHandler = pHandler;
	}
	
	@Override
	public void run() {
		
		try {
			while (MPApplication.online){
				DatagramPacket dp = new DatagramPacket(new byte[RECEIVE_LENGTH], RECEIVE_LENGTH);
				socket.receive(dp);
				String msg = new String(dp.getData()).trim();
				System.out.println(msg);
				String command = msg.split("#")[0];
				String address = msg.split("#")[1];
				if ("hello".equals(command)){
					DatagramPacket packet;
					String serverAddress;
					serverAddress = BroadcastClient.getLocalIpAddress(mContext).toString().split("/")[1];
					System.out.println("ip:"+serverAddress);
					if (!address.equals(serverAddress)){
						byte[] sendMsg = ("welcome"+"#"+serverAddress).getBytes();
						packet = new DatagramPacket(sendMsg,sendMsg.length,
								InetAddress.getByName(address), SERVER_PORT);
						socket.send(packet);
					}
				}else if ("welcome".equals(command)){
					boolean flag = true;
					for (Map<String, Object> map: SearchDevicesActivity.deviceInfoList){
						if (map.containsValue(address)){
							flag = false;
							break;
						}
					}
					if (flag){
						Map<String, Object> map = new HashMap<String, Object>();
				        map.put("ip", address);
						SearchDevicesActivity.deviceInfoList.add(map);
						if (mHandler != null)
							mHandler.sendEmptyMessage(0);
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			socket.close();
		}
	}
}
