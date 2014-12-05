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
	/**
	 * static field
	 */
	public static List<MusicInfo> musicInfoList = new ArrayList<MusicInfo>();
	public static final String UNKNOW = "<unknow>";
	
	Context mContext;
	public MusicFile(Context context){
		mContext = context;
	}
	public boolean load(){
		if (!checkSDCard()){
			//Toast.makeText(mContext, "SD卡尚未插入", Toast.LENGTH_SHORT).show();
			return false;
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
		System.out.println("cursor.getCount():"+cursor.getCount());
		while (cursor.moveToNext()) {

			Map<String, Object> map = new HashMap<String, Object>();
			System.out.println("cursor.getColumnCount():"+cursor.getColumnCount());
			for (int i = 0; i < 6; ++i){
				if (i >= cursor.getColumnCount()){
					map.put(str[i], UNKNOW);
					
				}else
					map.put(str[i], cursor.getString(i));
			}
			MusicInfo mi = new MusicInfo(map);
			musicInfoList.add(mi);
		}
		cursor.close();
		return true;
	}
	
	public boolean checkSDCard(){
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else return false;
	}
}
