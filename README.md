<div align="center">

# 🎬 CineReserve

### *Next-Generation Movie Ticket Booking & Dynamic Seat Auction System*

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Bootstrap 5](https://img.shields.io/badge/Bootstrap-5.3.3-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![Database](https://img.shields.io/badge/Database-H2%20%7C%20MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

<p align="center">
  A full-stack, enterprise-grade movie ticketing platform built with <b>Java 17</b>, <b>Spring Boot 3</b>, <b>Direct JDBC (<code>JdbcTemplate</code>)</b>, and <b>Bootstrap 5</b>. CineReserve reimagines traditional ticketing with real-time seat concurrency locking, dynamic live ticket auctions, GPS-based proximity cinema discovery, closed-loop loyalty cashback, and a credential-secured Admin Control Center.
</p>

[Quick Start](#-quick-start) • [Key Features](#-key-features) • [System Architecture](#-system-architecture) • [Demo Credentials](#-demo-credentials) • [Team Roles](#-team-engineering-roles) • [API & Routes](#-endpoints--routes)

---

</div>

## 🌟 Key Features

### 1. 💺 Interactive Seat Matrix with Concurrency Locking
- Visual auditorium layout with color-coded viewing tiers:
  - **Standard (₹200)**: Front rows with clear screen sightlines.
  - **Premium (₹350)**: Middle sweet-spot rows with optimal acoustics.
  - **VIP Recliner (₹500)**: Ultra-comfortable luxury seating.
- **Race Condition Prevention**: Prevents double-booking via atomic SQL conditional updates:
  ```sql
  UPDATE seats SET status = 'LOCKED' WHERE seat_id = ? AND status = 'AVAILABLE';
  ```
  If another user selects the seat at the exact same millisecond, the database returns `0` affected rows, instantly rejecting the duplicate reservation.

### 2. 🔨 Live Ticket Auction & Resale Arena
- High-demand VIP Recliner seats are flagged as **Auction Eligible** before showtime.
- **Real-Time Bidding**: Dynamic JavaScript countdown timer and 3-second polling to `/api/auction/{id}/status`.
- **Automated Resolution Daemon**: Non-blocking Spring `@Scheduled(fixedDelay = 10000)` background scheduler detects expired auctions, binds the winning bidder, and reserves the seat automatically.

### 3. 📍 GPS Proximity Cinema Recommender
- Uses the **Great-Circle Haversine Formula** to compute exact kilometer distances between the user's browser GPS coordinates and cinema halls:
  $$d = 2R \cdot \arcsin\left(\sqrt{\sin^2\left(\frac{\Delta \phi}{2}\right) + \cos(\phi_1)\cos(\phi_2)\sin^2\left(\frac{\Delta \lambda}{2}\right)}\right)$$
- Displays live proximity badges (e.g., `1.8 km away`) and orders cinemas from nearest to farthest.

### 4. 💰 10% Closed-Loop Loyalty Wallet
- **Automatic Cashback**: Every confirmed booking rewards **10% cashback** into the user's loyalty ledger ($1\text{ pt} = \text{₹}1.00$).
- **One-Click Redemption**: Redeem points at checkout for instant cash discounts.
- **Audit Ledger**: All transactions are immutably logged into `credit_transactions` with running balance snapshots.

### 5. 🛡️ Credential-Secured Admin Control Center
- Hidden from public visitors (no exposed admin buttons on navigation bar).
- **Automatic Role Detection**: Logging in with administrator credentials (`admin@cinereserve.com`) grants admin privileges and redirects directly to `/admin`.
- **Administrative Operations**:
  - Insert new movies with IMDb ratings, genres, and poster URLs.
  - Register new cinema halls with GPS coordinates.
  - Schedule new showtimes with **automated 30-seat grid generation**.
  - Delete expired movies, cinemas, and shows.

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│               Presentation Layer (Bootstrap 5.3)             │
│        Thymeleaf Server Templates + JavaScript Timers/APIs   │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP / REST
┌──────────────────────────────▼──────────────────────────────┐
│                  Spring MVC Controller Layer                 │
│  HomeController • MovieController • SeatController          │
│  BookingController • AuctionController • AdminController    │
└──────────────────────────────┬──────────────────────────────┘
                               │ Method Calls
┌──────────────────────────────▼──────────────────────────────┐
│                    Business Services Layer                  │
│  BookingService • AuctionService • CreditService            │
│  RecommendationService (Haversine) • AuctionScheduler      │
└──────────────────────────────┬──────────────────────────────┘
                               │ Direct SQL
┌──────────────────────────────▼──────────────────────────────┐
│              Spring JdbcTemplate (DAO Repositories)          │
│  UserRepository • MovieRepository • TheatreRepository       │
│  ShowRepository • SeatRepository • BookingRepository        │
└──────────────────────────────┬──────────────────────────────┘
                               │ SQL Queries
┌──────────────────────────────▼──────────────────────────────┐
│                     Relational Database                     │
│    H2 In-Memory (MySQL Dialect Mode) / Standalone MySQL 8.0 │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

| Layer | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java (OpenJDK) | 17 LTS / 21 | Modern switch expressions, record types, strong memory management |
| **Framework** | Spring Boot | 3.4.3 | Dependency injection, embedded Tomcat, `@Scheduled` background tasks |
| **Persistence** | Spring `JdbcTemplate` | 3.4.3 | High-throughput direct SQL DAO layer avoiding heavy ORM overhead |
| **Database** | H2 Database / MySQL | Embedded / 8.0 | Zero-setup MySQL-compatible in-memory database with persistent fallback |
| **View Engine** | Thymeleaf | 3.1 | Clean server-side HTML template rendering |
| **Styling** | Bootstrap | 5.3.3 | Mobile-responsive dark cinema theme |
| **Icons** | Bootstrap Icons | 1.11.1 | Modern vector icon set |
| **Build Tool** | Apache Maven | 3.9+ | Dependency resolution and automated compilation |

---

## 🚀 Quick Start

### Prerequisites
- **Java Development Kit (JDK) 17** or higher installed (`java -version`).
- Git installed.

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/movie-ticket-booking.git
cd movie-ticket-booking
```

### 2. Run the Application
The project includes a pre-configured embedded H2 database running in MySQL-compatible mode. **No MySQL setup or external installations are required!**

#### On Windows (Command Prompt / PowerShell):
```powershell
.\mvnw.cmd spring-boot:run
```

#### On Linux / macOS:
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### 3. Open in Browser
Visit **`http://localhost:8080`** in your browser.

*(Optional)* Access the H2 In-Memory Web Console:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:movie_booking_db`
- **Username**: `sa`
- **Password**: *(leave blank)*

---

## 🔑 Demo Credentials

| Role | Email | Password | City / Note | Default Wallet |
| :--- | :--- | :--- | :--- | :--- |
| **Customer** | `alice@example.com` | `password123` | Mumbai | 50 Loyalty Pts |
| **Customer** | `bob@example.com` | `password123` | Delhi | 0 Loyalty Pts |
| **Administrator** | `admin@cinereserve.com` | `admin123` | System Administrator | Full Admin Access |

> **Note**: New accounts created via `/register` automatically receive a **50-credit welcome bonus**.

---

## 👥 Team Engineering Roles

This project was developed by a 4-member engineering team. Workload and responsibilities are balanced across distinct architectural tiers:

| Member | Architectural Role | Assigned Files | Responsibilities |
| :--- | :--- | :--- | :--- |
| **Member 1** | **Database & Project Setup** | `schema.sql`, `data.sql`, `application.properties`, `pom.xml`, `User.java`, `Movie.java`, `Theatre.java`, `Show.java` | 10-table normalized relational schema, primary/foreign key cascading, seed catalog data, Maven scaffolding, core domain models. |
| **Member 2** | **Models & Repositories** | `Seat.java`, `Booking.java`, `Auction.java`, `Credit.java`, all 7 `*Repository.java` files | Domain models, RowMappers, direct `JdbcTemplate` DAO layer, CRUD query methods. |
| **Member 3** | **Business Logic & Rules** | `BookingService.java`, `CreditService.java`, `RecommendationService.java`, `AuctionService.java`, `AuctionScheduler.java` | Atomic seat locking concurrency, 10% loyalty cashback math, Haversine distance formula, background auction resolution daemon. |
| **Member 4** | **UI, Admin & Connectors** | `AdminController.java`, `admin.html`, all `*Controller.java` files, all 10 Thymeleaf HTML templates, `style.css`, `main.js` | Responsive dark UI, real-time JavaScript total calculator, auction countdown timer, REST endpoints, credential-secured Admin Portal. |

*Detailed file breakdown available in [`TEAM_ROLES_AND_FILES.txt`](TEAM_ROLES_AND_FILES.txt).*

---

## 🌐 Endpoints & Routes

| HTTP Method | Route | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Home catalog, featured movie carousels, genre filter | Public |
| `GET` | `/movie/{id}` | Movie details and showtime schedules grouped by cinema | Public |
| `GET` | `/seats/{showId}` | Interactive color-coded seat matrix | Public |
| `POST` | `/api/seats/lock` | Atomic seat locking API | Public |
| `GET` | `/auctions` | Live ticket auction arena | Public |
| `GET` | `/auction/{id}` | Live bidding room with countdown timer | Public |
| `POST` | `/auction/{id}/bid` | Submit competitive bid | Logged-in |
| `GET` | `/checkout` | Checkout review and loyalty points discount redemption | Logged-in |
| `POST` | `/booking/confirm` | Confirm booking, award 10% cashback, issue e-ticket | Logged-in |
| `GET` | `/profile` | User wallet balance and previous booking history | Logged-in |
| `GET` | `/admin` | Administrative dashboard for movies, cinemas & shows | **Admin Only** |
| `POST` | `/admin/movies/add` | Insert new movie into catalog | **Admin Only** |
| `POST` | `/admin/theatres/add`| Register new cinema with GPS coordinates | **Admin Only** |
| `POST` | `/admin/shows/add` | Schedule show with automatic 30-seat grid creation | **Admin Only** |

---

## 📁 Project Directory Structure

```
movie-ticket-booking/
├── pom.xml                                      # Maven build configuration
├── TEAM_ROLES_AND_FILES.txt                     # Team workload & file division document
├── LICENSE                                      # MIT License
├── README.md                                    # Project documentation
├── sql/
│   ├── schema.sql                               # 10-table normalized DDL schema
│   └── data.sql                                 # Seed catalog (movies, cinemas, users, shows)
└── src/main/
    ├── java/com/moviebooking/
    │   ├── MovieBookingApplication.java         # Spring Boot entry point & @EnableScheduling
    │   ├── config/                              # Database configuration
    │   ├── controller/                          # Spring MVC Web Controllers (including Admin)
    │   ├── model/                               # Object-Oriented Domain Entities & Enums
    │   ├── repository/                          # Direct JDBC DAOs & RowMappers
    │   ├── scheduler/                           # Auction background resolution daemon
    │   └── service/                             # Business logic (Booking, Credit, Haversine, Auction)
    └── resources/
        ├── application.properties               # Database & server port configuration
        ├── schema.sql & data.sql                # Startup auto-load SQL scripts
        ├── static/
        │   ├── css/style.css                    # Dark cinema styling & seat grid visuals
        │   └── js/main.js                       # Real-time timers & price calculators
        └── templates/                           # Thymeleaf server-side templates
            ├── admin.html                       # Credential-secured Admin Control Center
            ├── auction.html & auctions.html     # Live bidding rooms
            ├── checkout.html & confirmation.html# Payment & e-ticket confirmation
            ├── home.html & shows.html           # Catalog & showtimes
            ├── layout.html                      # Navbar fragment with role-based links
            ├── login.html & register.html       # Authentication views
            ├── profile.html                     # Wallet & booking history
            └── seats.html                       # Interactive seat matrix
```

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
