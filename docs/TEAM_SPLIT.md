# HMS — Team Task Split & Shared Data Format

Based on `2607-OODJ-Assignment-QS`, Table 1.0 splits the system into **4 access
roles**, which maps onto **4 team members**. The key risk in a group Java
project like this isn't the individual modules — it's the *seams* where
modules touch (e.g. a Patient books a slot that a Doctor must see, a Doctor
writes a prescription the Patient must read). This plan gives each person
clear ownership **plus** one shared foundation everyone codes against from
day one.

## 1. Team Structure

| Member | Role Owned | Why |
|---|---|---|
| **Member 1 — System/Admin Lead** | Admin Staff module **+** Login/Registration **+** shared OOP core (`User` hierarchy, `FileHandler` utility) | Admin creates/deletes all users, so this person naturally owns the base `User` class and the authentication flow everyone else logs into. |
| **Member 2 — Medical Manager** | Medical Manager module | Departments, shift rosters, hospital-wide reports. |
| **Member 3 — Doctor** | Doctor module | Vitals/notes, prescriptions, lab/X-ray requests. |
| **Member 4 — Patient** | Patient module | Booking, medical history view, feedback/ratings. |

### Detailed feature breakdown (from Table 1.0)

**Member 1 — Admin Staff**
- Login screen (shared by all 4 roles) + user registration
- Create / Read / Update / Delete end users
- Assign doctors to Medical Managers
- Manage hospital assets: consultation rooms, wards, labs, imaging rooms
- Configure base consultation rates & accepted insurance networks
- Also owns: `User` abstract class + subclasses skeleton, `FileHandler` utility class (finished first/early — see §3)

**Member 2 — Medical Manager**
- Edit own profile
- Create/update medical departments/specialties (e.g. Cardiology)
- Design medical assessment/check-up types (e.g. "General Checkup" needs BP + temp + weight)
- Design & modify doctor shift rosters
- View hospital-wide analytical reports & revenue summaries

**Member 3 — Doctor**
- Edit own profile
- Key in medical assessment & lab results, log vitals, write consultation notes
- Issue digital prescriptions → written to a file the Patient module reads
- Issue lab/X-ray requests → written to a file the Admin module reads
- Provide clinical feedback tied to a specific appointment

**Member 4 — Patient**
- Edit own profile
- Browse doctor's available slots; book / reschedule / cancel appointment → written to a file the Doctor module reads
- View own medical history & prescriptions (reads files written by Doctor)
- Submit ratings/comments on doctors & visits

### Shared/cross-cutting work

- Medical grading & billing logic — Member 1 (ties to consultation rates they already own)
- Analytical reports — Member 2, since Admin/Manager both need summary views
- GUI look-and-feel consistency (common colours, fonts, a shared `MainFrame`/navigation shell) — one person owns this even if everyone builds their own screens

## 1.1 Detailed Task Breakdown per Member

### Member 1 — Admin Staff

**GUI screens:** login, registration, user management (list/edit/delete/filter),
assign doctor → manager, hospital assets CRUD, rates & insurance config.

**Classes/methods:** `User` (abstract), `Admin`, `FileHandler`, `AuthService.login()`,
`UserManager` (`createUser`/`updateUser`/`deleteUser`/`listUsersByRole`),
`AssetManager` (`addWard`/`addRoom`/`updateAssetStatus`), `RateConfig`
(`setConsultationRate`/`toggleInsurance`).

**Files touched:** `users.txt`, `admins.txt`, `doctors.txt` (update `managerID`),
`wards.txt`, `rates.txt`, `insurance.txt` — all via `FileHandler`.

**Validation:** username uniqueness, required fields, valid email/IC/contact
formats, prevent deleting a user with active records, doctor must exist before
being assigned to a manager.

**OOP concepts:** inheritance (`User` → subclasses), encapsulation, the start
of polymorphism via `showDashboard()` called polymorphically from `AuthService`.

### Member 2 — Medical Manager

**GUI screens:** profile edit, department management, assessment type
designer, shift roster (with conflict detection), reports (filterable by
date range/department).

**Classes/methods:** `MedicalManager`, `Department`, `AssessmentType`,
`RosterManager` (`assignShift`/`checkConflict`/`getRosterByDoctor`),
`ReportGenerator` (`getRevenueSummary`/`getAppointmentCountByDept`/`getDoctorWorkload`).

**Files touched:** `departments.txt`, `assessmentTypes.txt`, `doctors.txt`
(update `shiftSchedule`), read-only `appointments.txt`/`billing.txt`/`feedback.txt`.

**Validation:** department name uniqueness, roster conflict check, assessment
type needs ≥1 required field.

