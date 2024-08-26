import java.util.concurrent.locks.Lock;

import java.util.concurrent.locks.ReentrantLock;

public class RefuelingTruck implements Runnable {

    private int planeId;
    private static final Lock refuelingLock = new ReentrantLock();

    public RefuelingTruck(int planeId) {
        this.planeId = planeId;
    }

    @Override
    public void run() {

        refuelingLock.lock();

        try {
            System.out.println(Thread.currentThread().getName() + ": Refueling Truck Assignd for Plane " + planeId + "...");

            try 
            {
                Thread.sleep(4000); // Simulate time taken for refueling
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + ": Plane " + planeId + " has been refueled.");

        } 
        finally 
        {
            refuelingLock.unlock();
        }
    }
}
