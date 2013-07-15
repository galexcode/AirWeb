package com.paperairplane.browser;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class BookmarkActivity extends Activity{

	ListView lv;
	PopupWindow mPopupWindow;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		
		ImageButton ib = (ImageButton) findViewById(R.id.imageButton1);
		ib.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(R.anim.anim_none, R.anim.anim_window_close);
			}
		});
		
		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(new BookmarkList(getApplicationContext(),BookmarkProvider.getBookMark()));
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("url", BookmarkProvider.getBookMark().get(arg2).get(1));
				msg.setData(bundle);
				msg.what = 0;
				MainActivity.BrowserHandler.sendMessage(msg);
				finish();
				overridePendingTransition(R.anim.anim_none, R.anim.anim_window_close);
			}
		});
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (mPopupWindow == null){
		    		View view = getLayoutInflater().inflate(R.layout.activity_bookmark_delete, null);
		    		
		    		Button button1 = (Button) view.findViewById(R.id.btn_cancel);
		    		button1.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View arg0) {
							mPopupWindow.dismiss();
						}
		    		});

		    		Button button2 = (Button) view.findViewById(R.id.btn_ok);
		    		button2.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View arg0) {
							BookmarkProvider.deleteMark(BookmarkProvider.getBookMark().get(arg2).get(0));
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.bookmark_deleted),
									Toast.LENGTH_SHORT
									)
									.show();
							lv.setAdapter(new BookmarkList(getApplicationContext(),BookmarkProvider.getBookMark()));
							mPopupWindow.dismiss();
						}
		    		});
		    		
		    		mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT);
		    		mPopupWindow.setFocusable(true);
		    		mPopupWindow.setAnimationStyle(R.style.anim_popup);
		    	}
				
				View parent = BookmarkActivity.this.getWindow().getDecorView();
				mPopupWindow.showAtLocation(parent ,Gravity.TOP ,0 ,0);
				return false;
			}
		});
	}
}
