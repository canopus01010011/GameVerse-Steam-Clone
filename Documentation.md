# GameVerse — Technical Documentation

This document goes one level deeper than the README: how the screens connect, how data flows between the UI and MySQL, and what each controller is actually responsible for. It's meant for anyone (including future me) picking this project back up after a while away from it.

## 1. Architecture overview

GameVerse follows the standard JavaFX MVC-ish pattern that FXML encourages:

- **View** — `.fxml` files under `src/`, laid out visually in Scene Builder.
- **Controller** — one Java class per FXML file, annotated with `@FXML`, wired via `fx:controller` in the FXML root element.
- **Model** — small plain classes (`Game`, `Purchase`) that hold data pulled from the database, used mostly to populate `TableView`/`ListView` cells.
- **Data access** — every controller currently opens its own JDBC connection inline via `DriverManager.getConnection(...)`, rather than going through a shared connection pool. `DBConnection.java` exists as a first step toward centralizing this but isn't yet used consistently across all controllers.

There's no separate service/repository layer — SQL queries live directly inside the controllers. That's a deliberate simplification for a project this size, but it's the first thing to refactor if the app grows (see [Suggested refactors](#6-suggested-refactors)).

## 2. Screen-by-screen flow

```
f1.fxml (Login)
  ├─→ signup.fxml (Sign Up) ──────────────┐
  └─→ ProfilScene.fxml (Profile) ←────────┘  (on successful signup/login)
        ├─→ Editscene.fxml (Edit Profile) → back to ProfilScene
        ├─→ purchase_history.fxml (Library) → back to ProfilScene
        └─→ StoreScene.fxml (Store)
              ├─→ DetailsScene.fxml (Game Details) → back to StoreScene
              └─→ purchase_history.fxml (Library) → back to StoreScene
```

Every scene transition follows the same pattern: load the target FXML with `FXMLLoader`, grab its controller, pass along `currentUserId` (session state is just this one int, carried scene-to-scene — there's no separate session object), swap the `Stage`'s `Scene`, and reapply `application.css`.

### Login — `f1.fxml` / `Scenecontroller.java`
Takes username + password, runs `authenticateUser()` against the `users` table, and on success loads the Profile screen with the matched `UserID`. On failure, shows an error `Alert`.

### Sign Up — `signup.fxml` / `SignupController.java`
Validates every field before hitting the database:
- First/last name: letters only (regex)
- Email: standard email pattern
- Phone: must start with `05`, `06`, or `07` and be 10 digits (Algerian mobile number format)
- Password: minimum 8 characters
- Age: must be 18+, computed from the `DatePicker` value via `java.time.Period`
- Terms checkbox must be checked

On success, inserts the new row, retrieves the generated `UserID`, and routes straight into the Profile screen — no separate "verify your email" step, this is a local single-machine app.

### Profile — `ProfilScene.fxml` / `ProfileController.java`
Displays name, email, phone, birthdate, and account creation date, pulled with a single `SELECT` on `UserID`. Also hosts navigation to Edit, Purchase History, Store (via the house icon), Logout, and Delete Account (with a confirmation `Alert` before the `DELETE` runs).

### Edit Profile — `Editscene.fxml` / `EditSceneController.java`
Pre-fills fields from the current user's row, then runs an `UPDATE` against all editable fields on submit. Note: this currently updates the password field as plain text with no re-hashing step — see [Security notes](README.md#security-notes) in the README.

### Store — `StoreScene.fxml` / `StoreController.java`
Loads every row from `games` into `VBox` "cards" inside a `ListView`, each with an image, title, description, price, a **Buy** button, and a **Details** button. Category filtering and title search both re-run a parameterized `SELECT ... WHERE LOWER(Title) LIKE ?` query rather than filtering client-side, so results stay accurate against the live catalog.

### Game Details — `DetailsScene.fxml` / `DetailsController.java`
A read-only detail view for a single `Game` object already loaded in memory (no extra query needed) — release date, price, category, rating, system requirements, and cover image.

### Purchase flow (inside `StoreController.buyGame()`)
1. Check `purchases` for an existing `(UserID, GameID)` pair — block duplicate purchases.
2. If clear, `INSERT INTO purchases (UserID, GameID, PurchaseDate) VALUES (?, ?, NOW())`.
3. Show a success or failure `Alert`.

### Purchase History / Library — `purchase_history.fxml` / `PurchaseHistoryController.java`
A `TableView<Purchase>` populated by joining `purchases` and `games` on `GameID`, filtered to the current `UserID`. Columns: title, price, purchase date.

## 3. Database schema reference

```sql
users (
  UserID INT PK AUTO_INCREMENT,
  Username VARCHAR(255) UNIQUE,
  Firstname VARCHAR(255) UNIQUE,
  Lastname VARCHAR(255) UNIQUE,
  Email VARCHAR(255) UNIQUE,
  PasswordHash VARCHAR(255),
  CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  age INT,
  phone_number INT
)

games (
  GameID INT PK AUTO_INCREMENT,
  Title VARCHAR(255),
  Description TEXT,
  Price DECIMAL(10,2),
  Category VARCHAR(100),
  ReleaseDate DATE,
  Rating DECIMAL(3,2),
  SystemRequirements TEXT,
  ImagePath VARCHAR(500)
)

purchases (
  PurchaseID INT PK AUTO_INCREMENT,
  UserID INT FK → users.UserID,
  GameID INT FK → games.GameID,
  PurchaseDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

A couple of things worth knowing if you extend the schema:
- `Firstname` and `Lastname` are currently `UNIQUE` — that means two users literally cannot share a first or last name. This is almost certainly not intended long-term and is worth dropping in favor of just keeping `Username`/`Email` unique.
- `phone_number` is stored as `INT`, which silently drops any leading zero and can't hold non-numeric formatting. `VARCHAR` is the safer type here.

## 4. Styling

`application.css` (identical copies live under `src/application/` and `bin/application/` — the `bin/` one is just the compiled/copied output) defines the whole visual identity: a dark navy background (`#1b2838`), Steam-blue accents (`#66c0f4`), styled buttons/inputs/date-pickers/tables with hover and pressed states, and a dedicated red palette for the destructive "Delete Account" button (`#Deleteb`). Every scene attaches this stylesheet after loading, so keep new scenes consistent by doing the same:

```java
String css = this.getClass().getResource("application.css").toExternalForm();
scene.getStylesheets().add(css);
```

## 5. Known limitations

- No session/auth token — `currentUserId` is just passed around as a plain int between controllers. Fine for a single local desktop app; not something to carry into a networked version.
- Passwords are neither hashed nor salted.
- Every controller opens and closes its own JDBC connection rather than sharing a pool — fine at this scale, but won't hold up under concurrent access.
- `bin/` (compiled output) is currently tracked alongside `src/` — normally this would be excluded via `.gitignore`.
- Two duplicate DB-connection helper snippets exist (`DBConnection.java` and the ad hoc `test.java`) — only one should remain long-term.

## 6. Suggested refactors

If this project keeps growing, the next real milestones are:

1. **Extract a DAO/repository layer** (`UserRepository`, `GameRepository`, `PurchaseRepository`) so controllers stop talking to SQL directly.
2. **Centralize configuration** — pull host/user/password out of source into an external, git-ignored properties file, loaded once by `DBConnection`.
3. **Hash passwords** with BCrypt (or Argon2) at signup and verify with a hash comparison at login, instead of storing/matching plain text.
4. **Connection pooling** (e.g. HikariCP) instead of a fresh `DriverManager.getConnection()` per call.
5. **Clean up unique constraints** on `Firstname`/`Lastname`.
