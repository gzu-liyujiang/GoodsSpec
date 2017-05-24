### 标签组布局及商品规格选择视图
标签组布局，可用来实现诸如热门搜索词、商品规格之类的。参阅：http://blog.csdn.net/lmj623565791/article/details/38352503

### 效果图镇库

### 使用方法：
下载library模块依赖：
```groovy
    compile project(':library')
```
参考app模块，示例代码如下：
```java
       String specValues1[] = {"XS", "S", "M", "L", "XL", "XXL", "XXXL"};
       String specValues2[] = {"白色", "黑色", "黄绿色", "藏青", "蝴蝶蓝"};
       GoodsSpecView goodsSpecView = (GoodsSpecView) findViewById(R.id.goods_spec);
        goodsSpecView.setOnSelectedListener(this);
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
        goodsSpecView.setData(specNames);

```