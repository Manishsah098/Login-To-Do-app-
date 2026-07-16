# Secure To-Do List App with User Authentication

A native Android application that features a secure, local user login and registration system, alongside a personalized To-Do list manager. This application ensures data privacy by isolating tasks per user and securely hashing credentials.

---

## 🌟 Key Features

### 🔐 User Authentication
- **Secure Registration:** Users can sign up with their name, email, and password.
- **Login Validation:** Authenticates user credentials with input error feedback.
- **Secure Password Storage:** Automatically hashes passwords locally using **SHA-256** encryption before database insertion.
- **Input Verification:** Prevents invalid emails or empty fields.

### 📝 Task Management (CRUD)
- **Personalized Dashboard:** Displays tasks specific to the logged-in user.
- **Task Creation:** Custom dialog to quickly add a task with a title and descriptive notes.
- **Interactive Checklists:** Easily mark tasks as complete/incomplete with dynamic visual updates.
- **Task Deletion:** Swipe/click to remove outdated tasks.
- **Relational Integrity:** Utilizes cascading deletes (deleting a user removes all their associated tasks).

### 🎨 Modern UI/UX Design
- **Material Components:** Employs CardViews, RecyclerView, and TextInputLayouts.
- **Feedback & Messages:** Implements user feedback via Toasts and validation hints.
- **Clean Structure:** Uses custom Dialogs for task entry instead of separate screens to keep interactions fluid.

---

## 🛠️ Technology Stack & Libraries

- **Language:** Java (JDK 8)
- **Minimum Android SDK:** 24 (Android 7.0 Nougat)
- **Target Android SDK:** 34 (Android 14)
- **Database:** Local SQLite database accessed via `SQLiteOpenHelper`
- **UI Framework:** XML (Material Design 3 elements)
- **Build System:** Gradle (Kotlin DSL / Groovy)

---

## 📂 Project Structure

```text
To Do login App/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/oasis/todoapp/
│   │   │   │   ├── adapter/
│   │   │   │   │   └── TaskAdapter.java       # RecyclerView Adapter for Task list
│   │   │   │   ├── database/
│   │   │   │   │   └── DatabaseHelper.java    # SQLite CRUD operations & SHA-256 Hashing
│   │   │   │   ├── model/
│   │   │   │   │   ├── Task.java              # Task Object Model
│   │   │   │   │   └── User.java              # User Object Model
│   │   │   │   │
│   │   │   │   ├── LoginActivity.java         # User Authentication screen
│   │   │   │   ├── RegisterActivity.java      # User Signup screen
│   │   │   │   └── MainActivity.java          # To-Do Dashboard (Home Screen)
│   │   │   │
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       │   ├── activity_login.xml     # Login Screen UI
│   │   │       │   ├── activity_register.xml  # Registration Screen UI
│   │   │       │   ├── activity_main.xml      # Dashboard RecyclerView UI
│   │   │       │   ├── dialog_add_task.xml    # Task Entry Popup UI
│   │   │       │   └── item_task.xml          # Single Task Layout template
│   │   │       └── ...
│   │   └── ...
│   └── build.gradle                           # App module configuration
│
├── .gitignore                                 # Git exclusions
├── build.gradle                               # Project level build configuration
├── settings.gradle                            # Project repository configuration
└── README.md                                  # Documentation
```

---

## ⚙️ Installation & Setup

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/todo-login-app.git
   cd "todo-login-app"
   ```

2. **Open in Android Studio:**
   - Launch Android Studio.
   - Choose **File > Open...** and select the project root directory.
   - Let Gradle sync completely.

3. **Run the App:**
   - Connect an Android device (via USB Debugging) or start an Emulator.
   - Click the **Run** button (green play icon) or press `Shift + F10`.

---

## 🔒 Security Implementation Detail
Passwords are never stored in plain text. When a user registers, their password is processed using:
```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hash = digest.digest(password.getBytes());
```
The resulting hex string is saved. During login, the password input is hashed using the same function and compared against the stored hash, protecting against raw credential exposure.
