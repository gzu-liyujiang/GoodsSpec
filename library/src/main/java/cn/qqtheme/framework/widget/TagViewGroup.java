package cn.qqtheme.framework.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.qqtheme.framework.GoodsSpec.R;

/**
 * 标签组布局，如热门搜索词、商品规格。参阅：http://blog.csdn.net/lmj623565791/article/details/38352503
 * <p>
 * Created by liyujiang on 2017/5/24 10:20.
 *
 * @see UiConfig
 * @see GoodsSpecView
 */
public class TagViewGroup extends ViewGroup implements CompoundButton.OnCheckedChangeListener {
    //存储所有的标签
    private List<TagValue> data = new ArrayList<>();
    //UI配置
    private UiConfig config = new UiConfig();
    //存储所有的View，按行记录
    private List<List<View>> allChildViews = new ArrayList<>();
    //记录每一行的最大高度
    private List<Integer> lineHeights = new ArrayList<>();
    //存储每一行的所有View
    private List<View> lineViews = new ArrayList<>();
    //选择成功回调
    private OnSelectedListener onSelectedListener;

    public TagViewGroup(Context context) {
        this(context, null);
    }

    public TagViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            setData(new String[]{"穿青人", "未定民族", "已识别民族"}, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //父控件传进来的宽度和高度以及对应的测量模式
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0;//自己测量的 宽度
        int height = 0;//自己测量的高度
        //记录每一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;
        //获取子view的个数
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //测量子View的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //子View占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //子View占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            Log.d(null, "onMeasure: 第" + i + "个子View: lineWidth=" + lineWidth + ",childWidth=" + childWidth);
            if (lineWidth + childWidth > sizeWidth) {//换行时候
                //对比得到最大的宽度
                width = Math.max(childWidth, lineWidth);
                //记录行高
                height += lineHeight;
                //重置行宽高
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {//不换行情况
                //叠加行宽
                lineWidth += childWidth;
                //得到最大行高
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //处理最后一个子View的情况
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        Log.d(null, "onMeasure: sizeWidth=" + sizeWidth + ",width=" + width + ",height=" + height
                + ",lineWidth=" + lineWidth + ",lineHeight=" + lineHeight);
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        allChildViews.clear();
        lineViews.clear();
        lineHeights.clear();
        //获取当前ViewGroup的宽度
        int width = getWidth();
        Log.d(null, "onLayout: width=" + width);

        int lineWidth = 0;
        int lineHeight = 0;
        //记录当前行的view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //如果需要换行
            if (lineWidth + childWidth > width - getPaddingLeft() - getPaddingRight()) {
                //记录LineHeight
                lineHeights.add(lineHeight);
                //记录当前行的Views
                allChildViews.add(lineViews);
                //重置行的宽高
                lineWidth = 0;
                lineHeight = childHeight;
                //重置view的集合
                lineViews = new ArrayList<>();
            }
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
        }
        //处理最后一行
        lineHeights.add(lineHeight);
        allChildViews.add(lineViews);
        //设置子View的位置
        int left = 0;
        int top = 0;
        //获取行数
        int lineCount = allChildViews.size();
        Log.d(null, "onLayout: 共" + lineCount + "行");
        for (int i = 0; i < lineCount; i++) {
            //当前行的views和高度
            lineViews = allChildViews.get(i);
            lineHeight = lineHeights.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                //判断是否显示
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int cLeft = left + lp.leftMargin + getPaddingLeft();
                int cTop = top + lp.topMargin + getPaddingTop();
                int cRight = cLeft + child.getMeasuredWidth();
                int cBottom = cTop + child.getMeasuredHeight();
                //进行子View进行布局
                child.layout(cLeft, cTop, cRight, cBottom);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = 0;
            top += lineHeight;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!config.isMultipleMode) {
            doNotCheckedAll();
        }
        if (isChecked) {
            buttonView.setChecked(true);
            buttonView.setTextColor(config.buttonSelectedTextColor);
            if (onSelectedListener != null) {
                for (TagValue value : data) {
                    if (value.getName().equals(buttonView.getText().toString())) {
                        onSelectedListener.onSelected(value);
                        break;
                    }
                }
            }
        } else {
            buttonView.setChecked(false);
            buttonView.setTextColor(config.buttonTextColor);
        }
    }

    public void doNotCheckedAll() {
        for (int i = 0; i < getChildCount(); i++) {
            CompoundButton button = (CompoundButton) getChildAt(i);
            button.setChecked(false);
            button.setTextColor(config.buttonTextColor);
        }
    }

    public void doOnlyCheckedOne(String item) {
        for (int i = 0; i < getChildCount(); i++) {
            CompoundButton button = (CompoundButton) getChildAt(i);
            if (button.getText().toString().equals(item)) {
                button.setChecked(true);
                button.setTextColor(config.buttonSelectedTextColor);
            } else {
                button.setChecked(false);
                button.setTextColor(config.buttonTextColor);
            }
        }
    }

    public void doNotCheckedOne(String item) {
        for (int i = 0; i < getChildCount(); i++) {
            CompoundButton button = (CompoundButton) getChildAt(i);
            if (button.getText().toString().equals(item)) {
                button.setChecked(false);
                button.setTextColor(config.buttonTextColor);
            }
        }
    }

    public boolean isCheckedAll(String[] items) {
        int checkedCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            CompoundButton button = (CompoundButton) getChildAt(i);
            String tmp = button.getText().toString();
            for (String item : items) {
                if (tmp.equals(item) && button.isChecked()) {
                    checkedCount++;
                }
            }
        }
        return checkedCount == items.length;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void refreshView() {
        removeAllViews();
        if (data == null || data.size() == 0) {
            return;
        }
        Log.d(null, "tags=" + Arrays.deepToString(data.toArray()));
        Context context = getContext();
        setPadding(dip2px(context, config.containerPadding), 0, dip2px(context, config.containerPadding), 0);
        for (int i = 0; i < data.size(); i++) {
            CompoundButton button;
            if (config.isMultipleMode) {
                button = new CheckBox(context);
            } else {
                button = new RadioButton(context);
            }
            button.setOnCheckedChangeListener(this);
            //设置按钮的参数
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    dip2px(context, config.buttonHeight));
            //设置文字的边距
            int padding = dip2px(context, config.textPadding);
            button.setPadding(padding, 0, padding, 0);
            //设置margin属性，需传入LayoutParams否则会丢失原有的布局参数
            MarginLayoutParams marginParams = new MarginLayoutParams(buttonParams);
            marginParams.leftMargin = dip2px(context, config.buttonLeftMargin);
            marginParams.topMargin = dip2px(context, config.buttonTopMargin);
            button.setLayoutParams(marginParams);
            button.setGravity(Gravity.CENTER);
            button.setBackgroundResource(config.buttonBackgroundResource);
            button.setButtonDrawable(android.R.color.transparent);
            button.setText(data.get(i).getName());
            button.setTextColor(config.buttonTextColor);
            button.setTextSize(config.buttonTextSize);
            addView(button);
        }
    }

