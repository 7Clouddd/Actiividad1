package com.example.actividad1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText taskInput;
    Button addTaskBtn;
    ListView taskList;
    ProgressBar progressBar;
    TextView progressText, welcome;

    ArrayList<String> tasks = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome = findViewById(R.id.welcome);
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            welcome.setText("Mis actividades (" + email + ")");
        }

        taskInput = findViewById(R.id.taskInput);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        taskList = findViewById(R.id.taskList);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, tasks);
        taskList.setAdapter(adapter);
        taskList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        addTaskBtn.setOnClickListener(v -> {
            String t = taskInput.getText().toString().trim();
            if (t.isEmpty()) {
                Toast.makeText(this, "Escribe una actividad", Toast.LENGTH_SHORT).show();
                return;
            }
            tasks.add(t);
            adapter.notifyDataSetChanged();
            taskInput.setText("");
            updateProgress();
        });

        // Toggle completar
        taskList.setOnItemClickListener((parent, view, position, id) -> updateProgress());

        // Long click: menú editar / eliminar
        taskList.setOnItemLongClickListener((parent, view, position, id) -> {
            String current = tasks.get(position);
            String[] options = {"Editar", "Eliminar"};
            new AlertDialog.Builder(this)
                    .setTitle(current)
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            showEditDialog(position, current);
                        } else {
                            tasks.remove(position);
                            adapter.notifyDataSetChanged();
                            updateProgress();
                        }
                    }).show();
            return true;
        });
    }

    private void showEditDialog(int position, String current) {
        final EditText input = new EditText(this);
        input.setText(current);
        new AlertDialog.Builder(this)
                .setTitle("Editar actividad")
                .setView(input)
                .setPositiveButton("Guardar", (d, w) -> {
                    String newText = input.getText().toString().trim();
                    if (!newText.isEmpty()) {
                        tasks.set(position, newText);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateProgress() {
        int total = tasks.size();
        if (total == 0) {
            progressBar.setProgress(0);
            progressText.setText("0% completado");
            return;
        }
        int checked = 0;
        for (int i = 0; i < total; i++) {
            if (taskList.isItemChecked(i)) checked++;
        }
        int percent = (int) ((checked * 100.0f) / total);
        progressBar.setProgress(percent);
        progressText.setText(percent + "% completado");
    }
}
