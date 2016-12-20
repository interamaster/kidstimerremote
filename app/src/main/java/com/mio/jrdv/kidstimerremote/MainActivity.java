package com.mio.jrdv.kidstimerremote;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.JsonObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static  String KIDFireBaseUID;
    public static  String KID2FireBaseUID;

    private  boolean Kid1YAtieneUID;
    private  boolean Kid2YAtieneUID;
    //Referencing EditText placed inside in xml layout file
    TextView ChildrenName ;

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

                Kid1YAtieneUID=false;
                EnviarKid1( v);


                return false;
            }
        });


        MiFotoHijo2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Kid2YAtieneUID=false;
                EnviarKid2( v);


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



    }

    private void enviarNotificaction(String EXTRATIME,String KIDUID) {


        //cresamo el helper

        FCMHelper fcm=FCMHelper.getInstance();

        //JsonObject notificationObject = new JsonObject(); notificationObject.addProperty("KEY", "VALUE"); // See GSON-Reference


        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("EXTRATIME", EXTRATIME); // 1 HORA
        dataObject.addProperty("body","TUS PADRES TE DAN MAS TIEMPO");
        dataObject.addProperty("title","TE ACABAN DE DAR TIEMPO EXTRA");
       // String RECIPIENT="cF_GGomFh10:APA91bGBRA8W7VlJ5xK8JjU8mUJ1vujnc1XlJhZTix5Xm39o4zwFCv8KfE0YxshtzTdBJ4hUDGXuJW9WIm8XOf9tzhBzJ5X1wf_v-UDXcrCJ5MBS648qPJoh-1cko5NemJvu8ySm20Wo";//yo!!

        String RECIPIENT=KIDUID;

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
        String Fotokid1path = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1,"NONE");//por defecto vale 0

        if (!Fotokid1path.equals("NONE")){
            Kid1YAtieneUID=true;
        }



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
                        }
                    });


            final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();

            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...


                    //1º)chequeamos si el nombre si esta bien

                    if (userInputDialogEditText.getText().toString().isEmpty() || userInputDialogEditText.getText().toString().length() <4 || userInputDialogEditText.getText().toString().length()>8){

                        //esta mal el nombre
                        // dialogBox.cancel();

                        //en vez de cancel mejor le avisamos:
                        userInputDialogEditText.setError("min 4 and max 8 characteres");
                    }

                    else {

                        //1º)guaradmos el nombre

                        Myapplication.preferences.edit().putString(Myapplication.PREF_NAME_KID1,userInputDialogEditText.getText().toString()).commit();
                        //2º)la foto


                        Log.d("INFO", "tomando nombre kid 1: "+userInputDialogEditText.getText().toString());

                           TAGKid = "KID1";



                        //ponemos a true para que se cierre:
                        wantToCloseDialog=true;
                    }

                    if(wantToCloseDialog)
                        alertDialogAndroid.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.


                    MeterUIDKid();
                }
            });


        }





        else {

            //ya tine foto ponemos elm nombre ene l edittext

            //ChildrenName.setText("INMA");

            //idem con pref

            String kid1NMameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1,"NONE");//por defecto vale 0

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

                        Log.d("INFO", "tomando UID kid 1: "+userInputDialogEditText.getText().toString());

                        Kid1YAtieneUID = true;
                        //  guardamos el path en pref

                        Myapplication.preferences.edit().putString(Myapplication.PREF_UID_KID1, userInputDialogEditText.getText().toString()).commit();



                        String kid1NMameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID1,"NONE");//por defecto vale 0

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

                        Log.d("INFO", "tomando UID kid 2: "+userInputDialogEditText2.getText().toString());

                        Kid1YAtieneUID = true;
                        //  guardamos el path en pref

                        Myapplication.preferences.edit().putString(Myapplication.PREF_UID_KID2, userInputDialogEditText2.getText().toString()).commit();


                        String kid2NMameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2,"NONE");//por defecto vale 0

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


        String kidname = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2,"NONE");//por defecto vale 0

        if (!kidname.equals("NONE")){
             Kid2YAtieneUID=true;
        }



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
                        }
                    });


            final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();

            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...


                    //1º)chequeamos si el nombre si esta bien

                    if (userInputDialogEditText.getText().toString().isEmpty() || userInputDialogEditText.getText().toString().length() <4 || userInputDialogEditText.getText().toString().length()>8){

                        //esta mal el nombre
                        // dialogBox.cancel();

                        //en vez de cancel mejor le avisamos:
                        userInputDialogEditText.setError("min 4 and max 8 characteres");
                    }

                    else {

                        //1º)guaradmos el nombre

                        Myapplication.preferences.edit().putString(Myapplication.PREF_NAME_KID2,userInputDialogEditText.getText().toString()).commit();
                        //2º)la foto


                        Log.d("INFO", "tomando nombre kid 2: "+userInputDialogEditText.getText().toString());

                        TAGKid = "KID2";



                        //ponemos a true para que se cierre:
                        wantToCloseDialog=true;
                    }

                    if(wantToCloseDialog)
                        alertDialogAndroid.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.


                    MeterUIDKid();
                }
            });


        }





        else {

            //ya tine foto ponemos elm nombre ene l edittext

            //ChildrenName.setText("INMA");

            //idem con pref

            String kid1NMameFromPref = Myapplication.preferences.getString(Myapplication.PREF_NAME_KID2,"NONE");//por defecto vale 0

            ChildrenName.setText(kid1NMameFromPref);

        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////GENERAR CODES/////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    public void generar15minCode(View view) {

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


    }//fin


    public void generar30minCode(View view) {


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

    public void generar1HORACode(View view) {

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

    public void generar3HORASCode(View view) {


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

    public void generarCASTIGOCODE(View view) {

        //TODO castigo

    }





    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////SONIDOS///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void SuenaalGenerar() {

        int sonidomp3 = getResourceID("musical002", "raw", getApplicationContext());
        mp = MediaPlayer.create(MainActivity.this, sonidomp3);
        mp.start();

        Vibrator vibrator;
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);


        vibrator.vibrate(300); // vibrate for 0.3 seconds (e.g 300 milliseconds)

    }




    protected final static int getResourceID (final String resName, final String resType, final Context ctx) {
        final int ResourceID =  ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {



            //en vez de una excepcion que lo ponga en el log solo

            Log.e("INFO", "ojo no existe el resource: " + resName);
            return 0;


        } else {
            return ResourceID;
        }


    }

}
