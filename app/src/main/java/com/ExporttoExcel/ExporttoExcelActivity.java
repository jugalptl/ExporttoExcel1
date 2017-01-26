package com.ExporttoExcel;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExporttoExcelActivity extends Activity implements OnClickListener {

	TextView file_path;
	Button export;

	ArrayList<String> fnames;
	EditText input=null;
	ArrayList<String> lnames;
	ArrayList<String> unames;
	ArrayList<String> passwords;
	
	String download_file_name = "";

	File file_download = null;
	
	   public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	    public static final int DIALOG_DOWNLOAD_COMPLETE = 1;
	    private static final int DIALOG_EXPORT=3;
	    ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_excel);

		file_path = (TextView) findViewById(R.id.fiel_path);
		export = (Button) findViewById(R.id.button1);
		export.setOnClickListener(this);

		unames = new ArrayList<String>();
		passwords = new ArrayList<String>();
		fnames = new ArrayList<String>();
		lnames = new ArrayList<String>();

		Toast.makeText(getApplicationContext(), "In Exporting",
				Toast.LENGTH_LONG).show();
		DBAdapter dba = new DBAdapter(getApplicationContext());
		dba.open();

	/*	try {
			File f = new File("/data/data/com.ExporttoExcel/databases/DB");
			if (!f.exists()) {
				InputStream databaseInputStream1;
				databaseInputStream1 = getAssets().open("DB");

				DBAdapter db = new DBAdapter(this);
				db.open();
				db.close();

				ImportDatabase ipd = new ImportDatabase(databaseInputStream1);
				ipd.copyDataBase();
				System.out.println("Database copied");

			} else {
				System.out.println("Database file already exist");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}*/

		/*DBAdapter dba = new DBAdapter(this);
		dba.open();
		*/
		dba.insertRegistrationDetails("ABC","DEF","GHI","123");
		dba.insertRegistrationDetails("AVGD","DEFsdfsdf","GHIsfsdf","123sdfkjsn");
		dba.insertRegistrationDetails("AJDHF","DEFdfdsf","dsffkh","123fdjfnjd");
		dba.insertRegistrationDetails("AJDUD","Dsddf","GHI","123o659");

		Cursor cur = dba.getAllData();
		cur.moveToFirst();
		for (int j = 0; j < cur.getCount(); j++) {
			fnames.add(cur.getString(cur.getColumnIndex("uname")));
			lnames.add(cur.getString(cur.getColumnIndex("lname")));
			unames.add(cur.getString(cur.getColumnIndex("uname")));
			passwords.add(cur.getString(cur.getColumnIndex("password")));
			
			cur.moveToNext();
		}

		dba.close();

	}

	@Override
	public void onClick(View v) {
		
		showDialog(DIALOG_EXPORT);
		
		/*try{
			new DownloadFileAsync().execute();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}*/
	
		
		/*Thread t = new Thread() {
			public void run() {
				try {
					new DownloadFileAsync().execute();
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		t.start();*/

	}

	public class WriteExcel {

		private WritableCellFormat timesBoldUnderline;
		private WritableCellFormat times;
		private String inputFile;

		public void setOutputFile(String inputFile) {
			this.inputFile = inputFile;
		}

		public void write() throws IOException, WriteException {
			File file = new File(inputFile);
			WorkbookSettings wbSettings = new WorkbookSettings();

			wbSettings.setLocale(new Locale("en", "EN"));

			WritableWorkbook workbook = Workbook.createWorkbook(file,
					wbSettings);
			workbook.createSheet("Report", 0);
			WritableSheet excelSheet = workbook.getSheet(0);
			createLabel(excelSheet);
			createContent(excelSheet);
			

			workbook.write();
			workbook.close();
		}

		private void createLabel(WritableSheet sheet) throws WriteException {
			// Lets create a times font
			WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
			// Define the cell format
			times = new WritableCellFormat(times10pt);
			// Lets automatically wrap the cells
			times.setWrap(true);

			// Create create a bold font with unterlines
			WritableFont times10ptBoldUnderline = new WritableFont(
					WritableFont.TIMES, 10, WritableFont.BOLD, false,
					UnderlineStyle.SINGLE);
			timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
			// Lets automatically wrap the cells
			timesBoldUnderline.setWrap(true);

			CellView cv = new CellView();
			cv.setFormat(times);
			cv.setFormat(timesBoldUnderline);
			//cv.setAutosize(true);

			// Write a few headers
			addCaption(sheet, 0, 0, "First Name");
			addCaption(sheet, 1, 0, "Last Name");
			addCaption(sheet, 2, 0, "User Name");
			addCaption(sheet, 3, 0, "Password");
			addCaption(sheet, 0,fnames.size(), "Total");
		}
		
	

		private void createContent(WritableSheet sheet) throws WriteException,
				RowsExceededException {
			
		/*	
			// Write a few number
			for (int i = 1; i < 10; i++) {
				// First column
				addNumber(sheet, 0, i, i + 10);
				// Second column
				addNumber(sheet, 1, i, i * i);
			}
			// Lets calculate the sum of it
			StringBuffer buf = new StringBuffer();
			buf.append("SUM(A2:A10)");
			Formula f = new Formula(0, 10, buf.toString());
			sheet.addCell(f);
			buf = new StringBuffer();
			buf.append("SUM(B2:B10)");
			f = new Formula(1, 10, buf.toString());
			sheet.addCell(f);*/

			// Now a bit of text
			for (int i = 1; i < fnames.size(); i++) {
				// First column
				//addLabel(sheet, 0, i, "Boring text " + i);
				addLabel(sheet, 0, i, fnames.get(i));
				addLabel(sheet, 1, i, lnames.get(i));
				addLabel(sheet, 2, i, unames.get(i));
				addLabel(sheet, 3, i, passwords.get(i));
				// Second column
				//addLabel(sheet, 1, i, "Another text");
			}
		}

		private void addCaption(WritableSheet sheet, int column, int row,
				String s) throws RowsExceededException, WriteException {
			Label label;
			label = new Label(column, row, s, timesBoldUnderline);
			sheet.addCell(label);
		}

		private void addNumber(WritableSheet sheet, int column, int row,
				Integer integer) throws WriteException, RowsExceededException {
			Number number;
			number = new Number(column, row, integer, times);
			sheet.addCell(number);
		}

		private void addLabel(WritableSheet sheet, int column, int row, String s)
				throws WriteException, RowsExceededException {
			Label label;
			label = new Label(column, row, s, times);
			sheet.addCell(label);
		}

	}
	
	 @Override
	    protected Dialog onCreateDialog(int id) {
	        switch (id) {
			case DIALOG_DOWNLOAD_PROGRESS:
				mProgressDialog = new ProgressDialog(this);
				mProgressDialog.setMessage("Exporting file..");
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgressDialog.setCancelable(true);
				mProgressDialog.show();
				return null;
				
			case DIALOG_DOWNLOAD_COMPLETE:
				
				mProgressDialog.dismiss();
				
				AlertDialog.Builder dia = new Builder(this);
				dia.setTitle("Open File");
				dia.setMessage("Do you want to open this excel file?");
				dia.setIcon(android.R.drawable.ic_menu_agenda);
				dia.setCancelable(true);
				dia.setPositiveButton("Open", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						

				File file2 = new File(Environment.getExternalStorageDirectory().toString()+"/Download/"+download_file_name);
								
						
						
					 if((download_file_name.endsWith(".xls"))||
								(download_file_name.endsWith(".xlsx"))){
				
							Intent intent = new Intent();
							
							intent.setAction(android.content.Intent.ACTION_VIEW);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							 Uri uri = Uri.fromFile(file2);
			                 intent.setDataAndType(uri,"application/*");
								try {
									mProgressDialog.dismiss();
									startActivity(intent);
								} catch (Exception e) {
									
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getParent());
									builder.setTitle("No Application Found");
									builder.setMessage("Download application from Android Market?");
									builder.setPositiveButton(
											"Yes, Please",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													Intent marketIntent = new Intent(
															Intent.ACTION_VIEW);
													marketIntent.setData(Uri
															.parse("market://details?id=com.infraware.polarisoffice4"));
													
													mProgressDialog.dismiss();
													startActivity(marketIntent);
												}
											});
									builder.setNegativeButton("No, Thanks",
											null);
									builder.create().show();
								}
							
						
						}
						
					}
				});
				
				dia.setNegativeButton("Send", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						
						Intent i2 = new Intent(getApplicationContext(),
								SendEmail.class);
						Bundle b=new Bundle();
						b.putString("Exportfile", file_download.getPath());
					    i2.putExtras(b);
						startActivity(i2);
						
						//dialog.cancel();
					}
				});

				AlertDialog alrt = dia.create();
				alrt.show();
				
				return  alrt;
				
			case DIALOG_EXPORT:
				input = new EditText(this);
				input.setSingleLine();
				input.clearFocus();
				input.clearComposingText();
				
				
				return new AlertDialog.Builder(this)
				.setTitle("Save As")
				.setMessage("Enter file Name")
				.setView(input)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						
						int len = input.length();
						
						if (!(len == 0)) {
							
							callProgressBar();
							input.clearComposingText();
							
							download_file_name=input.getText().toString();

						} else {
							input.clearComposingText();
							Toast.makeText(
									getApplicationContext(),
									"Enter Value Properly",
									Toast.LENGTH_LONG)
									.show();
							input.clearComposingText();
						}	
					}

				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.create();
	        
			default:
				return null;
	        }
	 }
	 
	

	class DownloadFileAsync extends AsyncTask<Void, Void, Void> {
		   
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		
		}

		@Override
		protected Void doInBackground(Void... aurl) {

		try {
			
			file_download = new File(Environment
					.getExternalStorageDirectory().toString()
					+ "/Download/" + download_file_name);

			WriteExcel test = new WriteExcel();
			test.setOutputFile(file_download.getPath());
			test.write();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

		}

		@Override
		protected void onPostExecute(Void unused) {
			//dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			showDialog(DIALOG_DOWNLOAD_COMPLETE);
			//mProgressDialog.dismiss();
			
			Toast.makeText(getApplicationContext(), "Please check the result file under "
					+ file_download.getPath().toString(), Toast.LENGTH_LONG).show();
			
			file_path.setText("file path:  "+file_download.getPath());
		}
	}
	
	public void callProgressBar() {
		
		try{
			new DownloadFileAsync().execute();
		}
		catch(Exception e){
			e.printStackTrace();
		}
			}
	
	

}

