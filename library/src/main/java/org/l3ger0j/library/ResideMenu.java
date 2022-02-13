package org.l3ger0j.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ResideMenu extends FrameLayout {

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;
    private static final int PRESSED_MOVE_HORIZONTAL = 2;
    private static final int PRESSED_DOWN = 3;
    private static final int PRESSED_DONE = 4;
    private static final int PRESSED_MOVE_VERTICAL = 5;

    private ImageView imageViewShadow;
    private ImageView imageViewBackground;
    private View leftMenuView;
    private View rightMenuView;
    private View showingMenuView;
    private LinearLayout layoutLeftMenu;
    private LinearLayout layoutRightMenu;
    private View scrollViewLeftMenu;
    private View scrollViewRightMenu;
    /**
     * Current attaching activity.
     */
    private Activity activity;
    /**
     * The DecorView of current activity.
     */
    private ViewGroup viewDecor;
    private TouchDisableView viewActivity;
    /**
     * The flag of menu opening status.
     */
    private boolean isOpened;
    private float shadowAdjustScaleX;
    private float shadowAdjustScaleY;
    /**
     * Views which need stop to intercept touch events.
     */
    private List<View> ignoredViews;
    private List<ResideMenuItem> leftMenuItems;
    private List<ResideMenuItem> rightMenuItems;
    private final DisplayMetrics displayMetrics = new DisplayMetrics();
    private OnMenuListener menuListener;
    private float lastRawX;
    private boolean isInIgnoredView = false;
    private int scaleDirection = DIRECTION_LEFT;
    private int pressedState = PRESSED_DOWN;
    private final List<Integer> disabledSwipeDirection = new ArrayList<>();
    // Valid scale factor is between 0.0f and 1.0f.
    private float mScaleValue = 0.5f;
    private boolean mUse3D;
    private static final int ROTATE_Y_ANGLE = 10;

    public static ImageLoader imageLoader;

    /*
     * private MenuCustomBinding menuCustomBinding;
     * private MenuCustomLeftScrollviewBinding leftScrollviewBinding;
     * private MenuCustomRightScrollviewBinding rightScrollviewBinding;
     * private MenuItemBinding itemBinding;
     */

    public interface ImageLoader{
        void loadFromUrl(String url, ImageView imageView);
    }

    public ResideMenu(Context context) {
        super(context);
        initViews(context, -1, -1);
    }

    /**
     * This constructor provides you to create menus with your own custom
     * layouts, but if you use custom menu then do not call addMenuItem because
     * it will not be able to find default views
     */
    public ResideMenu(Context context, int customLeftMenuId,
                      int customRightMenuId) {
        super(context);
        initViews(context, customLeftMenuId, customRightMenuId);
    }

    private void initViews(@NonNull Context context, int customLeftMenuId,
                           int customRightMenuId) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.menu_custom , this);

        if (customLeftMenuId >= 0) {
            scrollViewLeftMenu = inflater.inflate(customLeftMenuId, this, false);
        } else {
            scrollViewLeftMenu = inflater.inflate(
                    R.layout.menu_custom_left_scrollview , this, false);
            layoutLeftMenu = scrollViewLeftMenu.findViewById(R.id.layout_left_menu);
        }

        if (customRightMenuId >= 0) {
            scrollViewRightMenu = inflater.inflate(customRightMenuId, this, false);
        } else {
            scrollViewRightMenu = inflater.inflate(
                    R.layout.menu_custom_right_scrollview , this, false);
            layoutRightMenu = scrollViewRightMenu.findViewById(R.id.layout_right_menu);
        }

        imageViewShadow = findViewById(R.id.iv_shadow);
        imageViewBackground = findViewById(R.id.iv_background);
        leftMenuView = scrollViewLeftMenu;
        rightMenuView = scrollViewRightMenu;
        RelativeLayout menuHolder = findViewById(R.id.sv_menu_holder);
        menuHolder.addView(scrollViewLeftMenu);
        menuHolder.addView(scrollViewRightMenu);
    }

    /**
     * Returns left menu view so you can findViews and do whatever you want with
     */
    public View getLeftMenuView() {
        return scrollViewLeftMenu;
    }

    /**
     * Returns right menu view so you can findViews and do whatever you want with
     */
    public View getRightMenuView() {
        return scrollViewRightMenu;
    }

    @Override
    @Deprecated
    protected boolean fitSystemWindows(@NonNull Rect insets) {
        // Applies the content insets to the view's padding, consuming that
        // content (modifying the insets to be 0),
        // and returning true. This behavior is off by default and can be
        // enabled through setFitsSystemWindows(boolean)
        // in api14+ devices.

        // This is added to fix soft navigationBar's overlapping to content above LOLLIPOP
        int bottomPadding = viewActivity.getPaddingBottom() + insets.bottom;
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (!hasBackKey || !hasHomeKey) {//there's a navigation bar
            bottomPadding += getNavigationBarHeight();
        }

        this.setPadding(viewActivity.getPaddingLeft() + insets.left,
                viewActivity.getPaddingTop() + insets.top,
                viewActivity.getPaddingRight() + insets.right,
                bottomPadding);
        insets.left = insets.top = insets.right = insets.bottom = 0;
        return true;
    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * Set up the activity;
     *
     * @param activity activity
     */
    public void attachToActivity(Activity activity) {
        initValue(activity);
        setShadowAdjustScaleXByOrientation();
        viewDecor.addView(this, 0);
    }

    private void initValue(@NonNull Activity activity) {
        this.activity = activity;
        leftMenuItems = new ArrayList<>();
        rightMenuItems = new ArrayList<>();
        ignoredViews = new ArrayList<>();
        viewDecor = (ViewGroup) activity.getWindow().getDecorView();
        viewActivity = new TouchDisableView(this.activity);

        View mContent = viewDecor.getChildAt(0);
        viewDecor.removeViewAt(0);
        viewActivity.setContent(mContent);
        addView(viewActivity);

        ViewGroup parent = (ViewGroup) scrollViewLeftMenu.getParent();
        parent.removeView(scrollViewLeftMenu);
        parent.removeView(scrollViewRightMenu);
    }

    private void setShadowAdjustScaleXByOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            shadowAdjustScaleX = 0.034f;
            shadowAdjustScaleY = 0.12f;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            shadowAdjustScaleX = 0.06f;
            shadowAdjustScaleY = 0.07f;
        }
    }

    /**
     * Set the background image of menu;
     *
     * @param imageResource int
     */
    public void setBackground (int imageResource) {
        imageViewBackground.setImageResource(imageResource);
    }

    public void setBackground(String imageUrl) {
        if (imageLoader != null) {
            imageLoader.loadFromUrl(imageUrl, imageViewBackground);
        } else {
            Log.e(ResideMenu.class.getName(), "ImageLoader not defined.");
        }
    }

    /**
     * The visibility of the shadow under the activity;
     *
     * @param isVisible boolean
     */
    public void setShadowVisible(boolean isVisible) {
        if (isVisible)
            imageViewShadow.setBackgroundResource(R.drawable.shadow);
        else
            imageViewShadow.setBackgroundResource(0);
    }

    /**
     * Add a single item to the left menu;
     * <p/>
     * WARNING: It will be removed from v2.0.
     *
     * @param menuItem ResideMenuItem
     */
    @Deprecated
    public void addMenuItem(ResideMenuItem menuItem) {
        this.leftMenuItems.add(menuItem);
        layoutLeftMenu.addView(menuItem);
    }

    /**
     * Add a single items;
     *
     * @param menuItem ResideMenuItem
     * @param direction int
     */
    public void addMenuItem(ResideMenuItem menuItem, int direction) {
        if (direction == DIRECTION_LEFT) {
            this.leftMenuItems.add(menuItem);
            layoutLeftMenu.addView(menuItem);
        } else {
            this.rightMenuItems.add(menuItem);
            layoutRightMenu.addView(menuItem);
        }
    }

    /**
     * WARNING: It will be removed from v2.0.
     *
     * @param menuItems List(ResideMenuItem)
     */
    @Deprecated
    public void setMenuItems(List<ResideMenuItem> menuItems) {
        this.leftMenuItems = menuItems;
        rebuildMenu();
    }

    /**
     * Set menu items by a array;
     *
     * @param menuItems List(ResideMenuItem)
     * @param direction int
     */
    public void setMenuItems(List<ResideMenuItem> menuItems, int direction) {
        if (direction == DIRECTION_LEFT)
            this.leftMenuItems = menuItems;
        else
            this.rightMenuItems = menuItems;
        rebuildMenu();
    }

    private void rebuildMenu() {
        if (layoutLeftMenu != null) {
            layoutLeftMenu.removeAllViews();
            for (ResideMenuItem leftMenuItem : leftMenuItems)
                layoutLeftMenu.addView(leftMenuItem);
        }

        if (layoutRightMenu != null) {
            layoutRightMenu.removeAllViews();
            for (ResideMenuItem rightMenuItem : rightMenuItems)
                layoutRightMenu.addView(rightMenuItem);
        }
    }

    /**
     * WARNING: It will be removed from v2.0.
     *
     * @return List(ResideMenuItem)
     */
    @Deprecated
    public List<ResideMenuItem> getMenuItems() {
        return leftMenuItems;
    }

    /**
     * Return instances of menu items;
     *
     * @return List(ResideMenuItem)
     */
    public List<ResideMenuItem> getMenuItems(int direction) {
        if (direction == DIRECTION_LEFT)
            return leftMenuItems;
        else
            return rightMenuItems;
    }

    /**
     * If you need to do something on closing or opening menu,
     * set a listener here.
     *
     */
    public void setMenuListener(OnMenuListener menuListener) {
        this.menuListener = menuListener;
    }


    public OnMenuListener getMenuListener() {
        return menuListener;
    }

    /**
     * Show the menu;
     */
    public void openMenu(int direction){
        openMenu(direction, true);
    }

    public void openMenu(int direction, boolean withAnimations){
        setScaleDirection(direction);
        isOpened = true;
        int duration = withAnimations ? 250 : 0;
        AnimatorSet activityScaleDown = buildScaleDownAnimation(viewActivity, mScaleValue, mScaleValue, duration);
        AnimatorSet shadowScaleDown = buildScaleDownAnimation(imageViewShadow,
                mScaleValue + shadowAdjustScaleX, mScaleValue + shadowAdjustScaleY, duration);
        AnimatorSet menuAlpha = buildMenuAnimation(showingMenuView, 1.0f, duration);
        shadowScaleDown.addListener(animationListener);
        activityScaleDown.playTogether(shadowScaleDown);
        activityScaleDown.playTogether(menuAlpha);
        activityScaleDown.start();
    }

    /**
     * Close the menu;
     */
    public void closeMenu(){
        closeMenu(true);
    }

    public void closeMenu(boolean withAnimations) {
        isOpened = false;
        int duration = withAnimations ? 250 : 0;
        AnimatorSet activityScaleUp = buildScaleUpAnimation(viewActivity, 1.0f, 1.0f, duration);
        AnimatorSet shadowScaleUp = buildScaleUpAnimation(imageViewShadow, 1.0f, 1.0f, duration);
        AnimatorSet alpha_menu = buildMenuAnimation(showingMenuView, 0.0f, duration);
        activityScaleUp.addListener(animationListener);
        activityScaleUp.playTogether(shadowScaleUp);
        activityScaleUp.playTogether(alpha_menu);
        activityScaleUp.start();
    }

        @Deprecated
    public void setDirectionDisable(int direction) {
        disabledSwipeDirection.add(direction);
    }

    public void setSwipeDirectionDisable(int direction) {
        disabledSwipeDirection.add(direction);
    }

    private boolean isInDisableDirection(int direction) {
        return disabledSwipeDirection.contains(direction);
    }

    private void setScaleDirection(int direction) {

        int screenWidth = getScreenWidth();
        float pivotX;
        float pivotY = getScreenHeight() * 0.5f;

        if (direction == DIRECTION_LEFT) {
            showingMenuView = leftMenuView;
            pivotX = screenWidth * 1.5f;
        } else {
            showingMenuView = rightMenuView;
            pivotX = screenWidth * -0.5f;
        }
        viewActivity.setPivotX(pivotX);
        viewActivity.setPivotY(pivotY);
        imageViewShadow.setPivotX(pivotX);
        imageViewShadow.setPivotY(pivotY);
        scaleDirection = direction;
    }

    /**
     * Return the flag of menu status;
     *
     * @return boolean
     */
    public boolean isOpened() {
        return isOpened;
    }

    /**
     * Return the current opening direction of menu.
     *
     * @return current direction
     */
    public int getCurrentDirection() {
        return scaleDirection;
    }

    private final OnClickListener viewActivityOnClickListener = view -> {
        if (isOpened()) closeMenu();
    };

    private final Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (isOpened()) {
                showMenu(showingMenuView);
                if (menuListener != null)
                    menuListener.openMenu();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            // reset the view;
            if (isOpened()) {
                viewActivity.setTouchDisable(true);
                viewActivity.setOnClickListener(viewActivityOnClickListener);
            } else {
                viewActivity.setTouchDisable(false);
                viewActivity.setOnClickListener(null);
                hideMenu(leftMenuView);
                hideMenu(rightMenuView);
                if (menuListener != null)
                    menuListener.closeMenu();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    /**
     * A helper method to build scale down animation;
     *
     * @param target view
     * @param targetScaleX float
     * @param targetScaleY float
     * @return animatorSet
     */
    @NonNull
    private AnimatorSet buildScaleDownAnimation(
            View target, float targetScaleX, float targetScaleY, int duration){

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
                ObjectAnimator.ofFloat(target, "scaleY", targetScaleY)
        );

        if (mUse3D) {
            float angle = scaleDirection == DIRECTION_LEFT ? -ROTATE_Y_ANGLE : ROTATE_Y_ANGLE;
            scaleDown.playTogether(ObjectAnimator.ofFloat(target, "rotationY", angle));
        }

        scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity,
                android.R.anim.decelerate_interpolator));
        scaleDown.setDuration(duration);
        return scaleDown;
    }

    /**
     * A helper method to build scale up animation;
     *
     * @param target view
     * @return animatorSet
     */
    @NonNull
    private AnimatorSet buildScaleUpAnimation(
            View target, float targetScaleX, float targetScaleY, int duration){

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", 1.0F),
                ObjectAnimator.ofFloat(target, "scaleY", 1.0F)
        );

        if (mUse3D) {
            scaleUp.playTogether(ObjectAnimator.ofFloat(target, "rotationY", 0));
        }

        scaleUp.setDuration(duration);
        return scaleUp;
    }

    @NonNull
    private AnimatorSet buildMenuAnimation(View target, float alpha, int duration){

        AnimatorSet alphaAnimation = new AnimatorSet();
        alphaAnimation.playTogether(
                ObjectAnimator.ofFloat(target, "alpha", alpha)
        );

        alphaAnimation.setDuration(duration);
        return alphaAnimation;
    }

    /**
     * If there were some view you don't want reside menu
     * to intercept their touch event, you could add it to
     * ignored views.
     *
     * @param v view
     */
    public void addIgnoredView(View v) {
        ignoredViews.add(v);
    }

    /**
     * Remove a view from ignored views;
     *
     * @param v view
     */
    public void removeIgnoredView(View v) {
        ignoredViews.remove(v);
    }

    /**
     * Clear the ignored view list;
     */
    public void clearIgnoredViewList() {
        ignoredViews.clear();
    }

    /**
     * If the motion event was relative to the view
     * which in ignored view list,return true;
     *
     * @param ev motionEvent
     * @return boolean
     */
    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        for (View v : ignoredViews) {
            v.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY()))
                return true;
        }
        return false;
    }

    private void setScaleDirectionByRawX(float currentRawX) {
        if (currentRawX < lastRawX)
            setScaleDirection(DIRECTION_RIGHT);
        else
            setScaleDirection(DIRECTION_LEFT);
    }

    private float getTargetScale(float currentRawX) {
        float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 0.75f;
        scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX : scaleFloatX;

        float targetScale = viewActivity.getScaleX() - scaleFloatX;
        targetScale = Math.min(targetScale , 1.0f);
        targetScale = Math.max(targetScale , 0.5f);
        return targetScale;
    }

    private float lastActionDownX, lastActionDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentActivityScaleX = viewActivity.getScaleX();
        if (currentActivityScaleX == 1.0f)
            setScaleDirectionByRawX(ev.getRawX());

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastActionDownX = ev.getX();
                lastActionDownY = ev.getY();
                isInIgnoredView = isInIgnoredView(ev) && !isOpened();
                pressedState = PRESSED_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isInIgnoredView || isInDisableDirection(scaleDirection))
                    break;

                if (pressedState != PRESSED_DOWN &&
                        pressedState != PRESSED_MOVE_HORIZONTAL)
                    break;

                int xOffset = (int) (ev.getX() - lastActionDownX);
                int yOffset = (int) (ev.getY() - lastActionDownY);

                if (pressedState == PRESSED_DOWN) {
                    if (yOffset > 25 || yOffset < -25) {
                        pressedState = PRESSED_MOVE_VERTICAL;
                        break;
                    }
                    if (xOffset < -50 || xOffset > 50) {
                        pressedState = PRESSED_MOVE_HORIZONTAL;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else if (pressedState == PRESSED_MOVE_HORIZONTAL) {
                    if (currentActivityScaleX < 0.95)
                        showMenu(showingMenuView);

                    float targetScale = getTargetScale(ev.getRawX());
                    if (mUse3D) {
                        int angle = scaleDirection == DIRECTION_LEFT ? -ROTATE_Y_ANGLE : ROTATE_Y_ANGLE;
                        angle *= (1 - targetScale) * 2;
                        viewActivity.setRotationY(angle);
                    }
                    imageViewShadow.setScaleX(targetScale - shadowAdjustScaleX);
                    imageViewShadow.setScaleY(targetScale - shadowAdjustScaleY);
                    viewActivity.setScaleX(targetScale);
                    viewActivity.setScaleY(targetScale);
                    showingMenuView.setAlpha((1 - targetScale) * 2.0f);
                    lastRawX = ev.getRawX();
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:

                if (isInIgnoredView) break;
                if (pressedState != PRESSED_MOVE_HORIZONTAL) break;

                pressedState = PRESSED_DONE;
                if (isOpened()) {
                    if (currentActivityScaleX > 0.56f)
                        closeMenu();
                    else
                        openMenu(scaleDirection);
                } else {
                    if (currentActivityScaleX < 0.94f) {
                        openMenu(scaleDirection);
                    } else {
                        closeMenu();
                    }
                }

                break;

        }
        lastRawX = ev.getRawX();
        return super.dispatchTouchEvent(ev);
    }

    public int getScreenHeight() {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void setScaleValue(float scaleValue) {
        this.mScaleValue = scaleValue;
    }

    public void setUse3D(boolean use3D) {
        mUse3D = use3D;
    }

    public interface OnMenuListener {

        /**
         * This method will be called at the finished time of opening menu animations.
         */
        void openMenu();

        /**
         * This method will be called at the finished time of closing menu animations.
         */
        void closeMenu();
    }

    private void showMenu (View menuView){
        if (menuView != null && menuView.getParent() == null){
            addView(menuView);
        }
    }

    private void hideMenu(View menuView){
        if (menuView != null && menuView.getParent() != null){
            removeView(menuView);
        }
    }
}
