# ✈️ Concurrent Airport Simulation System  

## 📌 Overview  
The **Concurrent Airport Simulation System** is a Java-based project that simulates airport operations using **concurrent programming** concepts.  
It demonstrates how synchronization, semaphores, locks, and thread management can optimize resource usage, prevent race conditions, and ensure safe operations in multi-threaded systems.  

Developed as part of the **Concurrent Programming module (CT074-3-2-CCP)** at **Asia Pacific University of Technology & Innovation (APU)**.  

---

## 🎯 Objectives  
- Simulate realistic **airport workflows** (landing, ground operations, and takeoff).  
- Demonstrate concurrency mechanisms in **Java**.  
- Handle **shared resources** safely.  
- Ensure **fair scheduling** with emergency prioritization.  
- Prevent deadlocks and race conditions.  

---

## 📐 Project Assumptions  
- Only **1 runway** is available for both landing and takeoff.  
- Maximum **3 planes** allowed on airport grounds (including runway).  
- **6 planes** attempt to land during the simulation.  
- Each plane holds up to **50 passengers**.  
- No waiting area for planes if gates are full.  
- Ground operations (passenger processing, cleaning, supplies) can occur concurrently.  
- **1 refueling truck** available → fueling must be exclusive.  

---

## ⚙️ Features & Implementation  

### 1. **Runway Handling**  
- Managed using **Semaphore (1 permit)**.  
- Guarantees only one plane can **land/takeoff** at a time.  

### 2. **Plane Operations**  
- **Landing / Takeoff** → synchronized methods ensure one operation at a time.  
- **Ground Operations** → concurrent sub-threads for:  
  - Disembarking  
  - Cleaning & Supplies  
  - Refueling (exclusive with `ReentrantLock`)  
  - Embarking  

### 3. **Airport Capacity**  
- Maximum **3 planes** → ATC checks gate availability before granting permission.  

### 4. **Emergency Handling**  
- Emergency planes are added to the **front of the landing queue**.  
- ATC always prioritizes emergency landings.  
- Managed with **PriorityBlockingQueue + synchronized blocks**.  

---

## 🛠️ Technologies Used  
- **Java** (Concurrency APIs)  
- **Semaphores & Locks**  
- **Threads & Synchronization**  
- **Priority Queues**  

---

## 📊 System Flow
<img width="856" height="670" alt="Screenshot 2025-09-11 031550" src="https://github.com/user-attachments/assets/3bb6861e-e07f-4ca3-9c62-a2ff21657640" />
<img width="472" height="762" alt="Screenshot 2025-09-11 031628" src="https://github.com/user-attachments/assets/6add3cb7-8230-4501-932d-c2481051d750" />



## 🚀 How to Run  

```bash
# Clone the repository
git clone https://github.com/your-username/Concurrent-Airport-Simulation-System.git
cd Concurrent-Airport-Simulation-System

# Compile the Java files
javac *.java

# Run the simulation
java AirportSimulation
