package com.gg.module;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;

/*		��ʾ���ز�������		*/
public class VoiceControl {

	private Activity activity; // ��ʾ�������ع��ܵ�Activity
	private boolean flag; // ����Ƿ��������ع���

	public VoiceControl(Activity activity) {// ��ʼ�����ݳ�Ա
		this.activity = activity;
		flag = false;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void start() {// ʹ�����ع��ܣ�������Google Voice��Activity
		if (flag == true) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			// intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
			// RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH); // ������������ʵ���书��
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "����¼��");

			activity.startActivityForResult(intent, 1);
		}
	}

	public ArrayList<String> result(int requestCode, int resultCode, Intent data) {// ���Google
																					// Voice���ص�ƥ���ַ�������

		if (flag == true) {
			if (requestCode == 1) {
				if (resultCode == Activity.RESULT_OK) {
					ArrayList<String> matches = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					// for(int i=0;i<matches.size();++i){ // �������ƥ����ַ���
					// System.out.println(matches.get(i));
					// }
					return matches;
				}
			}
		}
		return null;
	}

}
