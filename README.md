# 🎵 Song & Playlist Management System

A microservices-based Song and Playlist Management System built with Spring Boot, MongoDB, and JWT Authentication.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0-green)
![License](https://img.shields.io/badge/License-MIT-blue)

---

## 🏗️ Architecture

```
Console Client
     │
     ├──► User Service (8081)      - Authentication & JWT
     ├──► Song Service (8082)      - Song Management
     └──► Playlist Service (8083)  - Playlist Management
                │
                └──► MongoDB Database
```

---

## ✨ Features

### 🔐 User Service
- User Registration with BCrypt encryption
- JWT-based Authentication
- Token validation

### 🎵 Song Service
- CRUD operations for songs
- Search by title, artist, genre
- User-specific song management

### 📝 Playlist Service
- CRUD operations for playlists
- Add/Remove songs to playlists
- Inter-service communication with Song Service
- View playlists with full song details

### 🖥️ Console Application
- Interactive menu-driven interface
- Session management with JWT
- User-friendly CRUD operations

---

## 🛠️ Tech Stack

- **Backend:** Spring Boot 3.2.0, Spring Security, Spring Data MongoDB
- **Database:** MongoDB
- **Authentication:** JWT (JSON Web Tokens)
- **Password Encryption:** BCrypt
- **Build Tool:** Maven
- **Java Version:** 17

---

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB (running on localhost:27017)

---

## 🚀 Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/song-playlist-system.git
cd song-playlist-system
```

### 2. Start MongoDB
```bash
# Using Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Or start MongoDB service
mongosh  # Verify it's running
```

### 3. Build the project
```bash
mvn clean install
```

### 4. Run the services (in separate terminals)

**Terminal 1 - User Service:**
```bash
cd user-service
mvn spring-boot:run
```

**Terminal 2 - Song Service:**
```bash
cd song-service
mvn spring-boot:run
```

**Terminal 3 - Playlist Service:**
```bash
cd playlist-service
mvn spring-boot:run
```

**Terminal 4 - Console App:**
```bash
cd console-app
mvn spring-boot:run
```

---

## 📡 API Endpoints

### User Service (Port 8081)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Login & get JWT token |
| GET | `/api/users/validate` | Validate JWT token |

### Song Service (Port 8082)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/songs` | Add new song |
| GET | `/api/songs` | Get all songs |
| GET | `/api/songs/{id}` | Get song by ID |
| GET | `/api/songs/search?title=` | Search songs |
| PUT | `/api/songs/{id}` | Update song |
| DELETE | `/api/songs/{id}` | Delete song |

### Playlist Service (Port 8083)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/playlists` | Create playlist |
| GET | `/api/playlists` | Get all playlists |
| GET | `/api/playlists/{id}/with-songs` | Get playlist with songs |
| POST | `/api/playlists/{id}/songs/{songId}` | Add song to playlist |
| DELETE | `/api/playlists/{id}/songs/{songId}` | Remove song |
| DELETE | `/api/playlists/{id}` | Delete playlist |

---

## 🎮 Usage Example

### 1. Register a User
```bash
POST http://localhost:8081/api/users/register
Content-Type: application/json

{
  "name": "John Doe",
  "username": "john",
  "password": "password123",
  "email": "john@example.com"
}
```

### 2. Add a Song
```bash
POST http://localhost:8082/api/songs
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "title": "Bohemian Rhapsody",
  "artist": "Queen",
  "album": "A Night at the Opera",
  "genre": "Rock",
  "duration": 354
}
```

### 3. Create Playlist and Add Songs
```bash
POST http://localhost:8083/api/playlists
Authorization: Bearer <JWT_TOKEN>

{
  "name": "My Favorites",
  "description": "Best songs collection"
}
```

---

## 📁 Project Structure

```
song-playlist-system/
├── user-service/          # Authentication service
├── song-service/          # Song management service
├── playlist-service/      # Playlist management service
├── console-app/           # Console UI client
└── pom.xml               # Parent POM
```

---

## 🔑 Key Features Demonstrated

✅ **Microservices Architecture** - Independent, loosely-coupled services  
✅ **Inter-Service Communication** - REST-based service-to-service calls  
✅ **JWT Authentication** - Stateless token-based security  
✅ **MongoDB Integration** - NoSQL database with Spring Data  
✅ **RESTful APIs** - Standard HTTP methods and status codes  
✅ **Security** - BCrypt password encryption + JWT tokens  
✅ **Clean Architecture** - Separation of concerns with DTOs, Services, Repositories

---

## 🐛 Troubleshooting

### MongoDB Connection Issues
```bash
# Check if MongoDB is running
mongosh

# Start MongoDB service
sudo systemctl start mongod  # Linux
net start MongoDB            # Windows
```

### Port Already in Use
```bash
# Change port in application.yml
server:
  port: 8091  # Use different port
```

### JWT Token Expired
Simply login again to get a new token. Token validity is 24 hours by default.

---

## 📝 Configuration

Edit `application.yml` in each service to customize:

```yaml
server:
  port: 8081  # Change port

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/songplaylist

jwt:
  secret: yourSecretKey
  expiration: 86400000  # 24 hours
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

## 📄 License

This project is licensed under the MIT License.

---

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- MongoDB for the flexible NoSQL database
- JWT.io for JWT implementation guidance

---

## ⭐ Show Your Support

Give a ⭐️ if this project helped you!

---

**Built with ❤️ using Spring Boot Microservices**