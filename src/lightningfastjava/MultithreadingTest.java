package lightningfastjava;

public class MultithreadingTest implements Runnable {

    private int threadNumber;
    public MultithreadingTest(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++){
            System.out.println(i + " from thread #" + threadNumber);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

}