**OOP concepts:** aggregation (Manager "has" Departments), abstraction via
`ReportGenerator.generateReport(type)` hiding multi-file aggregation.

### Member 3 — Doctor

**GUI screens:** profile edit, today's appointments, assessment entry
(fields driven by `AssessmentType`), prescription screen, lab/X-ray request
screen.

**Classes/methods:** `Doctor`, `Assessment`, `Prescription`, `LabRequest`,
`DoctorService` (`getTodaysAppointments`/`recordAssessment`/`issuePrescription`/`requestLabTest`).

**Files touched:** `assessments.txt`, `prescriptions.txt`, `labRequests.txt`
(append), read `appointments.txt` filtered by own `doctorID`, read
`assessmentTypes.txt`.

**Validation:** can only record an assessment for a `BOOKED`/`COMPLETED`
appointment assigned to this doctor, required vitals per assessment type,
non-empty medication/dosage.

**OOP concepts:** polymorphism — `Assessment` behaving differently per
`AssessmentType`.

### Member 4 — Patient

**GUI screens:** profile edit, browse doctors/slots, booking, my
appointments (reschedule/cancel), medical history (read-only), feedback.

**Classes/methods:** `Patient`, `Appointment`, `Feedback`, `PatientService`
(`getAvailableSlots`/`bookAppointment`/`rescheduleAppointment`/`cancelAppointment`/`getMedicalHistory`/`submitFeedback`).

**Files touched:** `appointments.txt` (append/rewrite), read `doctors.txt` +
roster for available slots, read `assessments.txt`/`prescriptions.txt`
filtered by own `patientID`, `feedback.txt` (append).

**Validation:** can't double-book a slot, can only cancel/reschedule own
upcoming appointments, feedback only on `COMPLETED` appointments and only
once per appointment.

**OOP concepts:** encapsulation — enforce in code (not just UI hiding) that a
patient can only ever touch their own records.

## 2. Build Order (so no one is blocked)

1. **Week 1:** Member 1 finishes the `User` hierarchy + `FileHandler` + login
   screen skeleton. Everyone else designs class diagrams & agrees on the file
   formats in §3.
2. **Week 2 onward:** Each member builds their own module against the shared
   `FileHandler`, using the txt formats below. Because everyone reads/writes
   through the *same* method signatures, one member's output is automatically
   readable by another's module.
3. **Integration checkpoints:** After the Doctor module can write
   prescriptions, immediately test that the Patient module can read them (and
   vice versa for appointments). Don't leave integration to the end.

## 3. Shared Data Format

Every module writes to and reads from the *same* set of `.txt` files, using
the *same* delimiter and the *same* utility class — so a Doctor writing a
prescription and a Patient reading it are guaranteed to agree on the format,
because they call identical code.

### 3.1 Rules everyone follows

- All data files live in `/data/`.
- **Delimiter:** pipe `|`.
- **One record per line**, no header row.
- **IDs:** every entity has a unique, prefixed ID (`U001`, `D001`, `P001`,
  `A001`, `RX001`, ...).
- **Status/enum fields:** fixed uppercase strings (`BOOKED`, `RESCHEDULED`,
  `CANCELLED`, `COMPLETED`) — never free text.
- Nobody hand-edits the txt files with a raw `FileWriter` in their own
  module — everyone calls the shared `FileHandler` methods (§3.3).

### 3.2 File-by-file layout

| File | Fields (in order) |
|---|---|
| `users.txt` | `userID\|role\|username\|password\|fullName\|icNumber\|contactNo\|email\|status` |
| `admins.txt` | `adminID\|userID` |
| `managers.txt` | `managerID\|userID\|departmentID` |
| `doctors.txt` | `doctorID\|userID\|specialty\|departmentID\|managerID\|shiftSchedule` |
| `patients.txt` | `patientID\|userID\|dob\|gender\|bloodType\|address` |
| `departments.txt` | `deptID\|deptName\|managerID` |
| `wards.txt` | `wardID\|wardName\|type\|capacity\|status` |
| `assessmentTypes.txt` | `typeID\|typeName\|requiredFields` (e.g. `BP,Temp,Weight`) |
| `appointments.txt` | `apptID\|patientID\|doctorID\|date\|time\|roomID\|status` |
| `assessments.txt` | `assessmentID\|apptID\|typeID\|doctorID\|patientID\|vitals\|labResults\|notes\|date` |
| `prescriptions.txt` | `rxID\|assessmentID\|patientID\|doctorID\|medication\|dosage\|instructions\|date` |
| `labRequests.txt` | `requestID\|doctorID\|patientID\|testType\|status\|date` |
| `billing.txt` | `billID\|patientID\|apptID\|amount\|grade\|insuranceStatus\|paymentStatus` |
| `feedback.txt` | `feedbackID\|patientID\|doctorID\|apptID\|rating\|comment\|date` |
| `rates.txt` | `deptID\|baseRate` |
| `insurance.txt` | `providerName\|status` |

