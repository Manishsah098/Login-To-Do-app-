package com.oasis.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.oasis.todoapp.model.Task;
import com.oasis.todoapp.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_app.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PASSWORD = "password_hash";

    // Tasks table
    private static final String TABLE_TASKS = "tasks";
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_TASK_USER_ID = "user_id";
    private static final String KEY_TASK_TITLE = "title";
    private static final String KEY_TASK_NOTES = "notes";
    private static final String KEY_TASK_IS_COMPLETED = "is_completed";
    private static final String KEY_TASK_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_EMAIL + " TEXT UNIQUE,"
                + KEY_USER_PASSWORD + " TEXT" + ")";

        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TASK_USER_ID + " INTEGER,"
                + KEY_TASK_TITLE + " TEXT,"
                + KEY_TASK_NOTES + " TEXT,"
                + KEY_TASK_IS_COMPLETED + " INTEGER DEFAULT 0,"
                + KEY_TASK_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + KEY_TASK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ") ON DELETE CASCADE" + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Hash Password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Register User
    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_EMAIL, email.trim().toLowerCase());
        values.put(KEY_USER_PASSWORD, hashPassword(password));

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Check if email already exists
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID},
                KEY_USER_EMAIL + "=?", new String[]{email.trim().toLowerCase()},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Validate User Login and Return User Object
    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);
        Cursor cursor = db.query(TABLE_USERS, null,
                KEY_USER_EMAIL + "=? AND " + KEY_USER_PASSWORD + "=?",
                new String[]{email.trim().toLowerCase(), hashedPassword},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_USER_ID);
            int nameIndex = cursor.getColumnIndex(KEY_USER_NAME);
            int emailIndex = cursor.getColumnIndex(KEY_USER_EMAIL);
            int passIndex = cursor.getColumnIndex(KEY_USER_PASSWORD);

            user = new User(
                    cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(emailIndex),
                    cursor.getString(passIndex)
            );
        }
        cursor.close();
        return user;
    }

    // Add Task
    public boolean addTask(int userId, String title, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TASK_USER_ID, userId);
        values.put(KEY_TASK_TITLE, title);
        values.put(KEY_TASK_NOTES, notes);
        values.put(KEY_TASK_IS_COMPLETED, 0);

        long result = db.insert(TABLE_TASKS, null, values);
        return result != -1;
    }

    // Get all tasks for a specific user
    public List<Task> getTasksForUser(int userId) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_TASKS, null,
                KEY_TASK_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, KEY_TASK_IS_COMPLETED + " ASC, " + KEY_TASK_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_TASK_ID);
            int userIdIndex = cursor.getColumnIndex(KEY_TASK_USER_ID);
            int titleIndex = cursor.getColumnIndex(KEY_TASK_TITLE);
            int notesIndex = cursor.getColumnIndex(KEY_TASK_NOTES);
            int completedIndex = cursor.getColumnIndex(KEY_TASK_IS_COMPLETED);
            int createdIndex = cursor.getColumnIndex(KEY_TASK_CREATED_AT);

            do {
                Task task = new Task(
                        cursor.getInt(idIndex),
                        cursor.getInt(userIdIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(notesIndex),
                        cursor.getInt(completedIndex) == 1,
                        cursor.getString(createdIndex)
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return taskList;
    }

    // Update task completion status
    public boolean updateTaskCompletion(int taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TASK_IS_COMPLETED, isCompleted ? 1 : 0);

        int result = db.update(TABLE_TASKS, values, KEY_TASK_ID + "=?", new String[]{String.valueOf(taskId)});
        return result > 0;
    }

    // Delete Task
    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, KEY_TASK_ID + "=?", new String[]{String.valueOf(taskId)});
        return result > 0;
    }
}
