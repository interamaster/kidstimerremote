package com.mio.jrdv.kidstimerremote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static String KIDFireBaseUID;
    public static String KID2FireBaseUID;

    private boolean Kid1YAtieneUID;
    private boolean Kid2YAtieneUID;
    //Referencing EditText placed inside in xml layout file
    TextView ChildrenName;

    //para el dialog del nombe

    final Context c = this;

    //para generar codigoi

    private int Horas;
    private int minutes;

    private ImageView MiFotoHijo1;
    private ImageView MiFotoHijo2;

    //para los timepos

    private ImageView generate15min;
    private ImageView generate30min;
    private ImageView generate60min;
    private ImageView generate3horas;

    private String TAGKid;


    //para el sonido del CODIGO
    private MediaPlayer mp;

    //para evitar un doble click rapido

    private long mLastClickTime = 0;


    //para la aniamcion:

    private static final int ANIMATION_DURATION = 1500;
    private static final int ANIMATION_OFFSET = 250;


    //para el password por si falla el remote:
    private TextView Password;

    //PARA LA INTRO HELP

    TapTargetSequence sequence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //To hide AppBar for fullscreen.
        ActionBar ab = getSupportActionBar();
        ab.hide();


        //allow network in maintrerad:

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        //intro help

        introHelp();
        //info:

        //https://github.com/AndreiD/Spring-Android-Push-Notifications-FCM-
        //https://github.com/MOSDEV82/fcmhelper


        //Referencing EditText widgets and Button placed inside in xml layout file
        ChildrenName = (TextView) findViewById(R.id.txtname_check_children);
        //por defecto no tine uid que lo saque de PREFS

        Kid1YAtieneUID = false;
        Kid2YAtieneUID = false;


        //para la foto:

        MiFotoHijo1 = (ImageView) findViewById(R.id.kidsreg1);
        MiFotoHijo2 = (ImageView) findViewById(R.id.kidsreg2);

        //AÑADIMO LISTENER PARA EL LONGCLICK:

        MiFotoHijo1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Kid1YAtieneUID = false;


                EnviarKid1(v);


                return false;
            }
        });


        MiFotoHijo2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Kid2YAtieneUID = false;


                EnviarKid2(v);


                return false;
            }
        });


        //para los timepos en XMLÑ salen sin redeondear ?¿?¿


        generate15min = (ImageView) findViewById(R.id.min15pass);
        generate30min = (ImageView) findViewById(R.id.min30pass);
        generate60min = (ImageView) findViewById(R.id.hora1pass);
        generate3horas = (ImageView) findViewById(R.id.hora3pass);


        //losponemos desde resources

        generate15min.setImageResource(R.drawable.icon_15_minutes);
        generate30min.setImageResource(R.drawable.icon_30_minutes);
        generate60min.setImageResource(R.drawable.icon_45_minutes);
        generate3horas.setImageResource(R.drawable.icon_3_horas);


        //CHCEQEUAMOS SI YA EXISTEN LOS DATOS DE LOS KIDS O NO

        String Fotokid1path = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1, "NONE");//por defecto vale 0

        if (!Fotokid1path.equals("NONE")) {
            Kid1YAtieneUID = true;
        }

        String kidname = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2, "NONE");//por defecto vale 0

        if (!kidname.equals("NONE")) {
            Kid2YAtieneUID = true;
        }


        //para el passw


        Password = (TextView) findViewById(R.id.secretCodetxt);

    }

    public void introHelp(){

        ///////////////////////////////////////INTRO HELP/////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////


        sequence = new TapTargetSequence(this)
                .targets(


                        // TapTarget.forView(findViewById(R.id.txtname_check_children),"Enter kid Name","Enter your kid Name manually."),

                        TapTarget.forView(findViewById(R.id.emailbutton), getString(R.string.info0titu), getString(R.string.info0)),


                        //TapTarget.forView(findViewById(R.id.secretCodetxt),"EXTRA TIME CODE","Make sure your kid`s name is the same you generate the code, and also that your device and your kid's device have the same TIME(HOUR AND MINUTES)!!!. \n\nOnce generated you have to enter it on your kid's device in less than 30 secs!!(Don`t worry you can generate it again just clicking again))")

                        TapTarget.forView(findViewById(R.id.secretCodetxt), getString(R.string.info1titu), getString(R.string.info1))

                                .transparentTarget(true)
                                .outerCircleColor(R.color.colorAccent),


                        //  TapTarget.forView(findViewById(R.id.kidsreg1),"Enter your kid Name and picture","Make Sure the name is the same as introduced in KIDSTIMER FREE!!.This way you only have to click on the image to generate the code.Long Press to change!!")


                        TapTarget.forView(findViewById(R.id.kidsreg1), getString(R.string.info2titu), getString(R.string.info2))
                                .outerCircleColor(R.color.color_blue)
                                .targetCircleColor(R.color.color_negro)
                                .titleTextColor(R.color.color_negro)
                                .transparentTarget(true),

                        //TapTarget.forView(findViewById(R.id.hora1pass),"Generate EXTRA TIME CODES!!","After selecting Kid(or enter manually the name) click on the desired time to generate EXTRA TIME CODES!!.")

                        TapTarget.forView(findViewById(R.id.hora1pass), getString(R.string.info3titu), getString(R.string.info3))
                                .outerCircleColor(R.color.color_white)
                                .targetCircleColor(R.color.colorPrimary)
                                .titleTextColor(R.color.color_negro),

                        // TapTarget.forView(findViewById(R.id.txtname_check_children),"Enter kid Name","Enter your kid Name manually."),


                        //TapTarget.forView(findViewById(R.id.angry_btn),"GAME OVER","This CODE will disable till midnight your kid's device(unless you enter a new EXTRA TIME CODE.)")

                        TapTarget.forView(findViewById(R.id.angry_btn), getString(R.string.info5titu), getString(R.string.info5))
                                .transparentTarget(true)
                                .outerCircleColor(R.color.colorAccent)

                ).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {

                        // editor.putBoolean("finished",true);
                        // editor.commit();
                        //idem con mi class:
                        // Myapplication.preferences.edit().putString(Myapplication.PREF_NAME_KID1,userInputDialogEditText.getText().toString()).commit();

                        Myapplication.preferences.edit().putBoolean("finished", true).commit();
                        //2º)la foto


                    }

                    @Override
                    public void onSequenceCanceled() {

                        //  editor.putBoolean("finished",true);
                        // editor.commit();
                        //idem con mi class:
                        Myapplication.preferences.edit().putBoolean("finished", true).commit();
                    }
                });

        // boolean isSequenceFinished = sharedPref.getBoolean("finished",false);

        //idem con mi class:
        // String Fotokid1path = Myapplication.preferences.getString(Myapplication.PREF_FOTO_PATH_KID1,"NONE");//por defecto vale 0
        boolean isSequenceFinished = Myapplication.preferences.getBoolean("finished", false);//por defecto vale 0


        if (!isSequenceFinished) {//TODO poner a !isSequenceFinished para funcionamineto normal

            sequence.start();

        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////


    }

    private void enviarNotificaction(String EXTRATIME, String KIDUID) {


        //cresamo el helper

        FCMHelper fcm = FCMHelper.getInstance();

        //JsonObject notificationObject = new JsonObject(); notificationObject.addProperty("KEY", "VALUE"); // See GSON-Reference


        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("EXTRATIME", EXTRATIME); // 1 HORA
        dataObject.addProperty("body", "TUS PADRES TE DAN MAS TIEMPO");
        dataObject.addProperty("title", "TE ACABAN DE DAR TIEMPO EXTRA");
        // String RECIPIENT="cF_GGomFh10:APA91bGBRA8W7VlJ5xK8JjU8mUJ1vujnc1XlJhZTix5Xm39o4zwFCv8KfE0YxshtzTdBJ4hUDGXuJW9WIm8XOf9tzhBzJ5X1wf_v-UDXcrCJ5MBS648qPJoh-1cko5NemJvu8ySm20Wo";//yo!!

        String RECIPIENT = KIDUID;

        try {
            //  fcm.sendNotification(FCMHelper.TYPE_TO, RECIPIENT, notificationObject); // RECIPIENT is the token of the device, or device group, or a topic.

            //PARA MANDAR NOTIFICATION Y DATA:
            // For Example to send notification and data
            //  fcm.sendNotifictaionAndData(FCMHelper.TYPE_TO, RECIPIENT, notificationObject, dataObject);


            //SOLO DATA:

            fcm.sendData(FCMHelper.TYPE_TO, RECIPIENT, dataObject);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void EnviarKid1(View view) {




/*
        String Fotokid1path = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1,"NONE");//por defecto vale 0

        if (!Fotokid1path.equals("NONE")){
            Kid1YAtieneUID=true;
        }
*/


        if (!Kid1YAtieneUID) {
            //1º dialog para nonbre


            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
            View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
            alertDialogBuilderUserInput.setView(mView);


            final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {

                            //Do nothing here because we override this button later to change the close behaviour.
                            //However, we still need this because on older versions of Android unless we
                            //pass a handler the button doesn't get instantiated

                        }
                    })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            dialogBox.cancel();
                            Kid1YAtieneUID = true;
                        }
                    });


            final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();

            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...


                    //1º)chequeamos si el nombre si esta bien

                    if (userInputDialogEditText.getText().toString().isEmpty() || userInputDialogEditText.getText().toString().length() < 4 || userInputDialogEditText.getText().toString().length() > 8) {

                        //esta mal el nombre
                        // dialogBox.cancel();

                        //en vez de cancel mejor le avisamos:
                        userInputDialogEditText.setError("min 4 and max 8 characteres");
                    } else {

                        //1º)guaradmos el nombre

                        Myapplication.preferences.edit().putString(Myapplication.PREF_NAME_KID1, userInputDialogEditText.getText().toString()).commit();
                        //2º)la foto


                        Log.d("INFO", "tomando nombre kid 1: " + userInputDialogEditText.getText().toString());

                        TAGKid = "KID1";


                        //ponemos a true para que se cierre:
                        wantToCloseDialog = true;
                    }

                    if (wantToCloseDialog)
                        alertDialogAndroid.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.


                    MeterUIDKid();
                }
            });


        } else {

            //ya tine foto ponemos elm nombre ene l edittext

            //ChildrenName.setText("INMA");

            //idem con pref

            String kid1NMameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1, "NONE");//por defecto vale 0

            ChildrenName.setText(kid1NMameFromPref);

        }


    }

    private void MeterUIDKid() {


        switch (TAGKid) {


            case "KID1":


                //solicitamos el UID

                //1º dialog para nonbre


                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box2, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput.setView(mView);


                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                //Do nothing here because we override this button later to change the close behaviour.
                                //However, we still need this because on older versions of Android unless we
                                //pass a handler the button doesn't get instantiated

                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                                Kid1YAtieneUID = true;
                            }
                        });


                final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();

                //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean wantToCloseDialog = false;
                        //Do stuff, possibly set wantToCloseDialog to true then...

                        Log.d("INFO", "tomando UID kid 1: " + userInputDialogEditText.getText().toString());

                        Kid1YAtieneUID = true;
                        //  guardamos el path en pref

                        Myapplication.preferences.edit().putString(Myapplication.PREF_UID_KID1, userInputDialogEditText.getText().toString()).commit();


                        String kid1NMameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1, "NONE");//por defecto vale 0

                        ChildrenName.setText(kid1NMameFromPref);


                        alertDialogAndroid.dismiss();
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.

                    }
                });


                break;

            case "KID2":

                //solicitamos el UID

                //1º dialog para nonbre


                LayoutInflater layoutInflaterAndroid2 = LayoutInflater.from(c);
                View mView2 = layoutInflaterAndroid2.inflate(R.layout.user_input_dialog_box2, null);
                AlertDialog.Builder alertDialogBuilderUserInput2 = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput2.setView(mView2);


                final EditText userInputDialogEditText2 = (EditText) mView2.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput2
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                //Do nothing here because we override this button later to change the close behaviour.
                                //However, we still need this because on older versions of Android unless we
                                //pass a handler the button doesn't get instantiated

                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                                Kid2YAtieneUID = true;
                            }
                        });


                final AlertDialog alertDialogAndroid2 = alertDialogBuilderUserInput2.create();
                alertDialogAndroid2.show();

                //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                alertDialogAndroid2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean wantToCloseDialog = false;
                        //Do stuff, possibly set wantToCloseDialog to true then...

                        Log.d("INFO", "tomando UID kid 2: " + userInputDialogEditText2.getText().toString());

                        Kid2YAtieneUID = true;
                        //  guardamos el path en pref

                        Myapplication.preferences.edit().putString(Myapplication.PREF_UID_KID2, userInputDialogEditText2.getText().toString()).commit();


                        String kid2NMameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2, "NONE");//por defecto vale 0

                        ChildrenName.setText(kid2NMameFromPref);
                        alertDialogAndroid2.dismiss();
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.

                    }
                });


                break;

            default:
                break;
        }

    }


    public void EnviarKid2(View view) {


        if (!Kid2YAtieneUID) {
            //1º dialog para nonbre


            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
            View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
            alertDialogBuilderUserInput.setView(mView);


            final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {

                            //Do nothing here because we override this button later to change the close behaviour.
                            //However, we still need this because on older versions of Android unless we
                            //pass a handler the button doesn't get instantiated

                        }
                    })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            dialogBox.cancel();
                            Kid2YAtieneUID = true;
                        }
                    });


            final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();

            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...


                    //1º)chequeamos si el nombre si esta bien

                    if (userInputDialogEditText.getText().toString().isEmpty() || userInputDialogEditText.getText().toString().length() < 4 || userInputDialogEditText.getText().toString().length() > 8) {

                        //esta mal el nombre
                        // dialogBox.cancel();

                        //en vez de cancel mejor le avisamos:
                        userInputDialogEditText.setError("min 4 and max 8 characteres");
                    } else {

                        //1º)guaradmos el nombre

                        Myapplication.preferences.edit().putString(Myapplication.PREF_NAME_KID2, userInputDialogEditText.getText().toString()).commit();
                        //2º)la foto


                        Log.d("INFO", "tomando nombre kid 2: " + userInputDialogEditText.getText().toString());

                        TAGKid = "KID2";


                        //ponemos a true para que se cierre:
                        wantToCloseDialog = true;
                    }

                    if (wantToCloseDialog)
                        alertDialogAndroid.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.


                    MeterUIDKid();
                }
            });


        } else {

            //ya tine foto ponemos elm nombre ene l edittext

            //ChildrenName.setText("INMA");

            //idem con pref

            String kid1NMameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2, "NONE");//por defecto vale 0

            ChildrenName.setText(kid1NMameFromPref);

        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////GENERAR CODES/////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void generar15minCode(View view) {


