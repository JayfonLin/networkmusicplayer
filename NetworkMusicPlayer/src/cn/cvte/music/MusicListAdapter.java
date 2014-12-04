package cn.cvte.music;

import java.util.Formatter;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.cvte.networkmusicplayer.R;

public class MusicListAdapter extends BaseAdapter{
	List<MusicInfo> musicInfoList;
	private LayoutInflater mInflater;
	
	public MusicListAdapter(Context context, List<MusicInfo> list){
		musicInfoList = list;
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return musicInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return musicInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		MusicInfo mi = (MusicInfo) getItem(position);
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.music_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name_tv = (TextView) convertView.findViewById(R.id.music_name_tv);
			viewHolder.size_tv = (TextView) convertView.findViewById(R.id.size_tv);
			viewHolder.duration_tv = (TextView) convertView.findViewById(R.id.duration_tv);
			viewHolder.artist_tv = (TextView) convertView.findViewById(R.id.artist_tv);
			viewHolder.data_tv = (TextView) convertView.findViewById(R.id.data_tv);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (!MusicFile.UNKNOW.equals(mi.name)){
			String name = mi.name;
			int i = name.lastIndexOf(".");
			if (i != -1)
				name = name.substring(0, name.lastIndexOf("."));
			viewHolder.name_tv.setText(name);
		}else{
			viewHolder.name_tv.setText(MusicFile.UNKNOW);
		}
		if (!MusicFile.UNKNOW.equals(mi.size)){
			long size = Long.parseLong(mi.size);
			float f_size = size/(1024f*1024f);
			String size_str = format(f_size);
			viewHolder.size_tv.setText(size_str+"M");
		}else {
			viewHolder.size_tv.setText(MusicFile.UNKNOW);
		}
		if (!MusicFile.UNKNOW.equals(mi.duration)){
			int time = Integer.parseInt(mi.duration);
			time/=1000;
			int minutes = time/60;
			int seconds = time%60;
			String timeStr = minutes+":"+String.format("%02d", seconds); 
			viewHolder.duration_tv.setText(timeStr);
		}else {
			viewHolder.duration_tv.setText(MusicFile.UNKNOW);
		}
		if (!MusicFile.UNKNOW.equals(mi.artist)){
			viewHolder.artist_tv.setText(mi.artist);
		}else{
			viewHolder.artist_tv.setText(MusicFile.UNKNOW);
		}
		if (!MusicFile.UNKNOW.equals(mi.data)){
			viewHolder.data_tv.setText(mi.data);
		}else{
			viewHolder.data_tv.setText(MusicFile.UNKNOW);
		}
		return convertView;
	}
	
	static class ViewHolder{
		TextView name_tv, size_tv, duration_tv, artist_tv, data_tv;
	}

	/** 
     * 使用java.util.Formatter,保留小数点后两位 
     */  
    public static String format(double value) {  
        /* 
         * %.2f % 表示 小数点前任意位数 2 表示两位小数 格式后的结果为 f 表示浮点型 
         */  
        return new Formatter().format("%.2f", value).toString();  
    }  
}
