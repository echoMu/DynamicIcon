在项目开发中，我们的应用通常会有很多icon，这些icon有的需要根据不同的条件来更换（比如说会员版本），那么我们就在想，能不能做到动态改变，不需修改代码就可以完成?

### 需求
1. 有固定若干数量的svg格式的icon库；
2. 配置信息由服务器下发的json数据描述，客户端通过读取json数据来设置icon；
3. 支持修改icon颜色；

### 思路
1. 为每个需要动态设置图标的控件设置tag，通常是该图标的功能名称，也与icon库的文件名相对应。
    应该维护一份Icon数据，存放这些tag，并和json配置共享；
2. json数据格式定义为{"tag":"help","resId":"ic_help"}，其中tag是控件的tag值，resId是要设置给控件的图标文件名
3. 通过反射读取存放于drawable目录下各个icon的文件名和文件id；
4. 得到相应tag值要求的图标文件名称，最终获得目标图标的文件id，将VectorDrawable转drawable或者Bitmap,提供给调用者使用

#### VectorDrawable
VectorDrawable是从Android 5.0开始引入的一个新的Drawable子类，能够加载矢量图。现在通过support-library已经至少能适配到Android 4.0了。

Android中的VectorDrawable只支持SVG的部分属性，相当于阉割版。它虽然是个类，但是一般通过配置xml再设置到要使用的控件上。在Android工程中，在资源文件夹res/drawable/的目录下，通过<vector></vector>标签描述。

因此，使用者要通过java代码使用svg，则间接通过将VectorDrawable转drawable或者转Bitmap使用。同时也能避免5.0以下版本对svg的兼容问题。

### 实现过程
###### 加载配置数据
        
    String jsonStr = "[{\"tag\":\"help\",\"resId\":\"ic_help\"},{\"tag\":\"output\",\"resId\":\"ic_output\"},{\"tag\":\"save\",\"resId\":\"ic_save\"}]";

###### 维护一份Icon
    
    public class Icons {
      public final static String HELP="help";
      public final static String OUTPUT="output";
      public final static String SAVE="save";
      ...
    }
维护一份Icon数据，存放控件的tag，并和json配置共享。

###### 设置tag

    ivMain01.setTag(Icons.HELP);
    ivMain02.setTag(Icons.OUTPUT);
    ivMain03.setTag(Icons.SAVE);
    ...

###### 根据tag获得icon资源id
 得到配置文件想要替换成的对应tag图标的资源名称
    
    String resId = "";

    for (IconBean iconBean : iconBeanList) {
        if (iconBean.getTag().equals(tag))
            resId = iconBean.getResId();
    }

再找到drawable目录下对应的资源id
    
        //获取drawable文件名列表，不包含扩展名
        Field[] fields = R.drawable.class.getDeclaredFields();

        for (Field field : fields) {
            if (resId.equals(field.getName())) {
                //获取文件名对应的系统生成的id,需指定(主module)包路径 getClass().getPackage().getName(),指定资源类型drawable
                int resID = mContext.getResources().getIdentifier(field.getName(),
                        "drawable", mContext.getClass().getPackage().getName());

                drawableId = resID;
            }
        }

获得drawableId后，我们就能根据drawableId来获取Drawable对象了，也能获取对应图标的bitmap。

        Drawable drawable = ContextCompat.getDrawable(mContext, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        if (color != 0) {
            Log.d("echoMu", "color:" + color);
            drawable.setTint(color);
        }

        return drawable;

我们是使用setTint方法来设置Drawable的颜色的，要注意这里支持的是RGB格式的颜色值。VectorDrawable转Bitmap的代码在这里：

        Drawable drawable = getDrawable(drawableId, color);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;

 想要设置Bitmap，则调用setIconWithBitmap(String tag)，例如 

    ivMain01.setImageBitmap(KitDynamicIcon.getInstance().setIconWithBitmap((String) ivMain01.getTag()));

如果是设置Drawable，则调用setIconWithDrawable(String tag)，例如

     ivMain03.setImageDrawable(KitDynamicIcon.getInstance().setIconWithDrawable((String) ivMain03.getTag()));

### 用到的工具
1. 力荐svgtoandroid插件，用过之后果然神清气爽。安装：File -> Setting -> Plugins -> Browser repositories -> 搜“svg2VectorDrawable” -> 安装并重启Android Studio
（貌似又不能用了...）
2. [SVG2Vector批量工具](https://github.com/MegatronKing/SVG-Android/tree/master/svg-vector-cli)

*注意*
矢量图特别适合icon图标的应用场景，但是不能用于比如加载相册时，设置的placeholder或者error这类需要频繁切换回收的应用场景，否则会造成非常明显的卡顿，因为矢量图是不被硬件加速支持的。

#### 参考资料
- [Android使用矢量图（SVG, VectorDrawable）实践篇](https://www.jianshu.com/p/0555b8c1d26a)
- [SVG-Android](https://github.com/MegatronKing/SVG-Android)
- [Android微信上的SVG](https://link.zhihu.com/?target=http%3A//mp.weixin.qq.com/s%3F__biz%3DMzAwNDY1ODY2OQ%3D%3D%26mid%3D207863967%26idx%3D1%26sn%3D3d7b07d528f38e9f812e8df7df1e3322%26scene%3D4%23wechat_redirect])
