package com.paperairplane.browser;

import java.util.ArrayList;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

@SuppressWarnings("unused")
public class BookmarkList extends BaseAdapter {
	private LayoutInflater li;
	private Context ct;
	private ArrayList<ArrayList<String>> title;

	public BookmarkList(Context context, ArrayList<ArrayList<String>> title){
		this.ct = context;
		this.title = title;
		this.li = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return title.size();
	}

	@Override
	public Object getItem(int arg0) {
		return title.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Myobj obj = null;
		if (arg1 == null){
			obj = new Myobj();
			arg1 = li.inflate(R.layout.bookmark_list, null);
			obj.title = (TextView) arg1.findViewById(R.id.bm_title);
			obj.url = (TextView) arg1.findViewById(R.id.bm_url);
			arg1.setTag(obj);
		} else {
			obj = (Myobj) arg1.getTag();
		}
		obj.title.setText(title.get(arg0).get(0));
		String url = title.get(arg0).get(1);
		if (url.length() > 40){
			url = url.substring(0, 36) + "...";
		}
		obj.url.setText(url);
		return arg1;
	}
	
	public final class Myobj{
		public TextView title, url;
	}
}
