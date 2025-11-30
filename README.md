# Ticket Management System

A sample **Ticket Management** project demonstrating **inter-process communication** using **UDP** and **RabbitMQ**. Contains separate modules for **server**, **client**, and **shared** code.

---

## Requirements

* Java 11+
* Git
* Gradle wrapper included (`gradlew` / `gradlew.bat`)
* RabbitMQ (optional, for RabbitMQ features)

---

## Setup

Clone the repository:

```bash
git clone https://github.com/your-org/Ticket-Management-System.git
cd Ticket-Management-System
```

---

## Build

**Unix / macOS:**

```bash
./gradlew clean build
./gradlew :server:jar  # optional: build only server
```

**Windows:**

```powershell
.\gradlew.bat clean build
.\gradlew.bat :server:jar  # optional: build only server
```

**Build outputs:**

* Classes: `server/build/classes/java/main`
* JARs: `server/build/libs/`

---

## Run

**Server (from classes):**

```bash
./gradlew :server:classes
java -cp server/build/classes/java/main backend.TicketServerMain
```

**Server (from JAR):**

```bash
java -cp "server/build/libs/*" backend.TicketServerMain
```

**Client:** Build module or run from IDE (classpath similar to server).

---

## Testing

```bash
./gradlew test        # Unix / macOS
.\gradlew.bat test    # Windows
```

---

## Shutdown

* Server runs until terminated.
* Stop with `Ctrl+C` or IDE stop button.
* JVM shutdown hook calls `shutdown()` on all RemoteAccess implementations.

---

## Git Notes

* Stop tracking generated files already in Git:

```powershell
git rm --cached "shared/bin/main/rpc/ticketmanagement/*.pb.meta"
git commit -m "Stop tracking generated files"
```

**Recommended `.gitignore`:**

```
*.pb.meta
/shared/bin/
shared/bin/
```

---

## Troubleshooting

* Ensure firewall allows UDP ports.
* RabbitMQ instance must be running and reachable.
* Run server in foreground to check logs if it stops unexpectedly.

---

## Contributing

* Fork, branch, implement changes, add tests, open PR.
* Keep commits small and descriptive.
* Run `./gradlew test` before submitting.

---

## Author ✨

**Nihar Sawant** – DevOps & Software Engineer, interested in **automation, cloud, and machine learning**.
