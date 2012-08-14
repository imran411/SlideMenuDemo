package com.android.jaxvy.slidemenudemo.util;

//Update the package name to match your app
import com.android.jaxvy.slidemenudemo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SlideMenuAnimationContainer extends ViewGroup {

	public final static int DURATION = 400;

	protected boolean isOpened;
	protected View slideMenuLinerLayout;
	protected View contentLinearLayout;
	protected int slideMenuWidth = 150;

	protected Animation slideMenuAnimation;
	protected SlideMenuOpenListener slideMenuOpenListener;
	protected SlideMenuCloseListener slideMenuCloseListener;
	protected Listener mListener;

	protected boolean isPressed = false;

	protected MenuItemSelectedAction menuItemSelectedAction;

	public SlideMenuAnimationContainer( Context context){
		this( context, null);
	}

	public SlideMenuAnimationContainer( Context context, AttributeSet attrs){
		super( context, attrs);
	}

	@Override
	public void onFinishInflate(){

		super.onFinishInflate();
		slideMenuLinerLayout = findViewById( R.id.slideMenuLinearLayout);
		if( slideMenuLinerLayout == null) {
			throw new NullPointerException( "Activity must have a LinearLayout with id:slideMenuLinearLayout in which the menu will be embedded");
		}

		contentLinearLayout = findViewById( R.id.contentLinearLayout);
		if( contentLinearLayout == null) {
			throw new NullPointerException( "Activity must have a LinearLayout with id:contentLinearLayout in which content will be embedded");
		}

		// Initialize SlideMenu listeners with default implementations
		slideMenuOpenListener = new SlideMenuOpenListener( slideMenuLinerLayout, contentLinearLayout);
		slideMenuCloseListener = new SlideMenuCloseListener( slideMenuLinerLayout, contentLinearLayout);
	}

	@Override
	public void onLayout( boolean changed, int l, int t, int r, int b){

		/* the title bar assign top padding, drop it */
		slideMenuLinerLayout.layout(	l,
										0,
										l + slideMenuWidth,
										0 + slideMenuLinerLayout.getMeasuredHeight());
		if( isOpened) {
			contentLinearLayout.layout( l + slideMenuWidth,
										0,
										r + slideMenuWidth,
										b);
		}
		else {
			contentLinearLayout.layout( l, 0, r, b);
		}
	}

	@Override
	public void onMeasure( int w, int h){

		super.onMeasure( w, h);
		super.measureChildren( w, h);
		slideMenuWidth = slideMenuLinerLayout.getMeasuredWidth();
	}

	@Override
	protected void measureChild( View child, int parentWSpec, int parentHSpec){

		/* the max width of Sidebar is 90% of Parent */
		if( child == slideMenuLinerLayout) {
			int mode = MeasureSpec.getMode( parentWSpec);
			int width = (int) (getMeasuredWidth() * 0.9);
			super.measureChild( child, MeasureSpec.makeMeasureSpec( width, mode), parentHSpec);
		}
		else {
			super.measureChild( child, parentWSpec, parentHSpec);
		}
	}

	@Override
	public boolean onInterceptTouchEvent( MotionEvent ev){

		if( !isOpening()) {
			return false;
		}

		int action = ev.getAction();

		if( action != MotionEvent.ACTION_UP && action != MotionEvent.ACTION_DOWN) {
			return false;
		}

		/*
		 * if user press and release both on Content while sidebar is opening,
		 * call listener. otherwise, pass the event to child.
		 */
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		if( contentLinearLayout.getLeft() < x && contentLinearLayout.getRight() > x && contentLinearLayout.getTop() < y && contentLinearLayout.getBottom() > y) {
			if( action == MotionEvent.ACTION_DOWN) {
				isPressed = true;
			}

			if( isPressed && action == MotionEvent.ACTION_UP && mListener != null) {
				isPressed = false;
				return mListener.onContentTouchedWhenOpening();
			}
		}
		else {
			isPressed = false;
		}

		return false;
	}

	public void setListener( Listener l){

		mListener = l;
	}

	/* to see if the Sidebar is visible */
	public boolean isOpening(){

		return isOpened;
	}

	public void toggleSlideMenu(){

		if( contentLinearLayout.getAnimation() != null) {
			return;
		}

		if( isOpened) {
			/* opened, make close animation */
			slideMenuAnimation = new TranslateAnimation( 0, -slideMenuWidth, 0, 0);
			slideMenuAnimation.setAnimationListener( slideMenuCloseListener);
		}
		else {
			/* not opened, make open animation */
			slideMenuAnimation = new TranslateAnimation( 0, slideMenuWidth, 0, 0);
			slideMenuAnimation.setAnimationListener( slideMenuOpenListener);
		}
		slideMenuAnimation.setDuration( DURATION);
		slideMenuAnimation.setFillAfter( true);
		slideMenuAnimation.setFillEnabled( true);
		contentLinearLayout.startAnimation( slideMenuAnimation);
	}

	public void openSlideMenu(){

		if( !isOpened) {
			toggleSlideMenu();
		}
	}

	public void closeSlideMenu(){

		if( isOpened) {

			slideMenuCloseListener.isActionRequired( false);
			toggleSlideMenu();
		}
	}

	public void closeSlideMenuAndActOnClick( AdapterView<?> parent, View view, int position, long id){

		if( isOpened) {

			slideMenuCloseListener.isActionRequired( true);
			slideMenuCloseListener.setClickActionArguments( parent, view, position, id);
			toggleSlideMenu();
		}
	}

	public class SlideMenuOpenListener implements Animation.AnimationListener {

		View slideMenuView;
		View contentView;

		public SlideMenuOpenListener( View slideMenuView, View contentView){

			this.slideMenuView = slideMenuView;
			this.contentView = contentView;
		}

		public void onAnimationRepeat( Animation animation){

		}

		public void onAnimationStart( Animation animation){

			slideMenuView.setVisibility( View.VISIBLE);
		}

		public void onAnimationEnd( Animation animation){

			contentView.clearAnimation();
			isOpened = !isOpened;
			requestLayout();
			if( mListener != null) {
				mListener.onSlideMenuOpened();
			}
		}
	}

	public void setMenuItemSelectedAction( MenuItemSelectedAction menuItemSelectedAction){

		this.menuItemSelectedAction = menuItemSelectedAction;
	}

	public class SlideMenuCloseListener implements Animation.AnimationListener {

		View slideMenuView;
		View contentView;

		boolean isActionRequired;
		
		// Information about the ListView item when clicked
		AdapterView<?> parent;
		View view;
		int position;
		long id;
		
		public SlideMenuCloseListener( View slideMenuView, View contentView){

			this.slideMenuView = slideMenuView;
			this.contentView = contentView;
		}

		public void isActionRequired( boolean isActionRequired){
			this.isActionRequired = isActionRequired; 
		}
		
		public void setClickActionArguments( AdapterView<?> parent, View view, int position, long id){
			this.parent = parent;
			this.view = view;
			this.position = position;
			this.id = id;
		}
		
		public void onAnimationRepeat( Animation animation){

		}

		public void onAnimationStart( Animation animation){

		}

		public void onAnimationEnd( Animation animation){

			contentView.clearAnimation();
			slideMenuView.setVisibility( View.INVISIBLE);
			isOpened = !isOpened;
			requestLayout();
			if( mListener != null) {
				mListener.onSlideMenuClosed();
			}
			
			if( isActionRequired == true){
				menuItemSelectedAction.execute( parent, view, position, id);
			}
		}
	}

	public interface Listener {

		public void onSlideMenuOpened();

		public void onSlideMenuClosed();

		public boolean onContentTouchedWhenOpening();
	}

	public interface MenuItemSelectedAction {

		public void execute( AdapterView<?> parent, View view, int position, long id);
	}
}
