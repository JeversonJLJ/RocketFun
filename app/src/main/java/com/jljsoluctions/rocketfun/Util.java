package com.jljsoluctions.rocketfun;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.jljsoluctions.rocketfun.Fragments.SoundsFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by jever on 02/07/2017.
 */

public class Util {
    private static final int MY_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 2;
    private static int CURRENT_DOWNLOADS;
    public static final String APP_STORAGE_PATCH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RocketFun";
    ;

    public static boolean checkWritePermission(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:com.jljsoluctions.rocketfun"));
                activity.startActivity(intent);
            }
        }
        return true;
    }

    public static boolean checkReadPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        if (file.exists())
            return true;
        else
            return false;

    }

    public static void firebaseDownloadFile(final Activity activity, final StorageReference storageRef, final File file) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (CURRENT_DOWNLOADS >= 5) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    CURRENT_DOWNLOADS++;
                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            CURRENT_DOWNLOADS--;

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            CURRENT_DOWNLOADS--;
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    public static boolean checkWifiConected(Activity activity) {
        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }


    public interface OnAsynchronousFirebaseDownloadFile {
        void onAsynchronousFirebaseDownloadFile(String filePatch, boolean sucess, Exception e);
    }

    public static void asynchronousFirebaseDownloadFile(final Activity activity , final StorageReference storageRef, final File file, final OnAsynchronousFirebaseDownloadFile asynchronousFirebaseDownloadFile ) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (CURRENT_DOWNLOADS >= 5) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    CURRENT_DOWNLOADS++;
                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            CURRENT_DOWNLOADS--;
                            asynchronousFirebaseDownloadFile.onAsynchronousFirebaseDownloadFile(file.getAbsolutePath(), true, null);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            CURRENT_DOWNLOADS--;
                            asynchronousFirebaseDownloadFile.onAsynchronousFirebaseDownloadFile(file.getAbsolutePath(), false, exception);
                        }
                    });
                } catch (Exception e) {
                    asynchronousFirebaseDownloadFile.onAsynchronousFirebaseDownloadFile(file.getAbsolutePath(), false, e);
                }
            }
        }).start();
    }





    /*
    public interface OnSincronizarVendedores {
        void onSincronizarVendedores(Activity contexto, boolean sucesso, Exception e);
    }

    public void sincronizarVendedores(final Activity contexto, final OnSincronizarVendedores sincronizarVendedores) {
        try {
            if (!Internet.VerificarInternet(contexto))
                throw new Exception(" Não será possivel sincronizar os dados pois não foi identificado nenhum tipo de conexão com a internet");
            WebServiceJSON webService = new WebServiceJSON();
            atualizaMensagemProgressoAguardandoWebService(contexto, "Vendedores");
            webService.ObterVendedores(new WebServiceJSON.OnObterVendedoresWebService() {
                @Override
                public void onObterVendedoresWebService(List<Vendedor> vendedores, Exception e) {
                    try {
                        if (e == null && vendedores != null) {
                            Banco mDbHelper = new Banco(contexto);
                            SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            db.execSQL("Delete From " + FeedReaderContract.FeedVendedores.TABLE_NAME);
                            long contadorVendedores = 1;
                            for (Vendedor vendedor : vendedores) {
                                atualizaMensagemProgressoRegistros(contexto, "Vendedores", contadorVendedores++, vendedores.size());
                                if (!Vendedor.InserirSQL(db, vendedor)) {
                                    mDbHelper.close();
                                    db.close();
                                    sincronizarVendedores.onSincronizarVendedores(contexto, false, new Exception("Erro ao atualizar tabela: " + FeedReaderContract.FeedVendedores.TABLE_NAME));
                                    return;
                                }
                            }

                            mDbHelper.close();
                            db.close();
                            sincronizarVendedores.onSincronizarVendedores(contexto, true, e);
                        } else {
                            sincronizarVendedores.onSincronizarVendedores(contexto, false, e);
                        }
                    } catch (Exception e1) {
                        sincronizarVendedores.onSincronizarVendedores(contexto, false, e1);
                    }
                }
            });
        } catch (Exception e) {
            sincronizarVendedores.onSincronizarVendedores(contexto, false, e);
        }
    }
*/







    private void saveFile(String filePath, String fileName, byte[] file) {
        new File(filePath).mkdirs();
        File soundFile = new File(filePath, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(soundFile);
            outputStream.write(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
