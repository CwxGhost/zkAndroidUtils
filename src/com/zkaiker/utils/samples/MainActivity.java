package com.zkaiker.utils.samples;

import android.app.Activity;
import android.os.Bundle;

import com.zkaiker.utils.CmdUtils;
import com.zkaiker.utils.CmdUtils.ProcessWatchListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CmdUtils.doCmds("Example", "chmod 777 /dev/input/event1", new ProcessWatchListener() {

			@Override
			public void onReadStream(int watchMode, String message) {
				System.out.println(message);
			}

			@Override
			public void onFinish(int exitCode) {
			}
		}, new ProcessWatchListener() {

			@Override
			public void onReadStream(int watchMode, String message) {
				System.out.println(message);
			}

			@Override
			public void onFinish(int exitCode) {
			}
		});
	}
}
