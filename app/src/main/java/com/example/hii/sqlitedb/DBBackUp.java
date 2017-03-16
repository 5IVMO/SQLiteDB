package com.example.hii.sqlitedb;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Owais on 3/15/2017.
 */
public class DBBackUp {
    private Context context;
    public DBBackUp(Context context) {
        this.context=context;
    }

    public static void importDB(String FilePath,String DBPath) throws IOException {
        InputStream mInput = new FileInputStream(FilePath);
        String outFileName = DBPath;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }
    //exporting database
    public static String exportDB(String Path) throws IOException {
        //Open your local db as the input stream
        String inFileName = Path;
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String root = Environment.getExternalStorageDirectory().toString();
       // String fname=DBConnect.DB_NAME+".db";
        String fname=Path.substring(Path.lastIndexOf('/'))+".db";
        File myDir = new File(root + "/dbBackup");
        myDir.mkdirs();
        File file = new File (myDir, fname);
        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(file);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        //Close the streams
        output.flush();
        output.close();
        fis.close();
        return file.getPath();
    }
}
