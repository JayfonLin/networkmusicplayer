package cn.cvte.activities;

import cn.cvte.music.MusicFile;
import cn.cvte.music.MusicListAdapter;
import cn.cvte.networkmusicplayer.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class MusicListActivity extends Activity{
	ListView audioList;
	ProgressDialog dialog;
	MusicListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		audioList = (ListView) findViewById(R.id.listView1);
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		LoadMusicTask task = new LoadMusicTask();
		task.execute(this);
	}
	
	class LoadMusicTask extends AsyncTask<Context, Integer, Void>
	{
		@Override
		protected Void doInBackground(Context... params) {
			// TODO Auto-generated method stub
			MusicFile mf = new MusicFile(params[0]);
			mf.load();
			return null;
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter = new MusicListAdapter(MusicListActivity.this, MusicFile.musicInfoList);
			audioList.setAdapter(adapter);
			dialog.dismiss();
			
			super.onPostExecute(result);
		}

		
	}
}
