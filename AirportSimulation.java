import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class AirportSimulation {

    // Semaphore for the runway with 1 permit
    public static final Semaphore runwaySemaphore = new Semaphore(1);

    // Semaphore for refueling truck (1 permit)
    static final Semaphore refuelingTruck = new Semaphore(1);

    // Queue for planes waiting to land
    static final LinkedBlockingQueue<Plane> landingQueue = new LinkedBlockingQueue<>();

    // Queue for planes waiting to take off
    public static final LinkedBlockingQueue<Plane> takeoffQueue = new LinkedBlockingQueue<>();

    private static final List<Gate> gates = new ArrayList<>();
    private static ATC atc;

    public static void main(String[] args) {
        Random rand = new Random();

        // Create and start gate threads one by one
        Gate gate1 = new Gate(1, gates);
        Thread gateThread1 = new Thread(gate1, "Gate 1");
        gates.add(gate1);

        Gate gate2 = new Gate(2, gates);
        Thread gateThread2 = new Thread(gate2, "Gate 2");
        gates.add(gate2);

        Gate gate3 = new Gate(3, gates);
        Thread gateThread3 = new Thread(gate3, "Gate 3");
        gates.add(gate3);

        gateThread1.start();
        gateThread2.start();
        gateThread3.start();

        atc = new ATC(gates);
        Thread atcThread = new Thread(atc, "ATC");
        atcThread.start();

        // Create and start plane threads
        Plane plane1 = new Plane(1, false, rand.nextInt(31) + 20, atc);
        Thread planeThread1 = new Thread(plane1, "Plane 1");
        planeThread1.start();

        try {
            Thread.sleep(rand.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Plane plane2 = new Plane(2, false, rand.nextInt(31) + 20, atc);
        Thread planeThread2 = new Thread(plane2, "Plane 2");
        planeThread2.start();

        // Simulate time delay to ensure gates 1 and 2 are occupied before plane 3 and 4 arrive
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Plane plane3 = new Plane(3, false, rand.nextInt(31) + 20, atc);
        Thread planeThread3 = new Thread(plane3, "Plane 3");

        Plane plane4 = new Plane(4, true, rand.nextInt(31) + 20, atc);
        Thread planeThread4 = new Thread(plane4, "Plane 4 (Emergency)");

        // Start plane 3 and plane 4 simultaneously
        planeThread3.start();
        planeThread4.start();

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Plane plane5 = new Plane(5, false, rand.nextInt(31) + 20, atc);
        Thread planeThread5 = new Thread(plane5, "Plane 5");
        planeThread5.start();

        try {
            Thread.sleep(rand.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Plane plane6 = new Plane(6, false, rand.nextInt(31) + 20, atc);
        Thread planeThread6 = new Thread(plane6, "Plane 6");
        planeThread6.start();

        try {
            planeThread1.join();
            planeThread2.join();
            planeThread3.join();
            planeThread4.join();
            planeThread5.join();
            planeThread6.join();

            gate1.stop();
            gate2.stop();
            gate3.stop();
            gateThread1.join();
            gateThread2.join();
            gateThread3.join();

            atc.stop();
            atcThread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print final statistics
        atc.printGateStatus();
        atc.printStatistics();
    }

    public static ATC getATC() {
        return atc;
    }

    public static List<Gate> getGates() {
        return gates;
    }
}
