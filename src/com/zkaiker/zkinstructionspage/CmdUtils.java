package com.zkaiker.zkinstructionspage;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 命令行工具类
 * 
 * @author cwx
 * 
 */
public class CmdUtils {

	/**
	 * 执行带参命令行的程序
	 * 
	 * @param process
	 *            进程
	 * @param argo
	 *            参数
	 * @param inputWatcher
	 *            inputStram的监听器
	 * @param errorWatcher
	 *            errorStram的监听器
	 * @return
	 */
	public static CmdResult doCmds(String process, String argo, ProcessWatchListener inputWatcher,
			ProcessWatchListener errorWatcher) {
		try {

			String processname = process;

			if (processname == null) {
				return null;
			}

			Process p = Runtime.getRuntime().exec(processname + " " + argo);

			ProcessWatcher watchInput = new ProcessWatcher(p, ProcessWatcher.WATCH_MODE_INPUT, inputWatcher);
			watchInput.start();

			ProcessWatcher watchError = new ProcessWatcher(p, ProcessWatcher.WATCH_MODE_ERROR, errorWatcher);
			watchError.start();

			p.waitFor();
			ArrayList<String> input = watchInput.getStream();
			watchInput.setOver(true);

			ArrayList<String> error = watchError.getStream();
			watchError.setOver(true);

			CmdResult cmdResult = new CmdResult();
			cmdResult.setExitCode(p.exitValue());

			String inputString = "";
			for (String it : input) {
				inputString = inputString + "\n" + it;
			}

			String errorString = "";
			for (String it : error) {
				errorString = errorString + "\n" + it;
			}

			cmdResult.setResultString(inputString);
			cmdResult.setErrorString(errorString);

			return cmdResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * process监视器，监听器
	 * 
	 * @author cwx
	 * 
	 */
	public interface ProcessWatchListener {
		/**
		 * 读取到stream
		 * 
		 */
		public void onReadStream(int watchMode, String message);

		public void onFinish(int exitCode);
	}

	/**
	 * process监视线程，用于监视process，防止因为inputStrem缓存导致waitFor()方法被阻塞
	 * 
	 * @author cwx
	 * 
	 */
	public static class ProcessWatcher extends Thread {

		private Process p;
		private boolean over;
		private ArrayList<String> stream;
		private int mWatchMode = 0;

		private ProcessWatchListener lis;

		public static int WATCH_MODE_INPUT = 0;
		public static int WATCH_MODE_ERROR = 1;

		/**
		 * 构造方法,在p.waitFor()之前调用start(),并在p.waitFor()之后调用setOver()
		 * 
		 * @param p
		 *            被监视的进程对象
		 * @param watchMode
		 *            监视模式 WATCH_MODE_INPUT(监视input) WATCH_MODE_ERROR(监视error)
		 * @param listener
		 *            监听器
		 */
		public ProcessWatcher(Process p, int watchMode, ProcessWatchListener listener) {
			this.p = p;
			over = false;
			stream = new ArrayList<String>();
			mWatchMode = watchMode;
			lis = listener;
		}

		@Override
		public void run() {
			try {
				if (p == null) {
					return;
				}
				Scanner br = null;
				if (mWatchMode == WATCH_MODE_INPUT) {
					br = new Scanner(p.getInputStream());
				} else if (mWatchMode == WATCH_MODE_ERROR) {
					br = new Scanner(p.getErrorStream());
				}
				while (true) {
					if (p == null || over || br == null) {
						break;
					}
					while (br.hasNextLine()) {
						String tempStream = br.nextLine();
						if (tempStream.trim() == null || tempStream.trim().equals("")) {
							continue;
						}
						if (lis != null) {
							lis.onReadStream(mWatchMode, tempStream);
						}
						stream.add(tempStream);
					}
				}
				if (p != null && over && lis != null) {
					lis.onFinish(p.exitValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 监视结束
		 * 
		 * @param over
		 */
		public void setOver(boolean over) {
			this.over = over;
		}

		/**
		 * 获取结果
		 * 
		 * @return
		 */
		public ArrayList<String> getStream() {
			return stream;
		}
	}

	/**
	 * 命令行执行结果对象
	 * 
	 * @author cwx
	 * 
	 */
	public static class CmdResult {
		private String resultString;
		private String errorString;
		private int exitCode;

		public String getResultString() {
			return resultString;
		}

		public void setResultString(String resultString) {
			this.resultString = resultString;
		}

		public String getErrorString() {
			return errorString;
		}

		public void setErrorString(String errorString) {
			this.errorString = errorString;
		}

		public int getExitCode() {
			return exitCode;
		}

		public void setExitCode(int exitCode) {
			this.exitCode = exitCode;
		}

	}

}
