package cn.qqtheme.framework.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.ArrayList;
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
    private List<String> data = new ArrayList<>();
    //UI配置
    private UiConfig config = new UiConfig();
    //存储所有的View
    private List<List<View>> allViews = new ArrayList<>();
    // 存放每一行的子view
    private List<View> lineViews = new ArrayList<>();
    //每一行的高度
    private List<Integer> lineHeights = new ArrayList<>();
    //选择成功回调
    private OnSelectedListener onSelectedListener;

    public TagViewGroup(Context context) {
        super(context);
    }

    public TagViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;

        // 记录每一行的宽度与高度
        int lineWidth = 0;
        int lineHeight = 0;

        // 得到内部元素的个数
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            // 通过索引拿到每一个子view
            View child = getChildAt(i);
            // 测量子View的宽和高,系统提供的measureChild
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到LayoutParams
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            // 子View占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // 子View占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            // 换行 判断 当前的宽度大于 开辟新行
            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                // 对比得到最大的宽度
                width = Math.max(width, lineWidth);
                // 重置lineWidth
                lineWidth = childWidth;
                // 记录行高
                height += lineHeight;
                lineHeight = childHeight;
            } else { // 未换行
                // 叠加行宽
                lineWidth += childWidth;
                // 得到当前行最大的高度
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 特殊情况,最后一个控件
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }
        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()
        );

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        allViews.clear();
        lineViews.clear();
        lineHeights.clear();

        // 当前ViewGroup的宽度
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 如果需要换行
            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
                // 记录LineHeight
                lineHeights.add(lineHeight);
                // 记录当前行的Views
                allViews.add(lineViews);

                // 重置我们的行宽和行高
                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                // 重置我们的View集合
                lineViews.clear();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
                    + lp.bottomMargin);
            lineViews.add(child);

        }
        // 处理最后一行
        lineHeights.add(lineHeight);
        allViews.add(lineViews);

        // 设置子View的位置

        int left = getPaddingLeft();
        int top = getPaddingTop();

        // 行数
        int lineNum = allViews.size();

        for (int i = 0; i < lineNum; i++) {
            // 当前行的所有的View
            lineViews = allViews.get(i);
            lineHeight = lineHeights.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                // 判断child的状态
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                // 为子View进行布局
                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton button = (RadioButton) child;
                button.setChecked(false);
                button.setTextColor(config.buttonTextColor);
            }
        }
        if (isChecked) {
            buttonView.setChecked(true);
            buttonView.setTextColor(config.buttonSelectedTextColor);
            if (onSelectedListener != null) {
                onSelectedListener.onSelected(buttonView.getText().toString());
            }
        }

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
        Context context = getContext();
        setPadding(dip2px(context, config.containerPadding), 0, dip2px(context, config.containerPadding), 0);
        for (int i = 0; i < data.size(); i++) {
            RadioButton button = new RadioButton(context);
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
            button.setText(data.get(i));
//            if (i == 0) {
//                //默认选中第一个
//                button.setChecked(true);
//            }
            button.setTextColor(config.buttonTextColor);
            button.setTextSize(config.buttonTextSize);
            addView(button);
        }
    }

    public void setData(List<String> data, OnSelectedListener listener) {
        setData(null, data, listener);
    }

    public void setData(UiConfig config, List<String> data, OnSelectedListener listener) {
        if (config != null) {
            this.config = config;
        }
        this.data = data;
        this.onSelectedListener = listener;
        refreshView();
    }

    public interface OnSelectedListener {

        void onSelected(String name);

    }

    public static class UiConfig {
        /**
         * 文字与按钮的边距
         */
        private int textPadding = 10;
        /**
         * 整个商品属性的左右间距
         */
        private int containerPadding = 16;
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
        private int buttonTopMargin = 5;
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

        public void setContainerPadding(int containerPadding) {
            this.containerPadding = containerPadding;
        }

        public void setTextPadding(int textPadding) {
            this.textPadding = textPadding;
        }

        public void setButtonHeight(int buttonHeight) {
            this.buttonHeight = buttonHeight;
        }

        public void setButtonLeftMargin(int buttonLeftMargin, int buttonTopMargin) {
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
