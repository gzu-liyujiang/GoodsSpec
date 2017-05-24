package cn.qqtheme.GoodsSpec;

import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cn.qqtheme.framework.widget.GoodsSpecView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoodsSpecView.OnSelectedListener {
    private String specValues1[] = {"XS", "S", "M", "L", "XL", "XXL", "XXXL"};
    private String specValues2[] = {"白色", "黑色", "黄绿色", "藏青", "蝴蝶蓝"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoodsSpecView goodsSpecView = (GoodsSpecView) findViewById(R.id.goods_spec);
        List<GoodsSpecView.SpecName> specNames = new ArrayList<>();
        GoodsSpecView.SpecName specName1 = new GoodsSpecView.SpecName("尺码");
        for (String value1 : specValues1) {
            specName1.addValue(new GoodsSpecView.SpecValue(value1));
        }
        GoodsSpecView.SpecName specName2 = new GoodsSpecView.SpecName("颜色");
        for (String value2 : specValues2) {
            specName2.addValue(new GoodsSpecView.SpecValue(value2));
        }
        specNames.add(specName1);
        specNames.add(specName2);
        goodsSpecView.setData(specNames, this);
    }

    @Override
    public void onBackPressed() {
        finish();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    @Override
    public <N, V> void onSelected(N specName, V specValue) {
        Toast.makeText(this, specName + "--" + specValue, Toast.LENGTH_SHORT).show();
    }

}
