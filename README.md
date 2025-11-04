# School Management System 🎓

A complete, enterprise-grade school administration platform built with **Spring Boot** and **React**. This system facilitates communication between administrators, teachers, students, and parents while managing academic records, finances, and performance tracking.

## 🌟 Key Features

### Core Functionality
- **Multi-Role Access Control**: Admin, Principal, Vice Principal, Teachers, Students, Parents
- **Academic Management**: Timetables, grades, exams, assignments, attendance
- **Financial Management**: Fee structures, payment tracking, receipt uploads, automated reminders
- **Performance Analytics**: Track student progress with trend analysis and predictions
- **Communication Hub**: Internal messaging, SMS notifications (Africa's Talking), email alerts
- **Appointment Scheduling**: Parent-teacher conferences with automated reminders
- **2FA Security**: SMS-based two-factor authentication via Africa's Talking

### Advanced Features
- **Conditional Result Release**: Results only visible when fees are paid
- **Performance Tracking**: Monthly/quarterly trend analysis with automatic alerts
- **Profile Management**: Upload profile pictures for students and teachers
- **Report Generation**: PDF transcripts, Excel grade sheets, attendance reports
- **Audit Trail**: Complete logging of all system activities
- **API Documentation**: Swagger/OpenAPI integration

## 🏗️ Architecture

### Backend Stack
- **Spring Boot 3.2** - Core framework
- **Spring Security + JWT** - Authentication & authorization
- **Spring Data JPA** - ORM with Hibernate
- **PostgreSQL** - Primary database
- **Africa's Talking SDK** - SMS & 2FA
- **iText PDF** - Report generation
- **Apache POI** - Excel generation
- **MapStruct** - DTO mapping
- **Swagger/OpenAPI** - API documentation

### Frontend Stack
- **React 18** - UI framework (JavaScript)
- **Redux Toolkit** - State management
- **React Router v6** - Navigation
- **Chart.js/Recharts** - Performance graphs
- **Tailwind CSS** - Styling
- **Axios** - API communication
- **React Hook Form** - Form handling

## 📁 Project Structure

```
school-management-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/schoolsystem/
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   ├── repository/      # Spring Data repositories
│   │   │   │   ├── service/         # Business logic
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── dto/            # Data Transfer Objects
│   │   │   │   ├── security/        # JWT, 2FA, authentication
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── exception/       # Custom exceptions
│   │   │   │   └── util/           # Utility classes
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── schema.sql
│   │   └── test/                    # Unit & integration tests
│   └── pom.xml
│
└── frontend/
    ├── public/
    ├── src/
    │   ├── components/
    │   │   ├── common/              # Reusable components
    │   │   ├── auth/                # Login, 2FA
    │   │   ├── dashboards/          # Role-specific dashboards
    │   │   ├── students/            # Student management
    │   │   ├── teachers/            # Teacher features
    │   │   ├── finance/             # Payment management
    │   │   └── timetable/           # Timetable views
    │   ├── redux/
    │   │   ├── store.js
    │   │   └── slices/              # Redux slices
    │   ├── services/                # API services
    │   ├── hooks/                   # Custom React hooks
    │   └── utils/                   # Helper functions
    └── package.json
```

## 🚀 Getting Started

### Prerequisites
- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Node.js 18+**
- **Africa's Talking Account** (for SMS features)

### Backend Setup

1. **Install PostgreSQL** and create database:
```bash
createdb school_management_db
```

2. **Configure application.properties**:
```properties
# Update these values
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
jwt.secret=your-secret-key-at-least-256-bits
africas.talking.api.key=your-africas-talking-api-key
```

3. **Run database schema**:
```bash
psql -d school_management_db -f backend/src/main/resources/schema.sql
```

4. **Build and run backend**:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### Frontend Setup

1. **Install dependencies**:
```bash
cd frontend
npm install
```

2. **Configure API endpoint** (if needed):
```javascript
// src/services/api.js
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});
```

3. **Start development server**:
```bash
npm start
```

Frontend will start on `http://localhost:3000`

## 🔐 Default Login

**Admin Account:**
- Username: `admin`
- Password: `Admin@123` (CHANGE THIS!)

## 📊 Database Schema

The system uses a comprehensive PostgreSQL schema with:
- **20+ tables** covering all aspects of school administration
- **Proper foreign key relationships** ensuring data integrity
- **Indexes** on frequently queried columns for performance
- **Triggers** for automatic timestamp updates
- **Enums** for type safety (roles, statuses, etc.)

### Key Entities
- Users (polymorphic: all user types)
- Students, Teachers, Parents (extends users)
- Classes, Sections, Subjects
- Academic Years, Terms/Quarters
- Exams, Grades, Assignments
- Payments, Fee Structures
- Timetables, Attendance
- Notifications, Messages, Appointments

## 🔌 API Endpoints

### Authentication
```
POST   /api/auth/login              # Login
POST   /api/auth/register           # Register new user
POST   /api/auth/verify-2fa         # Verify 2FA code
POST   /api/auth/refresh-token      # Refresh JWT
```

### Students
```
GET    /api/students                # List all students
GET    /api/students/{id}           # Get student details
POST   /api/students                # Create student
PUT    /api/students/{id}           # Update student
DELETE /api/students/{id}           # Delete student
GET    /api/students/{id}/performance  # Performance analytics
```

### Grades
```
GET    /api/grades/student/{id}     # Student's grades
POST   /api/grades                  # Submit grade
PUT    /api/grades/{id}             # Update grade
GET    /api/grades/results/{studentId}/{termId}  # Results (if fees paid)
```

### Payments
```
GET    /api/payments/student/{id}   # Student payment history
POST   /api/payments                # Record payment
POST   /api/payments/upload-receipt # Upload receipt
GET    /api/payments/pending        # List pending payments
```

**Full API documentation available at:** `http://localhost:8080/swagger-ui.html`

## 🎨 Frontend Features

### Dashboards
Each role has a customized dashboard:
- **Admin**: System overview, user management, reports
- **Principal**: School-wide analytics, staff management
- **Teachers**: Class management, grade submission, attendance
- **Students**: Grades, assignments, timetable, payment status
- **Parents**: Child's performance, fees, communication with teachers

### Key Components
- **Performance Charts**: Line graphs showing grade trends
- **Payment Tracker**: Visual fee payment status
- **Notification Bell**: Real-time alerts
- **Profile Picture Upload**: With cropping functionality
- **Timetable View**: Interactive weekly schedule
- **Grade Book**: Teacher-friendly grade entry interface

## 🔔 Notification System

### Channels
1. **In-App**: Real-time notifications within the platform
2. **SMS**: Via Africa's Talking for:
   - Fee reminders
   - Result notifications
   - Appointment confirmations
   - 2FA codes
3. **Email**: For detailed communications

### Triggers
- Fee payment due/overdue
- Results released
- Appointments scheduled
- Performance decline (2+ consecutive drops)
- Attendance issues

## 📈 Performance Analytics

The system tracks and analyzes student performance:
- **Trend Analysis**: IMPROVING, DECLINING, STABLE
- **Percentage Change**: Comparison with previous period
- **Rank Tracking**: Position in class
- **Automated Alerts**: Parents and teachers notified of concerning trends
- **Predictive Analytics**: Future performance predictions (ML-ready)

## 🔒 Security Features

- **JWT Authentication**: Stateless, secure token-based auth
- **Role-Based Access Control (RBAC)**: Granular permissions
- **2FA**: Optional SMS-based verification
- **Password Hashing**: BCrypt encryption
- **Audit Logging**: Track all system actions
- **CORS Configuration**: Secure cross-origin requests
- **Input Validation**: Bean Validation (JSR 380)

## 🚦 Development Workflow

### Running Tests
```bash
# Backend
cd backend
mvn test

# Frontend
cd frontend
npm test
```

### Building for Production
```bash
# Backend (creates JAR)
mvn clean package

# Frontend (creates optimized build)
npm run build
```

### Docker Deployment (Optional)
```bash
docker-compose up -d
```

## 📱 Africa's Talking Integration

### Setup
1. Sign up at [africas talking.com](https://africastalking.com)
2. Get your API key and username
3. Add credentials to `application.properties`
4. Top up account for SMS credits

### Features Used
- **SMS API**: Sending notifications
- **Airtime API**: Potential future integration
- **USSD**: Planned for feature phone support

## 🎯 Future Enhancements

### Phase 2
- [ ] Mobile app (React Native)
- [ ] Payment gateway integration (M-Pesa, Airtel Money)
- [ ] USSD menu for basic queries
- [ ] Biometric attendance
- [ ] AI-powered performance predictions

### Phase 3
- [ ] Multi-tenancy support (multiple schools)
- [ ] Advanced analytics dashboard
- [ ] Parent-teacher video conferencing
- [ ] Library management module
- [ ] Transport management module

## 🤝 Contributing

This is an educational project. Contributions and suggestions welcome!

## 📄 License

MIT License - Feel free to use this for learning or commercial purposes.

## 👨‍🏫 About

Built as a comprehensive portfolio project to demonstrate:
- Enterprise Spring Boot architecture
- Modern React development
- RESTful API design
- Database design & optimization
- Third-party API integration (Africa's Talking)
- Security best practices
- Professional documentation

---

**Need help?** Check the `/docs` folder for detailed guides or open an issue.

**Looking to hire?** This project showcases production-ready skills in Java/Spring Boot development, React, PostgreSQL, and system architecture.
