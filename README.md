# Scooter Lease Project Documentation

<div align="center">
<p align="center">
  <img src="./logo.png" alt="Scooter Lease Logo" height="200" width="200" style="border-radius: 6px;"/>
</p>
</div>

## 📋 Project Overview
Scooter Lease is a modular electric scooter rental management system that adopts a front-end and back-end separation design pattern. The project consists of three main parts: mobile application (app-frontend), backend service (snowy), and admin panel (snowy-admin-web). The backend uses a modular design with a plugin architecture for feature extension and maintenance.

## 🛠️ Technology Stack

### 📱 Mobile Application (app-frontend)
- Framework: Vue 3 + Tauri
- UI Components: TDesign Mobile Vue
- State Management: Pinia
- Routing: Vue Router
- Map Service: AMap API
- Build Tool: Vite

### ⚙️ Backend Service (snowy)
- Framework: Spring Boot 3.2.1
- Database: MySQL
- ORM: MyBatis-Plus
- Cache: Redis
- Security: Sa-Token
- Documentation: Knife4j (Swagger)
- Architecture Features:
  - Modular Design: System functions divided into independent modules
  - Plugin Architecture: Supports dynamic extension of functional modules
  - Unified Data Source: All modules share the same database
  - Unified Authentication: Centralized user authentication and authorization
  - Internationalization: Multi-language support
  - Data Encryption: Using SM-Crypto for data encryption

### 💻 Admin Panel (snowy-admin-web)
- Framework: Vue 3
- UI Components: Ant Design Vue 4.x
- State Management: Pinia
- Routing: Vue Router
- Charts: ECharts
- Rich Text: TinyMCE
- Build Tool: Vite

## 📁 Project Structure

### 📱 app-frontend (Mobile Application)
```
app-frontend/
├── src/                # Source code directory
├── src-tauri/         # Tauri configuration
├── public/            # Static resources
└── dist/              # Build output directory
```

### ⚙️ snowy (Backend Service)
```
snowy/
├── snowy-common/      # Common module: utilities and configurations
├── snowy-plugin/      # Plugin module: business functionality
├── snowy-plugin-api/  # Plugin API interface: interface specifications
├── snowy-web-app/     # Main startup module: integrates all functional modules
└── database/          # Database scripts: initialization scripts
```

### 💻 snowy-admin-web (Admin Panel)
```
snowy-admin-web/
├── src/              # Source code directory
├── public/           # Static resources
└── dist/             # Build output directory
```

## ✨ Features

### 📱 Mobile Application
- User registration and login
- Scooter rental
- Real-time location tracking
- Payment functionality
- Order management
- User feedback

### ⚙️ Backend Service
- User authentication and authorization
- Order management
- Payment processing
- Device management
- Data statistics and analysis
- System monitoring
- Plugin extensions

### 💻 Admin Panel
- User management
- Device management
- Order management
- Financial management
- Data statistics and visualization
- System configuration
- Permission management

## 🔧 Development Requirements

### 📱 Mobile Development
- Node.js >= 16
- Rust (for Tauri)
- Related development tools

### ⚙️ Backend Development
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 💻 Admin Panel Development
- Node.js >= 16
- Related development tools

## 🚀 Quick Start

### 📱 Mobile Application
```bash
cd app-frontend
npm install
npm run dev
```

Or

```bash
cd app-frontend
npm run tauri android build
```
Install and run the packaged APK on your mobile device

### ⚙️ Backend Service
```bash
cd snowy
mvn clean install
cd snowy-web-app
mvn spring-boot:run
```

### 💻 Admin Panel
```bash
cd snowy-admin-web
npm install
npm run dev
```

## 📦 Deployment Guide

### 📱 Mobile Application
1. Build the application:
```bash
cd app-frontend
npm run tauri android build
```
2. Publish the generated installation package

### ⚙️ Backend Service
1. Build the service:
```bash
cd snowy
mvn clean package
```
2. Deploy the generated JAR file

### 💻 Admin Panel
1. Build the application:
```bash
cd snowy-admin-web
npm run build
```
2. Deploy the dist directory to a web server

## ⚠️ Notes
1. Ensure all necessary environment variables and configuration files are properly set
2. Database needs to be pre-created with initial data imported
3. Ensure Redis service is running properly
4. Check network port configurations to avoid conflicts

## 🤝 Contributing
1. Fork the project
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License
Apache License 2.0 
