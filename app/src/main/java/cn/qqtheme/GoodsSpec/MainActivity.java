package cn.qqtheme.GoodsSpec;

import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cn.qqtheme.framework.widget.GoodsSpecView;
import cn.qqtheme.framework.widget.TagViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String keywords[] = {"穿青人", "少数民族", "未定民族", "已识别民族"};
    private String specValues1[] = {"XS", "S", "M", "L", "XL", "XXL", "XXXL"};
    private String specValues2[] = {"白色", "黑色", "黄绿色", "藏青", "蝴蝶蓝"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TagViewGroup tagViewGroup = (TagViewGroup) findViewById(R.id.hot_keyword);
        TagViewGroup.UiConfig config = new TagViewGroup.UiConfig();
        config.setMultipleMode(true);
        config.setButtonMargin(5, 5);
        config.setButtonTextColor(0xFF111111, 0xFFFF0000);
        config.setButtonBackgroundResource(R.drawable.hot_keyword_bg_selector);
        tagViewGroup.setData(config, keywords, new TagViewGroup.OnMultiSelectedListener() {
            @Override
            public void onSelected(List<TagViewGroup.TagValue> values) {
                Toast.makeText(getBaseContext(), values.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        GoodsSpecView goodsSpecView = (GoodsSpecView) findViewById(R.id.goods_spec);
        GoodsSpecView.SpecName specName1 = new GoodsSpecView.SpecName("尺码");
        for (String value1 : specValues1) {
            specName1.addValue(new GoodsSpecView.SpecValue(value1));
        }
        GoodsSpecView.SpecName specName2 = new GoodsSpecView.SpecName("颜色");
        for (String value2 : specValues2) {
            specName2.addValue(new GoodsSpecView.SpecValue(value2));
        }
        List<GoodsSpecView.SpecName> specNames = new ArrayList<>();
        specNames.add(specName1);
        specNames.add(specName2);
        goodsSpecView.setData(specNames, new GoodsSpecView.OnSelectedListener() {
            @Override
            public <N, V> void onSelected(N specName, V specValue) {
                Toast.makeText(getBaseContext(), specName + "--" + specValue, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

}
