# Deployment Guide for STOR Course Scheduler

This guide covers several options for deploying the Spring Boot application online.

## Prerequisites

1. **Gurobi License**: Ensure you have a valid Gurobi license that can be used on the deployment server
2. **Java 17+**: The server must have Java 17 or higher installed
3. **Maven**: For building the application

## Option 1: Heroku (Easiest - Recommended for Quick Deployment)

### Steps:

1. **Install Heroku CLI**: Download from https://devcenter.heroku.com/articles/heroku-cli

2. **Login to Heroku**:
   ```bash
   heroku login
   ```

3. **Create a Heroku App**:
   ```bash
   heroku create storschedular
   ```

4. **Set Java Version** (if needed):
   ```bash
   heroku buildpacks:set heroku/java
   ```

5. **Deploy**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git push heroku main
   ```

6. **Note**: You'll need to handle the Gurobi JAR file. Options:
   - Upload `gurobi.jar` to a cloud storage and download it during build
   - Use Heroku's file system (ephemeral, resets on restart)
   - Consider using environment variables for Gurobi license

### Limitations:
- Free tier has limitations (sleeps after inactivity)
- File uploads are stored temporarily (ephemeral filesystem)
- May need paid tier for production use

---

## Option 2: Railway (Modern, Easy)

### Steps:

1. **Sign up**: Go to https://railway.app

2. **Create New Project**:
   - Click "New Project"
   - Select "Deploy from GitHub repo" (recommended) or "Empty Project"

3. **Configure**:
   - Add environment variables if needed
   - Set build command: `mvn clean package -DskipTests`
   - Set start command: `java -jar target/storschedular-1.0.0.jar`

4. **Deploy**:
   - Connect your GitHub repository
   - Railway will automatically build and deploy

### Advantages:
- Free tier available
- Easy GitHub integration
- Automatic deployments

---

## Option 3: Render (Simple, Free Tier)

### Steps:

1. **Sign up**: Go to https://render.com

2. **Create New Web Service**:
   - Connect your GitHub repository
   - Select "Web Service"

3. **Configure**:
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/storschedular-1.0.0.jar`
   - Environment: Java

4. **Deploy**:
   - Render will automatically build and deploy

---

## Option 4: AWS EC2 (More Control)

### Steps:

1. **Launch EC2 Instance**:
   - Choose Ubuntu or Amazon Linux
   - t2.micro (free tier) or larger
   - Configure security group to allow HTTP (port 80) and HTTPS (port 443)

2. **SSH into Instance**:
   ```bash
   ssh -i your-key.pem ubuntu@your-ec2-ip
   ```

3. **Install Java and Maven**:
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk maven -y
   ```

4. **Clone and Build**:
   ```bash
   git clone your-repo-url
   cd STORSchedular
   mvn clean package -DskipTests
   ```

5. **Run Application**:
   ```bash
   java -jar target/storschedular-1.0.0.jar
   ```

6. **Use PM2 or systemd for Process Management**:
   ```bash
   # Install PM2
   npm install -g pm2
   
   # Start with PM2
   pm2 start java --name "storschedular" -- -jar target/storschedular-1.0.0.jar
   pm2 save
   pm2 startup
   ```

7. **Set up Nginx as Reverse Proxy** (optional but recommended):
   ```bash
   sudo apt install nginx
   # Configure nginx to proxy to localhost:8080
   ```

---

## Option 5: DigitalOcean App Platform

### Steps:

1. **Sign up**: Go to https://www.digitalocean.com

2. **Create App**:
   - Connect GitHub repository
   - Select "Web Service"

3. **Configure**:
   - Build Command: `mvn clean package -DskipTests`
   - Run Command: `java -jar target/storschedular-1.0.0.jar`
   - Environment Variables: Add any needed

4. **Deploy**: DigitalOcean handles the rest

---

## Important Considerations

### 1. Gurobi License
- The Gurobi license file needs to be accessible on the server
- Consider using environment variables for license key
- May need to update Gurobi license for server IP/domain

### 2. File Storage
- Uploaded files are currently stored in `temp/` directory
- Consider using cloud storage (AWS S3, Google Cloud Storage) for production
- Or use a database to store file metadata

### 3. Environment Variables
Create a `.env` file or set in deployment platform:
```properties
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080
```

### 4. Database (Optional)
For production, consider storing file metadata in a database instead of filesystem.

### 5. SSL/HTTPS
- Use Let's Encrypt for free SSL certificates
- Or use platform-provided SSL (Heroku, Railway, Render all provide this)

### 6. Custom Domain
- Most platforms allow custom domain configuration
- Update DNS records to point to your deployment

---

## Quick Start Recommendation

For fastest deployment, I recommend **Railway** or **Render**:
- Easy setup
- Free tier available
- Automatic deployments from GitHub
- Built-in SSL
- Good documentation

---

## Production Checklist

- [ ] Set up proper error logging
- [ ] Configure file cleanup (delete old temp files)
- [ ] Set up monitoring/health checks
- [ ] Configure backup strategy
- [ ] Set up CI/CD pipeline
- [ ] Configure environment variables securely
- [ ] Set up custom domain
- [ ] Configure SSL certificate
- [ ] Test Gurobi license on server
- [ ] Set up file storage solution (if needed)

