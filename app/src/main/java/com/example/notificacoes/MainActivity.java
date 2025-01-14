package com.example.notificacoes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Define um intent para ir para a tela principal (com ação "home")
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setAction("home");
        PendingIntent homePendingIntent = PendingIntent.getActivity(this, 0, homeIntent, PendingIntent.FLAG_IMMUTABLE);

        String action = getIntent().getAction();
        // Verifica qual ação deve ser executada
        if ("schedule".equals(action)) {
            // Obtém o TextView com id text e define seu valor
            TextView text = findViewById(R.id.text);
            text.setText("Agende a próxima entrega");
        } else {
            // Notificação simples
//        Notification nBuilder = simpleNotification(homePendingIntent);

            // Notificação expandida de texto longo
//        Notification nBuilder = expandableNotification(homePendingIntent);

            // Notificação com ações
            Notification nBuilder = actionNotification(homePendingIntent);

            sendNotification(nBuilder);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(Notification nBuilder) {
        if (!checkPermissions()) {
            return;
        }

        // Cria um canal de notificação (necessário para versões android 8.0 e superiores)
        createNotificationChannel();

        // Cria um gerenciador de notificações e envia a notificação
        NotificationManagerCompat nManager = NotificationManagerCompat.from(this);
        nManager.notify(1, nBuilder);
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Verifica se a permissão de notificação está habilitada e solicita se não estiver.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }

            // Emite um toast se a permissão de notificação foi negada.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permissão de notificação negada", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    // Cria um canal de notificação, o canal serve para agrupar notificações semelhantes e
    // definir propriedades como a importância da notificação.
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaçoes do App";
            String description = "Canal de notificações";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification simpleNotification(PendingIntent contentIntent) {
        return new NotificationCompat.Builder(this, "123")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Define o ícone da notificação
            .setContentTitle("Seu pedido saiu para entrega") // Define o título da notificação
            .setContentText("Ótima noticia, chega hoje!") // Define o texto da notificação
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Define a prioridade da notificação
            .setContentIntent(contentIntent) // Define a ação de clique na notificação
            .setAutoCancel(true) // Define que a notificação deve ser removida automaticamente ao ser clicada
            .build(); // Cria efetivamente a notificação
    }

    private Notification expandableNotification(PendingIntent contentIntent) {
        return new NotificationCompat.Builder(this, "123")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Define o ícone da notificação
            .setContentTitle("Parabéns") // Define o título da notificação
            .setContentText("Você ganhou uma máquina de lavar...") // Define o texto da notificação
            .setStyle(new NotificationCompat.BigTextStyle() // Define o estilo da notificação para texto longo
            .bigText("Você ganhou uma máquina de lavar! Para receber seu prêmio basta pagar R$3000 de frete!")) // Define o texto da notificação expandida
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Define a prioridade da notificação
            .setContentIntent(contentIntent) // Define a ação de clique na notificação
            .setAutoCancel(true) // Define que a notificação deve ser removida automaticamente ao ser clicada
            .build(); // Cria efetivamente a notificação
    }

    private Notification actionNotification(PendingIntent contentIntent) {
        // Define um intent para ir para a tela principal (com ação "schedule")
        Intent scheduleIntent = new Intent(this, MainActivity.class);
        scheduleIntent.setAction("schedule");
        PendingIntent schedulePendingIntent = PendingIntent.getActivity(this, 0, scheduleIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, "123")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Define o ícone da notificação
            .setContentTitle("Seu pedido saiu para entrega") // Define o título da notificação
            .setContentText("Já está chegando!") // Define o texto da notificação
            .addAction(0, "Agendar próxima entrega", schedulePendingIntent) // Adiciona uma ação na notificação
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Define a prioridade da notificação
            .setContentIntent(contentIntent) // Define a ação de clique na notificação
            .setAutoCancel(true) // Define que a notificação deve ser removida automaticamente ao ser clicada
            .build(); // Cria efetivamente a notificação
    }
}