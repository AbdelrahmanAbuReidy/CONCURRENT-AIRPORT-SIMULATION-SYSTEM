public class PassengersProcess implements Runnable {

    private int planeId;
    private String action;
    private int passengerCount;
    public PassengersProcess(int planeId, String action, int passengerCount) {
        this.planeId = planeId;
        this.action = action;
        this.passengerCount = passengerCount;
    }
    @Override
    public void run() {

        if (action.equals("disembark")) 
        {
            System.out.println(Thread.currentThread().getName() + ": Passengers are disembarking from Plane " + planeId);

            try 
            {
                Thread.sleep(3000); // Simulate time taken for passengers to disembark
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + ": Passengers have finished disembarking from Plane "+ planeId + ".");
        } 
        else if (action.equals("embark")) 
        {

            System.out.println(Thread.currentThread().getName() + ": Passengers are embarking onto Plane " + planeId + " with " + passengerCount + " passengers...");

            try {
                Thread.sleep(3000); // Simulate time taken for passengers to embark
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + ": Passengers have finished embarking onto Plane " + planeId + ".");
        }
    }
}