    public List<TagValue> getSelectedItems() {
        List<TagValue> tagValues = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            CompoundButton button = (CompoundButton) getChildAt(i);
            if (button.isChecked()) {
                for (TagValue value : data) {
                    if (value.getName().equals(button.getText().toString())) {
                        tagValues.add(value);
                        break;
                    }
                }
            }
        }
        return tagValues;
    }

    public void setUiConfig(UiConfig config) {
        if (config != null) {
            this.config = config;
        }
        refreshView();
    }

    public void setData(List<TagValue> data) {
        this.data = data;
        refreshView();
    }

    public void setData(String[] data) {
        for (String d : data) {
            this.data.add(new StringTag(d));
        }
        setData(this.data);
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public void setData(String[] data, OnSelectedListener listener) {
        setData(null, data, listener);
    }

    public void setData(UiConfig config, String[] data, OnSelectedListener listener) {
        for (String d : data) {
            this.data.add(new StringTag(d));
        }
        setData(config, this.data, listener);
    }

    public void setData(List<TagValue> data, OnSelectedListener listener) {
        setData(null, data, listener);
    }

    public void setData(UiConfig config, List<TagValue> data, OnSelectedListener listener) {
        if (config != null) {
            this.config = config;
        }
        this.data = data;
        this.onSelectedListener = listener;
        refreshView();
    }

    public interface TagValue {

        String getName();

    }

    public static class StringTag implements TagValue {
        private String name;

        public StringTag(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public interface OnSelectedListener {

        void onSelected(TagValue value);

    }

    public static class UiConfig {
        /**
         * 是否多选
         */
        private boolean isMultipleMode = false;
        /**
         * 文字与按钮的边距
         */
        private int textPadding = 10;
        /**
         * 整个商品属性的左右间距
         */
        private int containerPadding = 15;
        /**
         * 属性按钮的高度
         */
        private int buttonHeight = 25;
        /**
         * 属性按钮之间的左边距
         */
        private int buttonLeftMargin = 10;
        /**
         * 属性按钮之间的上边距
         */
        private int buttonTopMargin = 4;
        /**
         * 属性按钮背景
         */
        private int buttonBackgroundResource = R.drawable.tag_bg_selector;
        /**
         * 属性按钮文字颜色
         */
        private int buttonTextColor = 0xFF111111;
        /**
         * 选择的属性按钮文字颜色
         */
        private int buttonSelectedTextColor = 0xFFFF5555;
        /**
         * 属性按钮文字大小
         */
        private int buttonTextSize = 12;

        public void setMultipleMode(boolean multipleMode) {
            isMultipleMode = multipleMode;
        }

        public void setTextPadding(int textPadding) {
            this.textPadding = textPadding;
        }

        public void setContainerPadding(int containerPadding) {
            this.containerPadding = containerPadding;
        }

        public void setButtonHeight(int buttonHeight) {
            this.buttonHeight = buttonHeight;
        }

        public void setButtonMargin(int buttonLeftMargin, int buttonTopMargin) {
            this.buttonLeftMargin = buttonLeftMargin;
            this.buttonTopMargin = buttonTopMargin;
        }

        public void setButtonBackgroundResource(@DrawableRes int res) {
            this.buttonBackgroundResource = res;
        }

        public void setButtonTextColor(@ColorInt int normalColor, @ColorInt int selectedColor) {
            this.buttonTextColor = normalColor;
            this.buttonSelectedTextColor = selectedColor;
        }

        public void setButtonTextSize(int buttonTextSize) {
            this.buttonTextSize = buttonTextSize;
        }

    }

}
