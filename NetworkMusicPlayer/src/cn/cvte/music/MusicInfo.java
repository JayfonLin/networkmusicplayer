package cn.cvte.music;

import java.io.Serializable;
import java.util.Map;

import android.provider.MediaStore;

public class MusicInfo implements Serializable{
	String id, name, artist, duration, size, data;
	
	public MusicInfo(Map<String, Object> map){
		id = map.get(MediaStore.Audio.Media._ID).toString();
		name = map.get(MediaStore.Audio.Media.DISPLAY_NAME).toString();
		artist = map.get(MediaStore.Audio.Media.ARTIST).toString();
		duration = map.get(MediaStore.Audio.Media.SIZE).toString();
		size = map.get(MediaStore.Audio.Media.SIZE).toString();
		data = map.get(MediaStore.Audio.Media.DATA).toString();
	}
}
