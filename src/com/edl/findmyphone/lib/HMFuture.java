package com.edl.findmyphone.lib;

public interface HMFuture {

	boolean isCancelled();

	boolean cancel(boolean mayInterruptIfRunning);

	boolean isFinished();
}
