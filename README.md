# zkAndroidUtils
##宅客工具类:


###CmdUtils.java:

```java

	CmdUtils.doCmds("ls", "-l", new ProcessWatchListener() {

			@Override
			public void onReadStream(int watchMode, String message) {
				//这里显示命令返回结果
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
```