///////////////////////////////////////////////////////////////////
///////////////para evitar dobles clicks rapidos //////////////
///////////////////////////////////////////////////////////////////


        // mis -clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // do your magic here . . . .


        //1º chequeamos que tien nombre y esta bien:

        if (ChildrenName.getText().toString().isEmpty() || ChildrenName.getText().toString().length() < 4 || ChildrenName.getText().toString().length() > 10) {

            //no hay nombre o no esta bien
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must insert a valid Kid Name(or click picture if already inserted new kid)");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            ChildrenName.setError("min 4 and max 8 characteres");


        } else {


            //1º)ponemos el codigo visual:

            Password.setText(generaNumeroClave("15min"));

            //Y LO ANIMAMOS

            final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce); // Use bounce interpolator with amplitude 0.2 and frequency 20
            BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
            myAnim.setInterpolator(interpolator);
            Password.startAnimation(myAnim);


            //2º)vamos aver cual es:
            //si hay un nombre vamos aver cual:

            //EJEMPLO:
        /*
        KIDFireBaseUID="cF_GGomFh10:APA91bGBRA8W7VlJ5xK8JjU8mUJ1vujnc1XlJhZTix5Xm39o4zwFCv8KfE0YxshtzTdBJ4hUDGXuJW9WIm8XOf9tzhBzJ5X1wf_v-UDXcrCJ5MBS648qPJoh-1cko5NemJvu8ySm20Wo";//yo!!
        String TIME="3600000";
        enviarNotificaction(TIME, RECIPIENT);
        */

            String TIME = "900000";
            String kid1NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1, "NONE");//por defecto vale 0
            String kid2NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2, "NONE");//por defecto vale 0


            if (ChildrenName.getText().toString() == kid1NameFromPref) {
                //es kid1


                // lo enviamos al kid 1
                String kid1UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID1, "NONE");//por defecto vale 0


                //es kid1 y 15 min!!!
                flyEnvelope("kidsreg1", "min15pass");


                enviarNotificaction(TIME, kid1UIDFromPref);


                SuenaalGenerar();

            } else if (ChildrenName.getText().toString() == kid2NameFromPref) {
                //es kid2


                // lo enviamos al kid 2
                String kid2UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID2, "NONE");//por defecto vale 0


                //es kid2 y 15 min!!!
                flyEnvelope("kidsreg2", "min15pass");

                enviarNotificaction(TIME, kid2UIDFromPref);


                SuenaalGenerar();

            }


        }


    }//fin


    public void generar30minCode(View view) {


///////////////////////////////////////////////////////////////////
///////////////para evitar dobles clicks rapidos //////////////
///////////////////////////////////////////////////////////////////


        // mis -clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // do your magic here . . . .


        //1º chequeamos que tien nombre y esta bien:

        if (ChildrenName.getText().toString().isEmpty() || ChildrenName.getText().toString().length() < 4 || ChildrenName.getText().toString().length() > 10) {

            //no hay nombre o no esta bien
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must insert a valid Kid Name(or click picture if already inserted new kid)");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            ChildrenName.setError("min 4 and max 8 characteres");


        } else {

            //1º)ponemos el codigo visual:

            Password.setText(generaNumeroClave("30min"));

            //Y LO ANIMAMOS

            final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce); // Use bounce interpolator with amplitude 0.2 and frequency 20
            BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
            myAnim.setInterpolator(interpolator);
            Password.startAnimation(myAnim);


            //2º)vamos aver cual es:
            //si hay un nombre vamos aver cual:

            //EJEMPLO:
        /*
        KIDFireBaseUID="cF_GGomFh10:APA91bGBRA8W7VlJ5xK8JjU8mUJ1vujnc1XlJhZTix5Xm39o4zwFCv8KfE0YxshtzTdBJ4hUDGXuJW9WIm8XOf9tzhBzJ5X1wf_v-UDXcrCJ5MBS648qPJoh-1cko5NemJvu8ySm20Wo";//yo!!
        String TIME="3600000";
        enviarNotificaction(TIME, RECIPIENT);
        */

            String TIME = "1800000";
            String kid1NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1, "NONE");//por defecto vale 0
            String kid2NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2, "NONE");//por defecto vale 0


            if (ChildrenName.getText().toString() == kid1NameFromPref) {
                //es kid1


                // lo enviamos al kid 1
                String kid1UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID1, "NONE");//por defecto vale 0


                //es kid1 y 15 min!!!
                flyEnvelope("kidsreg1", "min30pass");

                enviarNotificaction(TIME, kid1UIDFromPref);


                SuenaalGenerar();

            } else if (ChildrenName.getText().toString() == kid2NameFromPref) {
                //es kid2


                // lo enviamos al kid 2
                String kid2UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID2, "NONE");//por defecto vale 0


                //es kid2 y 15 min!!!
                flyEnvelope("kidsreg2", "min30pass");
                enviarNotificaction(TIME, kid2UIDFromPref);


                SuenaalGenerar();

            }


        }

    }

    public void generar1HORACode(View view) {


///////////////////////////////////////////////////////////////////
///////////////para evitar dobles clicks rapidos //////////////
///////////////////////////////////////////////////////////////////


        // mis -clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // do your magic here . . . .


        //1º chequeamos que tien nombre y esta bien:

        if (ChildrenName.getText().toString().isEmpty() || ChildrenName.getText().toString().length() < 4 || ChildrenName.getText().toString().length() > 10) {

            //no hay nombre o no esta bien
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must insert a valid Kid Name(or click picture if already inserted new kid)");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            ChildrenName.setError("min 4 and max 8 characteres");


        } else {


            //1º)ponemos el codigo visual:

            Password.setText(generaNumeroClave("1HORA"));

            //Y LO ANIMAMOS

            final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce); // Use bounce interpolator with amplitude 0.2 and frequency 20
            BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
            myAnim.setInterpolator(interpolator);
            Password.startAnimation(myAnim);


            //2º)vamos aver cual es:
            //si hay un nombre vamos aver cual:

            //EJEMPLO:
        /*
        KIDFireBaseUID="cF_GGomFh10:APA91bGBRA8W7VlJ5xK8JjU8mUJ1vujnc1XlJhZTix5Xm39o4zwFCv8KfE0YxshtzTdBJ4hUDGXuJW9WIm8XOf9tzhBzJ5X1wf_v-UDXcrCJ5MBS648qPJoh-1cko5NemJvu8ySm20Wo";//yo!!
        String TIME="3600000";
        enviarNotificaction(TIME, RECIPIENT);
        */

            String TIME = "3600000";
            String kid1NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1, "NONE");//por defecto vale 0
            String kid2NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2, "NONE");//por defecto vale 0


            if (ChildrenName.getText().toString() == kid1NameFromPref) {
                //es kid1


                // lo enviamos al kid 1
                String kid1UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID1, "NONE");//por defecto vale 0

                //es kid1 y 60 min!!!
                flyEnvelope("kidsreg1", "hora1pass");
                enviarNotificaction(TIME, kid1UIDFromPref);

                SuenaalGenerar();

            } else if (ChildrenName.getText().toString() == kid2NameFromPref) {
                //es kid2


                // lo enviamos al kid 2
                String kid2UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID2, "NONE");//por defecto vale 0

                //es kid2 y 60 min!!!
                flyEnvelope("kidsreg2", "hora1pass");
                enviarNotificaction(TIME, kid2UIDFromPref);


                SuenaalGenerar();

            }


        }

    }

    public void generar3HORASCode(View view) {


///////////////////////////////////////////////////////////////////
///////////////para evitar dobles clicks rapidos //////////////
///////////////////////////////////////////////////////////////////


        // mis -clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // do your magic here . . . .


        //1º chequeamos que tien nombre y esta bien:

        if (ChildrenName.getText().toString().isEmpty() || ChildrenName.getText().toString().length() < 4 || ChildrenName.getText().toString().length() > 10) {

            //no hay nombre o no esta bien
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must insert a valid Kid Name(or click picture if already inserted new kid)");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            ChildrenName.setError("min 4 and max 8 characteres");


        } else {


            //1º)ponemos el codigo visual:

            Password.setText(generaNumeroClave("3HORAS"));

            //Y LO ANIMAMOS

            final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce); // Use bounce interpolator with amplitude 0.2 and frequency 20
            BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
            myAnim.setInterpolator(interpolator);
            Password.startAnimation(myAnim);


            //2º)vamos aver cual es:
            //si hay un nombre vamos aver cual:

            //EJEMPLO:
        /*
        KIDFireBaseUID="cF_GGomFh10:APA91bGBRA8W7VlJ5xK8JjU8mUJ1vujnc1XlJhZTix5Xm39o4zwFCv8KfE0YxshtzTdBJ4hUDGXuJW9WIm8XOf9tzhBzJ5X1wf_v-UDXcrCJ5MBS648qPJoh-1cko5NemJvu8ySm20Wo";//yo!!
        String TIME="3600000";
        enviarNotificaction(TIME, RECIPIENT);
        */

            String TIME = "10800000";
            String kid1NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1, "NONE");//por defecto vale 0
            String kid2NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2, "NONE");//por defecto vale 0


            if (ChildrenName.getText().toString() == kid1NameFromPref) {
                //es kid1


                // lo enviamos al kid 1
                String kid1UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID1, "NONE");//por defecto vale 0

                //es kid1 y 3 horas !!!
                flyEnvelope("kidsreg1", "hora3pass");

                enviarNotificaction(TIME, kid1UIDFromPref);
                SuenaalGenerar();

            } else if (ChildrenName.getText().toString() == kid2NameFromPref) {
                //es kid2


                // lo enviamos al kid 2
                String kid2UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID2, "NONE");//por defecto vale 0


                //es kid2 y 3 horas !!!
                flyEnvelope("kidsreg2", "hora3pass");


                enviarNotificaction(TIME, kid2UIDFromPref);

                SuenaalGenerar();

            }


        }

    }

    public void generarCASTIGOCODE(View view) {


///////////////////////////////////////////////////////////////////
///////////////para evitar dobles clicks rapidos //////////////
///////////////////////////////////////////////////////////////////


        // mis -clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // do your magic here . . . .


        //1º chequeamos que tien nombre y esta bien:

        if (ChildrenName.getText().toString().isEmpty() || ChildrenName.getText().toString().length() < 4 || ChildrenName.getText().toString().length() > 10) {

            //no hay nombre o no esta bien
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must insert a valid Kid Name(or click picture if already inserted new kid)");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            ChildrenName.setError("min 4 and max 8 characteres");


        } else {


            //1º)ponemos el codigo visual:

            Password.setText(generaNumeroClave("CASTIGO"));

            //Y LO ANIMAMOS

            final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce); // Use bounce interpolator with amplitude 0.2 and frequency 20
            BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
            myAnim.setInterpolator(interpolator);
            Password.startAnimation(myAnim);


            //2º)vamos aver cual es:
            //si hay un nombre vamos aver cual:

            //EJEMPLO:
        /*
        KIDFireBaseUID="cF_GGomFh10:APA91bGBRA8W7VlJ5xK8JjU8mUJ1vujnc1XlJhZTix5Xm39o4zwFCv8KfE0YxshtzTdBJ4hUDGXuJW9WIm8XOf9tzhBzJ5X1wf_v-UDXcrCJ5MBS648qPJoh-1cko5NemJvu8ySm20Wo";//yo!!
        String TIME="3600000";
        enviarNotificaction(TIME, RECIPIENT);
        */

            String TIME = "1";//TODO codigo de castigo es 1!!!
            String kid1NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1, "NONE");//por defecto vale 0
            String kid2NameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2, "NONE");//por defecto vale 0


            if (ChildrenName.getText().toString() == kid1NameFromPref) {
                //es kid1


                // lo enviamos al kid 1
                String kid1UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID1, "NONE");//por defecto vale 0

                enviarNotificaction(TIME, kid1UIDFromPref);
                SuenaalGenerar();

            } else if (ChildrenName.getText().toString() == kid2NameFromPref) {
                //es kid2


                // lo enviamos al kid 1
                String kid2UIDFromPref = Myapplication.preferences.getString(Myapplication.PREF_UID_KID2, "NONE");//por defecto vale 0

                enviarNotificaction(TIME, kid2UIDFromPref);

                SuenaalGenerar();

            }


        }

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////SONIDOS///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void SuenaalGenerar() {


        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        vibrator.vibrate(300); // vibrate for 0.3 seconds (e.g 300 milliseconds)


        int sonidomp3 = getResourceID("fly", "raw", getApplicationContext());
        mp = MediaPlayer.create(MainActivity.this, sonidomp3);
        mp.start();

    }


    protected final static int getResourceID(final String resName, final String resType, final Context ctx) {
        final int ResourceID = ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {


            //en vez de una excepcion que lo ponga en el log solo

            Log.e("INFO", "ojo no existe el resource: " + resName);
            return 0;


        } else {
            return ResourceID;
        }


    }


    /**
     * Performs the flying animation of the envelope.
     */
    public void flyEnvelope(String Kid1or2, String minutos) {


        // View botontimepovolar = findViewById(R.id.min15pass);

        //  View botonkidvolar = findViewById(R.id.kidsreg1);


        View botontimepovolar = findViewById(getResources().getIdentifier(minutos, "id", getPackageName()));

        View botonkidvolar = findViewById(getResources().getIdentifier(Kid1or2, "id", getPackageName()));


        int duration = ANIMATION_DURATION;
        RotateAnimation rotationAnimation = new RotateAnimation(0, 360, botontimepovolar.getWidth() / 2, botontimepovolar.getHeight() / 2);
        rotationAnimation.setDuration(duration);

        RotateAnimation rotationAnimation2 = new RotateAnimation(360, 0, botonkidvolar.getWidth() / 2, botonkidvolar.getHeight() / 2);
        rotationAnimation2.setDuration(duration);


        AnimationSet set = new AnimationSet(this, null);

        set.addAnimation(rotationAnimation);

        AnimationSet set2 = new AnimationSet(this, null);

        set2.addAnimation(rotationAnimation2);


        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // onEmailFlew();
                Log.d("INFO", "fin animacion");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        botontimepovolar.startAnimation(set);
        botonkidvolar.startAnimation(set2);


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////GENERAR CODE///////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String generaNumeroClave(String tiempo) {

        Calendar c = Calendar.getInstance();
        Horas = c.get(Calendar.HOUR_OF_DAY);//formato 24h
        minutes = c.get(Calendar.MINUTE);

        String clave = String.format("%02d", Horas) + String.format("%02d", minutes);
        Log.d("INFO", "el numero secreto sin invertir es: " + clave);

        clave = new StringBuilder(clave).reverse().toString();
        Log.d("INFO", "el numero secreto YA INVERTIDO  es: " + clave);

        //vasmoa a acompañarlo del nombre

        String NombreNino = ChildrenName.getText().toString();

        //y del tiempo:

        NombreNino = NombreNino + tiempo;

        NombreNino.equalsIgnoreCase(NombreNino);

        byte[] bytes = new byte[0];
        try {
            bytes = NombreNino.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            NombreNino = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int sumanombreniuno = 0;

        for (int i : bytes)
            sumanombreniuno += i;

        //1º) le restamos al numero invertido 1000 si se puede si no se deja (minimo sera las 01:00-->10)
        int numeroCalveFinalenInt = Integer.parseInt(clave);

        if (numeroCalveFinalenInt > 2000) {
            numeroCalveFinalenInt = numeroCalveFinalenInt - 1000;

        }

        //le sumamos el valor del nombre:
        numeroCalveFinalenInt = numeroCalveFinalenInt + sumanombreniuno;

        //lo convertimos en string

        clave = String.valueOf(numeroCalveFinalenInt);

        Log.d("INFO", "el numero secreto YA INVERTIDO  Y codificado con la suma es: " + clave);


        if (numeroCalveFinalenInt < 1000) {
            //si es menor de 1000 que le ponga un cero a la izqda

            clave = "0" + clave;


        }
        //lo devolvemos

        return clave;

    }


    public void EmailPulsado(View view) {


        //TODO PONER EMAIL CORRECTO
        String emailTo="jrdvsoft@gmail.com";
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
       // emailIntent.setType("text/plain");
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,   new String[]{ emailTo});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,  getString(R.string.emailtitu));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.emailcampo));


        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent,
                    "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "No email clients installed.",
                    Toast.LENGTH_SHORT).show();
        }

    }


    public void InfoPulsado(View view) {

        Myapplication.preferences.edit().putBoolean("finished", false).commit();

        introHelp();
    }
}
