package me.skywave.helloapplication.background;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkScan {
    public final static int SUBNET_SIZE = 256;
    public final static int THREAD_IN_QUEUE_SIZE = 16;
    public final static int THREAD_SLEEP_MS = 500;

    private Thread lastThread = null;

    public synchronized void scanNetwork(final String ip, final NetworkScanCallback callback) {
        final AtomicInteger resultCount = new AtomicInteger(0);

        abortCurrentThread();

        lastThread = new Thread() {
            @Override
            public void run() {
                boolean interrupt = false;

                Log.d("scan", "start");
                long startTime = System.nanoTime();

                String subNet = getSubnet(ip);

                ThreadPoolExecutor pool = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
                BlockingQueue<Runnable> queue = pool.getQueue();
                PingRunnable[] runnables = new PingRunnable[SUBNET_SIZE];

                for (int i = 0; i < SUBNET_SIZE && !(Thread.interrupted() || interrupt); i++) {
                    InetAddress address;
                    try {
                        address = InetAddress.getByName(subNet + i);
                    } catch (UnknownHostException e) {
                        continue;
                    }

                    while (queue.size() > THREAD_IN_QUEUE_SIZE) {
                        try {
                            Thread.sleep(THREAD_SLEEP_MS);
                        } catch (InterruptedException ignored) {
                            interrupt = true;
                            break;
                        }
                    }

                    runnables[i] = new PingRunnable(address, resultCount);
                    pool.execute(runnables[i]);
                }

//                Log.d("scan", String.valueOf(pool.getTaskCount()));
                for (PingRunnable runnable : runnables) {
                    while (!(Thread.interrupted() || interrupt) && !runnable.isFinished()) {
                        try {
                            Thread.sleep(THREAD_SLEEP_MS);
                        } catch (InterruptedException ignored) {
                            interrupt = true;
                            break;
                        }
                    }
                }

                if (Thread.interrupted() || interrupt) {
                    for (PingRunnable runnable : runnables) {
                        if (runnable != null) {
                            pool.remove(runnable);
                        }
                    }

                    Log.d("scan", "interrupted");
                } else {
                    long endTime = System.nanoTime();
                    Log.d("scan", "end " + (endTime - startTime) / 1000000);

                    callback.runAfterDone(resultCount.get());
                }
            }
        };

        lastThread.start();

    }

    public void abortCurrentThread() {
        if (lastThread != null) {
            lastThread.interrupt();
            lastThread = null;
        }
    }

    public interface NetworkScanCallback {
        void runAfterDone(int result);
    }

    private String getSubnet(String ip) {
        return ip.substring(0, ip.lastIndexOf(".") + 1);
    }

    // http://stackoverflow.com/questions/4068984/running-multiple-asynctasks-at-the-same-time-not-possible
    private class PingRunnable implements Runnable {
        private InetAddress target;
        private boolean finished;
        private AtomicInteger result;

        public PingRunnable(InetAddress target, AtomicInteger result) {
            this.target = target;
            this.finished = false;
            this.result = result;
        }

        public void run() {
            Runtime runtime = Runtime.getRuntime();
            boolean result;

            try {
                Process pingProcess = runtime.exec("/system/bin/ping -c 1 -W 1 " + target.getHostAddress());
                result = pingProcess.waitFor() < 1; // 0: pong, 1: no response, 2: not found
            } catch (IOException | InterruptedException e) {
                result = false;
            }

            if (!result) {
                try {
                    result = target.isReachable(100);
                } catch (IOException e) {
                    result = false;
                }
            }

//            Log.d("scan", target.getHostAddress());

            if (result) {
                this.result.incrementAndGet();
            }

            finished = true;
        }

        public boolean isFinished() {
            return finished;
        }
    }
}
