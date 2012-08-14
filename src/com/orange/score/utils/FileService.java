package com.orange.score.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;

public class FileService {

	public static String DATE_FORMAT = "yyMMddHH";
	 public static final String TAG = "FileService";  
     private Context context;  

     //使用设备的内部存储器来高速缓存数据  
     public FileService(Context context) {  
         this.context = context;  
     }  

	public void save(String fileName, String content) {
		Log.d(TAG, "save repository info to " + fileName);
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
     }  

     public String read(String fileName) throws Exception {  

         FileInputStream fis = context.openFileInput(fileName);  
         ByteArrayOutputStream baos = new ByteArrayOutputStream();  

         byte[] buf = new byte[1024];  
         int len = 0;  

         while ((len = fis.read(buf)) != -1) {  
             baos.write(buf, 0, len);  
         }  

         fis.close();  
         baos.close();  

         return baos.toString();  

     }  
     
     public static String currentDate() {
 		Date date = new Date();
 		return dateToStringByFormat(date, DATE_FORMAT);
 	}
     
     public static String dateToStringByFormat(Date date, String format) {
 		if (date == null || format == null)
 			return null;

 		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
 		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
 		return dateFormat.format(date);
 	}

 }  