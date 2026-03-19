# GenTech HR Portal - VM Deployment Guide

This guide covers deploying the GenTech HR Portal application on a Virtual Machine (VM).

## Table of Contents
1. [VM Specifications](#vm-specifications)
2. [Option 1: Docker Deployment (Recommended)](#option-1-docker-deployment-recommended)
3. [Option 2: Manual Deployment](#option-2-manual-deployment)
4. [Production Considerations](#production-considerations)

---

## VM Specifications

### Minimum Requirements
| Resource | Specification |
|----------|--------------|
| OS | Ubuntu 22.04 LTS / Windows Server 2019+ |
| CPU | 2 vCPU cores |
| RAM | 4 GB |
| Storage | 20 GB SSD |
| Network | Public IP (for external access) |

### Recommended for Production
| Resource | Specification |
|----------|--------------|
| OS | Ubuntu 22.04 LTS |
| CPU | 4 vCPU cores |
| RAM | 8 GB |
| Storage | 50 GB SSD |

### Ports Required
| Port | Service | Description |
|------|---------|-------------|
| 22 | SSH | Remote access (Linux) |
| 3389 | RDP | Remote access (Windows) |
| 80 | HTTP | Frontend access |
| 443 | HTTPS | Secure frontend access |
| 8080 | Backend API | Spring Boot (dev) |
| 5432 | PostgreSQL | Database |

---

## Option 1: Docker Deployment (Recommended)

This is the easiest and most reliable method.

### Step 1: Provision VM

**Cloud Providers:**
- **AWS:** EC2 t3.medium (2 vCPU, 4GB RAM)
- **Azure:** Standard_B2s (2 vCPU, 4GB RAM)
- **GCP:** e2-medium (2 vCPU, 4GB RAM)
- **DigitalOcean:** Basic Droplet 4GB RAM

### Step 2: Install Docker

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
sudo apt install -y docker.io docker-compose

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group
sudo usermod -aG docker $USER
newgrp docker
```

### Step 3: Create Docker Compose File

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: hr-portal-db
    environment:
      POSTGRES_DB: hrportal
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: your_secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - hr-portal-network

  backend:
    build: ./backend
    container_name: hr-portal-backend
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/hrportal
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: your_secure_password
      SPRING_MAIL_HOST: smtp.gmail.com
      SPRING_MAIL_PORT: 587
      SPRING_MAIL_USERNAME: your-email@gmail.com
      SPRING_MAIL_PASSWORD: your-app-password
    ports:
      - "8080:8080"
    networks:
      - hr-portal-network

  frontend:
    build: ./frontend
    container_name: hr-portal-frontend
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
```

### Step 4: Create Dockerfiles

**Backend Dockerfile** (`backend/Dockerfile`):

```dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/hr-portal-0.0.1-SNAPSHOT.jar"]
```

**Frontend Dockerfile** (`frontend/Dockerfile`):

```dockerfile
# Build stage
FROM node:18-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html

# Create nginx config for React Router
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html; \
        try_files $uri $uri/ /index.html; \
    } \
    location /api { \
        proxy_pass http://backend:8080; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### Step 5: Deploy Application

```bash
# Clone your repository
git clone <your-repo-url>
cd hr-portal

# Create backend application.properties for Docker
cat > backend/src/main/resources/application-docker.properties << 'EOF'
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/hrportal}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:password}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Email Configuration
spring.mail.host=${SPRING_MAIL_HOST:smtp.gmail.com}
spring.mail.port=${SPRING_MAIL_PORT:587}
spring.mail.username=${SPRING_MAIL_USERNAME:}
spring.mail.password=${SPRING_MAIL_PASSWORD:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT
jwt.secret=${JWT_SECRET:your-secret-key-here}
jwt.expiration=${JWT_EXPIRATION:86400000}
EOF

# Build and run
sudo docker-compose up --build -d

# Check status
sudo docker-compose ps
sudo docker-compose logs -f
```

### Step 6: Initialize Database

```bash
# Create database manually (if needed)
sudo docker exec -it hr-portal-db psql -U postgres -c "CREATE DATABASE hrportal;"

# Default super admin will be created on first run
```

---

## Option 2: Manual Deployment

### Step 1: Install Dependencies

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install -y openjdk-17-jdk
java -version

# Install Maven
sudo apt install -y maven
mvn -version

# Install Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs
node -v
npm -v

# Install PostgreSQL
sudo apt install -y postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Install Nginx (for serving frontend)
sudo apt install -y nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

### Step 2: Configure PostgreSQL

```bash
# Switch to postgres user
sudo -u postgres psql

# Create database
CREATE DATABASE hrportal;

# Create user (optional, use postgres user for simplicity)
ALTER USER postgres WITH PASSWORD 'your_secure_password';

# Exit
\q
```

### Step 3: Configure Backend

```bash
# Create application directory
sudo mkdir -p /opt/hr-portal/backend
sudo chown -R $USER:$USER /opt/hr-portal

# Copy backend files
cp -r backend/* /opt/hr-portal/backend/

# Update application.properties
cat > /opt/hr-portal/backend/src/main/resources/application.properties << 'EOF'
spring.datasource.url=jdbc:postgresql://localhost:5432/hrportal
spring.datasource.username=postgres
spring.datasource.password=your_secure_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# OTP Configuration
app.otp.expiry.minutes=15

# Server
server.port=8080

# JWT
jwt.secret=your-secret-key-here-change-in-production
jwt.expiration=86400000
EOF

# Build backend
cd /opt/hr-portal/backend
mvn clean package -DskipTests
```

### Step 4: Configure Frontend

```bash
# Create frontend directory
sudo mkdir -p /opt/hr-portal/frontend
sudo chown -R $USER:$USER /opt/hr-portal/frontend

# Copy frontend files
cp -r frontend/* /opt/hr-portal/frontend/

# Update API base URL
cd /opt/hr-portal/frontend
# Edit src/services/api.js to point to VM IP
# Change: baseURL: 'http://YOUR_VM_IP:8080'

# Build frontend
npm ci
npm run build

# Copy to nginx
sudo cp -r build/* /var/www/html/
sudo chown -R www-data:www-data /var/www/html
```

### Step 5: Create Systemd Services

**Backend Service** (`/etc/systemd/system/hr-portal-backend.service`):

```ini
[Unit]
Description=GenTech HR Portal Backend
After=network.target postgresql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/hr-portal/backend
ExecStart=/usr/bin/java -jar target/hr-portal-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=hr-portal-backend

[Install]
WantedBy=multi-user.target
```

**Commands:**

```bash
# Create service file
sudo tee /etc/systemd/system/hr-portal-backend.service > /dev/null << 'EOF'
[Unit]
Description=GenTech HR Portal Backend
After=network.target postgresql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/hr-portal/backend
ExecStart=/usr/bin/java -jar target/hr-portal-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=hr-portal-backend

[Install]
WantedBy=multi-user.target
EOF

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable hr-portal-backend
sudo systemctl start hr-portal-backend

# Check status
sudo systemctl status hr-portal-backend
sudo journalctl -u hr-portal-backend -f
```

### Step 6: Configure Nginx

```bash
# Create nginx config
sudo tee /etc/nginx/sites-available/hr-portal > /dev/null << 'EOF'
server {
    listen 80;
    server_name _;  # Accept any server name

    root /var/www/html;
    index index.html;

    # Frontend - React app
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Backend API proxy
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # Error pages
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        root /var/www/html;
    }
}
EOF

# Enable site
sudo ln -s /etc/nginx/sites-available/hr-portal /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# Test and reload nginx
sudo nginx -t
sudo systemctl reload nginx
```

---

## Production Considerations

### 1. SSL/HTTPS with Let's Encrypt

```bash
# Install certbot
sudo apt install -y certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal test
sudo certbot renew --dry-run
```

### 2. Firewall Configuration

```bash
# UFW (Ubuntu)
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# Check status
sudo ufw status
```

### 3. Database Backup Script

```bash
# Create backup script
sudo tee /opt/backup/backup-db.sh > /dev/null << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backup"
DB_NAME="hrportal"
DB_USER="postgres"

# Create backup
pg_dump -U $DB_USER $DB_NAME > $BACKUP_DIR/hrportal_$DATE.sql

# Keep only last 7 backups
ls -t $BACKUP_DIR/hrportal_*.sql | tail -n +8 | xargs -r rm
EOF

sudo chmod +x /opt/backup/backup-db.sh

# Add to crontab (daily at 2 AM)
echo "0 2 * * * /opt/backup/backup-db.sh" | sudo crontab -
```

### 4. Environment Variables Security

```bash
# Create .env file
sudo tee /opt/hr-portal/.env > /dev/null << 'EOF'
DB_PASSWORD=your_secure_password
MAIL_PASSWORD=your_app_password
JWT_SECRET=your-secret-key-here
EOF

sudo chmod 600 /opt/hr-portal/.env
sudo chown root:root /opt/hr-portal/.env
```

### 5. Monitoring with PM2 (optional)

```bash
# Install PM2
sudo npm install -g pm2

# For frontend (if not using nginx)
cd /opt/hr-portal/frontend
pm install -g serve
pm2 serve build 3000 --name "hr-portal-frontend"

# Save PM2 config
pm2 save
pm2 startup
```

### 6. Health Check Endpoint

Add to your `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

Access: `http://your-vm-ip:8080/actuator/health`

---

## Troubleshooting

### Backend Won't Start

```bash
# Check logs
sudo journalctl -u hr-portal-backend -n 50

# Check if port is in use
sudo netstat -tlnp | grep 8080

# Check database connection
sudo -u postgres psql -c "\l"
```

### Frontend Not Loading

```bash
# Check nginx error logs
sudo tail -f /var/log/nginx/error.log

# Check nginx config
sudo nginx -t

# Check if build exists
ls -la /var/www/html/
```

### Database Connection Issues

```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Check listening ports
sudo netstat -tlnp | grep 5432

# Test connection
psql -h localhost -U postgres -d hrportal
```

---

## Quick Start Checklist

- [ ] Provision VM (Ubuntu 22.04, 4GB RAM, 2 vCPU)
- [ ] Open ports 22, 80, 443, 8080
- [ ] Install Docker & Docker Compose (Option 1) OR Java, Node, PostgreSQL, Nginx (Option 2)
- [ ] Clone repository
- [ ] Configure database credentials
- [ ] Configure email credentials (for OTP)
- [ ] Build and deploy application
- [ ] Test all functionality
- [ ] Configure SSL (Let's Encrypt)
- [ ] Set up automated backups
- [ ] Configure firewall

---

## Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Super Admin | superadmin | superadmin123 |
| Admin | admin | admin123 |
| Employee | developer1 | developer123 |

**Important:** Change default passwords immediately after first login!

---

## Support

For issues or questions:
1. Check application logs: `sudo journalctl -u hr-portal-backend -f`
2. Check nginx logs: `sudo tail -f /var/log/nginx/error.log`
3. Check Docker logs: `sudo docker-compose logs -f`
