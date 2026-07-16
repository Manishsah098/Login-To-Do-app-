package com.oasis.todoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oasis.todoapp.adapter.TaskAdapter;
import com.oasis.todoapp.database.DatabaseHelper;
import com.oasis.todoapp.model.Task;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private TextView tvWelcomeName;
    private ImageButton btnLogout;
    private RecyclerView rvTasks;
    private LinearLayout emptyStateView;
    private FloatingActionButton fabAddTask;

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("userId")) {
            navigateToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        currentUserId = sharedPreferences.getInt("userId", -1);
        String userName = sharedPreferences.getString("userName", "User");

        dbHelper = new DatabaseHelper(this);

        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        btnLogout = findViewById(R.id.btnLogout);
        rvTasks = findViewById(R.id.rvTasks);
        emptyStateView = findViewById(R.id.emptyStateView);
        fabAddTask = findViewById(R.id.fabAddTask);

        tvWelcomeName.setText(userName + "\'s Tasks");

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this);
        rvTasks.setAdapter(taskAdapter);

        loadTasks();

        btnLogout.setOnClickListener(v -> handleLogout());
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void loadTasks() {
        taskList = dbHelper.getTasksForUser(currentUserId);
        taskAdapter.updateTasks(taskList);

        if (taskList.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            rvTasks.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            rvTasks.setVisibility(View.VISIBLE);
        }
    }

    private void handleLogout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    private void navigateToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etTaskNotes = dialogView.findViewById(R.id.etTaskNotes);
        TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnAddTask = dialogView.findViewById(R.id.btnAddTask);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAddTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            String notes = etTaskNotes.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                etTaskTitle.setError("Task name is required");
                return;
            }

            boolean success = dbHelper.addTask(currentUserId, title, notes);
            if (success) {
                Toast.makeText(MainActivity.this, "Task added!", Toast.LENGTH_SHORT).show();
                loadTasks();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Failed to add task", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onCompleteToggle(Task task, boolean isCompleted) {
        dbHelper.updateTaskCompletion(task.getId(), isCompleted);
        // Reload list to sort items (completed items are pushed down)
        loadTasks();
    }

    @Override
    public void onDeleteClick(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to permanently delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteTask(task.getId());
                    if (success) {
                        loadTasks();
                        Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