**Worked example — `appointments.txt`:**

```
A001|P002|D001|2026-09-10|09:30|R101|BOOKED
A002|P003|D001|2026-09-10|10:00|R101|CANCELLED
```

Patient P002 booked doctor D001; the Doctor module reads this same file to
see its own patient list for the day.

### 3.3 Shared utility class

See `src/main/java/hms/util/FileHandler.java` — `readRecords()`,
`appendRecord()`, `rewriteFile()`. This is what makes "doctor and patient
share the same functions" work in practice: nobody writes their own custom
file-parsing code, so formats can't drift apart between modules.

### 3.4 Class hierarchy (OOP marks)

- `User` (abstract) → `Admin`, `MedicalManager`, `Doctor`, `Patient` — **inheritance**
- `User` holds private fields with getters/setters — **encapsulation**
- Each subclass overrides `showDashboard()`/`getRole()` differently — **polymorphism**
- `FileHandler`, `Appointment`, `Prescription`, `Assessment` as shared
  model/utility classes used identically by all 4 GUIs — **abstraction**

## 4. Shared Coding Format

### 4.1 Project & package structure

```
src/main/java/hms/
 ├── Main.java                  (M1 — app entry point, opens Login)
 ├── model/                     (shared data classes)
 ├── util/
 │    ├── FileHandler.java      (M1 — build first, everyone imports this)
 │    └── Constants.java        (M1 — delimiter, file paths, status strings)
 ├── service/                   (business logic, no GUI code)
 └── gui/
      ├── LoginForm.java        (M1)
      ├── MainFrame.java        (M1 — shared navigation shell)
      ├── admin/                (M1's screens)
      ├── manager/              (M2's screens)
      ├── doctor/               (M3's screens)
      └── patient/              (M4's screens)
data/                           (the .txt files, from §3.2)
```

**Rule:** you only ever add *new* files inside your own subfolder
(`gui/doctor/`, etc.), or a new file inside `model/`/`service/` that nobody
else owns. Nobody edits someone else's existing class without a quick
message to the owner.

### 4.2 Naming conventions

| Item | Convention | Example |
|---|---|---|
| Class names | PascalCase, singular noun | `Appointment`, `FileHandler` |
| Method names | camelCase, verb-first | `bookAppointment()`, `getRosterByDoctor()` |
| Variable names | camelCase | `patientID`, `apptStatus` |
| Constants | UPPER_SNAKE_CASE, all in `Constants.java` | `DELIMITER`, `STATUS_BOOKED` |
| File paths | never hardcode a string twice — always `Constants.*_FILE` | `Constants.APPOINTMENTS_FILE` |
| Boolean methods | prefix `is`/`has`/`can` | `isSlotAvailable()`, `hasConflict()` |
| GUI classes | suffix `Form` or `Panel` | `LoginForm`, `BookingPanel` |
| Package names | all lowercase | `hms.gui.doctor` |

### 4.3 `Constants.java`

See `src/main/java/hms/util/Constants.java` — one constant per file path
plus every status string, so nobody types a filename or status by hand.

### 4.4 GUI consistency

- One shared `MainFrame` (built by M1) swaps role dashboards in/out as
  `JPanel`s rather than four separate `JFrame` windows.
- Agree on window size, colour scheme, font, button style before anyone
  starts their own screens.
- Agree on a common way to show validation errors (e.g. always a red
  `JLabel` under the field).

### 4.5 Before merging — a quick checklist

1. Everyone pulls the latest `model/`, `util/`, and `Constants.java` before
   their final push.
2. Test your own module against the **real** shared `FileHandler`, not a
   personal copy.
3. Do one combined test run together: M4 books → M3 sees it appear → M3
   prescribes → M4 sees it appear.
4. Agree on Java version and don't commit `target/`, `.idea/`, or `.class`
   files (see `.gitignore`).

## 5. Next steps for your team

1. Agree on this split (or adjust) in your first meeting and note it down —
   the report needs individual contribution details anyway.
2. Member 1 pushes the `User` hierarchy + `FileHandler` + sample `.txt`
   files (done in this repo — see `data/`) so everyone can start
   reading/writing against real files immediately.
3. Keep this document as the "contract" between modules — don't change a
   field order without telling the team.
