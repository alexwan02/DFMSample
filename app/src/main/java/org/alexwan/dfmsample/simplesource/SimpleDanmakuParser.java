package org.alexwan.dfmsample.simplesource;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.alexwan.dfmsample.model.DanmakuData;

import java.lang.reflect.Type;
import java.util.List;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

/**
 * SimpleDanmakuParser
 * Created by alexwan on 16/9/23.
 */
public class SimpleDanmakuParser extends BaseDanmakuParser{
    private static final String TAG = SimpleDanmakuParser.class.getSimpleName();
    @Override
    protected IDanmakus parse() {
        if(mDataSource != null && mDataSource instanceof DanmakuSource){
            DanmakuSource source = (DanmakuSource) mDataSource;
            return doParse(source.data());
        }
        return new Danmakus();
    }

    private Danmakus doParse(String json){
        Danmakus danmakus = new Danmakus();
        if(TextUtils.isEmpty(json)){
            return danmakus;
        }
        Type type = new TypeToken<List<DanmakuData>>(){}.getType();
        List<DanmakuData> list = new Gson().fromJson(json , type);
        int size = list.size();
        for (int i = 0 ; i < size; i ++){
            danmakus = parse(list.get(i) , danmakus , i);
        }
        return danmakus;
    }


    private Danmakus parse(DanmakuData data, Danmakus danmakus , int index) {
        if(danmakus == null){
            danmakus =  new Danmakus();
        }
        BaseDanmaku item = mContext.mDanmakuFactory.createDanmaku(data.getType() , mContext);
        if(item != null){
            item.setTime((long) (data.getTime() * 1000));
            item.textSize = data.getFontSize();
            item.textColor = data.getFontColor() | 0xFF000000 ;
            DanmakuUtils.fillText(item , data.getContent());
            item.index = index;
            item.setTimer(mTimer);
            danmakus.addItem(item);
            Log.i(TAG , "parse : time = " + data.getTime() * 1000 + " ; textSize = " + data.getFontSize()
                    + " ; textColor = " + item.textColor + " ; content = " + item.text);
        }
        return danmakus;
    }

}
