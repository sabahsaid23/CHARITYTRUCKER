# Charity Donation Tracker

A comprehensive donation management system consisting of an Android mobile application and a Spring Boot backend API. This system enables charity organizations to manage donations, projects, staff, and beneficiaries efficiently through role-based access control.

## 🏗️ Project Structure

```
Project/
├── backend-donation-tracker/          # Spring Boot Backend API
│   ├── src/main/java/com/charity/charity/donation/
│   │   ├── config/                    # Security and Web configuration
│   │   ├── controller/                # REST API controllers
│   │   ├── dto/                       # Data Transfer Objects
│   │   ├── model/                     # Entity models
│   │   ├── repository/                # Data access layer
│   │   └── services/                  # Business logic services
│   └── src/main/resources/
│       └── application.properties     # Database and server configuration
└── MyApplication/                     # Android Mobile Application
    └── app/src/main/
        ├── java/com/example/myapplication/
        │   ├── Activities/            # Android activities for different screens
        │   ├── Adapters/              # RecyclerView adapters
        │   ├── API/                   # Retrofit API interfaces
        │   └── Models/                # Data models
        └── res/
            ├── layout/                # XML layout files
            ├── values/                # Strings, colors, themes
            └── drawable/              # Images and drawable resources
```

## 🚀 Features

### 🔐 Authentication & User Management
- **Multi-role System**: Admin, Staff, and Donor roles with different permissions
- **Secure Login/Registration**: JWT-based authentication
- **Role-based Access Control**: Each role has specific features and UI

### 👨‍💼 Admin Features
- **Dashboard**: Overview of donations, projects, and allocations
- **User Management**: Add, edit, and manage staff and donor accounts
- **Project Management**: Create and assign projects to staff members
- **Allocation Management**: Assign donations to beneficiaries for specific projects
- **Donation Tracking**: View and manage all donations across the platform

### 👨‍💻 Staff Features
- **Assigned Projects**: View and manage projects assigned to them
- **Project Creation**: Add new projects (automatically assigned to self)
- **Beneficiary Management**: Add and manage beneficiaries for their projects
- **Donation Allocation**: Allocate donations to beneficiaries in need
- **Allocation History**: Track all allocations made by the staff member

### 💰 Donor Features
- **Project Browsing**: View available charity projects
- **Donation Making**: Contribute to projects of choice
- **Donation History**: Track personal donation history
- **Project Impact**: See projects they've supported

## 🛠️ Technology Stack

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.x
- **Database**: MySQL/PostgreSQL (configurable)
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **Java Version**: 17+

### Android App
- **Language**: Kotlin
- **Minimum SDK**: API 24 (Android 7.0)
- **Architecture**: Activity-based with RecyclerViews
- **Networking**: Retrofit for API calls
- **Build Tool**: Gradle

## 📱 Screenshots & User Interface

The app features a modern, intuitive interface with:
- **Role-specific dashboards** with relevant information
- **Clean, card-based layouts** for easy navigation
- **Form-based inputs** for data entry
- **List views** with search and filter capabilities
- **Responsive design** that works across different screen sizes

## 🔧 Installation & Setup

### Prerequisites
- Java 17 or higher
- Android Studio (for mobile app development)
- MySQL or PostgreSQL database
- Maven (for backend)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd backend-donation-tracker
   ```

2. **Configure database**
   - Update `src/main/resources/application.properties`
   - Set database URL, username, and password

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   The API will be available at `http://localhost:8080`

### Android App Setup

1. **Open in Android Studio**
   ```bash
   cd MyApplication
   # Open MyApplication folder in Android Studio
   ```

2. **Configure API endpoint**
   - Update the base URL in API interfaces to match your backend server
   - Default: `http://10.0.2.2:8080` (for Android emulator)

3. **Build and run**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio

## 📊 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Admin Endpoints
- `GET /api/admin/dashboard` - Admin dashboard data
- `GET /api/admin/users` - Get all users
- `POST /api/admin/users` - Create new user
- `GET /api/admin/projects` - Get all projects
- `POST /api/admin/projects` - Create new project
- `GET /api/admin/allocations` - Get all allocations
- `POST /api/admin/allocations` - Create new allocation

### Staff Endpoints
- `GET /api/staff/dashboard` - Staff dashboard data
- `GET /api/staff/projects` - Get assigned projects
- `POST /api/staff/projects` - Create new project
- `GET /api/staff/beneficiaries` - Get project beneficiaries
- `POST /api/staff/beneficiaries` - Add beneficiary

### Donor Endpoints
- `GET /api/donor/dashboard` - Donor dashboard data
- `GET /api/donor/projects` - Get available projects
- `POST /api/donor/donations` - Make donation
- `GET /api/donor/donations` - Get donation history

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-based Authorization**: API endpoints protected by user roles
- **Input Validation**: Server-side validation for all inputs
- **CORS Configuration**: Proper cross-origin resource sharing setup

## 📈 Database Schema

### Core Entities
- **User**: Admin, Staff, and Donor accounts
- **Project**: Charity projects with targets and assignments
- **Donation**: Financial contributions from donors
- **Beneficiary**: People receiving help from projects
- **Allocation**: Distribution of donations to beneficiaries

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file: `./mvnw clean package`
2. Deploy to your preferred cloud platform (AWS, Google Cloud, Azure)
3. Configure environment variables for database connection

### Android App Deployment
1. Generate signed APK in Android Studio
2. Upload to Google Play Store or distribute directly
3. Update API endpoints for production server

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -am 'Add feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation in the project files

## 🔄 Version History

- **v1.0.0**: Initial release with core features
- Multi-role authentication system
- Complete donation management workflow
- Mobile app with role-specific interfaces

---

**Built with ❤️ for charity organizations worldwide** 