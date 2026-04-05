# GenTech HR Portal - VM Deployment Guide

Complete step-by-step guide for deploying the GenTech HR Portal application on a Virtual Machine (VM).

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [VM Setup](#vm-setup)
3. [Project Setup](#project-setup)
4. [Configuration](#configuration)
5. [Build & Deploy](#build--deploy)
6. [Troubleshooting](#troubleshooting)
7. [Post-Deployment](#post-deployment)

---

## Prerequisites

### VM Requirements

| Resource | Minimum | Recommended |
|----------|---------|-------------|
| **OS** | Ubuntu 22.04 LTS | Ubuntu 22.04 LTS |
| **CPU** | 2 vCPU | 4 vCPU |
| **RAM** | 4 GB | 8 GB |
| **Storage** | 20 GB SSD | 50 GB SSD |

### Required Ports

| Port | Service | Purpose |
|------|---------|---------|
| 22 | SSH | Remote access |
| 80 | HTTP | Frontend |
| 8081 | Backend API | Spring Boot |
| 5432 | PostgreSQL | Database |

### Cloud Provider Firewall Rules

**Google Cloud (GCP) - Foolproof UI Method (Recommended):**
1. Go to the **VM instances** page in your Google Cloud Console.
2. Click on your instance name (e.g., `hrportal`).
3. Click the **EDIT** pencil icon at the top of the page.
4. Scroll down to the **Firewalls** section.
5. Check **BOTH** boxes for:
   - `☑ Allow HTTP traffic`
   - `☑ Allow HTTPS traffic`
6. Click **Save** at the bottom.

*(Advanced)* Custom Ports (like 8081 for Backend API):
1. Search **Firewall (VPC network)** in the Google Cloud top bar.
2. Click **Create Firewall Rule**.
3. Fill out the form:
   - **Name**: `allow-hr-portal-api`
   - **Targets**: Change dropdown to **All instances in the network**
   - **Source IPv4 ranges**: Type **`0.0.0.0/0`**
   - **Protocols and ports**: Check **Specified protocols and ports**, check **TCP** and type **`8081, 5432`**
4. Click **Create** at the bottom.

**Alternative - UFW (Ubuntu Internal Firewall):**
If your VM uses an internal firewall, run these inside the SSH terminal:
```bash
sudo ufw allow 80/tcp
sudo ufw allow 8081/tcp
sudo ufw allow 5432/tcp
sudo ufw reload
sudo ufw status
```

---

## VM Setup

### Step 1: Install Docker

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
sudo apt install -y docker.io docker-compose

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group (logout and login after this)
sudo usermod -aG docker $USER
newgrp docker
```

### Step 2: Verify Docker Installation

```bash
docker --version
docker-compose --version
```

---

## Project Setup

### Step 1: Clone Repository

```bash
cd ~
git clone https://github.com/shivaranjeet/GenTechHRPortal.git
cd GenTechHRPortal
```

---

## Configuration

### Step 1: Create Backend Dockerfile

Copy and paste this exact block into your terminal to safely wipe and configure your backend Dockerfile in one go.

```bash
cd ~/GenTechHRPortal

cat << 'EOF' > backend/Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy project files
COPY pom.xml .
COPY src src

# Build
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
```

### Step 2: Create Frontend Dockerfile

Copy and paste this exact block into your terminal for the frontend setup:

```bash
cd ~/GenTechHRPortal

cat << 'EOF' > frontend/Dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
EOF
```

### Step 3: Create docker-compose.yml

Copy and paste this block into your terminal to create the main docker stack config:

```bash
cd ~/GenTechHRPortal

cat << 'EOF' > docker-compose.yml
services:
  postgres:
    image: postgres:15-alpine
    container_name: hr-portal-db
    restart: unless-stopped
    environment:
      POSTGRES_DB: hrportal
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ${DB_PASSWORD:-changeme}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - hr-portal-network

  backend:
    build: ./backend
    container_name: hr-portal-backend
    restart: unless-stopped
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/hrportal
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-changeme}
      SPRING_MAIL_HOST: ${MAIL_HOST:-smtp.gmail.com}
      SPRING_MAIL_PORT: ${MAIL_PORT:-587}
      SPRING_MAIL_USERNAME: ${MAIL_USERNAME:-}
      SPRING_MAIL_PASSWORD: ${MAIL_PASSWORD:-}
      JWT_SECRET: ${JWT_SECRET:-your-secret-key-change-this}
    ports:
      - "8081:8081"
    networks:
      - hr-portal-network

  frontend:
    build: ./frontend
    container_name: hr-portal-frontend
    restart: unless-stopped
    depends_on:
      - backend
    ports:
      - "80:80"
    networks:
      - hr-portal-network

volumes:
  postgres_data:

networks:
  hr-portal-network:
    driver: bridge
EOF
```

### Step 4: Create .env File

Create `.env` file in project root with your database, jwt, and email credentials:

```bash
cd ~/GenTechHRPortal

cat << 'EOF' > .env
DB_PASSWORD=YourSecurePassword123
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
JWT_SECRET=this-is-a-much-longer-secret-key-that-is-32-chars-long
EOF

chmod 600 .env
```

**Important:** JWT_SECRET must be at least 32 characters long! Make sure to replace `MAIL_USERNAME` and `MAIL_PASSWORD` with your actual setup.

### Step 5: Update Backend SecurityConfig (CORS)

You must open `SecurityConfig.java` to whitelist your VM IP address manually:

```bash
nano ~/GenTechHRPortal/backend/src/main/java/com/gentech/hrportal/config/SecurityConfig.java
```

Scroll through the file and replace the `corsConfigurationSource()` function with this one, and modify **YOUR_VM_IP** with your actual VM External IP:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(Arrays.asList(
        "https://gentechhrportalapp.onrender.com",
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://YOUR_VM_IP",          // Replace with your VM IP
        "http://YOUR_VM_IP:80",       // Replace with your VM IP
        "*"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","*"));
    configuration.setAllowCredentials(false);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```
*(Save with `Ctrl + O` -> `Enter`, then exit with `Ctrl + X`)*

**Replace `YOUR_VM_IP` with your actual VM IP address (e.g., 34.100.129.168).**

### Step 6: Update Frontend API URL

Set your Frontend to target your VM's public IP address instead of localhost.

```bash
nano ~/GenTechHRPortal/frontend/src/services/api.js
```

Change the `API_URL` variable to point to your backend:
```javascript
// Change from localhost to VM IP
const API_URL = 'http://YOUR_VM_IP:8081/api';  // Replace with your VM IP
```
*(Save with `Ctrl + O` -> `Enter`, then exit with `Ctrl + X`)*

---

## Build & Deploy

### Step 1: Build and Start Containers

```bash
cd ~/GenTechHRPortal

# Stop any existing containers
sudo docker-compose down

# Remove old images (optional but recommended for clean build)
sudo docker rmi gentechhrportal-backend gentechhrportal-frontend 2>/dev/null

# Build and start
sudo docker-compose up --build -d

# Wait for startup (backend takes ~2 minutes)
sleep 120
```

### Step 2: Verify Deployment

```bash
# Check container status
sudo docker-compose ps

# Check backend logs
sudo docker-compose logs --tail=20 backend

# Test API
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"superadmin","password":"superadmin123"}'
```

### Step 3: Access Application

Open browser and navigate to:
- **Frontend:** http://YOUR_VM_IP
- **Backend API:** http://YOUR_VM_IP:8081

---

## Troubleshooting

### Issue 1: Port 8081 Not Accessible

**Symptom:** Browser shows "This site can't be reached"

**Solution:**
```bash
# Open port in internal firewall (if applicable)
sudo ufw allow 8081/tcp
sudo ufw reload

# Google Cloud Firewall Check:
# Ensure you ticked "Allow HTTP traffic" on the VM Edit page AND created the custom VPC Firewall rule for port 8081 with Targets set to "All instances in the network".
```

### Issue 2: CORS Error

**Symptom:** Browser console shows "CORS policy: No 'Access-Control-Allow-Origin' header"

**Solution:**
Update `SecurityConfig.java` to include your VM IP in `corsConfigurationSource()`.

### Issue 3: JWT Secret Too Short

**Symptom:** Login fails with "WeakKeyException: The specified key byte array is 208 bits"

**Solution:**
Update `.env` with longer JWT_SECRET (minimum 32 characters):
```bash
cat << 'EOF' > .env
JWT_SECRET=this-is-a-much-longer-secret-key-that-is-32-chars-long
EOF
```

Then restart backend:
```bash
sudo docker-compose stop backend
sudo docker-compose rm -f backend
sudo docker-compose up -d backend
```

### Issue 4: Database Connection Failed

**Symptom:** Backend logs show "Connection refused" to PostgreSQL

**Solution:**
```bash
# Check if postgres is running
sudo docker-compose ps

# Restart postgres
sudo docker-compose restart postgres

# Wait 30 seconds
sleep 30

# Restart backend
sudo docker-compose restart backend
```

### Issue 5: Frontend Not Reflecting Changes

**Symptom:** Changes to api.js not reflected in browser

**Solution:**
```bash
# Hard rebuild frontend
sudo docker-compose up --build -d frontend

# Clear browser cache (Ctrl + Shift + R)
```

---

## Post-Deployment

### Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Super Admin | `superadmin` | `superadmin123` |
| Admin | `admin` | `admin123` |
| Developer | `developer1` | `developer123` |
| HR | `hr1` | `hr123` |
| Manager | `manager1` | `manager123` |

### Useful Commands

```bash
# View logs
sudo docker-compose logs -f

# View specific service logs
sudo docker-compose logs -f backend
sudo docker-compose logs -f frontend
sudo docker-compose logs -f postgres

# Restart services
sudo docker-compose restart backend

# Stop all services
sudo docker-compose down

# Backup database
sudo docker exec hr-portal-db pg_dump -U postgres hrportal > backup.sql

# Restore database
cat backup.sql | sudo docker exec -i hr-portal-db psql -U postgres -d hrportal
```

### SSL/HTTPS Setup (Optional)

```bash
# Install certbot
sudo apt install -y certbot

# Get certificate
sudo certbot certonly --standalone -d your-domain.com

# Update docker-compose to use HTTPS
```

---

## Summary

Your GenTech HR Portal should now be accessible at:
- **Frontend:** http://YOUR_VM_IP
- **Backend API:** http://YOUR_VM_IP:8081

**Key Configuration Files Modified:**
1. `backend/src/main/java/com/gentech/hrportal/config/SecurityConfig.java` - CORS settings
2. `frontend/src/services/api.js` - API URL
3. `.env` - Environment variables
4. `docker-compose.yml` - Container orchestration
5. `backend/Dockerfile` - Backend container build
6. `frontend/Dockerfile` - Frontend container build

**Important Security Notes:**
- Change default passwords after first login
- Use strong JWT_SECRET (32+ characters)
- Restrict firewall rules in production
- Enable HTTPS for production use

---

## Support

For issues, check:
1. Container logs: `sudo docker-compose logs`
2. Application logs in browser DevTools (F12)
3. Network tab in browser DevTools for API calls
