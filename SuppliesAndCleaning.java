public class SuppliesAndCleaning implements Runnable {

    private int planeId;

    public SuppliesAndCleaning(int planeId) {
        this.planeId = planeId;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": Cleaning and Refilling supplies for Plane " + planeId + "...");
        try 
        {
            Thread.sleep(2000); // Simulate time taken for refilling supplies

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + ": Cleaning and Refilling supplies have finished for Plane " + planeId + ".");

    }
}
