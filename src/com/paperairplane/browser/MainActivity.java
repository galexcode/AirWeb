package com.paperairplane.browser;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {

	static WebView web_content;
	static EditText edittext;
	static LinearLayout actionbar;
	DisplayMetrics metrics;
	boolean isActivedMenu;
	int BarDeafultHeight, backclickedtimes;
	static String url_now = "null";
	GestureDetector detector;
	PopupWindow mPopupWindow = null;
	ImageButton btn_star;
	
	int ab_speed = 0;
	
	static Handler UIHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what){
			case 0:
				LayoutParams lp = (LayoutParams) actionbar.getLayoutParams();
		        lp.height = lp.height + msg.arg1;
		        actionbar.setLayoutParams(lp);
				break;
			}
		}
	};
	
	static Handler BrowserHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				GoTo(msg.getData().getString("url"));
				break;
			}
		}
	};
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ImageButton btn_left = (ImageButton) findViewById(R.id.button_left);
		btn_left.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (web_content.canGoBack()) web_content.goBack();
				url_now = web_content.getUrl();
				if (BookmarkProvider.isMarked(url_now)){
					btn_star.setImageResource(R.drawable.star_marked);
				} else {
					btn_star.setImageResource(R.drawable.star);
				}
			}
		});
		
		ImageButton btn_right = (ImageButton) findViewById(R.id.button_right);
		btn_right.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (web_content.canGoForward()) web_content.goForward();
				url_now = web_content.getUrl();
				if (BookmarkProvider.isMarked(url_now)){
					btn_star.setImageResource(R.drawable.star_marked);
				} else {
					btn_star.setImageResource(R.drawable.star);
				}
			}
			
		});
		
		ImageButton btn_reload = (ImageButton) findViewById(R.id.button_reload);
		btn_reload.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				web_content.reload();
				url_now = web_content.getUrl();
				if (BookmarkProvider.isMarked(url_now)){
					btn_star.setImageResource(R.drawable.star_marked);
				} else {
					btn_star.setImageResource(R.drawable.star);
				}
			}
			
		});
		
		btn_star = (ImageButton) findViewById(R.id.starButton);
		btn_star.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (BookmarkProvider.isMarked(url_now)){
					BookmarkProvider.deleteMark(MainActivity.this.getTitle().toString());
					Toast
					.makeText(getApplicationContext(), getString(R.string.bookmark_deleted), Toast.LENGTH_SHORT)
					.show();
					btn_star.setImageResource(R.drawable.star);
				} else {
					BookmarkProvider.Mark(MainActivity.this.getTitle().toString(), url_now);
					Toast
					.makeText(getApplicationContext(), getString(R.string.bookmark_succeed), Toast.LENGTH_SHORT)
					.show();
					btn_star.setImageResource(R.drawable.star_marked);
				}
			}
		});
		btn_star.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), BookmarkActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				startActivity(i);
				overridePendingTransition(R.anim.anim_window_open, R.anim.anim_none);
				return false;
			}
		});
		
		edittext = (EditText) findViewById(R.id.editbox);
		edittext.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
					GoTo(edittext.getText().toString());
					return true;
				}
				return false;

			}
		});
		
		edittext.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					GoTo(edittext.getText().toString());
					return true;
				}
				return false;
			}
		});
		
		ListView listview = (ListView) findViewById(R.id.menuView);
		ArrayList<String> title = new ArrayList<String>();
		title.add(getString(R.string.menu_1));
		title.add(getString(R.string.menu_2));
		title.add(getString(R.string.menu_3));
		listview.setAdapter(new MenuList(getApplicationContext(), title));
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2){
				case 0:
					
					break;
				case 1:
					showAbout();
					break;
				case 2:
					System.gc();
					finish();
					break;
				}
				showOrHideMenu();
			}
		});
		
		/* Get Screen Metrics */
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		/* Set up bottom bar */
		detector= new GestureDetector (new GestureListener());
		actionbar = (LinearLayout) findViewById(R.id.bar);
		actionbar.setOnTouchListener(new TouchListener());
		
		/* Set up bottom menu */
		isActivedMenu = false;
		LayoutParams lp = (LayoutParams) actionbar.getLayoutParams();
		BarDeafultHeight = lp.height;
		
		/* Init back clicked times*/
		backclickedtimes = 0;
		
		WebViewInit();
	}
	
    @SuppressLint("SetJavaScriptEnabled")
	private void WebViewInit() {
    	
        web_content = (WebView) findViewById(R.id.webView);
        WebSettings set = web_content.getSettings();
		set.setSupportZoom(true);
		set.setJavaScriptEnabled(true);
		set.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		web_content.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				web_content.requestFocus();
				return false;
			}

		});

		web_content.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				edittext.setText(url);
				url_now = url;
				if (BookmarkProvider.isMarked(url)){
					btn_star.setImageResource(R.drawable.star_marked);
				} else {
					btn_star.setImageResource(R.drawable.star);
				}
				return true;
			}
			
		});
		
		web_content.setWebChromeClient(new WebChromeClient(){
		        @Override 
		        public void onProgressChanged(WebView view, int newProgress) { 
		            MainActivity.this.setProgress(newProgress*100); 
		            super.onProgressChanged(view, newProgress); 
		        }
		        
		        @Override
		        public void onReceivedTitle(WebView view, String title) { 
		            MainActivity.this.setTitle(title);
		            super.onReceivedTitle(view, title); 
		        }
		});
		GoTo("http://www.google.com");
	}

    private static void GoTo(String url){
    	if (url.indexOf("http://") == -1){
    		url = "http://" + url;
    	}
		web_content.loadUrl(url);
		edittext.setText(url);
		url_now = url;
    }
    
    private class TouchListener implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return detector.onTouchEvent(event);
		}
		
	}
    
    private class GestureListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent arg0) {
			Log.i("TouchEvent", "X:"+arg0.getX()+" Y:"+arg0.getY());
			if (arg0.getX() > metrics.widthPixels / 4 * 3){
				showOrHideMenu();
			}
			return false;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			return false;
		}
    	
    }
    
    public boolean onKeyDown(int keyCode,KeyEvent event){
    	if (keyCode==KeyEvent.KEYCODE_BACK){
    		if (backclickedtimes == 0){
    			new Thread(){
    				public void run(){
    					try {
    						Thread.sleep(1500);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    					backclickedtimes = 0;
    				}
    			}.start();
    		}
    		backclickedtimes++;
    		if (backclickedtimes == 1){
    			Toast.makeText(getApplicationContext(), getString(R.string.exit_hint), Toast.LENGTH_SHORT)
    			     .show();
    		}
    		if (backclickedtimes > 1){
    			System.gc();
    			finish();
    		}
    		return true;
    	}
    	if (keyCode==KeyEvent.KEYCODE_MENU){
    		showOrHideMenu();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    private void showOrHideMenu(){
    	if (isActivedMenu){
			//LayoutParams lp = (LayoutParams) actionbar.getLayoutParams();
	        //lp.height = BarDeafultHeight;
	        //actionbar.setLayoutParams(lp);
    		ab_speed = -20;
		} else {
			//LayoutParams lp = (LayoutParams) actionbar.getLayoutParams();
	        //lp.height = 250;
	        //actionbar.setLayoutParams(lp);
    		ab_speed = +20;
		}
    	new Thread(){
			public void run(){
				for (;ab_speed < 1|ab_speed > 1;) {
					if (ab_speed > 0) ab_speed -= 0.5; else ab_speed += 0.5;
					Message msg = new Message();
					msg.what = 0;
					msg.arg1 = ab_speed;
					UIHandler.sendMessage(msg);
			        try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		isActivedMenu = isActivedMenu ? false : true;
    }
    
    private void showAbout(){
    	if (mPopupWindow == null){
    		View view = getLayoutInflater().inflate(R.layout.activity_about, null);
    		
    		Button button = (Button) view.findViewById(R.id.button1);
    		button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					mPopupWindow.dismiss();
				}
    		});

    		mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT);
    		mPopupWindow.setFocusable(true);
    		mPopupWindow.setAnimationStyle(R.style.anim_popup);
    	}
		View parent = this.getWindow().getDecorView();
		mPopupWindow.showAtLocation(parent ,Gravity.TOP ,0 ,0);
    }
}
