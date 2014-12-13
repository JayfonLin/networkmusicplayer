package cn.cvte.music;

import java.io.Serializable;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class MusicInfo implements Parcelable{
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
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(artist);
		dest.writeString(duration);
		dest.writeString(size);
		dest.writeString(data);
	}
	public static final Parcelable.Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {

		@Override
		public MusicInfo createFromParcel(Parcel source) {
			MusicInfo musicInfo = new MusicInfo();
			musicInfo.id = source.readString();
			musicInfo.name = source.readString();
			musicInfo.artist = source.readString();
			musicInfo.duration = source.readString();
			musicInfo.size = source.readString();
			musicInfo.data = source.readString();
			return musicInfo;
		}

		@Override
		public MusicInfo[] newArray(int size) {
			return new MusicInfo[size];
		}
		
	};
}
