package com.edl.lib.findmyphone.future;



import com.edl.findmyphone.lib.HMFuture;
import com.loopj.android.http.RequestHandle;



public class HttpFuture implements HMFuture {

	private RequestHandle handle;

	public HttpFuture(RequestHandle handle) {
		this.handle = handle;
	}

	public void test() {
		handle.isFinished();
	}

	@Override
	public boolean isCancelled() {
		return handle == null || handle.isCancelled();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return handle == null || handle.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isFinished() {
		return handle == null || handle.isFinished();
	}
}
