#### 如何使用DynamicIcon？

### 一、初始化
1. 为你的控件设置tag,例如

        ivMain01.setTag(Icons.HELP);
2. 调用init(Context context, String jsonStr)

### 二、设置图标
1. 设置Bitmap，则调用setIconWithBitmap(String tag),例如

        ivMain01.setImageBitmap(DynamicIcon.getInstance().setIconWithBitmap((String) ivMain01.getTag()));

2. 设置Drawable，则调用setIconWithDrawable(String tag),例如

        ivMain03.setImageDrawable(DynamicIcon.getInstance().setIconWithDrawable((String) ivMain03.getTag()));

至此，你就可以随心所欲地改变你的配置数据，来动态地变换图标啦~

### 三、进阶用法
1. 如果想要改变图标的颜色？只需要调用

        init(Context context, String jsonStr, int color)
   即可设置全部图标的颜色了；

   想要设置单个图标的颜色？调用

        setIconWithBitmap(String tag, int color)

        或者

        setIconWithDrawable(String tag, int color)