package cn.cvte.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

public class MusicFile {
	Context mContext;
	public static List<Map<String, Object>> musicInfoList;
	public static final String UNKNOW = "<unknow>"; 
	public MusicFile(Context context){
		mContext = context;
		musicInfoList = new ArrayList<Map<String,Object>>();
	}
	public void load(){
		//ArrayList<String> listAudio = new ArrayList<String>();
		if (!checkSDCard()){
			Toast.makeText(mContext, "SD卡尚未插入", Toast.LENGTH_SHORT).show();
			return;
		}
		musicInfoList.clear();
		
		// 扫描外部设备中的音频
		String str[] = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.SIZE,
				MediaStore.Audio.Media.DATA
				};
		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str, null,
				null, null);
		System.out.println("count = " + cursor.getCount());  //获取总共有多少个条目
		while (cursor.moveToNext()) {
			//每个条目有多少项信息
			//System.out.println("ColumnCount = " + cursor.getColumnCount());
			/*System.out.println(cursor.getString(0)); // 音频ID
			System.out.println(cursor.getString(1)); // 音频文件名
			System.out.println(cursor.getString(2)); // 音频艺术家
			System.out.println(cursor.getString(3)); // 音频时长
			System.out.println(cursor.getString(4)); // 音频的大小 字节
			System.out.println(cursor.getString(5)); // 音频绝对路径
*/			
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < 6; ++i){
				if (i >= cursor.getColumnCount()){
					map.put(str[i], UNKNOW);
				}else
					map.put(str[i], cursor.getString(i));
			}
			musicInfoList.add(map);
			
			//listAudio.add(cursor.getString(2));
		}
	}
	
	public boolean checkSDCard(){
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else return false;
	}
}
