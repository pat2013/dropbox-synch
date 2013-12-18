package com.example.androidsync;

import android.os.Bundle;
import android.app.Activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class CommentActivity extends Activity {

	private static final String appKey = "phfvfwmfhbccv0i";
	private static final String appSecret = "ockocoiv9u7byj3";
	private static final String TAG = "commentActivity";
	private DbxAccountManager mDbxAcctMgr;
	
	
	public DbxAccountManager getDbcAccountMgr(Context context) {
		if (mDbxAcctMgr == null) {
			mDbxAcctMgr = DbxAccountManager.getInstance(
					context.getApplicationContext(), appKey, appSecret);
		}

		return mDbxAcctMgr;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		// Get the message from the intent
		Intent intent = getIntent();
		String image_path = intent.getStringExtra("IMAGE_PATH");
		// String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		// // Create the text view
		// TextView textView = new TextView(this);
		// textView.setTextSize(40);
		// textView.setText(message);
		// // Set the text view as the activity layout
		// setContentView(textView);
		alertForComment(this, image_path);
	}

	public void alertForComment(Context context, String imagePath) {
		
		final String imgPath = imagePath;
		final Context cntxt = context; 
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Add comment");
		alert.setMessage("Add user comment to image");

		// Set an EditText view to get user input
		final EditText input = new EditText(context);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Editable value = input.getText();
				if(value != null && value.toString() != ""){
					addComment(value.toString(), imgPath);
				}
				
				writeToCloud(imgPath, cntxt);
				finish();
			}
		});

		alert.setNegativeButton("Save without comment",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						writeToCloud(imgPath, cntxt);
						finish();
					}
				});

		alert.show();
	}

	public void addComment(String p_comment, String image_path) {
		String commentPrefix = "        ";
		String comment = commentPrefix + p_comment;

		ExifInterface exif;
		try {
			exif = new ExifInterface(image_path);
			Log.v(TAG, "Setting Comment: "+p_comment);
			exif.setAttribute("UserComment", comment);
			exif.setAttribute("XPComment", comment);
			exif.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeToCloud(String filePath, Context context) {

		String tokens[] = filePath.split("/");
		String fileName = tokens[tokens.length - 1];
		Log.v("FileName to store", fileName);
		DbxPath path = new DbxPath("/" + fileName);

		DbxAccountManager dbxAcctmgr = getDbcAccountMgr(context);

		DbxAccount acct = dbxAcctmgr.getLinkedAccount();
		DbxFile newDbxFile = null;
		DbxFileSystem fs;
		try {
			fs = DbxFileSystem.forAccount(acct);
			newDbxFile = fs.create(path);
		} catch (DbxException.NotFound e) {
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// mFile.addListener(listener);
			FileOutputStream out = newDbxFile.getWriteStream();
			File sdcard = new File(filePath);
			FileInputStream in = new FileInputStream(sdcard);
			// FileUtil.copyFile(in, out);
			BufferedInputStream buf = new BufferedInputStream(in);
			FileUtils.copyFile(sdcard, out);
			/*
			 * BufferedOutputStream outbuf = new BufferedOutputStream(out); int
			 * data; while((data = buf.read()) != -1){ //outbuf.write(data); }
			 */
			out.flush();
			out.close();
			in.close();
			Toast.makeText(context, "Picture saved to Dropbox ",5).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		newDbxFile.close();
	}
}
