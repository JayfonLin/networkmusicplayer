package cn.cvte.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import cn.cvte.activities.SearchDevicesActivity;

public class BroadcastClient implements Runnable{
	
	MulticastSocket multiSocket;
	DatagramSocket socket;
	Context mContext;
	public BroadcastClient(DatagramSocket pSocket, Context context) {
		socket = pSocket;
		mContext = context;
	}
	@Override
	public void run() {
		String serverAddress;
		try {
			serverAddress = getLocalIpAddress(mContext).toString();
			System.out.println("ip:"+serverAddress);
			byte[] sendMsg = ("hello"+"#"+serverAddress).getBytes();
			DatagramPacket packet;
			packet = new DatagramPacket(sendMsg,sendMsg.length,
					InetAddress.getByName(UDPServer.BROADCAST_STR), 30000);
			socket.send(packet);//发送报文
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
}
