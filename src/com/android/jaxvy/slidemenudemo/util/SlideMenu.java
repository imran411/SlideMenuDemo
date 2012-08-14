package com.android.jaxvy.slidemenudemo.util;

import com.android.jaxvy.slidemenudemo.R;
import com.android.jaxvy.slidemenudemo.activity.Activity1;
import com.android.jaxvy.slidemenudemo.activity.Activity2;
import com.android.jaxvy.slidemenudemo.activity.Activity3;
import com.android.jaxvy.slidemenudemo.util.SlideMenuAnimationContainer.MenuItemSelectedAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SlideMenu implements SlideMenuAnimationContainer.Listener {

	public final static String[] slideMenuOptions = { "Activity 1", "Activity 2", "Activity 3" };
	public final static String TAG = "SlideMenu";

	private Context context;
	private SlideMenuAnimationContainer slideMenuAnimationContainer;

	public SlideMenu( Context context, SlideMenuAnimationContainer mainAnimationLayout){

		this.context = context;
		this.slideMenuAnimationContainer = mainAnimationLayout;
	}

	public void init(){

		final Activity activity = (Activity) context;

		//Set Content's show menu button's action
		Button showMenuButton = (Button) activity.findViewById( R.id.content_button);
		showMenuButton.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick( View v){

				slideMenuAnimationContainer.toggleSlideMenu();
			}
		});

		//Call slideMenuAnimationContainer.closeSlideMenuAndActOnClick() is called to act the implemented logic.
		//You can integrate you custom adapter here
		ListView menuListView = (ListView) activity.findViewById( R.id.slideMenuListView);
		menuListView.setAdapter( new ArrayAdapter<String>( context, android.R.layout.simple_list_item_1, slideMenuOptions));
		menuListView.setOnItemClickListener( new OnItemClickListener(){

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id){

				// Close sidebar
				slideMenuAnimationContainer.closeSlideMenuAndActOnClick( parent, view, position, id);
			}
		});
		
		//Implement your logic here within the execute() function when an item is clicked with in the SlideMenu
		//Called after the SlideMenu collapses.
		slideMenuAnimationContainer.setMenuItemSelectedAction( new MenuItemSelectedAction(){

			@Override
			public void execute( AdapterView<?> parent, View view, int position, long id){

				//Start new activity
				CharSequence selectedActivityName = ((TextView) view).getText();

				Intent intent;
				if( selectedActivityName.equals( slideMenuOptions[0])) {
					intent = new Intent( activity, Activity1.class);
				}
				else if( selectedActivityName.equals( slideMenuOptions[1])) {
					intent = new Intent( activity, Activity2.class);
				}
				else {
					intent = new Intent( activity, Activity3.class);
				}
				intent.setFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION);
				activity.startActivity( intent);
			}
		});
	}

	//Callback of SlideMenuAnimationContainer.Listener to monitor status of SlideMenu
	@Override
	public void onSlideMenuOpened(){

		Log.d( TAG, "opened");
	}

	//Callback of SlideMenuAnimationContainer.Listener to monitor status of SlideMenu
	@Override
	public void onSlideMenuClosed(){

		Log.d( TAG, "closed");
	}

	//Callback of SlideMenuAnimationContainer.Listener to monitor status of SlideMenu
	@Override
	public boolean onContentTouchedWhenOpening(){

		//The content area is touched when sidebar opening, close sidebar
		Log.d( TAG, "going to close sidebar");
		slideMenuAnimationContainer.closeSlideMenu();
		return true;
	}
}
