package com.gg.view;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

/*		���湤���ࣨ�������ģʽ������ͬ�Ľ�����ֻҪ������ע��Ϳ��Ժܷ����ʹ��		*/
public class SurfaceViewFactory {

	public final static int WELCOME = 0; // ��ʾ��ӭ����ĳ���
	public final static int MAIN_MENU = 1; // ��ʾ���˵�����ĳ���
	public final static int CLASSIC_GAME = 2; // ��ʾ��ͳģʽ����ĳ���
	public final static int SELECT = 3; // ��ʾѡ�����ĳ���
	public final static int SCORE = 4; // ��ʾ�߷ְ����ĳ���
	public final static int HELP = 5; // ��ʾ��������ĳ���
	public final static int END = 6;
	public final static int FIRST_TIME = 7;
	public final static int GAME_MODE = 8;
	public final static int TIME_GAME = 9;
	

	private static SurfaceView surfaceView; // ������Ļ���������ã����ڻ�ȡ��ͬ�Ľ��������
	

	private SurfaceViewFactory(){ // ���ƴ������������
		
	}
	
	public static SurfaceView getView(MainActivity mainActivity, int index){ // ͨ����紫���Ľ������������ز�ͬ�Ľ��������
		switch(index){
		case WELCOME:
			surfaceView = new WelcomeSurfaceView(mainActivity);
			break;
		case MAIN_MENU:
			surfaceView = new MainMenuSurfaceView(mainActivity);
			break;
		case CLASSIC_GAME:
			surfaceView = new ClassicGameSurfaceView(mainActivity);
			break;
		case SELECT:
			surfaceView = new SelectSurfaceView(mainActivity);
			break;	
		case SCORE:
			surfaceView = new ScoreSurfaceView(mainActivity);
			break;
		case HELP:
			surfaceView = new HelpSurfaceView(mainActivity);
			break;
		case END:
			surfaceView = new EndSurfaceView(mainActivity);
			break;
		case FIRST_TIME:
			surfaceView = new FirstTimeSurfaceView(mainActivity);
			break;
		case GAME_MODE:
			surfaceView = new GameModeSurfaceView(mainActivity);
			break;
		case TIME_GAME:
			surfaceView = new TimeGameSurfaceView(mainActivity);
			break;
		default:
			break;
		}
		return surfaceView;
	}
	
	
}
