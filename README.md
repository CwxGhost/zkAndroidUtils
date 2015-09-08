# zkAndroidUtils
##宅客工具类:


###CmdUtils.java:

```java

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
```
