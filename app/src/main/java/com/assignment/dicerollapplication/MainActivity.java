package com.assignment.dicerollapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dice_types, R.layout.spinner_data);
        adapter.setDropDownViewResource(R.layout.spinner_data);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // update the change value based on user selection from spinner
                selectedType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        diceImage = (ImageView) findViewById(R.id.imageView);
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
                resultTextView1ForTwoRoll.setVisibility(View.INVISIBLE);
                resultTextView2ForTwoRoll.setVisibility(View.INVISIBLE);
                resultTextView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isOneRollTriggered){
                    resultTextView.setVisibility(View.VISIBLE);
                }else{
                    resultTextView1ForTwoRoll.setVisibility(View.VISIBLE);
                    resultTextView2ForTwoRoll.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        diceSize = findViewById(R.id.editTextNumber);
        buttonOnce = findViewById(R.id.buttonOnce);
        buttonTwice = findViewById(R.id.buttonTwice);
        buttonOnce.setOnClickListener(this::rollOnce);
        buttonTwice.setOnClickListener(this::rollTwice);

        resultTextView = findViewById(R.id.resultTextView);
        resultTextView1ForTwoRoll = findViewById(R.id.resultTextView1ForTwoRoll);
        resultTextView2ForTwoRoll = findViewById(R.id.resultTextView2ForTwoRoll);
        resultTextView.setVisibility(View.INVISIBLE);
        resultTextView1ForTwoRoll.setVisibility(View.INVISIBLE);
        resultTextView2ForTwoRoll.setVisibility(View.INVISIBLE);

    }


    private void rollTwice(View view) {
        isOneRollTriggered = false;
        diceImage.startAnimation(rotate);
        diceImage.setVisibility(View.INVISIBLE);
        resultTextView1ForTwoRoll.setText(calculateResult());
        resultTextView2ForTwoRoll.setText(calculateResult());
    }

    private void rollOnce(View view) {
        isOneRollTriggered = true;
        diceImage.startAnimation(rotate);
        diceImage.setVisibility(View.INVISIBLE);
        resultTextView.setText(calculateResult());
    }

    private String calculateResult(){
        int value = 0;
        switch(selectedType){
            case 0:
                value = rollDice(1);
                break;
            case 1:
                value = rollDice(10);
                break;
            case 2:
                return String.valueOf((int)Math.floor(Math.random() * (10)));
        }
        return String.valueOf(value);

    }

    /**
     * This is used to roll a dice and give a random side
     *
     * @return
     */
    public int rollDice(int i) {
        return (int) (Math.floor(Math.random() * (Integer.parseInt(diceSize.getText().toString()))) + 1)*i;
    }
}