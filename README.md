# Traffic Management System (TMS)

[![Java](https://img.shields.io/badge/Java-EE-blue.svg)](https://www.oracle.com/java/)
[![JPA](https://img.shields.io/badge/JPA-2.0-green.svg)](https://www.oracle.com/java/technologies/persistence-jsp.html)
[![WebLogic](https://img.shields.io/badge/WebLogic-Server-orange.svg)](https://www.oracle.com/middleware/technologies/weblogic.html)

A comprehensive Traffic Management System built with Java EE technologies for managing vehicle registrations, traffic offenses, and violations.

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [User Roles](#user-roles)
- [Project Structure](#project-structure)
- [Development](#development)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

The Traffic Management System (TMS) is an enterprise-grade application designed to streamline traffic-related operations including:
- Vehicle registration and management
- Traffic offense tracking and reporting
- Owner information management
- User role-based access control
- Report generation

## ✨ Features

### For Administrators
- ✅ User management (create, update, delete users)
- ✅ Role management and assignment
- ✅ System configuration
- ✅ View all system activities

### For Clerks
- ✅ Vehicle registration processing
- ✅ Owner information management
- ✅ Application processing
- ✅ Vehicle transfer processing

### For Police Officers (Cops)
- ✅ Report traffic offenses
- ✅ Upload offense evidence
- ✅ Track offense status
- ✅ View offense history

### For RTO Officers
- ✅ Approve vehicle registrations
- ✅ Manage vehicle applications
- ✅ Generate reports
- ✅ View vehicle details

### For Vehicle Owners
- ✅ View owned vehicles
- ✅ Check offense history
- ✅ Transfer vehicle ownership
- ✅ Update personal information

## 🛠 Technology Stack

### Backend
- **Java EE 6+** - Enterprise Java platform
- **EJB 3.x** - Enterprise JavaBeans for business logic
- **JPA 2.0** - Java Persistence API with EclipseLink
- **WebLogic Server** - Application server
- **Oracle Database** - Relational database

### Frontend
- **JavaServer Faces (JSF)** - Component-based web framework
- **Oracle ADF Faces** - Rich UI components
- **Apache Trinidad** - Additional JSF components
- **JSPX** - JSP XML syntax

### Build & Development
- **Oracle JDeveloper** - IDE
- **WebLogic Maven Plugin** - Build automation

## 🏗 Architecture

The application follows a **3-tier architecture**:

```
┌─────────────────────────────────────┐
│     Presentation Layer (Web)        │
│   - JSF Pages (JSPX)                │
│   - Backing Beans                   │
│   - Navigation Rules                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Business Layer (EJB)            │
│   - Session Beans (Stateless)       │
│   - Business Logic                  │
│   - Transaction Management          │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Persistence Layer (JPA)         │
│   - Entity Classes                  │
│   - Named Queries                   │
│   - Relationships                   │
└──────────────┬──────────────────────┘
               │
         ┌─────▼─────┐
         │  Database │
         │  (Oracle) │
         └───────────┘
```

## 🗄 Database Schema

### Core Tables
- **USERS** - User accounts and authentication
- **ROLES** - User roles (Admin, Clerk, Cop, RTO, Owner)
- **OWNER** - Vehicle owner information
- **VEHICLE** - Vehicle details
- **VEHICLE_APPLICATION** - Vehicle registration applications
- **OFFENSE** - Traffic offense records
- **OFFENSE_DETAILS** - Offense type definitions

### Entity Relationships
```
USERS ──┬─→ ROLES
        └─→ OFFENSE

OWNER ──→ VEHICLE_APPLICATION ──┬─→ VEHICLE
                                 └─→ OFFENSE

OFFENSE ──→ OFFENSE_DETAILS
```

## 📥 Installation

### Prerequisites
- **Java Development Kit (JDK)** 1.6 or higher
- **Oracle WebLogic Server** 10.3+ or 12c
- **Oracle Database** 11g or higher
- **Oracle JDeveloper** 11g or 12c (recommended for development)

### Step 1: Clone the Repository
```bash
git clone https://github.com/9703790514/Traffic__Management__System__.git
cd Traffic__Management__System__
```

### Step 2: Database Setup
1. Create an Oracle database instance
2. Execute the database schema creation scripts:
   ```sql
   -- Located in Model/database/Connection1/TMS/
   -- Import tables in the following order:
   -- 1. ROLES.table
   -- 2. USERS.table
   -- 3. OWNER.table
   -- 4. VEHICLE.table
   -- 5. OFFENSE_DETAILS.table
   -- 6. VEHICLE_APPLICATION.table
   -- 7. OFFENSE.table
   ```

3. Create sequences:
   ```sql
   CREATE SEQUENCE VEHICLE_SEQ START WITH 1 INCREMENT BY 1;
   CREATE SEQUENCE OWNER_ID_SEQ START WITH 1 INCREMENT BY 1;
   CREATE SEQUENCE OFFENCE_ID_SEQ START WITH 1 INCREMENT BY 1;
   CREATE SEQUENCE VEHICLE_APP_SEQ START WITH 1 INCREMENT BY 1;
   ```

4. Insert default roles:
   ```sql
   INSERT INTO ROLES (ROLENAME) VALUES ('admin');
   INSERT INTO ROLES (ROLENAME) VALUES ('clerk');
   INSERT INTO ROLES (ROLENAME) VALUES ('cop');
   INSERT INTO ROLES (ROLENAME) VALUES ('rto');
   INSERT INTO ROLES (ROLENAME) VALUES ('owner');
   COMMIT;
   ```

5. Create default admin user:
   ```sql
   INSERT INTO USERS (USERNAME, PASSWORD, ROLENAME) 
   VALUES ('admin', 'admin123', 'admin');
   COMMIT;
   ```

### Step 3: Configure Data Source
1. Log in to WebLogic Server Admin Console (http://localhost:7001/console)
2. Navigate to Services → Data Sources
3. Create a new data source:
   - **JNDI Name:** `java:/app/jdbc/jdbc/Connection1DS`
   - **Database Type:** Oracle
   - **Database Name:** Your Oracle SID
   - **Host Name:** localhost
   - **Port:** 1521
   - **Database User Name:** Your database user
   - **Password:** Your database password
4. Test the connection and deploy to your target server

### Step 4: Update Configuration Files
Update the following files with your environment-specific settings:

**src/META-INF/Connection1-jdbc.xml:**
```xml
<jdbc-connection-pool>
  <connection-url>jdbc:oracle:thin:@localhost:1521:ORCL</connection-url>
  <principal>
    <name>YOUR_DB_USER</name>
  </principal>
  <credential>YOUR_DB_PASSWORD</credential>
</jdbc-connection-pool>
```

**Model/src/META-INF/persistence.xml:**
```xml
<persistence-unit name="Model">
  <jta-data-source>java:/app/jdbc/jdbc/Connection1DS</jta-data-source>
  <!-- Entity classes are already configured -->
</persistence-unit>
```

### Step 5: Build and Deploy
1. Open the project in Oracle JDeveloper
2. Right-click on **TmsApp.jws** → **Build**
3. Wait for the build to complete
4. Right-click on **ViewController.jpr** → **Deploy** → **To Application Server**
5. Select your WebLogic Server instance
6. Follow the deployment wizard

Alternative - Manual Deployment:
```bash
# Build the project
ant build  # or use JDeveloper build

# Deploy to WebLogic
cp ViewController/deploy/*.war $WEBLOGIC_HOME/domains/your_domain/autodeploy/
```

## ⚙ Configuration

### Application Configuration

**ViewController/public_html/WEB-INF/web.xml:**
- Configure servlet mappings
- Set context parameters
- Configure filters (JPS, Trinidad)

**ViewController/public_html/WEB-INF/faces-config.xml:**
- Define managed beans
- Configure navigation rules
- Set up converters and validators

**ViewController/public_html/WEB-INF/trinidad-config.xml:**
- Trinidad framework configuration
- Skin configuration

### Security Configuration

The application uses Oracle Java Platform Security (JPS) for authentication and authorization. Configuration is in:
- `src/META-INF/jps-config.xml`
- `ViewController/public_html/WEB-INF/web.xml` (JpsFilter)

## 🚀 Usage

### Accessing the Application
1. Start WebLogic Server
2. Navigate to: `http://localhost:7001/ViewController-context-root/faces/welcomepage.jspx`
   - Replace `ViewController-context-root` with your actual context root
3. Default credentials:
   - **Username:** admin
   - **Password:** admin123

### Application Workflow

#### Vehicle Registration (Clerk)
1. Login as clerk
2. Navigate to "Owner Management"
3. Add new owner details
4. Go to "Vehicle Management"
5. Register new vehicle
6. Process vehicle application

#### Report Offense (Cop)
1. Login as police officer
2. Navigate to "Report Offense"
3. Enter vehicle number
4. Select offense type
5. Upload evidence image (optional)
6. Submit offense report

#### Approve Registration (RTO)
1. Login as RTO officer
2. Navigate to "Pending Applications"
3. Review application details
4. Approve or reject application

#### Check Offenses (Owner)
1. Login as vehicle owner
2. View "My Vehicles"
3. Check offense history
4. View offense details

## 👥 User Roles

| Role | Permissions |
|------|------------|
| **Admin** | Full system access, user management, role assignment |
| **Clerk** | Vehicle registration, owner management, application processing |
| **Cop** | Report offenses, upload evidence, view offense records |
| **RTO** | Approve registrations, manage applications, generate reports |
| **Owner** | View owned vehicles, check offenses, transfer ownership |

## 📁 Project Structure

```
TmsApp/
├── TmsApp.jws                    # JDeveloper workspace file
│
├── Model/                        # EJB and Entity Model Project
│   ├── Model.jpr                 # JDeveloper project file
│   ├── src/
│   │   ├── META-INF/
│   │   │   ├── persistence.xml   # JPA configuration
│   │   │   └── weblogic-ejb-jar.xml
│   │   └── training/iqgateway/
│   │       ├── entities/         # JPA Entity classes
│   │       │   ├── Users.java
│   │       │   ├── Roles.java
│   │       │   ├── Owner.java
│   │       │   ├── Vehicle.java
│   │       │   ├── VehicleApplication.java
│   │       │   ├── Offense.java
│   │       │   └── OffenseDetails.java
│   │       ├── services/         # Session EJB beans
│   │       │   ├── AdminSessionEJB*.java
│   │       │   ├── ClerkSessionEJB*.java
│   │       │   ├── CopSessionEJB*.java
│   │       │   ├── RtoSessionEJB*.java
│   │       │   └── OwnerSessionEJB*.java
│   │       └── client/           # EJB client test classes
│   ├── database/                 # Database schema files
│   │   └── Connection1/
│   │       └── TMS/
│   │           ├── *.table       # Table definitions
│   │           └── TMS.schema
│   └── classes/                  # Compiled classes
│
├── ViewController/               # Web/UI Project
│   ├── ViewController.jpr
│   ├── public_html/
│   │   ├── *.jspx               # JSF pages (JSPX)
│   │   └── WEB-INF/
│   │       ├── web.xml          # Web application config
│   │       ├── faces-config.xml # JSF configuration
│   │       └── trinidad-config.xml
│   ├── src/
│   │   └── training/iqgateway/
│   │       ├── backing/         # JSF backing beans
│   │       │   ├── LoginBean.java
│   │       │   ├── UserBean.java
│   │       │   ├── VehicleBean.java
│   │       │   ├── OffenceBean.java
│   │       │   └── ...
│   │       └── servlet/         # Servlets
│   └── classes/                 # Compiled classes
│
├── src/
│   └── META-INF/                # Global configuration
│       ├── Connection1-jdbc.xml
│       └── jps-config.xml
│
├── README.md                    # This file
├── IMPROVEMENTS.md              # Code analysis and recommendations
└── .gitignore                   # Git ignore rules
```

## 💻 Development

### Setting Up Development Environment

1. **Install Oracle JDeveloper**
   - Download from Oracle website
   - Configure WebLogic Server integration

2. **Import Project**
   ```bash
   File → Open → TmsApp.jws
   ```

3. **Configure Database Connection**
   - Window → Database → New Connection
   - Enter Oracle database credentials
   - Test connection

4. **Run in Development Mode**
   - Right-click ViewController.jpr
   - Run → Run ViewController.jpr
   - JDeveloper will deploy to integrated WebLogic Server

### Coding Guidelines

- Follow Java naming conventions
- Use meaningful variable names
- Add JavaDoc comments for public methods
- Keep methods focused and concise
- Use proper exception handling
- Follow the existing architecture patterns

### Adding a New Entity

1. Create entity class in `Model/src/training/iqgateway/entities/`
2. Add JPA annotations
3. Update `persistence.xml` with new entity class
4. Create corresponding session EJB in services package
5. Add backing bean in ViewController
6. Create JSF page for UI

### Testing

Currently, the project lacks automated tests. Consider adding:
- **Unit Tests:** JUnit 5 for service layer
- **Integration Tests:** Arquillian for EJB testing
- **UI Tests:** Selenium for web interface

## 🐛 Known Issues

1. **Passwords stored in plain text** - See IMPROVEMENTS.md for security enhancement
2. **System.out.println** used for logging - Should migrate to proper logging framework
3. **Missing unit tests** - Test coverage needed
4. **TODO comments** - Some features not fully implemented

See [IMPROVEMENTS.md](IMPROVEMENTS.md) for detailed analysis and recommendations.

## 📝 API Documentation

### Session Beans

#### AdminSessionEJB
- `getUsersFindAll()` - Retrieve all users
- `persistUsers(Users)` - Create new user
- `mergeUsers(Users)` - Update existing user
- `removeUsers(Users)` - Delete user
- `getRolesFindAll()` - Retrieve all roles
- `findUserByUsername(String)` - Find user by username

#### ClerkSessionEJB
- `getOwnerFindAll()` - Retrieve all owners
- `persistOwner(Owner)` - Create new owner
- `mergeOwner(Owner)` - Update owner
- `getVehicleFindAll()` - Retrieve all vehicles
- `persistVehicle(Vehicle)` - Register new vehicle
- `persistVehicleApplication(VehicleApplication)` - Create application

#### CopSessionEJB
- `getOffenseFindAll()` - Retrieve all offenses
- `persistOffense(Offense)` - Report new offense
- `updateOffense(Offense)` - Update offense status
- `getOffenseDetailsFindAll()` - Get offense types

#### RtoSessionEJB
- `getVehicleApplicationFindAll()` - Get all applications
- `approveApplication(VehicleApplication)` - Approve registration
- `rejectApplication(VehicleApplication)` - Reject application

#### OwnerSessionEJB
- `getVehiclesByOwner(Long)` - Get owner's vehicles
- `getOffensesByVehicle(String)` - Get vehicle offenses
- `transferVehicle(String, Long)` - Transfer ownership

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes**
4. **Test thoroughly**
5. **Commit with clear messages**
   ```bash
   git commit -m "Add: Feature description"
   ```
6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```
7. **Create a Pull Request**

### Code Review Process
- All PRs require review
- Ensure code follows existing patterns
- Add tests for new features
- Update documentation

## 📄 License

This project is licensed for educational purposes.

## 👨‍💻 Authors

- Development Team - IQGateway Training

## 📞 Support

For issues and questions:
- Create an issue on GitHub
- Contact: [Your Contact Information]

## 🔮 Future Enhancements

- [ ] Implement password hashing (BCrypt)
- [ ] Add proper logging framework (SLF4J + Logback)
- [ ] Implement unit and integration tests
- [ ] Add RESTful API layer
- [ ] Mobile application
- [ ] Real-time notifications
- [ ] Advanced reporting and analytics
- [ ] Payment gateway integration for fines
- [ ] SMS/Email notifications
- [ ] Document management system
- [ ] Multi-language support
- [ ] Performance optimization and caching

## 📊 System Requirements

### Minimum Requirements
- **OS:** Windows 7+, Linux, macOS
- **RAM:** 4 GB
- **Disk Space:** 10 GB
- **Java:** JDK 1.6+
- **Database:** Oracle 11g+
- **Application Server:** WebLogic 10.3+

### Recommended Requirements
- **OS:** Windows 10+, Linux (Ubuntu 18.04+)
- **RAM:** 8 GB+
- **Disk Space:** 20 GB+
- **Java:** JDK 1.8+
- **Database:** Oracle 12c+
- **Application Server:** WebLogic 12c+

---

**Last Updated:** May 21, 2026  
**Version:** 1.0.0
