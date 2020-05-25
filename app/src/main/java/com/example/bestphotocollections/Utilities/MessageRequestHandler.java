package com.example.bestphotocollections.Utilities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageRequestHandler {

    public static void sendMessageRequest(String from, String to) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/"+ to+"/MessageRequest/");
        databaseReference.child(from).setValue(true);
    }

    public static void cancelRequest(String to, String from) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/"+ to+"/MessageRequest/");
        databaseReference.child(from).removeValue();
    }

    public static void acceptRequest(String to, String from) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/"+ to+"/MessageRequest/");
        databaseReference.child(from).removeValue();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("uploads/"+ to+"/ChatsAvailable/");
        databaseReference1.child(from).setValue(true);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("uploads/"+ from+"/ChatsAvailable/");
        databaseReference2.child(to).setValue(true);
    }
}
