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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import cn.cvte.activities.SearchDevicesActivity;

public class BroadcastClient implements Runnable{
	static final int CLIENT_PORT = 9998;
	final static int RECEIVE_LENGTH = 1024;
	MulticastSocket multiSocket;
	DatagramSocket socket;
	Context mContext;
	Handler mHandler;
	public BroadcastClient(Context context, Handler pHandler) {
		mHandler = pHandler;
		try {
			socket = new DatagramSocket(CLIENT_PORT);
			socket.setBroadcast(true);
			socket.setSoTimeout(0);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mContext = context;
	}
	@Override
	public void run() {
		String serverAddress;
		try {
			serverAddress = getLocalIpAddress(mContext).toString().split("/")[1];
			System.out.println("ip:"+serverAddress);
			byte[] sendMsg = ("hello"+"#"+serverAddress).getBytes();
			DatagramPacket packet;
			packet = new DatagramPacket(sendMsg,sendMsg.length,
					InetAddress.getByName(UDPServer.BROADCAST_STR), UDPServer.SERVER_PORT);
			socket.send(packet);//发送报文
			
			DatagramPacket dp = new DatagramPacket(new byte[RECEIVE_LENGTH], RECEIVE_LENGTH);
			System.out.println("bb");
			socket.receive(dp);
			System.out.println("aa");
			String msg = new String(dp.getData()).trim();
			System.out.println(msg);
			String command = msg.split("#")[0];
			String address = msg.split("#")[1];
			if ("welcome".equals(command)){
				//deviceIPList.add(address);
				Map<String, Object> map = new HashMap<String, Object>();
		        map.put("ip", address);
				SearchDevicesActivity.deviceInfoList.add(map);
				tcpConnect(address);
				mHandler.sendEmptyMessage(0);
			}
			//socket.disconnect();//断开套接字
			//socket.close();//关闭套接字
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public static InetAddress getLocalIpAddress(Context context) throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        
        return InetAddress.getByName(String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
    }
	
	void tcpConnect(String address) throws UnknownHostException, IOException{
		Socket clientSocket = new Socket(InetAddress.getByName(address),
				SearchDevicesActivity.TCPSERVER_PORT);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream()));
		outToServer.writeUTF("high");
	}
}
