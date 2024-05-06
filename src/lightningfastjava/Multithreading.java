package lightningfastjava;

public class Multithreading {
    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            MultithreadingTest myThread = new MultithreadingTest(i);
            Thread thread = new Thread(myThread);
            thread.start();
        }
    }
}
