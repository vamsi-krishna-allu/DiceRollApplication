package com.assignment.dicerollapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    int selectedType = 0;
    EditText diceSize;
    ImageView diceImage;
    RotateAnimation rotate;
    Button buttonOnce;
    Button buttonTwice;
    TextView resultTextView;
    TextView resultTextView1ForTwoRoll;
    TextView resultTextView2ForTwoRoll;
    boolean isOneRollTriggered = false;
    boolean isBasicDie = true;
    TextView previousValues;
    SharedPreferences sharedPreferences;
    Button clearButton;
    Spinner spinner;
    Switch switchToggle;
    TextView dieSelectionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // below line hides the status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // below line is to enable night mode
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        initializeWidgets();
        // listener for spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // update the change value based on user selection from spinner
                selectedType = i;
                // update the selected value in shared preferences
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putInt("selection", selectedType);
                myEdit.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        showDiceAnimation();


        buttonOnce.setOnClickListener(this::rollOnce);
        buttonTwice.setOnClickListener(this::rollTwice);
        clearButton.setOnClickListener(this::clearAll);
        switchToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // toggle between custom and default die based on switch toggle
            if (isChecked) {
                isBasicDie = false;
                spinner.setVisibility(View.INVISIBLE);
                dieSelectionTextView.setText(R.string.custom_die_selected);
                diceSize.setVisibility(View.VISIBLE);
            } else {
                isBasicDie = true;
                diceSize.setVisibility(View.INVISIBLE);
                dieSelectionTextView.setText(R.string.basic_die_selected);
                spinner.setVisibility(View.VISIBLE);
            }
        });

        updateDefaultVisibilityStatus();

        updateUsingSharedPreferences();
    }

    /**
     * This method sets the default visibility status of different widgets
     */
    private void updateDefaultVisibilityStatus() {
        resultTextView.setVisibility(View.INVISIBLE);
        resultTextView1ForTwoRoll.setVisibility(View.INVISIBLE);
        resultTextView2ForTwoRoll.setVisibility(View.INVISIBLE);
        diceSize.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
    }

    /**
     * update widgets values using shared preferences values
     */
    private void updateUsingSharedPreferences() {
        sharedPreferences = getSharedPreferences("myPreferences",MODE_PRIVATE);
        spinner.setSelection(sharedPreferences.getInt("selection", 0));
        previousValues.setText(sharedPreferences.getString("previousValues",""));
        diceSize.setText(sharedPreferences.getString("currentEnteredValue","0"));
    }

    /**
     * initialize all the widgets with corresponding id's
     */
    private void initializeWidgets() {
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dice_types, R.layout.spinner_data);
        adapter.setDropDownViewResource(R.layout.spinner_data);
        spinner.setAdapter(adapter);
        previousValues = findViewById(R.id.previousValueTextView);
        previousValues.setMovementMethod(new ScrollingMovementMethod());
        diceImage = (ImageView) findViewById(R.id.imageView);
        diceSize = findViewById(R.id.editTextNumber);
        buttonOnce = findViewById(R.id.buttonOnce);
        buttonTwice = findViewById(R.id.buttonTwice);
        clearButton = findViewById(R.id.clearButton);
        resultTextView = findViewById(R.id.resultTextView);
        resultTextView1ForTwoRoll = findViewById(R.id.resultTextView1ForTwoRoll);
        resultTextView2ForTwoRoll = findViewById(R.id.resultTextView2ForTwoRoll);
        switchToggle = findViewById(R.id.diceSwitch);
        dieSelectionTextView = findViewById(R.id.dieSelectionTextView);
    }

    /**
     * below method is to implement animation for the dice on user clicking the roll buttons
     */
    private void showDiceAnimation() {
        // setting the rotation as 360 degree
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
       // setting the animation duration as 2 seconds
        rotate.setDuration(2000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
                // At the start of animation all the result views are hidden and dice animation is shown
                resultTextView1ForTwoRoll.setVisibility(View.INVISIBLE);
                resultTextView2ForTwoRoll.setVisibility(View.INVISIBLE);
                resultTextView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Update the visibility of result text views and previous values text view at the end of animation
                if(isOneRollTriggered){
                    resultTextView.setVisibility(View.VISIBLE);
                    if(previousValues.getText().toString().isEmpty()){
                        previousValues.setText(String.format("%s", resultTextView.getText().toString()));
                    }else{
                        previousValues.setText(String.format("%s,%s", previousValues.getText(), resultTextView.getText().toString()));
                    }
                }else{
                    resultTextView1ForTwoRoll.setVisibility(View.VISIBLE);
                    resultTextView2ForTwoRoll.setVisibility(View.VISIBLE);
                    if(previousValues.getText().toString().isEmpty()){
                        previousValues.setText(String.format("[%s,%s]", resultTextView1ForTwoRoll.getText().toString(), resultTextView2ForTwoRoll.getText().toString()));
                    }else{
                        previousValues.setText(String.format("%s, [%s,%s]", previousValues.getText(), resultTextView1ForTwoRoll.getText().toString(), resultTextView2ForTwoRoll.getText().toString()));
                    }
                }
                // Update the shared preferences with the die roll results
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("previousValues", previousValues.getText().toString());
                myEdit.putString("currentEnteredValue", diceSize.getText().toString());
                myEdit.commit();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * Below method is used to clear everything on the UI and from shared preferences
     * @param view
     */
    private void clearAll(View view) {
        previousValues.setText("");
        diceSize.setText("0");
        spinner.setSelection(0);
        resultTextView.setText("0");
        resultTextView1ForTwoRoll.setText("0");
        resultTextView2ForTwoRoll.setText("0");
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("previousValues", previousValues.getText().toString());
        myEdit.putString("currentEnteredValue", diceSize.getText().toString());
        myEdit.putInt("selection", 0);
        myEdit.commit();
    }


    /**
     * Below method is to roll the die twice and show both results
     * @param view
     */
    private void rollTwice(View view) {
        isOneRollTriggered = false;
        // show toast message when user does not enter any value in edit text for custom die selection
        if(!isBasicDie && (diceSize.getText().toString().isEmpty() || diceSize.getText().toString().equals("0"))){
            Toast.makeText(this, "Enter valid dice size", Toast.LENGTH_SHORT).show();
            return;
        }
        // start the animation
        diceImage.startAnimation(rotate);
        diceImage.setVisibility(View.INVISIBLE);
        String firstRoll = isBasicDie ? calculateResult() : calculateCustomResult();
        resultTextView1ForTwoRoll.setText(firstRoll);
        String secondRoll = isBasicDie ? calculateResult() : calculateCustomResult();
        resultTextView2ForTwoRoll.setText(secondRoll);
    }

    private void rollOnce(View view) {
        isOneRollTriggered = true;
        // show toast message when user does not enter any value in edit text for custom die selection
        if(!isBasicDie && (diceSize.getText().toString().isEmpty() || diceSize.getText().toString().equals("0"))){
            Toast.makeText(this, "Enter valid dice size", Toast.LENGTH_SHORT).show();
            return;
        }
        // start the animation
        diceImage.startAnimation(rotate);
        diceImage.setVisibility(View.INVISIBLE);
        String rollResult = isBasicDie ? calculateResult() : calculateCustomResult();
        resultTextView.setText(rollResult);
    }

    /**
     * Method to calculate the result for custom die roll value using the edit text value
     * @return
     */
    private String calculateCustomResult() {
        return String.valueOf(rollDice(Integer.parseInt(diceSize.getText().toString())));
    }

    /**
     * Method to calculate the basic die roll result based on spinner selection
     * @return
     */
    private String calculateResult(){
        int value = 0;
        switch(selectedType){
            case 0:
                value = rollDice(4);
                break;
            case 1:
                value = rollDice(6);
                break;
            case 2:
                value = rollDice(8);
                break;
            case 3:
                value = rollDice(10);
                break;
            case 4:
                value = rollDice(12);
                break;
            case 5:
                value = rollDice(20);
                break;
            case 6:
                value = rollDice(10) * 10;
                break;
            case 7:
                value = (int)Math.floor(Math.random() * (10));
                break;
        }
        return String.valueOf(value);

    }

    /**
     * This is used to roll a dice and give a random side
     * @param value
     * @return
     */
    public int rollDice(int value) {
        return (int) (Math.floor(Math.random() * (value)) + 1);
    }
}