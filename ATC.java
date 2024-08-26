import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

class ATC implements Runnable {
    private final List<Gate> gates;
    private final LinkedBlockingQueue<Plane> waitingQueue = new LinkedBlockingQueue<>();
    private final List<Long> waitingTimes = new ArrayList<>();
    private final AtomicLong totalWaitingTime = new AtomicLong(0);
    private final AtomicLong planesServed = new AtomicLong(0);
    private final AtomicLong passengersBoarded = new AtomicLong(0);
    private final LinkedBlockingQueue<String> freeGateQueue = new LinkedBlockingQueue<>();

    private volatile boolean running = true;

    public ATC(List<Gate> gates) {
        this.gates = gates;
    }

    @Override
    public void run() {
        while (running) {
            try 
            {
                // Handle messages from planes
                String message = freeGateQueue.poll();
                if (message != null) {
                    System.out.println("\t" + Thread.currentThread().getName() + ": " + message);
                }

                // handle plane landing (permissions)
                Plane plane = AirportSimulation.landingQueue.poll();
                if (plane != null) 
                {
                    synchronized (AirportSimulation.runwaySemaphore) 
                    {
                        if (AirportSimulation.runwaySemaphore.tryAcquire()) 
                        {
                            Gate availableGate = getAvailableGate();
                            if (availableGate != null) 
                            {
                                long waitingTime = System.currentTimeMillis() - plane.getRequestTime();
                                waitingTimes.add(waitingTime);
                                totalWaitingTime.addAndGet(waitingTime);
                                allowLanding(plane, availableGate);
                            } 
                            else 
                            {
                                waitingQueue.offer(plane);
                                AirportSimulation.runwaySemaphore.release();
                            }
                        } 
                        else 
                        {
                            waitingQueue.offer(plane);
                        }
                    }
                }

                // handling waiting planes for landing
                if (!waitingQueue.isEmpty()) 
                {
                    synchronized (AirportSimulation.runwaySemaphore) 
                    {
                        if (AirportSimulation.runwaySemaphore.tryAcquire()) 
                        {
                            Plane nextPlane = waitingQueue.poll();
                            Gate availableGate = getAvailableGate();
                            if (availableGate != null && nextPlane != null) 
                            {
                                long waitingTime = System.currentTimeMillis() - nextPlane.getRequestTime();
                                waitingTimes.add(waitingTime);
                                totalWaitingTime.addAndGet(waitingTime);
                                allowLanding(nextPlane, availableGate);
                            } 
                            else 
                            {
                                waitingQueue.offer(nextPlane);
                                AirportSimulation.runwaySemaphore.release();
                            }
                        }
                    }
                }

                // Check for planes waiting to take off
                Plane planeForTakeoff = AirportSimulation.takeoffQueue.poll();
                if (planeForTakeoff != null) {
                    allowTakeoff(planeForTakeoff);
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // giving permission to land 
    private void allowLanding(Plane plane, Gate gate) throws InterruptedException 
    {
        gate.occupyGate();
        plane.setAssignedGate(gate.getGateNumber());
        System.out.println("\t" + Thread.currentThread().getName() + ": Runway and Gate " + gate.getGateNumber()+ " assigned to Plane " + plane.getName() + " for landing.");

        // notifying the plane in Plane class to proceed with landing
        synchronized (plane.planeLock) {
            plane.planeLock.notify();
        }
    }

    // giving permission to take-off
    private void allowTakeoff(Plane plane) throws InterruptedException 
    {
        System.out.println("\t" + Thread.currentThread().getName() + ": Runway assigned to Plane " + plane.getName()+ " for takeoff.");

        // notifying the plane in Plane Class that it can proceed with taking-off
        synchronized (plane.planeLock2) {
            plane.planeLock2.notify();
        }
    }


    // checking the availability of gates
    private Gate getAvailableGate() 
    {
        for (Gate gate : gates) {
            if (!gate.isOccupied()) {
                return gate;
            }
        }
        return null;
    }

    public void printStatistics() {
        long maxWaitingTime = waitingTimes.stream().max(Long::compare).orElse(0L);
        long minWaitingTime = waitingTimes.stream().min(Long::compare).orElse(0L);
        double avgWaitingTime = waitingTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        avgWaitingTime = Math.round(avgWaitingTime * 100.0) / 100.0;

        System.out.println("\n\t==================== Statistics ======================");
        System.out.println("\t| Metric                                 | Value     |");
        System.out.println("\t======================================================");
        System.out.println("\t| Maximum waiting time for a plane       | " + maxWaitingTime + " ms  |");
        System.out.println("\t| Average waiting time for a plane       | " + avgWaitingTime + " ms|");
        System.out.println("\t| Minimum waiting time for a plane       | " + minWaitingTime + " ms      |");
        System.out.println("\t| Number of planes served                | " + planesServed.get() + "         |");
        System.out.println("\t| Number of passengers boarded           | " + passengersBoarded.get() + "       |");
        System.out.println("\t======================================================\n");
    }

    public void printGateStatus() {
        System.out.println("\n\t======== Gate Status ==========");
        System.out.println("\t| Gate ID      | Status       |");
        System.out.println("\t===============================");

        for (Gate gate : gates) {
            String status = gate.isOccupied() ? "Occupied" : "Empty";
            System.out.printf("\t| %-12d | %-12s |\n", gate.getGateNumber(), status);
        }
        System.out.println("\t===============================\n");
    }

    public void stop() {
        running = false;
    }

    public void incrementPlanesServed() {
        planesServed.incrementAndGet();
    }

    public void incrementPassengersBoarded(long count) {
        passengersBoarded.addAndGet(count);
    }

    public void freeGateAndRunway(int gateNumber, String planeName) {
        freeGateQueue.offer("Runway has been released from plane " + planeName);
    }
}
