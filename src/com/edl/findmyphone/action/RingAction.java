package com.edl.findmyphone.action;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.edl.findmyphone.R;

import java.util.Map;

/**
 * 发出铃声
 * @author Administrator
 *
 */
public class RingAction extends Action {

	@Override
	public String getAction() {
		return "ring";
	}

	@Override
	public boolean doAction(Context context, Map<String, Object> data) {
		if (data == null) {
			return false;
		}

		String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString();

		MediaPlayer mediaPlayer = null;
		//播放报警音乐
		//在播放报警音乐之前将系统音量设置成最大
		//声音的管理者
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		//设置系统音量的大小
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);

		/*//判断是否在播放报警音乐
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}*/

		//获取音乐资源
		mediaPlayer = MediaPlayer.create(context, R.raw.ring);
		//一直播放音乐
		mediaPlayer.setLooping(true);
		mediaPlayer.start();

       return true;

	}

}
