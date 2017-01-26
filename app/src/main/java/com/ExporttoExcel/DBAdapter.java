package com.ExporttoExcel;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

	private static final String DATABASE_NAME = "DB";

	private static final int DATABASE_VERSION = 2;

	private final Context context;

	 DatabaseHelper DBHelper;
	private static SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// db.execSQL(DATABASE_CREATE);

			db.execSQL("CREATE TABLE IF NOT EXISTS  tbl_registration ("
					+ "fname" + " VARCHAR(20), "
					+ "lname" + " VARCHAR(20), "
					+ "uname" + " VARCHAR(20), "
					+ "password" + " VARCHAR(20) "
					+ ");");
			System.out.println("Database has been created-->>");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS tbl_registration");
			onCreate(db);

		}

	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	public boolean check_login(String uname, String pass) throws SQLException {

		// Cursor mCursor = db.rawQuery("select * from tbl_login", null);

		Cursor mCursor = db.rawQuery("SELECT * FROM tbl_login where username='"
				+ uname + "'" + " and " + " password='" + pass + "';", null);

		if (mCursor != null) {
			mCursor.moveToFirst();
			if (mCursor.getCount() > 0) {

				mCursor.close();

				return true;
			}
		}
		mCursor.close();

		return false;
	}

	public boolean insertLogin_info(String uname, String pass) {

		ContentValues initialValues = new ContentValues();

		initialValues.put("username", uname);
		initialValues.put("password", pass);

		return db.insert("tbl_login", null, initialValues) > 0;

	}

	public boolean insertRegistrationDetails(String fname, String lname,
			String uname, String pass) {

		ContentValues initialValues = new ContentValues();

		initialValues.put("fname", fname);
		initialValues.put("lname", lname);
		initialValues.put("uname", uname);
		initialValues.put("password", pass);

		return db.insert("tbl_registration", null, initialValues) > 0;

	}
	
	public boolean insertimage_upload(String image_path, boolean status) {

		ContentValues initialValues = new ContentValues();

		initialValues.put("image_path", image_path);
		initialValues.put("status", status);

		return db.insert("tbl_image_upload", null, initialValues) > 0;

	}
	

	// ---get data---
	
	public Cursor getAllData(){
		//Cursor mCursor=db.rawQuery("select * from tbl_registration where uname='"+ uname+"';", null);
		
		Cursor mCursor=db.rawQuery("select * from tbl_registration", null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
		
	}
	
	public Cursor getimage_path(String uname){
		Cursor mCursor=db.rawQuery("SELECT * FROM tbl_image_upload INNER JOIN tbl_login ON tbl_image_upload.id=tbl_login.id", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return  mCursor;
	}

}