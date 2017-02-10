package com.example.camilomontoya.servermensajeriamulticast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{

    private Button bt;
    private TextView result;
    private final String GROUP_ADDRESS = "228.5.6.7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = (Button) findViewById(R.id.enviar);
        result = (TextView) findViewById(R.id.resultados);

        result.setText("Resultados\n");

        CommunicationManager.getInstance().addObserver(this);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunicationManager.getInstance().enviarMensaje("correr","172.30.188.101",5000);
                CommunicationManager.getInstance().enviarMensaje("correr","228.5.6.7",5000);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        CommunicationManager.getInstance().enviarMensaje("correr",GROUP_ADDRESS,5000);
    }

    @Override
    protected void onPause(){
        super.onPause();
        CommunicationManager.getInstance().enviarMensaje("stop",GROUP_ADDRESS,5000);

    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof String){
            String message = (String) arg;
            llegadaMensaje(message);
        }
    }

    private void llegadaMensaje(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                result.setText(result.getText()+" "+msg+"\n");
            }
        });
    }
}
