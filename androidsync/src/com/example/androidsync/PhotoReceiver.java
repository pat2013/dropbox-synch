package com.example.androidsync;

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




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;

import android.util.Log;
import android.widget.Toast;

public class PhotoReceiver extends BroadcastReceiver {
	private static final String TAG = "photoReceiver";
	private DbxAccountManager mDbxAcctMgr;
	private static final String appKey = "phfvfwmfhbccv0i";
	private static final String appSecret = "ockocoiv9u7byj3";
	
	
	 
	 int duration = Toast.LENGTH_LONG;
	@Override
	public void onReceive(Context context, Intent intent) {
		 Log.v(TAG, "In photoReceiver");
		// TODO Auto-generated method stub
		 Cursor cursor = context.getContentResolver().query(intent.getData(),      null,null, null, null);
		    cursor.moveToFirst();
		    String image_path = cursor.getString(cursor.getColumnIndex("_data"));
		    //Toast.makeText(context, "New Photo is Saved as : -" + image_path, duration).show();

		    Log.v(TAG, "Received new photo");
	    
	    Log.v(TAG, "Setting");
	    
	    showCommentWindow(context, image_path);
//   try {
//	   String mString = "Your message here";
//	   //String tokens[] = image_path.split("/");
//	   //tokens[tokens.length - 1] = "exif" + tokens[tokens.length - 1];
//	   //image_path = "";
//	   //for (int i =1;i<=tokens.length-1;i++)
//		   //image_path = image_path+"/"+tokens[i];
//    	 ExifInterface exif = new ExifInterface(image_path);
//    	 Log.v(TAG, "image_path"+image_path);
//   	 Log.v(TAG, "Setting User Comment:before");
//   	
//   	Log.v(TAG, "Setting User Comment 1");
//	    exif.setAttribute("XPComment", mString);
//	    exif.setAttribute("UserComment", mString);
//	    Log.v(TAG, "Setting User Comment 2");
//		exif.saveAttributes();
//		ExifInterface exif2 = new ExifInterface(image_path);
//		String UserComment1 = exif2.getAttribute("UserComment");
//		//String UserComment2 = exif2.getAttribute("XPComment");
//	   	if(UserComment1 == null){
//	   		Log.v(TAG, "usercomment is null");
//	   	}else{
//	   		Log.v("User comment", UserComment1);
//	   		//Log.v("User comment", UserComment2);
//	   	}
//		Toast.makeText(context, "Inside exiifinterface" , duration).show();
//	Log.v(TAG, "Setting User Comment");
//	
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();	}
//	    
//	writeToCloud(image_path, context);   
		
	}
public void writeToCloud(String filePath, Context context){
		
		String tokens[] = filePath.split("/");
		String fileName = tokens[tokens.length - 1];
		Log.v("FileName to store", fileName);
		DbxPath path = new DbxPath("/"+ fileName);
		
		
		DbxAccountManager dbxAcctmgr = getDbcAccountMgr(context);
		//DbxAccount acct = Config.getDropboxAccountManager(context).getLinkedAccount();
		
		//DbxFileSystem fs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount()); 
		
		DbxAccount acct = dbxAcctmgr.getLinkedAccount();
		DbxFile newDbxFile = null;
		DbxFileSystem fs;
		try {
		 fs = DbxFileSystem.forAccount(acct);
		
			//newDbxFile = fs.open(path);
		 	newDbxFile = fs.create(path);
		} catch (DbxException.NotFound e) {
			//newDbxFile = fs.create(path);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
		//mFile.addListener(listener);
		FileOutputStream out = newDbxFile.getWriteStream();
		File sdcard = new File(filePath);
		FileInputStream in = new FileInputStream(sdcard);
		//FileUtil.copyFile(in, out);
		BufferedInputStream buf = new BufferedInputStream(in);
			FileUtils.copyFile(sdcard, out);
	/*	BufferedOutputStream outbuf = new BufferedOutputStream(out);
		int data;
		while((data = buf.read()) != -1){
			//outbuf.write(data);
		}*/
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		newDbxFile.close();
	}
public DbxAccountManager getDbcAccountMgr(Context context){
	if(mDbxAcctMgr == null){
		mDbxAcctMgr =  DbxAccountManager.getInstance(context.getApplicationContext(), appKey, appSecret);
	}
	
	return mDbxAcctMgr;
	
}
	
public void showCommentWindow(Context context, String imagePath){
    Intent intent = new Intent(context, CommentActivity.class);
    intent.putExtra("IMAGE_PATH", imagePath);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    context.startActivity(intent);
}	

}
