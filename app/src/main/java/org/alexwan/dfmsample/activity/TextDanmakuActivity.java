package org.alexwan.dfmsample.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.alexwan.dfmsample.R;
import org.alexwan.dfmsample.databinding.ActivityMainBinding;
import org.alexwan.dfmsample.simplesource.SimpleDanmakuLoader;
import org.alexwan.dfmsample.simplesource.SimpleDanmakuParser;
import org.alexwan.dfmsample.widget.DanmakuDraweeSpan;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;

import static android.R.id.list;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class TextDanmakuActivity extends AppCompatActivity {
    private static final String TAG = TextDanmakuActivity.class.getSimpleName();
    private ActivityMainBinding mMainBinding;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;

    public static void start(Context context){
        Intent intent = new Intent(context , TextDanmakuActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // setSupportActionBar(mMainBinding.toolbar);

        HashMap<Integer, Integer> maxLinesPair = new HashMap<>(); // 最大行数
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5);

        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>(); // 防止弹幕重叠
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext = DanmakuContext.create();   // 创建弹幕所需的上下文信息
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3) // 弹幕样式
                .setDuplicateMergingEnabled(true)    // 合并重复弹幕
                .setScrollSpeedFactor(1.5f)          // 滚动弹幕速度系数
                .setScaleTextSize(0.8f)              // 弹幕文字大小
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 缓存绘制
                .setMaximumLines(maxLinesPair)       // 弹幕最大行数
                .preventOverlapping(overlappingEnablePair); // 防止弹幕重叠

        mParser = createParser(this.getResources().openRawResource(R.raw.comments)); // 创建弹幕解析器
        //  mParser = customParser();
        mMainBinding.svDanmaku.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                // 弹幕准备完成
                Log.d("DFM" , "prepared");
                mMainBinding.svDanmaku.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {
                // 弹幕播放时间
                Log.d("DFM" , "updateTimer : timer = " + timer.currMillisecond);
            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
                // 播放新的一条弹幕
                Log.d("DFM" , "danmakuShown : danmaku = " + danmaku.text);
            }

            @Override
            public void drawingFinished() {
                // 弹幕播放结束
                Log.d("DFM" , "drawingFinished");
            }
        });

        mMainBinding.svDanmaku.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {
            @Override
            public boolean onViewClick(IDanmakuView view) {
                // 点击的弹幕
                return false;
            }

            @Override
            public boolean onDanmakuClick(IDanmakus danmakus) {
                // 弹幕
                Log.d("DFM", "onDanmakuClick danmakus size:" + danmakus.size());
                return false;
            }
        });

        // 准备
        mMainBinding.svDanmaku.prepare(mParser, mDanmakuContext);
        // 显示播放FPS,一般用作调试时使用
        mMainBinding.svDanmaku.showFPS(true);
        // 是否开启缓存
        mMainBinding.svDanmaku.enableDanmakuDrawingCache(true);


        // 横屏模式
        setRequestedOrientation(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        // TODO

        mMainBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                BaseDanmaku baseDanmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_BOTTOM);
                baseDanmaku.text = "简单的一条弹幕" + System.nanoTime(); // 文本内容
                baseDanmaku.textSize = 50f * (mParser.getDisplayer().getDensity() - 0.6f); // 字体大小
                baseDanmaku.textColor = 16711680;         // 字体颜色
                baseDanmaku.textShadowColor = Color.BLACK; // 描边颜色
                baseDanmaku.borderColor = 0;       // 弹幕边框颜色
                baseDanmaku.padding = 5;                   // 内边距
                baseDanmaku.priority = 0;                  // 弹幕优先级,0为低优先级
                baseDanmaku.isLive = false;                // 是否是直播弹幕
                baseDanmaku.underlineColor = 0;            // 0表示无下划线
                // baseDanmaku.lines = new String[]{"两行弹幕第1行弹幕" , "两行弹幕第2行弹幕"};
                baseDanmaku.setTime(mMainBinding.svDanmaku.getCurrentTime()); // 弹幕出现的时间
                mMainBinding.svDanmaku.addDanmaku(baseDanmaku);
            }
        });

        mMainBinding.addMultiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BaseDanmaku baseDanmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
                baseDanmaku.text = buildText(); // 文本内容
                baseDanmaku.textSize = 50f * (mParser.getDisplayer().getDensity() - 0.6f); // 字体大小
                baseDanmaku.textColor = 16711680;   // 字体颜色
                baseDanmaku.textShadowColor = Color.BLACK;    // 描边颜色
                baseDanmaku.borderColor = 0;        // 弹幕边框颜色
                baseDanmaku.padding = 5;            // 内边距
                baseDanmaku.priority = 0;           // 弹幕优先级,0为低优先级
                baseDanmaku.isLive = false;         // 是否是直播弹幕
                baseDanmaku.underlineColor = 0;     // 0表示无下划线
                // baseDanmaku.lines = new String[]{"两行弹幕第1行弹幕" , "两行弹幕第2行弹幕"};
                baseDanmaku.setTime(mMainBinding.svDanmaku.getCurrentTime());// 弹幕出现的时间
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMainBinding.svDanmaku.addDanmaku(baseDanmaku);
                    }
                });

            }
        });
        // mMainBinding.text.setMovementMethod(new ScrollingMovementMethod());
        // mMainBinding.text.setText(buildText());
    }
     private LinearLayout mContainer;
    CharSequence buildText() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("[img]");
        String[] urls = new String[]{"http://img2.3lian.com/2014/gif/10/9/25.jpg" ,
                "http://img2.3lian.com/2014/gif/10/9/17.jpg"
                    , "http://p3.wmpic.me/article/2014/11/03/1415003717_rDnPcZbL.png"};
        Random random = new Random();
        int id = random.nextInt(3);
        builder.setSpan(new DanmakuDraweeSpan.Builder(urls[id], false)
                        .setPlaceHolderImage(ContextCompat.getDrawable(this , R.mipmap.ic_launcher))
                        .setLayout(100, 100)
                        .setShowAnimaImmediately(true)
                        .build(),
                0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        // builder.append("alexwan , 移动开发");

        // build double cache view
        if(mContainer == null){
            mContainer = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_text_content, null, false);
        }
        mContainer.destroyDrawingCache();
        TextView name = (TextView) mContainer.findViewById(R.id.name);
        TextView content = (TextView) mContainer.findViewById(R.id.content);
        name.setText("alex-wan-ctrip");
        content.setText("android");
        // layout
        mContainer.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mContainer.layout(0, 0, mContainer.getMeasuredWidth(), mContainer.getMeasuredHeight());

        // mContainer.destroyDrawingCache();
        mContainer.buildDrawingCache();
        mContainer.setDrawingCacheBackgroundColor(Color.argb(0 , 0 , 0, 0));
        Bitmap bitmap = mContainer.getDrawingCache();

        // create tmp bitmap
        Bitmap tmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint);

        // builder.setSpan();
        BitmapDrawable drawable = new BitmapDrawable(getResources() , tmp);
        drawable.setBounds(0 , 0 , drawable.getIntrinsicWidth() , drawable.getIntrinsicHeight());

        // recycle bitmap resource
        bitmap.recycle();
        mContainer.destroyDrawingCache();

        //
        builder.append("  ");
        int start = builder.length();
        builder.append("[img]");
        builder.setSpan(new ImageSpan(drawable) , start , builder.length() ,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    private DanmakuDraweeSpan[] getImages(Spanned text) {
        if (!TextUtils.isEmpty(text))
            return text.getSpans(0, text.length(), DanmakuDraweeSpan.class);
        return new DanmakuDraweeSpan[0]; //TODO: pool empty typed array
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DanmakuDraweeSpan[] images = getImages((Spanned) danmaku.text);
                        for (DanmakuDraweeSpan image : images) {
                            image.setDanmaku(danmaku);
                            image.onAttach(mMainBinding.svDanmaku);
                        }
                        // mMainBinding.svDanmaku.invalidateDanmaku(danmaku, false);
                    }
                });
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            //
            if(danmaku != null){
                danmaku.text = "";
            }
        }
    };

    // 绘制有背景的弹幕
    private class BackgroundCacheStuffer extends SpannedCacheStuffer{
        final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
            danmaku.padding = 10;
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            mPaint.setColor(0xee90A4AE);
            // 圆角矩形
            RectF rectF = new RectF(left + 5, top + 5, left + danmaku.paintWidth - 5, top + danmaku.paintHeight - 5);
            canvas.drawRoundRect(rectF, 50 , 50 , mPaint);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
            // 绘制背景时建议禁止绘制描边
        }

        @Override
        public void drawText(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, TextPaint paint, boolean fromWorkerThread) {
            super.drawText(danmaku, lineText, canvas, left, top, paint, fromWorkerThread);
        }
    }

    /**
     * 创建弹幕解析器
     * @param stream stream
     * @return BaseDanmakuParser
     */
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        // TAG_BILI XML数据格式的弹幕。TAG_ACFUN JSON数据格式的弹幕
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    /**
     * 自定义解析器
     * @param stream stream
     * @return BaseDanmakuParser
     */
    private BaseDanmakuParser customParser(InputStream stream){
        ILoader loader = SimpleDanmakuLoader.instance();
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            Log.e(TAG , "customParser : stream error = " , e);
        }
        BaseDanmakuParser parser = new SimpleDanmakuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    private BaseDanmakuParser customParser(){
        ILoader loader = SimpleDanmakuLoader.instance();
        try {
            loader.load("ddd");
            Log.i(TAG , "customParser : json = " + new Gson().toJson(list) );
        } catch (IllegalDataException e) {
            Log.e(TAG , "customParser : json = " + new Gson().toJson(list) + " ; error = " , e);
        }
        BaseDanmakuParser parser = new SimpleDanmakuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }
}
