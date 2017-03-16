package com.example.hii.sqlitedb;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PICKFILE_RESULT_CODE = 1;
    DAL dal;
    Dialog dialog,dialogExport;
    EditText editTextCity,editTextProvince,tempEditTextCity,tempEditTextProvince;
    Button buttonAdd,buttonShow,updateCountry,buttonExport,buttonImport,buttonExport1,buttonCancel;
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
                    dialogExport = new Dialog(MainActivity.this);
                    dialogExport.setContentView(R.layout.dialog_export_db);
                    dialogExport.show();

                    radioGroup= (RadioGroup) dialogExport.findViewById(R.id.radioGroup);
                    buttonExport1= (Button) dialogExport.findViewById(R.id.mybutton_export1);
                    buttonCancel= (Button) dialogExport.findViewById(R.id.mybutton_cancel);
                    buttonExport1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            radioButton = (RadioButton) dialogExport.findViewById(selectedId);
                            try {
                                String destPath=DBBackUp.exportDB(getDatabasePath((String) radioButton.getText()).getPath());
                                textViewPath.setText(destPath);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Util.ToastLong(MainActivity.this,e.toString());
                            }
                            Toast.makeText(MainActivity.this,
                                    radioButton.getText(), Toast.LENGTH_SHORT).show();
                            dialogExport.dismiss();
                        }
                    });
                   buttonCancel.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialogExport.dismiss();
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
    }

    private void initView() {
        editTextCity = (EditText) findViewById(R.id.mytext_city);
        editTextProvince = (EditText) findViewById(R.id.mytext_province);
        buttonAdd = (Button) findViewById(R.id.mybutton_add);
        buttonShow = (Button) findViewById(R.id.mybutton_show);
        buttonExport= (Button) findViewById(R.id.mybutton_export);
        buttonImport= (Button) findViewById(R.id.mybutton_import);
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
                    final String DBPath=getDatabasePath(DBConnect.DB_NAME).getPath();
                    String extension= FilePath.substring(FilePath.lastIndexOf("."));

                    if(!TextUtils.isEmpty(extension) && extension.equals(".db")){
                        Util.ToastShort(MainActivity.this,"DB file");
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
                    }
                    else {
                        Util.ToastLong(MainActivity.this,"Please select .db file");
                    }

                }
        }
    }
}
