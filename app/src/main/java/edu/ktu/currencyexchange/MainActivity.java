package edu.ktu.currencyexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView convertFromDropdownTextView, convertToDropdownTextView, conversionRateText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog;
    Dialog toDialog;
    Button convertButton;
    String convertFromValue, convertToValue, conversionValue;
    String[] currency = {"EUR", "AZN", "TL", "USD", "AUD"}; // TODO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertFromDropdownTextView = findViewById(R.id.convert_from_dropdown_menu);
        convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu);
        convertButton = findViewById(R.id.conversionButton);
        conversionRateText = findViewById(R.id.conversionRateText);
        amountToConvert = findViewById(R.id.amountToConvertValueEditText);

        arrayList = new ArrayList<>();
        for (String i : currency) {
            arrayList.add(i);
        }
        convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDialog = new Dialog(MainActivity.this);
                fromDialog.setContentView(R.layout.from_spinner);
                fromDialog.getWindow().setLayout(650, 800);
                fromDialog.show();

                EditText editText = fromDialog.findViewById(R.id.edit_text);
                ListView listView = fromDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        convertFromDropdownTextView.setText(adapter.getItem(i));
                        fromDialog.dismiss();
                        convertFromValue = adapter.getItem(i);
                    }
                });
            }
        });

        convertToDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDialog = new Dialog(MainActivity.this);
                toDialog.setContentView(R.layout.to_spinner);
                toDialog.getWindow().setLayout(650, 800);
                toDialog.show();

                EditText editText = toDialog.findViewById(R.id.edit_text);
                ListView listView = toDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        convertToDropdownTextView.setText(adapter.getItem(i));
                        toDialog.dismiss();
                        convertToValue = adapter.getItem(i);
                    }
                });
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Double amountToConvert = Double.valueOf(MainActivity.this.amountToConvert.getText().toString());
                    getConversionValue(convertFromValue, convertToValue, amountToConvert);
                } catch (Exception e){

                }
            }
        });

    }

    public String getConversionValue(String convertFrom, String convertTo, Double amountToConvert) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://free.currconv.com/api/v7/convert?q="+convertFrom+"_"+convertTo+"&compact=ultra&apiKey=3a695e0733337861f13a";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Double conversionRateValue = round(((Double) jsonObject.get(convertFrom + "_" + convertTo)), 2);
                    conversionValue = "" + round((conversionRateValue * amountToConvert), 2);
                    conversionRateText.setText(conversionValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
        return null;
    }

    public static double round(double value, int places) {
        if(places<0) throw new IllegalArgumentException();
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}