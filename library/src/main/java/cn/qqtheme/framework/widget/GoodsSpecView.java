package cn.qqtheme.framework.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.GoodsSpec.R;

/**
 * 商品规格选择视图。参阅：http://blog.csdn.net/a_zhon/article/details/72061028
 * <p>
 * Created by liyujiang on 2017/5/24 11:23.
 *
 * @see TagViewGroup
 */
public class GoodsSpecView extends LinearLayout {
    private List<? extends SpecName> data;
    private Context context;

    /**
     * 规格标题栏的文本间距
     */
    private int titleMargin = 8;
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
    private int buttonBackgroundResource = R.drawable.goods_spec_selector;
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
    /**
     * 选择后的回调监听
     */
    private OnSelectedListener onSelectedListener;


    public GoodsSpecView(Context context) {
        super(context);
        init(context);
    }

    public GoodsSpecView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        this.context = context;
    }

    private void refreshView() {
        if (data == null || data.size() == 0) {
            return;
        }
        for (final SpecName specName : data) {
            //设置规格分类的标题
            TextView textView = new TextView(context);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = dip2px(context, titleMargin);
            textView.setText(specName.getName());
            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);
            addView(textView);
            //设置一个规格分类下的所有小规格
            final TagViewGroup layout = new TagViewGroup(context);
            layout.setPadding(dip2px(context, containerPadding), 0, dip2px(context, containerPadding), 0);
            final List<? extends SpecValue> values = specName.getValues();
            if (onSelectedListener != null) {
                layout.setOnSelectedListener(new TagViewGroup.OnSelectedListener() {
                    @Override
                    public void onSelected(String name) {
                        for (int i = 0; i < layout.getChildCount(); i++) {
                            CompoundButton view = (CompoundButton) layout.getChildAt(i);
                            view.setTextColor(view.isChecked() ? buttonSelectedTextColor : buttonTextColor);
                        }
                        for (SpecValue specValue : values) {
                            if (specValue.getName().equals(name)) {
                                onSelectedListener.onSelected(specName, specValue);
                                break;
                            }
                        }
                    }
                });
            }
            for (int i = 0; i < values.size(); i++) {
                SpecValue specValue = values.get(i);
                RadioButton button = new RadioButton(context);
                //设置按钮的参数
                LayoutParams buttonParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        dip2px(context, buttonHeight));
                //设置文字的边距
                int padding = dip2px(context, textPadding);
                button.setPadding(padding, 0, padding, 0);
                //设置margin属性，需传入LayoutParams否则会丢失原有的布局参数
                MarginLayoutParams marginParams = new MarginLayoutParams(buttonParams);
                marginParams.leftMargin = dip2px(context, buttonLeftMargin);
                marginParams.topMargin = dip2px(context, buttonTopMargin);
                button.setLayoutParams(marginParams);
                button.setGravity(Gravity.CENTER);
                button.setBackgroundResource(buttonBackgroundResource);
                button.setButtonDrawable(android.R.color.transparent);
                button.setText(specValue.getName());
//                if (i == 0) {
//                    //默认选中第一个
//                    button.setChecked(true);
//                    button.setTextColor(buttonSelectedTextColor);
//                } else {
//                    button.setTextColor(buttonTextColor);
//                }
                button.setTextColor(buttonTextColor);
                button.setTextSize(buttonTextSize);
                layout.addView(button);
            }
            addView(layout);
        }
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setContainerPadding(int containerPadding) {
        this.containerPadding = containerPadding;
    }

    public void setTitleMargin(int titleMargin) {
        this.titleMargin = titleMargin;
    }

    public void setTextPadding(int textPadding) {
        this.textPadding = textPadding;
    }

    public void setButtonHeight(int buttonHeight) {
        this.buttonHeight = buttonHeight;
    }

    public void setButtonLeftMargin(int buttonLeftMargin) {
        this.buttonLeftMargin = buttonLeftMargin;
    }

    public void setButtonTopMargin(int buttonTopMargin) {
        this.buttonTopMargin = buttonTopMargin;
    }

    public void setButtonBackgroundResource(@DrawableRes int resid) {
        this.buttonBackgroundResource = resid;
    }

    public void setButtonTextColor(@ColorInt int buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public void setButtonSelectedTextColor(int buttonSelectedTextColor) {
        this.buttonSelectedTextColor = buttonSelectedTextColor;
    }

    public void setButtonTextSize(int buttonTextSize) {
        this.buttonTextSize = buttonTextSize;
    }

    public void setData(List<? extends SpecName> data) {
        this.data = data;
        refreshView();
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    /**
     * 规格名
     */
    public interface ISpecName {

        String getName();

        List<? extends ISpecValue> getValues();

    }

    /**
     * 规格值
     */
    public interface ISpecValue {

        String getName();

    }

    public static class SpecName implements ISpecName {
        private String name;
        private List<SpecValue> values;

        public SpecName(String name) {
            this.name = name;
            this.values = new ArrayList<>();
        }

        @Override
        public String getName() {
            return name;
        }

        public void addValue(SpecValue value) {
            this.values.add(value);
        }

        public void setValues(List<SpecValue> values) {
            this.values = values;
        }

        @Override
        public List<SpecValue> getValues() {
            return values;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public static class SpecValue implements ISpecValue {
        private String name;

        public SpecValue(String name) {
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

    /**
     * 选择成功回调
     */
    public interface OnSelectedListener {

        <N, V> void onSelected(N specName, V specValue);

    }

}
