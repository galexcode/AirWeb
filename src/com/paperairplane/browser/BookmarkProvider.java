package com.paperairplane.browser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SdCardPath")
public class BookmarkProvider {
	static boolean isInit = false;
	static ArrayList<ArrayList<String>> bookmark;
	
	public static boolean isMarked(String url){
		if (!isInit) readBookmark();
		if (bookmark.toString().indexOf(url) != -1)
			return true;
		else
			return false;
	}
	
	public static void Mark(String title, String url){
		String temp = "{title:\"" + title + "\",url:\"" + url + "\",time:\"" + System.currentTimeMillis()+"\"}";
		try {
			saveToSDCard(title + ".json", temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		readBookmark();
	}
	
	public static void deleteMark(String title){
		File f = new File("/sdcard/Bookmark/"+title+".json");
		try {
			f.delete();
		} catch (Exception e){
			
		}
		isInit = false;
	}
	
	public static ArrayList<ArrayList<String>> getBookMark(){
		if (!isInit) readBookmark();
		return bookmark;
	}
	
	private static void readBookmark(){
		bookmark = new ArrayList<ArrayList<String>>();
		ArrayList<String> temp;
		List<File> fileList = null;
		int i;
		String dir = "/sdcard/Bookmark";
		File a = new File(dir);
		if (!a.exists()){
			a.mkdir();
		}
		fileList = getFile(a);
		int FileCount = fileList.size();
		String fileContent = "";
		
		for (i = 0; i < FileCount ; i++){
			try {
				InputStreamReader read;
				read = new InputStreamReader(new FileInputStream(fileList.get(i)));
				BufferedReader br = new BufferedReader(read);
				String line = "";
				StringBuffer buffer = new StringBuffer();
				
				while ((line = br.readLine()) != null){
					buffer.append(line);
				}
				
				fileContent = buffer.toString();
				read.close();
				
				JSONTokener jsonParser = new JSONTokener(fileContent); 
				JSONObject person = (JSONObject) jsonParser.nextValue();
				
				temp = new ArrayList<String>();
				
				temp.add(person.getString("title"));
				temp.add(person.getString("url"));
				temp.add(person.getString("time"));
				
				bookmark.add(temp);
				
				} catch (Exception e){
					Log.e("JSON Loader","当前文件"+fileList.get(i).toString()+"读入失败。");
					Log.e("JSON Loader","错误:"+fileContent);
					e.printStackTrace();
			}
		}
		Log.i("JSON Loader",bookmark.toString());
		isInit = true;
	}
	
	private static void saveToSDCard(String filename,String content) throws Exception{
		String dir = "/sdcard/Bookmark";
		
		File a = new File(dir);
		if (!a.exists()){
			a.mkdir();
		}
		File file=new File(a, filename);
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(content.getBytes());
		outStream.close();
	}
	
	private static List<File> getFile(File file){
		List<File> mFileList = new ArrayList<File>();
		File[] fileArray =file.listFiles();
		for (File f : fileArray) {
			if(f.isFile()){
				mFileList.add(f);
			} else {
				getFile(f);
			}
		}
		return mFileList;
	}
}