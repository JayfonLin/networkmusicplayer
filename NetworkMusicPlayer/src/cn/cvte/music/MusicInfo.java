package cn.cvte.music;

import java.io.Serializable;
import java.util.Map;

import android.provider.MediaStore;

public class MusicInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	public String id, name, artist, duration, size, data;
	public MusicInfo(){}
	public MusicInfo(Map<String, Object> map){
		if (map.get(MediaStore.Audio.Media._ID) != null)
			id = map.get(MediaStore.Audio.Media._ID).toString();
		else
			id = MusicFile.UNKNOW;
		if (map.get(MediaStore.Audio.Media.DISPLAY_NAME) != null)
			name = map.get(MediaStore.Audio.Media.DISPLAY_NAME).toString();
		else 
			name = MusicFile.UNKNOW;
		if (map.get(MediaStore.Audio.Media.ARTIST) != null)
			artist = map.get(MediaStore.Audio.Media.ARTIST).toString();
		else 
			artist = MusicFile.UNKNOW;
		if (map.get(MediaStore.Audio.Media.DURATION) != null)
			duration = map.get(MediaStore.Audio.Media.DURATION).toString();
		else
			duration = MusicFile.UNKNOW;
		if (map.get(MediaStore.Audio.Media.SIZE) != null)
			size = map.get(MediaStore.Audio.Media.SIZE).toString();
		else
			size = MusicFile.UNKNOW;
		if (map.get(MediaStore.Audio.Media.DATA) != null)
			data = map.get(MediaStore.Audio.Media.DATA).toString();
		else
			data = MusicFile.UNKNOW;
	}
}
