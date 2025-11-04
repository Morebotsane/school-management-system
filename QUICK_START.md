# Quick Start Guide 🚀

Get the School Management System running on your machine in **15 minutes**!

## Prerequisites Checklist

- [ ] Java 17 or higher installed (`java -version`)
- [ ] Maven 3.8+ installed (`mvn -version`)
- [ ] PostgreSQL 14+ installed and running
- [ ] Node.js 18+ installed (`node -version`)
- [ ] Git installed
- [ ] A code editor (VS Code, IntelliJ IDEA, etc.)

---

## Step 1: Database Setup (5 minutes)

### 1.1 Start PostgreSQL

**Windows:**
```cmd
pg_ctl -D "C:\Program Files\PostgreSQL\14\data" start
```

**macOS/Linux:**
```bash
sudo service postgresql start
# or
brew services start postgresql
```

### 1.2 Create Database

```bash
# Connect to PostgreSQL
psql -U postgres

# In psql prompt:
CREATE DATABASE school_management_db;
\q
```

### 1.3 Run Schema

```bash
cd school-management-system/backend
psql -U postgres -d school_management_db -f src/main/resources/schema.sql
```

**Verify:**
```bash
psql -U postgres -d school_management_db
\dt  # Should show all tables
\q
```

---

## Step 2: Backend Setup (5 minutes)

### 2.1 Configure Database Connection

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=postgres
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

### 2.2 Build & Run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**Success indicators:**
- No errors in console
- See: "Started SchoolManagementApplication in X seconds"
- Backend running on: http://localhost:8080

**Test the API:**
```bash
curl http://localhost:8080/api-docs
# Or visit in browser: http://localhost:8080/swagger-ui.html
```

### 2.3 Test Default Login

**Admin credentials** (created by schema.sql):
- Username: `admin`
- Password: `Admin@123`

Test login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'
```

You should receive a JWT token!

---

## Step 3: Frontend Setup (5 minutes)

### 3.1 Initialize React Project

```bash
cd ../frontend

# If frontend folder doesn't exist yet, create it:
npx create-react-app frontend
cd frontend
```

### 3.2 Install Dependencies

```bash
npm install redux @reduxjs/toolkit react-redux react-router-dom axios
npm install chart.js react-chartjs-2
npm install react-hook-form
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p
```

### 3.3 Configure Tailwind

Edit `tailwind.config.js`:
```javascript
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

Edit `src/index.css`:
```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

### 3.4 Start Development Server

```bash
npm start
```

**Success:** Browser opens at http://localhost:3000

---

## Step 4: Verify Everything Works ✅

### Backend Health Check
```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

### Database Connection
```bash
psql -U postgres -d school_management_db -c "SELECT COUNT(*) FROM users;"
# Expected: count = 1 (admin user)
```

### Frontend-Backend Connection

Create `frontend/src/services/api.js`:
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// Test connection
api.get('/health').then(res => console.log('Connected!', res.data));

export default api;
```

---

## Troubleshooting 🔧

### Problem: "Port 8080 already in use"
**Solution:**
```bash
# Find process using port 8080
lsof -i :8080
# Kill it
kill -9 <PID>
```

### Problem: "Could not connect to PostgreSQL"
**Solutions:**
1. Check PostgreSQL is running: `pg_isready`
2. Verify credentials in `application.properties`
3. Check PostgreSQL is listening: `netstat -an | grep 5432`

### Problem: "Schema not found"
**Solution:**
```bash
# Re-run schema
psql -U postgres -d school_management_db -f backend/src/main/resources/schema.sql
```

### Problem: Maven build fails
**Solutions:**
1. Clean Maven cache: `mvn clean`
2. Delete `~/.m2/repository` and rebuild
3. Check Java version: `java -version` (needs 17+)

### Problem: npm install fails
**Solutions:**
1. Clear npm cache: `npm cache clean --force`
2. Delete `node_modules` and `package-lock.json`
3. Run `npm install` again

### Problem: CORS errors in browser
**Solution:**
Check `application.properties`:
```properties
cors.allowed.origins=http://localhost:3000
```

---

## Next Steps 📚

Now that everything is running:

1. **Explore the API**
   - Visit: http://localhost:8080/swagger-ui.html
   - Test endpoints with Swagger UI

2. **Check the Database**
   ```bash
   psql -U postgres -d school_management_db
   \dt  # List tables
   SELECT * FROM users;
   ```

3. **Start Building Features**
   - Follow `ROADMAP.md` for development plan
   - Begin with Phase 1: Authentication

4. **Read the Documentation**
   - `README.md` - Complete project overview
   - `ROADMAP.md` - Development phases
   - Code comments - Implementation details

---

## Development Workflow 🔄

### Daily Development Cycle

1. **Start PostgreSQL**
   ```bash
   sudo service postgresql start
   ```

2. **Run Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Run Frontend** (separate terminal)
   ```bash
   cd frontend
   npm start
   ```

4. **Make Changes**
   - Backend: Auto-reloads with `spring-boot-devtools`
   - Frontend: Hot-reloads automatically

5. **Test Changes**
   - Backend: Use Swagger UI or curl
   - Frontend: See changes in browser immediately

---

## Useful Commands 📋

### Backend
```bash
# Build without running tests
mvn clean install -DskipTests

# Run specific test
mvn test -Dtest=UserServiceTest

# Package for deployment
mvn clean package

# Check dependencies
mvn dependency:tree
```

### Frontend
```bash
# Install new package
npm install <package-name>

# Run tests
npm test

# Build for production
npm run build

# Check for outdated packages
npm outdated
```

### Database
```bash
# Backup database
pg_dump -U postgres school_management_db > backup.sql

# Restore database
psql -U postgres school_management_db < backup.sql

# View database size
psql -U postgres -c "SELECT pg_size_pretty(pg_database_size('school_management_db'));"
```

---

## Africa's Talking Setup (Optional) 📱

To enable SMS features:

1. **Sign up** at [africas talking.com](https://africastalking.com)

2. **Get credentials:**
   - Username (e.g., "sandbox")
   - API Key

3. **Update** `application.properties`:
   ```properties
   africas.talking.username=your-username
   africas.talking.api.key=your-api-key
   ```

4. **Test SMS** (sandbox mode):
   ```bash
   curl -X POST http://localhost:8080/api/notifications/test-sms \
     -H "Authorization: Bearer YOUR_JWT" \
     -H "Content-Type: application/json" \
     -d '{"phoneNumber":"+254700000000","message":"Test"}'
   ```

---

## Environment Variables (Recommended) 🔐

Instead of hardcoding sensitive data, use environment variables:

### Backend (.env or system environment)
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key
export AFRICAS_TALKING_API_KEY=your_key
```

### Update application.properties
```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
africas.talking.api.key=${AFRICAS_TALKING_API_KEY}
```

---

## Need Help? 💬

1. Check `ROADMAP.md` for feature implementation details
2. Review `README.md` for architecture overview
3. Check code comments in source files
4. Explore Swagger documentation at `/swagger-ui.html`

---

**You're all set! Happy coding!** 🎉

**Next:** Start building authentication in Phase 1 of the ROADMAP.
