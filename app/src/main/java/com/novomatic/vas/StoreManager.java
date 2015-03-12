package com.novomatic.vas;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by fpirazzi on 03/03/2015.
 */
public class StoreManager {

    private String FILENAME = "Prospect";

    public void SaveData(Context context, String dati){

        String string = dati;

        try {
            FileOutputStream fos;
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String GetData(Context context){

        String string = "";
        StringBuffer fileContent = new StringBuffer("");

        try {
            FileInputStream fis;
            fis = context.openFileInput(FILENAME);

            byte[] buffer = new byte[1024];

            int n;

            while ((n = fis.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }

            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent.toString();

    }

    public void DeleteData(Context context){

        try {
            File dir = context.getFilesDir();
            File file = new File(dir, FILENAME);
            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean DataExists (Context context){

        boolean result = false;

        try {

            File dir = context.getFilesDir();
            File file = new File(dir, FILENAME);
            result = file.exists();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  result;

    }


}
