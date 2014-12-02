package cn.cvte.music;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class MusicFile {
	Context mContext;
	public MusicFile(Context context){
		mContext = context;
	}
	public void load(){
		ArrayList<String> listAudio = new ArrayList<String>();
		// 扫描外部设备中的音频
		String str[] = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.SIZE};
		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str, null,
				null, null);
		System.out.println("count = " + cursor.getCount());  //获取总共有多少个条目
		while (cursor.moveToNext()) {
			//每个条目有多少项信息
			System.out.println("ColumnCount = " + cursor.getColumnCount());
			System.out.println(cursor.getString(0)); // 音频ID
			System.out.println(cursor.getString(1)); // 音频文件名
			System.out.println(cursor.getString(2)); // 音频绝对路径
			System.out.println(cursor.getString(3)); // 音频的大小 字节
			listAudio.add(cursor.getString(2));
		}
	}
}
