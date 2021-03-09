import java.util.concurrent.*;

public class Concurrency {
    private static final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

    public static String slowIntern(String s) {
        String previousValue = map.putIfAbsent(s, s);
        return previousValue == null
                ? s : previousValue;
    }

    public static String fastIntern(String s) {
        String result = map.get(s);
        if (result == null) {
            result = map.putIfAbsent(s, s);
            if (result == null) result = s;
        }
        return result;
    }
    /**
     * ConcurrentHashMap 은 get 같은 검색 기능에 최적화 되어 있으므로,
     * putIfAbsent 의 호출횟수를 줄이면 더 빨라진다.
     * 따라서 fastintern 이 slowIntern 보다 빠르다.
     */

    public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(concurrency);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown(); // 타이머에게 준비를 마쳤음을 알림
                try {
                    start.await(); // 모든 Executor 쓰레드가 준비될때까지 기다림
                    action.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown(); // 타이머에게 작업을 마쳤음을 알림
                }
            });
        }

        ready.await();
        long startNanos = System.nanoTime(); // 모든 Executor 가 준비될 때까지 기다림
        start.countDown(); // 모든 Executor 를 notify 함
        done.await(); // 모든 Executor 가 끝나기를 기다림
        return System.nanoTime() - startNanos;
    }

    public static void main(String[] args) throws InterruptedException {
        int concurrency = 5;
        ExecutorService executorService
                = Executors.newFixedThreadPool(concurrency);
//                = Executors.newFixedThreadPool(concurrency - 1);
        /**
         * executorService 가 concurrency 갯수보다 적은 스레드를 생성하면,
         * 스레드 기아 교착상태(thread starvation deadlock)에 걸린다.
         */

        long timeSlowIntern = time(executorService, concurrency, () -> {
            for (int i = 0; i < 5000; i++) {
                slowIntern(String.valueOf(i));
            }
        });
        System.out.println("slowIntern 끝나는데 걸린 시간: " + timeSlowIntern);

        long timeFastIntern = time(executorService, concurrency, () -> {
            for (int i = 0; i < 5000; i++) {
                fastIntern(String.valueOf(i));
            }
        });
        System.out.println("fastIntern 끝나는데 걸린 시간: " + timeFastIntern);
    }
}
