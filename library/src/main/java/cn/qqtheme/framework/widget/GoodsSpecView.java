package cn.qqtheme.framework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品规格选择视图。参阅：http://blog.csdn.net/a_zhon/article/details/72061028
 * <p>
 * Created by liyujiang on 2017/5/24 11:23.
 *
 * @see TagViewGroup
 * @see UiConfig
 * @see ISpecName
 * @see ISpecValue
 */
public class GoodsSpecView extends LinearLayout {
    private List<? extends ISpecName> data;
    private UiConfig config = new UiConfig();
    private OnSelectedListener onSelectedListener;

    public GoodsSpecView(Context context) {
        super(context);
        init();
    }

    public GoodsSpecView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        if (isInEditMode()) {
            String colors[] = {"白色", "黑色", "银色", "蓝色", "玫瑰金"};
            String storages[] = {"16GB", "32GB", "128GB", "256GB"};
            String suits[] = {"官方标配", "套餐一", "套餐二", "套餐三"};
            SpecName colorName = new SpecName("颜色");
            for (String color : colors) {
                colorName.addValue(new GoodsSpecView.SpecValue(color));
            }
            SpecName storageName = new SpecName("容量");
            for (String storage : storages) {
                storageName.addValue(new GoodsSpecView.SpecValue(storage));
            }
            SpecName suitName = new SpecName("套餐");
            for (String suit : suits) {
                suitName.addValue(new GoodsSpecView.SpecValue(suit));
            }
            List<SpecName> specNames = new ArrayList<>();
            specNames.add(colorName);
            specNames.add(storageName);
            specNames.add(suitName);
            setData(specNames, null);
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
        for (final ISpecName specName : data) {
            //设置规格分类的标题
            TextView textView = new TextView(context);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = dip2px(context, config.titleMargin);
            textView.setText(specName.getName());
            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);
            addView(textView);
            //设置一个规格分类下的所有小规格
            TagViewGroup tagViewGroup = new TagViewGroup(context);
            final List<? extends ISpecValue> values = specName.getValues();
            List<String> valueStrs = new ArrayList<>();
            for (ISpecValue value : values) {
                valueStrs.add(value.getName());
            }
            tagViewGroup.setData(config, valueStrs, new TagViewGroup.OnSelectedListener() {
                @Override
                public void onSelected(String name) {
                    if (onSelectedListener == null) {
                        return;
                    }
                    for (ISpecValue value : values) {
                        if (value.getName().equals(name)) {
                            onSelectedListener.onSelected(specName, value);
                            break;
                        }
                    }
                }
            });
            addView(tagViewGroup);
        }
    }

    public void setData(List<? extends ISpecName> data, OnSelectedListener listener) {
        setData(null, data, listener);
    }

    public void setData(UiConfig config, List<? extends ISpecName> data, OnSelectedListener listener) {
        if (config != null) {
            this.config = config;
        }
        this.data = data;
        this.onSelectedListener = listener;
        refreshView();
    }

    public static class UiConfig extends TagViewGroup.UiConfig {
        /**
         * 规格标题栏的文本间距
         */
        private int titleMargin = 8;

        public void setTitleMargin(int titleMargin) {
            this.titleMargin = titleMargin;
        }

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
