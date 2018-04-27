package com.echomu.dynamicicon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.List;

/**
 * <pre>
 *     author : echoMu
 *     e-mail :
 *     time   : 2018/04/25
 *     desc   :
 *     version:
 * </pre>
 */
public class DynamicIcon {

    private static DynamicIcon mDynamicIcon = null;

    private Context mContext;

    /**
     * 配置数据里的icon列表
     */
    private List<IconBean> iconBeanList;

    /**
     * 全局RGB颜色值
     */
    private int mColor = 0;

    public static DynamicIcon getInstance() {
        if (null == mDynamicIcon) {
            synchronized (DynamicIcon.class) {
                if (null == mDynamicIcon) {
                    mDynamicIcon = new DynamicIcon();
                }
            }
        }
        return mDynamicIcon;
    }

    /**
     * 初始化
     *
     * @param context
     * @param jsonStr json配置
     */
    public void init(Context context, String jsonStr) {
        this.mContext = context;

        iconBeanList = new Gson().fromJson(jsonStr, new TypeToken<List<IconBean>>() {
        }.getType());
    }

    /**
     * 初始化
     *
     * @param context
     * @param jsonStr json配置
     * @param color RGB颜色值
     */
    public void init(Context context, String jsonStr, int color) {
        this.mContext = context;
        this.mColor = color;

        iconBeanList = new Gson().fromJson(jsonStr, new TypeToken<List<IconBean>>() {
        }.getType());
    }

    /**
     * 设置配置数据
     *
     * @param jsonStr
     */
    public void setJsonStr(String jsonStr) {
        if (iconBeanList != null) {
            iconBeanList.clear();
            iconBeanList = null;
        }

        iconBeanList = new Gson().fromJson(jsonStr, new TypeToken<List<IconBean>>() {
        }.getType());
    }

    /**
     * 根据传入的控件的tag，寻找配置里对应的资源图标名，再找到drawable目录下对应的资源id
     *
     * @param tag
     * @return drawableId
     */
    private int getDrawableId(String tag) {
        if (TextUtils.isEmpty(tag)) {
            throw new NullPointerException("tag is null!");
        }

        //得到配置文件想要替换成的对应tag图标的资源名称
        String resId = "";

        for (IconBean iconBean : iconBeanList) {
            if (iconBean.getTag().equals(tag))
                resId = iconBean.getResId();
        }

        int drawableId = 0;
        //获取drawable文件名列表，不包含扩展名
        Field[] fields = R.drawable.class.getDeclaredFields();

        //获取applicationId
        String packageName=mContext.getPackageName();
        for (Field field : fields) {
            if (resId.equals(field.getName())) {
                //获取文件名对应的系统生成的id,需指定(主module)包路径 getClass().getPackage().getName(),指定资源类型drawable
                int resID = mContext.getResources().getIdentifier(field.getName(),
                        "drawable", packageName);
                drawableId = resID;
            }
        }

        if (drawableId == 0) {
            throw new NullPointerException("drawableId no found!");
        }

        return drawableId;
    }

    /**
     * 根据drawableId获取Drawable
     *
     * @param drawableId
     * @param color      RGB格式
     * @return Drawable
     */
    private Drawable getDrawable(int drawableId, int color) {
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        if (color != 0) {
            Log.d("echoMu", "color:" + color);
            drawable.setTint(color);
        }

        return drawable;
    }

    /**
     * 设置icon
     *
     * @param tag
     * @return drawableId
     */
    public Drawable setIconWithDrawable(String tag) {
        int drawableId = getDrawableId(tag);

        return getDrawable(drawableId, mColor);
    }

    /**
     * 设置icon
     *
     * @param tag
     * @return drawableId
     */
    public Drawable setIconWithDrawable(String tag, int color) {
        int drawableId = getDrawableId(tag);

        return getDrawable(drawableId, color);
    }

    /**
     * 设置icon
     *
     * @param tag
     * @return Bitmap
     */
    public Bitmap setIconWithBitmap(String tag) {
        int drawableId = getDrawableId(tag);

        return getBitmapFromVectorDrawable(drawableId, mColor);
    }

    /**
     * 设置icon
     *
     * @param tag
     * @return Bitmap
     */
    public Bitmap setIconWithBitmap(String tag, int color) {
        int drawableId = getDrawableId(tag);

        return getBitmapFromVectorDrawable(drawableId, color);
    }

    /**
     * 根据图标资源id获取对应图标的bitmap
     *
     * @param drawableId 图标id
     * @return Bitmap
     */
    private Bitmap getBitmapFromVectorDrawable(int drawableId, int color) {
        Drawable drawable = getDrawable(drawableId, color);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
