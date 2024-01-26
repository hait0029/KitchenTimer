// Import statements
package com.example.kitchentimer;

// Android dependencies
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

// AppCompatActivity from the AndroidX library
import androidx.appcompat.app.AppCompatActivity;

// Java utilities
import java.util.ArrayList;
import java.util.List;

// Main activity class
public class MainActivity extends AppCompatActivity {
    // Media player for notification sound
    private MediaPlayer mediaPlayer;

    // UI elements
    private EditText foodTypeEditText;
    private Button deleteButton, resetButton, startTimerButton;
    private ListView foodList;
    private ArrayAdapter<String> adapter;
    private List<FoodTimerItem> foodTimerItems;
    private NumberPicker hourPicker, minutePicker, secondPicker;

    // Timer status flag
    private boolean isTimerRunning = false;

    // Handler for updating UI
    private Handler handler;

    // Activity lifecycle method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        foodTypeEditText = findViewById(R.id.foodTypeEditText);
        deleteButton = findViewById(R.id.deleteButton);
        resetButton = findViewById(R.id.resetButton);
        startTimerButton = findViewById(R.id.startTimerButton);
        foodList = findViewById(R.id.foodList);
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        secondPicker = findViewById(R.id.secondPicker);

        // Set the minValue and maxValue for each picker
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        // Respond to value changes in pickers
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Respond to hour value change
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Respond to minute value change
            }
        });
        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Respond to second value change
            }
        });

        // Initialize data structures for timers and list view
        foodTimerItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        foodList.setAdapter(adapter);

        // Set ListView to allow single item selection
        foodList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Handler for updating UI
        handler = new Handler() {
            @Override
            public void handleMessage(Message inputMessage) {
                updateTimerText((Long) inputMessage.obj);
            }
        };

        // Initialize media player with default alarm sound
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);

        // Button click listeners
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedFoodTimerItem();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFoodTimerItem(); // Call the method to create a food timer item
                startTimer(); // Start the timer after creating the item
            }
        });
    }

    // Create a new food timer item
    private void createFoodTimerItem() {
        String foodType = foodTypeEditText.getText().toString();
        int hours = hourPicker.getValue();
        int minutes = minutePicker.getValue();
        int seconds = secondPicker.getValue();

        FoodTimerItem foodTimerItem = new FoodTimerItem(foodType, hours, minutes, seconds);
        foodTimerItems.add(foodTimerItem);

        // Refresh the ListView
        updateListView();

        // Clear the input fields
        foodTypeEditText.getText().clear();
        resetTimer();
    }

    // Delete the selected food timer item
    private void deleteSelectedFoodTimerItem() {
        int selectedPosition = foodList.getCheckedItemPosition();
        if (selectedPosition != ListView.INVALID_POSITION) {
            foodTimerItems.remove(selectedPosition);

            // Refresh the ListView
            updateListView();
        }
    }

    // Reset timer pickers to default values
    private void resetTimer() {
        hourPicker.setValue(0);
        minutePicker.setValue(0);
        secondPicker.setValue(0);
    }

    // Start the timer
    private void startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isTimerRunning) {
                        // Update the timers in the food items
                        for (FoodTimerItem item : foodTimerItems) {
                            if (item.getSeconds() > 0) {
                                item.decrementSecond();
                            } else {
                                if (item.getMinutes() > 0) {
                                    item.decrementMinute();
                                    item.setSeconds(59);
                                } else {
                                    if (item.getHours() > 0) {
                                        item.decrementHour();
                                        item.setMinutes(59);
                                        item.setSeconds(59);
                                    } else {
                                        // Timer for this item has reached 0
                                        item.setHours(0);
                                        item.setMinutes(0);
                                        item.setSeconds(0);

                                        // Play the notification sound
                                        playNotificationSound();
                                    }
                                }
                            }
                        }

                        // Send message to UI thread to update the ListView
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateListView();
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    // Update the ListView with current timer values
    private void updateListView() {
        List<String> foodNames = new ArrayList<>();
        for (FoodTimerItem item : foodTimerItems) {
            foodNames.add(item.getFoodType() + " - " + item.getHours() + ":" + item.getMinutes() + ":" + item.getSeconds());
        }
        adapter.clear();
        adapter.addAll(foodNames);
    }

    // Update timer UI element if needed
    private void updateTimerText(long millisRemaining) {
        // You can handle updating a timer UI element if needed
    }

    // Play the notification sound and stop when completed
    private void playNotificationSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Stop the media player when the sound is completed
                    stopNotificationSound();
                }
            });
        }
    }

    // Stop the notification sound
    private void stopNotificationSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Stop notification sound on activity destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNotificationSound();
    }
}
