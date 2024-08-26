import java.util.ArrayList;
import java.util.List;

class Plane implements Runnable {
    private final int name;
    private final boolean isEmergency;
    private int assignedGate;
    private final int passengerCount;
    private final ATC atc;
    private final long requestTime;

    final Object planeLock = new Object();
    final Object planeLock2 = new Object();

    public Plane(int name, boolean isEmergency, int passengerCount, ATC atc) {
        this.name = name;
        this.isEmergency = isEmergency;
        this.passengerCount = passengerCount;
        this.atc = atc;
        this.requestTime = System.currentTimeMillis();
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setAssignedGate(int assignedGate) {
        this.assignedGate = assignedGate;
    }

    public int getAssignedGate() {
        return assignedGate;
    }

    public int getName() {
        return name;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    @Override
    public void run() {
        try {

            if (isEmergency) {
                System.out.println(Thread.currentThread().getName()
                        + ": IS REQUESTING AN EMERGENCY LANDING DUE TO FUEL SHORTAGE.....");
            } else {
                System.out.println(Thread.currentThread().getName() + ": IS REQUESTING TO LAND.....");
            }

            // Adding planes to the landing queue
            synchronized (AirportSimulation.landingQueue) 
            {
                if (isEmergency) 
                {
                    List<Plane> tempList = new ArrayList<>(AirportSimulation.landingQueue);
                    tempList.add(0, this);
                    AirportSimulation.landingQueue.clear();
                    AirportSimulation.landingQueue.addAll(tempList);
                } 
                else 
                {
                    AirportSimulation.landingQueue.add(this);
                }
            }

            // Wait for ATC to give permission to land
            synchronized (planeLock) {
                planeLock.wait();
            }

            land();
            performGroundOperations();

            // Notify ATC that the plane is ready for takeoff
            synchronized (AirportSimulation.takeoffQueue) {
                AirportSimulation.takeoffQueue.add(this);
                synchronized (planeLock2) {
                    planeLock2.notify();
                }
            }

            // Wait for ATC to give permission to take off
            synchronized (planeLock2) {
                planeLock2.wait();
            }

            takeOff();

            // Notify ATC that the gate and runway are free after takeoff
            for (Gate gate : AirportSimulation.getGates()) 
            {
                if (gate.getGateNumber() == assignedGate) 
                {
                    gate.releaseGate();
                    atc.freeGateAndRunway(assignedGate, String.valueOf(name));
                    break;
                }
            }

            // Increment the number of planes served and passengers boarded
            atc.incrementPlanesServed();
            atc.incrementPassengersBoarded(passengerCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized void land() {
        try {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + ": Is landing...");
            Thread.sleep(2000);
            System.out.println(threadName + ": Is coasting to the gate...");
            Thread.sleep(2000);
            System.out.println(threadName + ": Has parked at the assigned gate " + assignedGate);

            synchronized (AirportSimulation.runwaySemaphore) {
                AirportSimulation.runwaySemaphore.release();
                // Notify the ATC thread that the runway has been released
                atc.freeGateAndRunway(assignedGate, String.valueOf(name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized void takeOff() 
    {
        if (isEmergency()) {
            System.out.println(Thread.currentThread().getName() + ": Emergency plane " + getName() + " is taking off...");
        } else {
            System.out.println(Thread.currentThread().getName() + ": Plane " + getName() + " is taking off...");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isEmergency()) {
            System.out.println(Thread.currentThread().getName() + ": Emergency plane " + getName() + " has taken off");
        } else {
            System.out.println(Thread.currentThread().getName() + ": Plane " + getName() + " has taken off");
        }
    }

    private void performGroundOperations() {
        System.out.println(Thread.currentThread().getName() + ": IS NOW PERFORMING GROUND OPERATIONS...");
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread passengersDisembarkThread = new Thread(new PassengersProcess(name, "disembark", passengerCount),
                "Disembarking");
        Thread suppliesAndCleaningThread = new Thread(new SuppliesAndCleaning(name), "Cleaning/Supplies");
        Thread refuelingThread = new Thread(new RefuelingTruck(name), "Refueling Truck");
        Thread passengersEmbarkThread = new Thread(new PassengersProcess(name, "embark", passengerCount), "Embarking");

        passengersDisembarkThread.start();
        try {
            passengersDisembarkThread.join(); // Wait for passengers to disembark before starting supplies and cleaning
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        suppliesAndCleaningThread.start();
        refuelingThread.start();
        try {
            suppliesAndCleaningThread.join(); // Wait for supplies and cleaning to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            refuelingThread.join(); // Ensure refueling thread has finished
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        passengersEmbarkThread.start();
        try {
            passengersEmbarkThread.join(); // Wait for passengers to embark
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
