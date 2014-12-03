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
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import cn.cvte.activities.SearchDevicesActivity;

public class BroadcastClient implements Runnable{
	static final int CLIENT_PORT = 9998;
	final static int RECEIVE_LENGTH = 1024;
	MulticastSocket multiSocket;
	static DatagramSocket socket;
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
			System.out.println("Do not support broadcast");
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
					getBroadcastAddress(mContext), UDPServer.SERVER_PORT);
			socket.send(packet);//发送报文
			
			DatagramPacket dp = new DatagramPacket(new byte[RECEIVE_LENGTH], RECEIVE_LENGTH);
			socket.receive(dp);
			String msg = new String(dp.getData()).trim();
			System.out.println(msg);
			String command = msg.split("#")[0];
			String address = msg.split("#")[1];
			if ("welcome".equals(command)){
				//deviceIPList.add(address);
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
					mHandler.sendEmptyMessage(0);
				}
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

	public static InetAddress getBroadcastAddress(Context context) throws IOException {
	    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    System.out.println("broadcast address:"+InetAddress.getByAddress(quads).toString());
	    return InetAddress.getByAddress(quads);
	}
}
