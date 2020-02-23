package jp.ecom_plat.saigaitask.util;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 実行時間間隔を動的に変更できるスケジューラ
 */
public class ReschedulableExecutor {
	private Runnable runnable;
	private TimerTask timerTask;
	private Object syncObj = new Object();
	private long lastExec = 0;

	/**
	 * コンストラクタ
	 * 
	 * @param runnable
	 */
	public ReschedulableExecutor() {
	}

	/**
	 * 一定時間間隔で呼ばれる処理
	 */
	private class TimerTask implements Runnable {
		private ScheduledExecutorService executor = null;

		private TimerTask(ScheduledExecutorService executor) {
			this.executor = executor;
		}

		@Override
		public void run() {
			// 実行間隔変更後の初回実行時に、前回の実行が終了していなかった場合の対策
			synchronized (syncObj) {
				synchronized (ReschedulableExecutor.this) {
					// run() が呼ばれてからここまでの間に、ReschedulableExecutor クラスの start メソッドが呼ばれた場合の対策
					if (executor.isShutdown())
						return;
					lastExec = Calendar.getInstance().getTimeInMillis();
				}
				runnable.run();
			}
		}
	}

	/**
	 * 実行間隔を変更し、スケジューラを再起動する
	 */
	public synchronized void start(Runnable runnable, long interval, long initialDelay) {
		if (timerTask != null) {
			timerTask.executor.shutdown();
		}

		this.runnable = runnable;
		timerTask = null;
		if (runnable != null && interval > 0) {
			long now = Calendar.getInstance().getTimeInMillis();
			long delay = 0;

			if (initialDelay > 0)
				delay = initialDelay;
			// 前回の実行から interval で指定された時間が経過していない場合は、初回の実行を遅らせる
			else if (lastExec > 0 && lastExec + interval * 1000 > now)
				delay = (lastExec + interval * 1000 - now) / 1000;
			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			timerTask = new TimerTask(executor);
			executor.scheduleAtFixedRate(timerTask, delay, interval, TimeUnit.SECONDS);
		}
	}

	public Runnable getRunnable() {
		return runnable;
	}
}
