package com.parnswir.unmp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.CoverList;
import com.parnswir.unmp.core.IconicAdapter;

public class ListFragment extends AbstractFragment {

	protected ListView contentList;
	protected IconicAdapter adapter;
	protected CoverList currentContent = new CoverList();


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	
    	inflate(R.layout.list_fragment);
    	showActionBar();
    	
    	onChangeToListFragment();
    	
        return rootView; 
    }
	
	
	public void onChangeToListFragment() {
		String tableName = C.LIST_FRAGMENT_TABLENAMES[activity.selectedItem - 2];
		String nameColumn = C.getNameColumnFor(tableName);
		
		adapter = new IconicAdapter(activity, currentContent); 
        contentList = (ListView) rootView.findViewById(R.id.contentList);
        contentList.setAdapter(adapter);
        currentContent.clear();
		
		Cursor cursor = DB.query(tableName, new String[] {nameColumn}, null, null, null, null, nameColumn);
		cursor.moveToFirst();
		while (! cursor.isAfterLast()) {
			currentContent.names.add(cursor.getString(0));
			cursor.moveToNext();
		} 
		cursor.close();
	}
	

	@Override
	public String getFragmentClass() {
		return "ListFragment";
	}
    
	
}
