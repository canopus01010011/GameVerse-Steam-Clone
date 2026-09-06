# GameVerse — Desktop Game Store (JavaFX + MySQL)

GameVerse is a desktop game storefront built with JavaFX, loosely inspired by Steam. It started as a way for me to properly learn JavaFX and JDBC beyond toy examples — a full flow with account creation, authentication, a browsable store, a purchase system, and a profile/library screen backed by a real MySQL database.

It's not trying to reinvent Steam. It's a focused, self-contained CRUD desktop app: users sign up, log in, browse and search a game catalog, buy games, and manage their account — all wired to a relational schema with proper foreign keys.

![Java](https://img.shields.io/badge/Java-23-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1)
![License](https://img.shields.io/badge/license-MIT-green)

---

## Features

- **Account system** — sign up with validation (name format, email format, Algerian phone number pattern, minimum password length, 18+ age check via birth date), login, and account deletion with confirmation.
- **Store** — browse the full game catalog as cards (cover image, title, description, price), filter by category, and search by title.
- **Purchases** — one-click buy, with a duplicate-purchase guard so you can't buy the same game twice.
- **Purchase history / library** — a dedicated table view listing everything a user owns, with price and purchase date.
- **Profile management** — view and edit your personal info (name, email, phone, password), see when the account was created, and navigate between Store, Profile, and Purchase History from a consistent shell.
- **Game details view** — a dedicated screen per game with system requirements, rating, release date, and full description.
- **Themed UI** — a custom dark "Steam-like" stylesheet (`application.css`) applied consistently across every scene.

## Tech stack

| Layer | Technology |
|---|---|
| UI | JavaFX 21 (FXML + Scene Builder) |
| Language | Java 23 |
| Data access | JDBC via `mysql-connector-j` |
| Database | MySQL 8.0 |
| Build/IDE | Eclipse with e(fx)clipse |

## Project structure

```
├── src/application/          # Controllers, models, entry point
│   ├── Main.java              # App bootstrap, loads f1.fxml
│   ├── Scenecontroller.java   # Login screen logic
│   ├── SignupController.java  # Registration + validation
│   ├── ProfileController.java # Profile view, edit, delete, logout
│   ├── EditSceneController.java
│   ├── StoreController.java   # Catalog, search, filter, buy
│   ├── DetailsController.java # Single game details screen
│   ├── PurchaseHistoryController.java
│   ├── DBConnection.java      # Centralized JDBC connection helper
│   ├── Game.java / Purchase.java  # Simple data models
├── src/*.fxml                 # Scene layouts (Scene Builder–generated)
├── src/application/application.css  # Shared dark theme
├── DataBase/                  # MySQL dump files (schema + seed data)
│   ├── games_users.sql
│   ├── games_games.sql
│   ├── games_purchases.sql
│   └── games_routines.sql
└── bin/                        # Compiled build output (mirrors src/)
```

## Database schema

Three core tables, related by foreign keys:

- **`users`** — `UserID`, `Username`, `Firstname`, `Lastname`, `Email`, `PasswordHash`, `CreatedAt`, `age`, `phone_number`
- **`games`** — `GameID`, `Title`, `Description`, `Price`, `Category`, `ReleaseDate`, `Rating`, `SystemRequirements`, `ImagePath`
- **`purchases`** — `PurchaseID`, `UserID` (FK → users), `GameID` (FK → games), `PurchaseDate`

Import order matters if you're loading the dumps manually: `games_users.sql` and `games_games.sql` first, then `games_purchases.sql` (since it references both).

## Getting started

### Prerequisites

- JDK 23
- JavaFX SDK 21 ([openjfx.io](https://openjfx.io))
- MySQL Server 8.0+
- MySQL Connector/J (JDBC driver)
- Eclipse with the e(fx)clipse plugin (or any IDE that can run JavaFX modules)

### 1. Set up the database

```sql
CREATE DATABASE games;
```

Then import the dumps in `DataBase/` in this order:

```bash
mysql -u <user> -p games < DataBase/games_users.sql
mysql -u <user> -p games < DataBase/games_games.sql
mysql -u <user> -p games < DataBase/games_purchases.sql
```

### 2. Configure your connection

Connection settings currently live in `application/DBConnection.java`. **Before running the project, update the URL, username, and password to match your local MySQL instance** — do not commit real credentials (see [Security notes](#security-notes) below).

```java
private static final String URL = "jdbc:mysql://localhost:3306/games?serverTimezone=UTC";
private static final String USER = "your_user";
private static final String PASSWORD = "your_password";
```

### 3. Run it

Open the project in Eclipse, make sure the JavaFX SDK and MySQL Connector/J are on the module path (see `.classpath`), and run `Main.java`.

## Security notes

This project was built as a learning exercise, and a few things should be fixed **before** treating it as production-ready or exposing this repo publicly with real data:

- Database credentials are currently hardcoded across several controller classes instead of being centralized and externalized (e.g. via environment variables or a `.gitignore`'d `config.properties`).
- Passwords are stored and compared as plain text rather than hashed (a `PasswordHash` column exists, but no hashing algorithm like BCrypt is applied yet).
- The seed data in `DataBase/games_users.sql` includes a realistic-looking name/email/phone — swap this for clearly fake placeholder data before publishing.

These are on the roadmap below.

## Roadmap

- [ ] Centralize DB config and remove hardcoded credentials from every controller
- [ ] Hash passwords with BCrypt before storing/comparing
- [ ] Add a proper `.gitignore` for `bin/` and local config
- [ ] Wishlist / cart support
- [ ] Pagination for large game catalogs
- [ ] Unit tests around validation logic in `SignupController`

## License

MIT — see `LICENSE`.

## Author

Built and maintained by me as a personal JavaFX/JDBC project. Issues and pull requests are welcome.
