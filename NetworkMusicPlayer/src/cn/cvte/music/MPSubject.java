package cn.cvte.music;

import java.util.ArrayList;
import java.util.List;

public interface MPSubject {
	public static final List<MPObserver> observerList = new ArrayList<MPObserver>();
	public void registerObserver(MPObserver observer);
	public void removeObserver(MPObserver observer);
	public void notifyObserver();
}
