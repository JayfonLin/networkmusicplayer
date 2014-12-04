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

import cn.cvte.activities.MPApplication;
import cn.cvte.activities.SearchDevicesActivity;

public class BroadcastClient implements Runnable{
	/**
	 * static field
	 */
	final static int RECEIVE_LENGTH = 1024;
	
	Context mContext;

	public BroadcastClient(Context context) {
		mContext = context;
	}
	
	@Override
	public void run() {
		broadcast();
	}

	public static InetAddress getLocalIpAddress(Context context) throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(
        		android.content.Context.WIFI_SERVICE );
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
	
	private void broadcast(){
		String serverAddress;
		try {
			serverAddress = getLocalIpAddress(mContext).toString().split("/")[1];
			System.out.println("ip:"+serverAddress);
			byte[] sendMsg = ("hello"+"#"+serverAddress).getBytes();
			DatagramPacket packet;
			packet = new DatagramPacket(sendMsg,sendMsg.length,
					getBroadcastAddress(mContext), UDPServer.SERVER_PORT);
			MPApplication.udpSocket.send(packet);//·¢ËÍ±¨ÎÄ
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
