## AndroidNewResideMenu (fork **[AndroidResideMenu](https://github.com/SpecialCyCi/AndroidResideMenu)**)

**This fork was made WhoYouAndM3. The original project (hereinafter **[AndroidResideMenu](https://github.com/SpecialCyCi/AndroidResideMenu)**) with an MIT license does not bear any responsibility for this fork **[AndroidRsideMenu](https://github.com/l3ger0j/AndroidRsideMenu.git)**.**

## Requirements
1. **[Android Studio](https://developer.android.com/studio/index.html)**
2. Android SDK (included in the package of Android Studio)
3. Android Virtual Device or not used smartphone (Android Version 5.0+)

## Import
### Gradle (temporarily not supported)

### Other
1. Clone the repository from:  
   `  
   git clone --recursive https://github.com/l3ger0j/AndroidRsideMenu.git `
2. Import library project to your workspace.
3. Make it as a dependency library project to your main project.

## Usage
init ResideMenu: write these code in Activity onCreate()
```java
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);

        // create menu items;
        String titles[] = { "Home", "Profile", "Calendar", "Settings" };
        int icon[] = { R.drawable.icon_home, R.drawable.icon_profile, R.drawable.icon_calendar, R.drawable.icon_settings };

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }
```
If you want to use slipping gesture to operate(lock/unlock) the menu, override this code in Acitivity dispatchTouchEvent() (please duplicate the followed code in dispatchTouchEvent() of Activity.
```java
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }
```
**On some occasions, the slipping gesture function for locking/unlocking menu, may have conflicts with your widgets, such as viewpager. By then you can add the viewpager to ignored view, please refer to next chapter – Ignored Views.**

open/close menu with animation (default true)
```java
resideMenu.openMenu(ResideNewMenu.DIRECTION_LEFT); // or ResideMenu.DIRECTION_RIGHT
resideMenu.closeMenu();
```

open/close menu withnout animation
```java
resideMenu.openMenu(ResideNewMenu.DIRECTION_LEFT, false); // or ResideNewMenu.DIRECTION_RIGHT
resideMenu.closeMenu();
```

listen in the menu state
```java
    resideMenu.setMenuListener(menuListener);
    private ResideNewMenu.OnMenuListener menuListener = new ResideNewMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };
```

disable a swipe direction
```java
  resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
```

use icons from iconify:
```java
IconDrawable drawable = new IconDrawable(context, FontAwesomeIcons.fa_home)
                           .colorRes(R.color.ab_icon)
                           .actionBarSize();
itemHome = new ResideNewMenuItem(this, drawable, "Home");
```

load background from URL:
```java
//Use this block on your Application class
ResideNewMenu.imageLoader = new ResideNewMenu.ImageLoader() {
    @Override
    public void loadFromUrl(String url, ImageView imageView) {
        //Exemple of loading from URL with picasso
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }
};
//and load from a url
resideMenu.setBackground("some url here");
```

## Custom Usage

Do your reside menu configurations, by creating an instance of ResideMenu with your custom layout's resource Ids. If you want to use default layout, just pass that variable as -1.

```java
        resideMenu = new ResideNewMenu(activity, R.layout.menu_left, R.layout.menu_right);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(activity);
        resideMenu.setScaleValue(0.5f);

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
```

As your configuration's completed, now you can customize side menus by getting instances of them as following:

```java
        View leftMenu = resideMenu.getLeftMenuView();
        // TODO: Do whatever you need to with leftMenu
        View rightMenu = resideMenu.getRightMenuView();
        // TODO: Do whatever you need to with rightMenu
```

## Ignored Views
On some occasions, the slipping gesture function for locking/unlocking menu, may have conflicts with your widgets such as viewpager. By then you can add the viewpager to ignored view.
```java
        // add gesture operation's ignored views
        FrameLayout ignored_view = (FrameLayout) findViewById(R.id.ignored_view);
        resideMenu.addIgnoredView(ignored_view);
```
So that in ignored view’s workplace, the slipping gesture will not be allowed to operate menu.

## Know issues
1. When trying to switch from the open left menu to the right (and vice versa), the shadow layout closes access to the left or right menu, respectively.
2. These methods are outdated and they need to come up with a replacement: fitSystemWindows and setTransitionStyle (setTransitionStyle was deleted).
3. There are quite noticeable performance issues

## Thanks
1. **[@akobylianskyi](https://github.com/akobylianskyi)** (https://github.com/SpecialCyCi/AndroidResideMenu/pull/101);
2. **[@faogustavo](https://github.com/faogustavo)** (https://github.com/SpecialCyCi/AndroidResideMenu/pull/114);
3. **[@niteshsirohi1](https://github.com/niteshsirohi1)** (https://github.com/SpecialCyCi/AndroidResideMenu/pull/124);
4. **[@rivasedg261](https://github.com/rivasedg261)** (https://github.com/SpecialCyCi/AndroidResideMenu/pull/162).
