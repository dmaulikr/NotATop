package com.gg.module;

import java.util.HashMap;
import java.util.Map;
import com.gg.view.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundControl {
	
	private MediaPlayer music ;  //music ����
	public AudioManager am;  //���ڻ�ȡϵͳ�����Ķ���
	private SoundPool soundPool ; // short music

	private boolean soundOn ; //short music control
	
	private Context context ;  
	
	private Map<Integer , Integer> soundMap;   //��Ч��ԴId�ͼ��ع������ԴId��ӳ���ϵ��
	
	
	public SoundControl(Context context){
		init(context);
	}
	
	//��ʼ�����ֲ�����
	private void initMusic(){
		music = MediaPlayer.create(context, R.raw.backmusic);
		music.setLooping(true);
	}
	
	
	//��ʼ����Ч������
	private void initSound(){
		soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,100);
		
		soundMap = new HashMap<Integer,Integer>();
		
		soundMap.put(R.raw.choose, soundPool.load(context, R.raw.choose,1));
		
		soundMap.put(R.raw.end, soundPool.load(context, R.raw.end,1));
		
		soundMap.put(R.raw.pai, soundPool.load(context, R.raw.pai,1));
		
		soundMap.put(R.raw.runing, soundPool.load(context, R.raw.runing,1));
		
	}			

	//������Ч��������Դ
	public void init(Context c){
	
		context = c ;
	
		initMusic();
		
		initSound();
	}
	
	
	//�����Ч����״̬
	public boolean isSoundOn(){
		return soundOn;
	}
	
	
	//�������ֲ���
	public void playSound(int resId){
		am=(AudioManager)context.getSystemService(context.AUDIO_SERVICE);
    	float audioMaxVolumn=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);	//���ص�ǰAudioManager������������ֵ
    	float audioCurrentVolumn=am.getStreamVolume(AudioManager.STREAM_MUSIC);//���ص�ǰAudioManager���������ֵ
    	float volumnRatio=audioCurrentVolumn/audioMaxVolumn;
    	if(isSoundOn() == false)//���������Ƿ񲥷�
			return ;
    	soundPool.play(
    			soundMap.get(resId), 					//���ŵ�����id
    			volumnRatio, 						//����������
    			volumnRatio, 						//����������
    			1, 									//���ȼ���0Ϊ���
    			0, 							//ѭ��������0�޲�ѭ����-1����Զѭ��
    			1									//�ط��ٶ� ����ֵ��0.5-2.0֮�䣬1Ϊ�����ٶ�
		);
		
		Integer soundId = soundMap.get(resId);
		if(soundId != null)
			soundPool.play(soundId, 1, 1, 1, 0, 1);
	}
	
	
	//��ͣ����
	public void setMusic(){
		if(music.isPlaying()){
			music.pause();
			soundOn = false;
		}
		else{
			music.start();
			soundOn = true;
		}
	}
	
	
	//��������
	public void paisound(){
		playSound(R.raw.pai);
	}
	
	public void end(){
		playSound(R.raw.end);
	}
	
	public void choose(){
		playSound(R.raw.choose);
	}
	
	public void running(){
		playSound(R.raw.runing);
	}
	
	
	//�ͷ����ֺ���Ч��Դ
	public void releasemusic(){
		music.release();
		soundPool.release();
	}
	
}