package com.example.bestphotocollections.MessagingActivities.DirectMessage;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bestphotocollections.Model.ModelChatMessages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Presenter implements Contract.Presenter {
    Contract.View mainView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/");
    String rightUid ;
    ArrayList<ModelChatMessages> list;

    public Presenter(Contract.View mainView) { this.mainView = mainView; }


    @Override
    public void sendMessage(String message, String oppositePersonUid) {
        ModelChatMessages model = new ModelChatMessages(true,false,message);
        list.add(model);
        mainView.notifyAdapterChange(list);

        CheckForRightUid(oppositePersonUid,2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("datachanged","outside*"+rightUid);
                if(rightUid ==null)
                    mainView.showToast("Sorry! Some error Occurred.");
                else {
                    String key = databaseReference.push().getKey();
                    if (rightUid.equals(oppositePersonUid)) {
                        databaseReference.child(rightUid + "/chatMessages/" + FirebaseAuth.getInstance().getUid() + "/"
                                + key + "/" + FirebaseAuth.getInstance().getUid()).setValue(message);
                        SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm:ss a");
                        String time = formatDate.format(Calendar.getInstance().getTime()).toString();
                        databaseReference.child(rightUid + "/chatMessages/" + FirebaseAuth.getInstance().getUid() + "/"
                                + key + "/time").setValue(time);
                    }
                    else if (rightUid.equals(FirebaseAuth.getInstance().getUid())) {
                        databaseReference.child(rightUid + "/chatMessages/" + oppositePersonUid + "/"
                                + key + "/" + FirebaseAuth.getInstance().getUid()).setValue(message);
                        SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm:ss a");
                        String time = formatDate.format(Calendar.getInstance().getTime()).toString();
                        databaseReference.child(rightUid + "/chatMessages/" + oppositePersonUid + "/"
                                + key + "/time").setValue(time);
                    }
                }
            }
        }, 2000);
    }

    private void CheckForRightUid(String oppositePersonUid ,int check) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("datachanged","entered");
                if(dataSnapshot.hasChild(oppositePersonUid+"/chatMessages/"+ FirebaseAuth.getInstance().getUid())){
                    rightUid = oppositePersonUid;
                    Log.d("datachanged","if*"+rightUid);
                }
                else if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getUid()+"/chatMessages/"+ oppositePersonUid)){
                    rightUid = FirebaseAuth.getInstance().getUid();
                    Log.d("datachanged","if*"+rightUid);
                }
                else{
                    if (check==1)
                        rightUid=null;
                    else if (check==2)
                        rightUid = FirebaseAuth.getInstance().getUid();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mainView.showToast(""+databaseError.getMessage());
            }
        });
    }

    @Override
    public void loadMessages(String oppositePersonUid, String name, String profilePicUri, boolean isPreviousMsg) {
        list = new ArrayList<>();
        mainView.loadRecyclerView(list);
        CheckForRightUid(oppositePersonUid,1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String uid = null;
                Log.d("ahdvhcjsd",""+rightUid);
                if (rightUid!=null && rightUid.equals(oppositePersonUid))
                    uid = FirebaseAuth.getInstance().getUid();
                else if (rightUid!=null && rightUid.equals(FirebaseAuth.getInstance().getUid()))
                    uid = oppositePersonUid;
                if(uid!=null) {
                    databaseReference.child(rightUid + "/chatMessages/" + uid)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    list.clear();
                                    for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()){
                                        ModelChatMessages model = new ModelChatMessages();
                                        if (groupSnapshot.hasChild(FirebaseAuth.getInstance().getUid()))
                                        model.setMyMessage(true);
                                        Log.d("MeraMessge h ya nehi",""+model.isMyMessage());
                                        if (model.isMyMessage()) {
                                            model.setDelivered(true);
                                            model.setMessages(groupSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue().toString());
                                        }
                                        else {
                                            model.setMessages(groupSnapshot.child(oppositePersonUid).getValue().toString());
                                            model.setName(name);
                                            model.setProfileUri(profilePicUri);
                                        }
                                        if (groupSnapshot.child("time").getValue()!=null)
                                        model.setTime(groupSnapshot.child("time").getValue().toString());

                                        list.add(model);
                                    }
                                    Log.d("listPrint",list.toString());
                                    mainView.notifyAdapterChange(list);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mainView.showToast(""+databaseError.getMessage());
                                }
                            });
                }
                else
                    mainView.userHaveNoMessages();
            }
        }, 2000);
    }
}
