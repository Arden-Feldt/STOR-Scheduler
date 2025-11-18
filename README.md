# Stor Scheduler
This is a project I am doing for [Michael O'Neill](https://stor.unc.edu/faculty-member/oneill-michael/) and the [UNC STOR Department](https://stor.unc.edu/).

I am designing a tool that takes in professor references, available courses, and department practices and returns a functional semester course schedule for the whole department. The project is in Java and uses [Gurobi](https://www.gurobi.com/) as the solver.

This project is currently designed with the STOR department's specific requirments and data in mind. My goal is to eventually generalize for other deparments.

## Running as an API

This project can now be run as a REST API using Spring Boot.

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Building and Running

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the Spring Boot application:**
   ```bash
   mvn spring-boot:run
   ```
   
   Or run the `ScheduleApplication` class directly from your IDE.

3. **The API will start on:** `http://localhost:8080`

### API Endpoints

- **POST/GET `/api/schedule/run`** - Triggers the course scheduling process
  - Returns a JSON response with success status, message, and output file paths
  
- **GET `/api/schedule/health`** - Health check endpoint
  - Returns a simple status message

### Example Usage

**Using curl:**
```bash
# Trigger scheduling
curl -X POST http://localhost:8080/api/schedule/run

# Health check
curl http://localhost:8080/api/schedule/health
```

**Example Response:**
```json
{
  "success": true,
  "message": "Scheduling completed successfully",
  "rawSchedulePath": "src/main/Schedule/course_schedule.csv",
  "displaySchedulePath": "src/main/Schedule/legible_schedule.csv"
}
```