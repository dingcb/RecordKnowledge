package colortrackview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dcb.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;


public class RoundColorTrackTabLayout extends TabLayout {
    private int mTabTextSize;
    private int mTabSelectedTextColor;
    private int tabIndicatorColor;
    private int mTabTextColor;
    private  int INVALID_TAB_POS = -1;
    /*
    * 最后选中的postition
    */
    private int mLastSelectedTabPosition = INVALID_TAB_POS;

    private ColorTrackTabLayoutOnPageChangeListener mPageChangeListenter;
    private ViewPager mViewPager;


    public RoundColorTrackTabLayout(Context context) {
        this(context, null);
    }

    public RoundColorTrackTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundColorTrackTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.ColorTrackTabLayout);
            try {
                mTabTextSize = ta.getDimensionPixelSize(
                        R.styleable.ColorTrackTabLayout_textSize, 0);
                mTabTextColor = ta.getColor(
                        R.styleable.ColorTrackTabLayout_tabTextColor, 0);
                mTabSelectedTextColor = ta.getColor(
                        R.styleable.ColorTrackTabLayout_tabSelectedTextColor, 0);
                tabIndicatorColor = ta.getColor(R.styleable.ColorTrackTabLayout_tabIndicatorColor, Color.BLACK);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ta.recycle();

        }
    }

    @Override
    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        RoundColorTrackView colorTrackView = new RoundColorTrackView(getContext());
        colorTrackView.setTabIndicatorColor(tabIndicatorColor);
        colorTrackView.setProgress(setSelected ? 1 : 0);
        colorTrackView.setText(tab.getText() + "");
        colorTrackView.setTextSize(mTabTextSize);
        colorTrackView.setTag(position);
        colorTrackView.setTextChangeColor(mTabSelectedTextColor);
        colorTrackView.setTextOriginColor(mTabTextColor);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        colorTrackView.setLayoutParams(layoutParams);
        tab.setCustomView(colorTrackView);

        super.addTab(tab, position, setSelected);
        int selectedTabPosition = getSelectedTabPosition();
        if ((selectedTabPosition == INVALID_TAB_POS && position == 0) || (selectedTabPosition == position)) {
            setSelectedView(position);
        }

        setTabWidth(position, colorTrackView);
    }

    private void setTabWidth(int position, RoundColorTrackView colorTrackView) {
        ViewGroup slidingTabStrip = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) slidingTabStrip.getChildAt(position);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        int w = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        //手动测量一下
        colorTrackView.measure(w, h);
        params.width = colorTrackView.getMeasuredWidth() + tabView.getPaddingLeft() + tabView.getPaddingRight();
        //设置tabView的宽度
        tabView.setLayoutParams(params);
    }

    /**
     * 设置每个Tab的左内边距和右内边距
     *
     * @param left
     * @param right
     */
    public void setTabPaddingLeftAndRight(int left, int right) {
        try {
            Field mTabPaddingStartField = TabLayout.class.getDeclaredField("mTabPaddingStart");
            Field mTabPaddingEndField = TabLayout.class.getDeclaredField("mTabPaddingEnd");

            mTabPaddingStartField.setAccessible(true);
            mTabPaddingEndField.setAccessible(true);

            mTabPaddingStartField.set(this, left);
            mTabPaddingEndField.set(this, right);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        super.setupWithViewPager(viewPager, autoRefresh);
        try {
            if (viewPager != null)
                mViewPager = viewPager;
            //通过反射找到mPageChangeListener
            Field field = TabLayout.class.getDeclaredField("mPageChangeListener");
            field.setAccessible(true);
            TabLayoutOnPageChangeListener listener = (TabLayoutOnPageChangeListener) field.get(this);
            if (listener != null) {
                //删除自带监听
                viewPager.removeOnPageChangeListener(listener);
                mPageChangeListenter = new ColorTrackTabLayoutOnPageChangeListener(this);
                mPageChangeListenter.reset();
                viewPager.addOnPageChangeListener(mPageChangeListenter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void tabScrolled(int position, float positionOffset) {

        if (positionOffset == 0.0F) {
            return;
        }
        RoundColorTrackView currentTrackView = getRoundColorTrackView(position);
        RoundColorTrackView nextTrackView = getRoundColorTrackView(position + 1);
        currentTrackView.setDirection(1);
        currentTrackView.setProgress(1.0F - positionOffset);
        nextTrackView.setDirection(0);
        nextTrackView.setProgress(positionOffset);
    }

    public RoundColorTrackView getRoundColorTrackView(int position) {
        return (RoundColorTrackView) getTabAt(position).getCustomView();
    }

    public static class ColorTrackTabLayoutOnPageChangeListener extends TabLayoutOnPageChangeListener {

        private final WeakReference<RoundColorTrackTabLayout> mTabLayoutRef;
        private int mPreviousScrollState;
        private int mScrollState;

        public ColorTrackTabLayoutOnPageChangeListener(TabLayout tabLayout) {
            super(tabLayout);
            mTabLayoutRef = new WeakReference<>((RoundColorTrackTabLayout) tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            RoundColorTrackTabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout == null) return;
            final boolean updateText = mScrollState != SCROLL_STATE_SETTLING ||
                    mPreviousScrollState == SCROLL_STATE_DRAGGING;
            if (updateText) {
                tabLayout.tabScrolled(position, positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            RoundColorTrackTabLayout tabLayout = mTabLayoutRef.get();
            mPreviousScrollState = SCROLL_STATE_SETTLING;
            tabLayout.setSelectedView(position);
        }

        void reset() {
            mPreviousScrollState = mScrollState = SCROLL_STATE_IDLE;
        }

    }


    protected void setSelectedView(int position) {
        final int tabCount = getTabCount();
        if (position < tabCount) {
            for (int i = 0; i < tabCount; i++) {
                getRoundColorTrackView(i).setProgress(i == position ? 1 : 0);
            }
        }
    }

    @Override
    public void removeAllTabs() {
        // Retain last selected position before removing all tabs
        mLastSelectedTabPosition = getSelectedTabPosition();
        super.removeAllTabs();
    }

    @Override
    public int getSelectedTabPosition() {
        // Override selected tab position to return your last selected tab position
        final int selectedTabPositionAtParent = super.getSelectedTabPosition();
        return selectedTabPositionAtParent == INVALID_TAB_POS ?
                mLastSelectedTabPosition : selectedTabPositionAtParent;
    }

    public void setLastSelectedTabPosition(int lastSelectedTabPosition) {
        mLastSelectedTabPosition = lastSelectedTabPosition;
    }

    public void setCurrentItem(int position) {
        if (mViewPager != null)
            mViewPager.setCurrentItem(position);
    }
}
