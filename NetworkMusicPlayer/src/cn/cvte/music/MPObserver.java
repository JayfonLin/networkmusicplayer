package cn.cvte.music;

import cn.cvte.music.SimpleMusicPlayerService.STATE;

public interface MPObserver {
	public void update(String path, STATE state, int curPos, int duration);
}
