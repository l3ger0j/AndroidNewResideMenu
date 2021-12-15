package org.l3ger0j.library;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ResideNewMenuItem extends LinearLayout{

    /** menu item  icon  */
    private ImageView iv_icon;
    /** menu item  title */
    private TextView tv_title;

    public ResideNewMenuItem(Context context) {
        super(context);
        initViews(context);
    }

    public ResideNewMenuItem(Context context, int icon, int title) {
        super(context);
        initViews(context);
        iv_icon.setImageResource(icon);
        tv_title.setText(title);
    }

    public ResideNewMenuItem(Context context, int icon, String title) {
        super(context);
        initViews(context);
        iv_icon.setImageResource(icon);
        tv_title.setText(title);
    }

    public ResideNewMenuItem (Context context, Drawable icon, int title) {
        super(context);
        initViews(context);
        iv_icon.setImageDrawable(icon);
        tv_title.setText(title);
    }

    public ResideNewMenuItem (Context context, Drawable icon, String title) {
        super(context);
        initViews(context);
        iv_icon.setImageDrawable(icon);
        tv_title.setText(title);
    }

    public ResideNewMenuItem(Context context, String icon, int title) {
        super(context);
        initViews(context);

        tv_title.setText(title);
        if (ResideNewMenu.imageLoader != null) {
            ResideNewMenu.imageLoader.loadFromUrl(icon, iv_icon);
        } else {
            Log.e(ResideNewMenu.class.getName(), "ImageLoader not defined.");
        }
    }

    public ResideNewMenuItem(Context context, String icon, String title) {
        super(context);
        initViews(context);

        tv_title.setText(title);
        if (ResideNewMenu.imageLoader != null) {
            ResideNewMenu.imageLoader.loadFromUrl(icon, iv_icon);
        } else {
            Log.e(ResideNewMenu.class.getName(), "ImageLoader not defined.");
        }
    }

    private void initViews(@NonNull Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.menu_item , this);
        iv_icon = findViewById(R.id.iv_icon);
        tv_title = findViewById(R.id.tv_title);
    }

    /**
     * set the icon resource;
     *
     * @param icon int
     */
    public void setIcon(int icon){
        iv_icon.setImageResource(icon);
    }

    /**
     * set the icon drawable;
     *
     * @param icon Drawable
     */
    public void setIcon(Drawable icon){
        iv_icon.setImageDrawable(icon);
    }

    /**
     * set the icon url;
     *
     * @param icon String
     */
    public void setIcon(String icon){
        if (ResideNewMenu.imageLoader != null) {
            ResideNewMenu.imageLoader.loadFromUrl(icon, iv_icon);
        } else {
            Log.e(ResideNewMenu.class.getName(), "ImageLoader not defined.");
        }
    }

    /**
     * set the title with resource;
     *
     * @param title resource
     */
    public void setTitle(int title){
        tv_title.setText(title);
    }

    /**
     * set the title with string;
     *
     * @param title string
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }
}
