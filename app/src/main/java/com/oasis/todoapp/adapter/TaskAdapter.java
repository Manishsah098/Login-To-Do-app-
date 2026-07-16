package com.oasis.todoapp.adapter;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.oasis.todoapp.R;
import com.oasis.todoapp.model.Task;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskClickListener clickListener;

    public interface OnTaskClickListener {
        void onCompleteToggle(Task task, boolean isCompleted);
        void onDeleteClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener clickListener) {
        this.taskList = taskList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, clickListener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbComplete;
        TextView tvTaskTitle;
        TextView tvTaskNotes;
        ImageButton btnDeleteTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbComplete = itemView.findViewById(R.id.cbComplete);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskNotes = itemView.findViewById(R.id.tvTaskNotes);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }

        public void bind(final Task task, final OnTaskClickListener listener) {
            tvTaskTitle.setText(task.getTitle());
            
            if (TextUtils.isEmpty(task.getNotes())) {
                tvTaskNotes.setVisibility(View.GONE);
            } else {
                tvTaskNotes.setVisibility(View.VISIBLE);
                tvTaskNotes.setText(task.getNotes());
            }

            // Prevent listener triggers during bind recycling
            cbComplete.setOnCheckedChangeListener(null);
            cbComplete.setChecked(task.isCompleted());

            applyStrikeThrough(task.isCompleted());

            cbComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                applyStrikeThrough(isChecked);
                if (listener != null) {
                    listener.onCompleteToggle(task, isChecked);
                }
            });

            btnDeleteTask.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(task);
                }
            });
        }

        private void applyStrikeThrough(boolean isCompleted) {
            if (isCompleted) {
                tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskTitle.setTextColor(itemView.getResources().getColor(R.color.text_secondary));
            } else {
                tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTaskTitle.setTextColor(itemView.getResources().getColor(R.color.white));
            }
        }
    }
}
