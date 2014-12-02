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
		// ɨ���ⲿ�豸�е���Ƶ
		String str[] = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.SIZE};
		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str, null,
				null, null);
		System.out.println("count = " + cursor.getCount());  //��ȡ�ܹ��ж��ٸ���Ŀ
		while (cursor.moveToNext()) {
			//ÿ����Ŀ�ж�������Ϣ
			System.out.println("ColumnCount = " + cursor.getColumnCount());
			System.out.println(cursor.getString(0)); // ��ƵID
			System.out.println(cursor.getString(1)); // ��Ƶ�ļ���
			System.out.println(cursor.getString(2)); // ��Ƶ����·��
			System.out.println(cursor.getString(3)); // ��Ƶ�Ĵ�С �ֽ�
			listAudio.add(cursor.getString(2));
		}
	}
}
