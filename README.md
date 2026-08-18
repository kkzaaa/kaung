# Hospital Management System (HMS)

Java Swing desktop application for `2607-OODJ-Assignment-QS`, split across
4 access roles: **Admin Staff**, **Medical Manager**, **Doctor**, and
**Patient**. See [`docs/TEAM_SPLIT.md`](docs/TEAM_SPLIT.md) for the full
team task split, per-member responsibilities, and the shared data-file
contract between modules.

## Getting started

Requires JDK 17+ and Maven.

```
mvn compile exec:java -Dexec.mainClass=hms.Main
```

or build a runnable jar:

```
mvn package
java -jar target/hms.jar
```

Run commands from the repository root so the relative `data/` path
resolves correctly.

## Sample login

The seeded `data/users.txt` includes one account per role for testing the
login flow end to end:

| Role | Username | Password |
|---|---|---|
| Admin | `admin1` | `pass123` |
| Medical Manager | `manager1` | `pass123` |
| Doctor | `doctor1` | `pass123` |
| Patient | `patient1` | `pass123` |

## Project layout

```
src/main/java/hms/
 ├── Main.java           app entry point
 ├── model/              shared data classes (User hierarchy, Appointment, Prescription, ...)
 ├── util/                FileHandler + Constants — the shared foundation every module imports
 ├── service/             business logic per role, no GUI code
 └── gui/                 LoginForm, MainFrame, and one subpackage per role's screens
data/                     pipe-delimited .txt files acting as the shared "database"
docs/TEAM_SPLIT.md         full team split, task breakdown, and data-format contract
```

Every module reads and writes through `hms.util.FileHandler` against the
same `.txt` files described in `docs/TEAM_SPLIT.md` §3 — that shared
contract is what lets, e.g., a Doctor's prescription be immediately
readable by the Patient module.
