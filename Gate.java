import java.util.List;

class Gate implements Runnable {
    private final int gateNumber;
    private boolean occupied;
    private volatile boolean running = true;

    public Gate(int gateNumber, List<Gate> gates) {
        this.gateNumber = gateNumber;
        this.occupied = false;
    }

    public int getGateNumber() {
        return gateNumber;
    }

    public synchronized void occupyGate() {
        occupied = true;
    }

    public synchronized void releaseGate() {
        occupied = false;
    }

    public synchronized boolean isOccupied() {
        return occupied;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
    }
}
