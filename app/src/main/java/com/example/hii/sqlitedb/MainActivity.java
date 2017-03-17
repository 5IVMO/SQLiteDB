package com.example.hii.sqlitedb;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends AppCompatActivity {

    private static final int PICKFILE_RESULT_CODE = 1;
    DAL dal;
    Dialog dialog,dialogBackup;
    EditText editTextCity,editTextProvince,tempEditTextCity,tempEditTextProvince;
    Button buttonAdd,buttonShow,updateCountry,buttonExport,buttonImport,buttonOK,buttonCancel,buttonXLS;
    ListView listView;
    ArrayList<DataBean> myarrayList;
    ArrayList<String> tempList;
    myCustomAdapter myCustomAdapter;
    TextView textViewPath;
    RadioGroup radioGroup;
    RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBean dataBean=new DataBean();
                dataBean.setCity(editTextCity.getText().toString());//get value from edit text
                dataBean.setProvince(editTextProvince.getText().toString()); //get value from spinner.
                dal.insertCountry(dataBean);
                myarrayList.add(dataBean);
                myCustomAdapter.notifyDataSetChanged();
            }
        });
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStudent();
            }
        });
        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                   // backupDatabase();
                    initDialog();
                    dialogBackup.setTitle("Export Database");
                    buttonOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            radioButton = (RadioButton) dialogBackup.findViewById(selectedId);
                            try {
                                String destPath=DBBackUp.exportDB(getDatabasePath((String) radioButton.getText()).getPath());
                                textViewPath.setText(destPath);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Util.ToastLong(MainActivity.this,e.toString());
                            }
                            Toast.makeText(MainActivity.this,
                                    radioButton.getText(), Toast.LENGTH_SHORT).show();
                            dialogBackup.dismiss();
                        }
                    });
                   buttonCancel.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialogBackup.dismiss();
                       }
                   });
                }
                catch (Exception e){
                    Util.ToastLong(MainActivity.this,e.toString());
                    Log.e("Exception",""+e);
                }
            }
        });
        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                try {
                    startActivityForResult(intent, PICKFILE_RESULT_CODE);
                } catch (ActivityNotFoundException e) {
                    Util.ToastLong(MainActivity.this,e.toString());
                }
            }
        });
        buttonXLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path=getDatabasePath(DBConnect.DB_NAME).getPath();
                File dbFile= new File(path);
                DBConnect dbhelper = new DBConnect(getApplicationContext());
                File exportDir = new File(Environment.getExternalStorageDirectory(), "");
                if (!exportDir.exists())
                {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, "csvname.csv");
                try
                {
                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    SQLiteDatabase db = dbhelper.getReadableDatabase();
                    Cursor curCSV = db.rawQuery("SELECT * FROM country",null);
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while(curCSV.moveToNext())
                    {
                        //Which column you want to exprort
                        String arrStr[] ={curCSV.getString(1),curCSV.getString(2)};
                        csvWrite.writeNext(arrStr);
                    }
                    csvWrite.close();
                    curCSV.close();
                }
                catch(Exception sqlEx)
                {
                    Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                }
            }
        });
    }

    private void initDialog() {
        dialogBackup = new Dialog(MainActivity.this);
        dialogBackup.setContentView(R.layout.dialog_backup_db);
        dialogBackup.show();
        radioGroup= (RadioGroup) dialogBackup.findViewById(R.id.radioGroup);
        buttonOK= (Button) dialogBackup.findViewById(R.id.mybutton_backup);
        buttonCancel= (Button) dialogBackup.findViewById(R.id.mybutton_cancel);
    }

    private void initView() {
        editTextCity = (EditText) findViewById(R.id.mytext_city);
        editTextProvince = (EditText) findViewById(R.id.mytext_province);
        buttonAdd = (Button) findViewById(R.id.mybutton_add);
        buttonShow = (Button) findViewById(R.id.mybutton_show);
        buttonExport= (Button) findViewById(R.id.mybutton_export);
        buttonImport= (Button) findViewById(R.id.mybutton_import);
        buttonXLS= (Button) findViewById(R.id.mybutton_xls);
        listView = (ListView) findViewById(R.id.mylistView);
        dal = new DAL(this);
        textViewPath= (TextView) findViewById(R.id.textViewPath);
    }

    public void backupDatabase() throws IOException {
        //Open your local db as the input stream
        String inFileName = getDatabasePath(DBConnect.DB_NAME).getPath();
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String root = Environment.getExternalStorageDirectory().toString();
        String fname="MYDB.db";
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
    }
    public void showStudent() {
        myarrayList = dal.getAllCountries();
       /* tempList=new ArrayList<>();
        for (int i=0;i<myarrayList.size();i++){
            tempList.add(myarrayList.get(i).getCity());
        }*/
        //ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tempList);
        myCustomAdapter=new myCustomAdapter(this,myarrayList);
        listView.setAdapter(myCustomAdapter);

    }
    public class  myCustomAdapter extends ArrayAdapter{

        public myCustomAdapter(Context context, ArrayList<DataBean> list) {
            super(context,0,list);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=getLayoutInflater().inflate(R.layout.custom_list_item,null);
            }
            final DataBean dt= (DataBean) getItem(position);
            TextView t1= (TextView) convertView.findViewById(R.id.textView_city);
            t1.setText(dt.getCity());
            TextView t2= (TextView) convertView.findViewById(R.id.textView_province);
            t2.setText(dt.getProvince());
            Button buttonEdit= (Button) convertView.findViewById(R.id.button_edit);
            Button buttonDelete= (Button) convertView.findViewById(R.id.button_delete);
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Edit "+dt.getID(), Toast.LENGTH_SHORT).show();
                    dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_edit_country);
                    // dialog.setTitle("TodoTask...");
                    dialog.show();
                    updateCountry = (Button) dialog.findViewById(R.id.button_update);
                    tempEditTextCity = (EditText) dialog.findViewById(R.id.editText_city1);
                    tempEditTextProvince = (EditText) dialog.findViewById(R.id.editText_province1);
                    tempEditTextCity.setText(dt.getCity());
                    tempEditTextProvince.setText(dt.getProvince());
                    updateCountry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String tempCity = tempEditTextCity.getText().toString();
                            String tempProvince = tempEditTextProvince.getText().toString();

                            if (!(tempCity.equals("") || tempProvince.equals(""))) {

                                dt.setCity(tempCity);
                                dt.setProvince(tempProvince);
                                dal.updateCountry(dt);
                                myarrayList.get(position).setCity(tempCity);
                                myarrayList.get(position).setProvince(tempProvince);
                                myCustomAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Fields Should not be left Empty", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    // dal.updateCountry();
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Delete: " +dt.getID(), Toast.LENGTH_SHORT).show();
                    dal.removeCountry(dal.getCountry(dt.getID()));
                    myCustomAdapter.remove(dt);
                    myCustomAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Fix no activity available
        if (data == null)
            return;
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    //FilePath is your file as a string
                    final String FilePath = data.getData().getPath();
                    String extension="";
                   // final String DBPath=getDatabasePath(DBConnect.DB_NAME).getPath();
                    if(FilePath.contains(".")){
                        extension= FilePath.substring(FilePath.lastIndexOf("."));
                    }

                    if(!TextUtils.isEmpty(extension) && extension.equals(".db")){
                        Util.ToastShort(MainActivity.this,"DB file");
                        initDialog();
                        dialogBackup.setTitle("Import Database");
                        buttonOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int selectedId = radioGroup.getCheckedRadioButtonId();
                                radioButton = (RadioButton) dialogBackup.findViewById(selectedId);

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                builder1.setTitle("Remove Old Database");
                                builder1.setIcon(R.drawable.ic_warning_black_24dp);
                                builder1.setMessage("Are you Sure You want to remove old Database ?");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                try {
                                                    String DBPath=getDatabasePath((String) radioButton.getText()).getPath();
                                                    DBBackUp.importDB(FilePath,DBPath);
//                                        InputStream mInput = new FileInputStream(FilePath);
//                                        String outFileName = getDatabasePath(DBConnect.DB_NAME).getPath();;
//                                        OutputStream mOutput = new FileOutputStream(outFileName);
//                                        byte[] mBuffer = new byte[1024];
//                                        int mLength;
//                                        while ((mLength = mInput.read(mBuffer))>0)
//                                        {
//                                            mOutput.write(mBuffer, 0, mLength);
//                                        }
//                                        mOutput.flush();
//                                        mOutput.close();
//                                        mInput.close();

                                                } catch (Exception e) {
                                                    Util.ToastLong(MainActivity.this,e.toString());
                                                }
                                                dialog.cancel();
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                                
                                Toast.makeText(MainActivity.this,
                                        radioButton.getText(), Toast.LENGTH_SHORT).show();
                                dialogBackup.dismiss();
                            }
                        });
                        buttonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBackup.dismiss();
                            }
                        });
                    
                    }
                    else {
                        Util.ToastLong(MainActivity.this,"Please select .db file");
                    }

                }
        }
    }
}
