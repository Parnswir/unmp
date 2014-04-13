package com.parnswir.unmp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LibraryActivity extends Activity {
	
	private ListView mLibraryFolders;
	private String m_chosenDir = "";
    private boolean m_newFolderEnabled = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		setupFolderList();
		setupActionBar();
	}
	
	private void setupFolderList() {
		String[] folders = {getString(R.string.addFolderToLibrary)};
		mLibraryFolders = (ListView) findViewById(R.id.libraryFolders);
		mLibraryFolders.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, folders));
		mLibraryFolders.setOnItemClickListener(new FolderClickListener());
	}
	
	private class FolderClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        TextView item = (TextView) view;
	        String selectedText = item.getText().toString();
	        String addFolder = getString(R.string.addFolderToLibrary);
	        
	        if (selectedText.equals(addFolder)) {
	        	showDirectorySelector();
	        }
	    	return;
	    }
	}
	
	private void showDirectorySelector() {
		DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog(
			LibraryActivity.this, 
	        new DirectoryChooserDialog.ChosenDirectoryListener() {
	            @Override
	            public void onChosenDir(String chosenDir) {
	                m_chosenDir = chosenDir;
	                Toast.makeText(
	                LibraryActivity.this, "Chosen directory: " + 
	                chosenDir, Toast.LENGTH_LONG).show();
	            }
		});
		directoryChooserDialog.chooseDirectory();
	}

	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.library, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
