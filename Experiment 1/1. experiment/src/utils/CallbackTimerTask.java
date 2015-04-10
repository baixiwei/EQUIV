package utils;

import java.util.TimerTask;

public class CallbackTimerTask extends TimerTask {
	Callback callback;
	public CallbackTimerTask(Callback c) {
		callback = c;
	}
	@Override
	public void run() {
		callback.call();
		this.cancel();
	}
}
