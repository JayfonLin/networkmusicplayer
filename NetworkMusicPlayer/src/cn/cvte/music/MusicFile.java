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
			Toast.makeText(mContext, "SD����δ����", Toast.LENGTH_SHORT).show();
			return;
		}
		musicInfoList.clear();
		
		// ɨ���ⲿ�豸�е���Ƶ
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
		System.out.println("count = " + cursor.getCount());  //��ȡ�ܹ��ж��ٸ���Ŀ
		while (cursor.moveToNext()) {
			//ÿ����Ŀ�ж�������Ϣ
			//System.out.println("ColumnCount = " + cursor.getColumnCount());
			/*System.out.println(cursor.getString(0)); // ��ƵID
			System.out.println(cursor.getString(1)); // ��Ƶ�ļ���
			System.out.println(cursor.getString(2)); // ��Ƶ������
			System.out.println(cursor.getString(3)); // ��Ƶʱ��
			System.out.println(cursor.getString(4)); // ��Ƶ�Ĵ�С �ֽ�
			System.out.println(cursor.getString(5)); // ��Ƶ����·��
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
