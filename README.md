# STOR Schedular

STOR Schedular generates a semester course schedule from CSV inputs:
- professor time preferences
- course-to-instructor assignments
- optional hard constraints
- optional course conflicts

It is a Java + Spring Boot project and uses the Gurobi optimizer.

## 1) Prerequisites

- Java 17+
- Maven 3.6+
- Gurobi Optimizer (with a valid license)

Check your tools:

```bash
java -version
mvn -version
```

## 2) Get a Gurobi Academic License (Windows)

If you are a student/faculty member, you can use Gurobi's free academic license.

1. Go to [Gurobi Academic Program](https://www.gurobi.com/academia/academic-program-and-licenses/).
2. Sign in with your academic email and request a named-user academic license.
3. Install Gurobi Optimizer from [Downloads](https://www.gurobi.com/downloads/gurobi-software/).
4. Open a terminal and run the license command you receive from Gurobi:

   ```bash
   grbgetkey YOUR-LICENSE-KEY
   ```

5. Confirm the license is active:

   ```bash
   grbprobe
   ```

If `grbprobe` succeeds, your license is set up correctly.

## 3) Build and run

From the project root:

```bash
mvn clean install
mvn spring-boot:run
```

App URL:

- API: `http://localhost:8080/api/schedule`
- Web UI: `http://localhost:8080`

## 4) Fastest way for beginners: upload files via API/UI

The easiest path is to run the app, then upload your CSV files (instead of editing Java constants).

- Web UI upload flow is available at `http://localhost:8080`.
- API endpoint for upload + run:
  - `POST /api/schedule/upload-and-run`
  - required form files: `preferenceFile`, `courseDataFile`
  - optional form files: `hardsetFile`, `conflictFile`

### Example curl upload

```bash
curl -X POST http://localhost:8080/api/schedule/upload-and-run \
  -F "preferenceFile=@src/main/Faculty/ProfessorData/F26/profPrefF26.csv" \
  -F "courseDataFile=@src/main/Faculty/ProfessorData/F26/classAssignmentsF26.csv" \
  -F "hardsetFile=@src/main/givenData/hardsets.csv" \
  -F "conflictFile=@src/main/givenData/conflicts.csv"
```

## 5) Input CSV format (important)

The parser is strict about comma-separated columns. Do not use extra commas inside values unless quoted.

### A) Professor preferences CSV (`preferenceFile`) - required

Expected layout:

- Column 1: timestamp (text)
- Column 2: professor name
- Column 3: back-to-back preference (`1` for yes, `0` for no)
- Columns 4-18: numeric willingness for each timeslot (blank is treated as 0)
- Final notes column is optional free text

Timeslot columns should appear in this order:

1. `MWF 8:00-8:50`
2. `MWF 9:05-9:55`
3. `MWF 10:10-11:00`
4. `MWF 11:15-12:05`
5. `MWF 12:20-1:10`
6. `MWF 1:25-2:15`
7. `MWF 2:30-3:20`
8. `MWF 5:45-6:35`
9. `TTH 8:00-9:15`
10. `TTH 9:30-10:45`
11. `TTH 11:00-12:15`
12. `TTH 12:30-1:45`
13. `TTH 2:00-3:15`
14. `TTH 3:30-4:45`
15. `TTH 5:00-6:15`

Example:

```csv
Timestamp,Name,Back to Back?,MWF 8:00-8:50,MWF 9:05-9:55,MWF 10:10-11:00,MWF 11:15-12:05,MWF 12:20-1:10,MWF 1:25-2:15,MWF 2:30-3:20,MWF 5:45-6:35,TTH 8:00-9:15,TTH 9:30-10:45,TTH 11:00-12:15,TTH 12:30-1:45,TTH 2:00-3:15,TTH 3:30-4:45,TTH 5:00-6:15,Notes
2025-11-21 14:19,Amarjit,1,6,6,6,6,6,6,6,6,1,1,1,1,1,10,10,Prefers TTh
```

### B) Course assignments CSV (`courseDataFile`) - required

Expected layout:

- Column 1: course number (for example `320`)
- Column 2: expected students
- Column 3: recitation/section size (blank uses default)
- Column 4+: one or more instructor names

Notes:
- Instructor names should match names in the preferences file.
- Unknown names are auto-created as placeholders.
- Special values supported in instructor columns: `NH`, `DS`, `NOASSIGNMENT`, `GS`.

Example:

```csv
ClassNum,StudentNum,Recitations,Instructors,,,,,
320,120,,Jeff,NewTAPDS,NewTAPDS
155,100,,Teressa,Remi,Eva,Chuanshu,Ben,Greg
```

### C) Hardsets CSV (`hardsetFile`) - optional

Use this to force a specific assignment.

Header and columns:
- `name,faculty,room,time`

Example:

```csv
name,faculty,room,time
320,ML,Hanes120,TTH800915
```

### D) Conflicts CSV (`conflictFile`) - optional

Use this to indicate two courses that should not conflict.

Header and columns:
- `Course1,Course2`

Example:

```csv
Course1,Course2
320,120
```

## 6) Output files

After a successful run:

- raw optimizer output CSV is saved in `temp/` (or default schedule path for `/run`)
- display-friendly schedule CSV is saved in `temp/` (or default display path for `/run`)

The API response includes the output path(s).

## 7) Troubleshooting

- **"Gurobi license" errors**: run `grbprobe` and confirm your academic license is active.
- **No courses loaded**: verify CSV headers and commas; make sure required files are not empty.
- **Names do not match**: keep instructor names consistent between preferences and course files.
- **Build issues**: run `mvn clean install` again and confirm Java is version 17+.

## 8) Basic API endpoints

- `GET /api/schedule/health`
- `POST /api/schedule/run` (uses default file paths configured in code)
- `POST /api/schedule/upload-and-run` (recommended)