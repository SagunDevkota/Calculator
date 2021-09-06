package com.sd2.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button button0,button1,button2,button3,button4,button5,button6,button7,button8,button9,buttonPlus,buttonMinus,buttonDivide,buttonMultiply,buttonModulo,buttonAC,buttonDel,buttonSwap,buttonDot,buttonEquals;
    TextView result;
    Double finalResult;
    RecyclerView recyclerView;
    ArrayList<String> historyQuestion = new ArrayList<>();
    ArrayList<String> historyResult = new ArrayList<>();
    boolean dot = false;
    boolean equals = false;
    DecimalFormat numberFormat = new DecimalFormat("########.######");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        assignView();
        buttonListeners();
        buttonEquals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result.getText().length()>0) {
                    try {
                        calculation();
                    }catch (NumberFormatException e){
                        result.setText("");
                    }
                }
                equals = true;
                dot = true;
            }
        });
    }

    private void recyclerCall() {
        recyclerView = findViewById(R.id.recyclerView);
        AdapterRecycler adapterRecycler = new AdapterRecycler(getApplicationContext(),historyQuestion,historyResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterRecycler);
        recyclerView.smoothScrollToPosition(historyResult.size()-1);
    }

    private void calculation() {
                ArrayList<Double> numbers = new ArrayList<>(); //to store numbers separated from operators
                ArrayList<Character> operations = new ArrayList<>(); // to store operators
                /*
                Number and operator maintain index.
                eg: 5+7-6
                array {5,7,6}
                array {+,-}
                * */
                char[] precedenceList = {'/','*','+','%'};
                /*
                To implement bodmas rule - is not included because
                5-6 = 5+(-6) = -1
                * */
                String currentNumber = "";
                /*
                numbers obtained so far.
                eg: 55+63
                then first number must be 55 and second be 63
                The current number is used to merge every characters from one operator to another.
                * */
                String currentProblem = result.getText().toString(); //input given by user
                for (int i=0; i<currentProblem.length();i++){ //to extract every character of user input  which include numbers and operators.
                    if(String.valueOf(currentProblem.charAt(i)).equals(".")){
                        // to check for decimal value
                        currentNumber+=".";
                        continue;
                    }
                    if(isDouble(String.valueOf(currentProblem.charAt(i)))!=null){
                        // to check if character is a number or not for more check isDouble method.
                        currentNumber = currentNumber+String.valueOf(currentProblem.charAt(i));
                        // if input is a number then add that number to current number string.
                    }else if(currentProblem.charAt(i)=='-'){
                        // To implement 5-6 = 5+(-6)
                        if(i!=0 && isDouble(String.valueOf(currentProblem.charAt(i - 1)))!=null) {
                            numbers.add(Double.parseDouble(currentNumber));
                        }
                        currentNumber = "";
                        currentNumber = "-"+currentNumber;
                        if(i!=0 && isDouble(String.valueOf(currentProblem.charAt(i - 1)))!=null) {
                            // to check if number starts from - eg:-5+4
                            operations.add('+');
                        }
                    }else{
                        // if isDouble is null then character is operator so add it to operations.
                        operations.add(currentProblem.charAt(i));
                        numbers.add(Double.parseDouble(currentNumber));
                        // add current numbers to number after an operator is found.
                        currentNumber = "";
                        // reset the value of current number
                        // eg: 55 + 66 after + add current number (55) to list and empty current number to hold 66.
                    }
                }
                numbers.add(Double.parseDouble(currentNumber));
                // to add last number as there is no operator at last.
                // eg: 55+66 first number is added after + is found but there is not operator after 66 so it is added after loop exits.
                for(int i=0;i<precedenceList.length;i++){
                    // iterate over precedence list to implement bodmas.
                    int j=0;
                    // current pointer
                    int lastSize;
                    // previous size of operations list i.e. size before calculation
                    while(operations.size()!=0){
                        // loop until all operations are done.
                        lastSize = operations.size();
                        if(precedenceList[i]==operations.get(j)){
                            // to check for divide of user input in first iteration.
                            if(precedenceList[i]=='/'){
                                finalResult = numbers.get(j)/numbers.get(j+1);
                            }else if(precedenceList[i]=='*'){
                                finalResult = numbers.get(j)*numbers.get(j+1);
                            }else if(precedenceList[i]=='+'){
                                finalResult = numbers.get(j)+numbers.get(j+1);
                            }else if(precedenceList[i]=='-'){
                                finalResult = numbers.get(j)-numbers.get(j+1);
                            }else if(precedenceList[i]=='%'){
                                finalResult = numbers.get(j)%numbers.get(j+1);
                            }
                            numbers.remove(j);
                            if(numbers.size()!=1) {
                                numbers.remove(j);
                            }
                            operations.remove(j);
                            /*
                            * for 5+6+3
                            * after the + operation of 5+6 is done then we must drop 5&6 from list
                            * i.e. drop two numbers and add result
                            * then drop the operator of completed operation
                            * */
                            numbers.add(j,finalResult);
                        }
                        j++;
                        if(operations.size()!=lastSize){
                            j=0;
                            /*
                            * eg: 5/6/6
                            * in first iteration it finds / operator and evaluates 5/6=0.833
                            * then it must again evaluate 0.833/6 but our loop moves forward with increase
                            * in value of j so j is reset to 0 to reevaluate the operations list
                            * */
                        }
                        if (j==operations.size()){
                            break;
                            /*
                            to prevent array index error in next iteration.
                            * */
                        }
                    }
                }
                historyQuestion.add(result.getText().toString());
                // to add previous question in adapter view
                if(finalResult != null) {
                    // to prevent value error.
                    result.setText("" + numberFormat.format(finalResult));
                }
                historyResult.add(result.getText().toString());
                recyclerCall();
                finalResult = null;
    }

    private Double isDouble(String charAt) {
        try{
            if(charAt.equals('.')){
                return 0.0;
            }
            return Double.parseDouble(charAt);
        }catch (Exception e){
            return null;
        }
    }

    private void buttonListeners() {
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }
                result.setText(result.getText().toString()+"0");
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }

                result.setText(result.getText().toString()+"1");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(equals){
                    result.setText("");
                    equals = false;
                }

                result.setText(result.getText().toString()+"2");
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }
                result.setText(result.getText().toString()+"3");
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }
                result.setText(result.getText().toString()+"4");
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }
                result.setText(result.getText().toString()+"5");
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }
                result.setText(result.getText().toString()+"6");
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }
                result.setText(result.getText().toString()+"7");
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }
                result.setText(result.getText().toString()+"8");
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                }
                result.setText(result.getText().toString()+"9");
            }
        });
        buttonDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(equals){
                    result.setText("");
                    equals = false;
                    dot = false;
                }
                if(dot==false) {
                    result.setText(result.getText().toString() + ".");
                    dot = true;
                    equals = false;
                }
            }
        });
        buttonModulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(result.getText().toString()+"%");
                dot = false;
                equals = false;
            }
        });
        buttonDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(result.getText().toString()+"/");
                dot = false;
                equals = false;
            }
        });
        buttonMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(result.getText().toString()+"*");
                dot = false;
                equals = false;
            }
        });
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(result.getText().toString()+"-");
                dot = false;
                equals = false;
            }
        });
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(result.getText().toString()+"+");
                dot = false;
                equals = false;
            }
        });
        buttonAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("");
                dot = false;
                equals = false;
            }
        });
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result.getText().toString().length()>0) {
                    if(result.getText().toString().charAt(result.getText().toString().length()-1)=='.'){
                        dot = false;
                    }
                    result.setText(result.getText().toString().substring(0, result.getText().toString().length() - 1));
                }
            }
        });
    }

    private void assignView() {
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        buttonDivide = findViewById(R.id.buttonDivide);
        buttonMultiply = findViewById(R.id.buttonMultiply);
        buttonPlus = findViewById(R.id.buttonPlus);
        buttonMinus = findViewById(R.id.buttonMinus);
        buttonModulo = findViewById(R.id.buttonModulo);
        buttonDot = findViewById(R.id.buttonDot);
        buttonAC = findViewById(R.id.buttonAC);
        buttonDel = findViewById(R.id.buttonDel);
        buttonEquals = findViewById(R.id.buttonEquals);
        buttonSwap = findViewById(R.id.buttonSwap);
        result = findViewById(R.id.result);
    }
}