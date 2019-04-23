package net.kerod.android.questionbank.utility;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;

import net.kerod.android.questionbank.manager.SettingsManager;
import net.kerod.android.questionbank.model.AppUser;
import net.kerod.android.questionbank.model.Exam;
import net.kerod.android.questionbank.model.Instruction;
import net.kerod.android.questionbank.model.Question;
import net.kerod.android.questionbank.model.UserAttempt;
import net.kerod.android.questionbank.model.UserAttemptSummary;
import net.kerod.android.questionbank.model.UserNote;

import androidx.annotation.NonNull;

public class FirestoreMigrator {

    public static void backUpInstruction() {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Instruction.TAG.toLowerCase());
        final CollectionReference mCollectionReference = Instruction.getCollectionReference();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot current : dataSnapshot.getChildren()) {
                    Instruction instruction = current.getValue(Instruction.class);

                    mCollectionReference
                            .document(Instruction.createDocumentUid())
                            .set(instruction)
                            .addOnCompleteListener(task -> {
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static void backUpUserAttemptSummary() {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("antibiogram");
        final CollectionReference mCollectionReference = UserAttemptSummary.getCollectionReference();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot current : dataSnapshot.getChildren()) {
                    UserAttemptSummary userAttemptSummary = current.getValue(UserAttemptSummary.class);

                    mCollectionReference
                            .document(UserAttemptSummary.createDocumentUid())
                            .set(userAttemptSummary)
                            .addOnCompleteListener(task -> {
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static void backUpAppUser() {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("antibiogram");
        final CollectionReference mCollectionReference = AppUser.getCollectionReference();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot current : dataSnapshot.getChildren()) {
                    AppUser appUser = current.getValue(AppUser.class);

                    mCollectionReference
                            .document(AppUser.createDocumentUid())
                            .set(appUser)
                            .addOnCompleteListener(task -> {
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static void backUpExam() {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("antibiogram");
        final CollectionReference mCollectionReference = Exam.getCollectionReference();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot current : dataSnapshot.getChildren()) {
                    Exam exam = current.getValue(Exam.class);

                    mCollectionReference
                            .document(Exam.createDocumentUid())
                            .set(exam)
                            .addOnCompleteListener(task -> {
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public static void backUpQuestion() {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("antibiogram");
        final CollectionReference mCollectionReference = Question.getCollectionReference();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot current : dataSnapshot.getChildren()) {
                    Question question = current.getValue(Question.class);

                    mCollectionReference
                            .document(Question.createDocumentUid())
                            .set(question)
                            .addOnCompleteListener(task -> {
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void backUpUserNote() {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(UserNote.TAG.toLowerCase());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot current : dataSnapshot.getChildren()) {
                    UserNote userNote = current.getValue(UserNote.class);
                    final CollectionReference mCollectionReference = UserNote.getCollectionReference(SettingsManager.getUserUid(), userNote.getExamShortName());

                    mCollectionReference
                            .document(UserNote.createDocumentUid())
                            .set(userNote)
                            .addOnCompleteListener(task -> {
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void backUpUserAttempt() {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("antibiogram");
        //final CollectionReference mCollectionReference = UserAttempt.getCollectionReference();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot current : dataSnapshot.getChildren()) {
                    UserAttempt userAttempt = current.getValue(UserAttempt.class);
                    final CollectionReference mCollectionReference = UserAttempt.getCollectionReference(SettingsManager.getUserUid(), current.getKey());

                    mCollectionReference
                            .document(UserAttempt.createDocumentUid())
                            .set(userAttempt)
                            .addOnCompleteListener(task -> {
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
