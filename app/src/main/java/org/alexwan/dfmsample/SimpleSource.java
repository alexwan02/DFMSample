package org.alexwan.dfmsample;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.util.IOUtils;

/**
 * SimpleSource
 * Created by alexwan on 16/9/23.
 */
public class SimpleSource implements IDataSource<List<DanmakuData>> {
    private List<DanmakuData> mDataList;
    private InputStream mInput;

    public SimpleSource(String json) {
        init(json);
    }

    public SimpleSource(InputStream in) {
        init(in);
    }

    public SimpleSource(URL url) throws IOException {
        this(url.openStream());
    }

    public SimpleSource(File file) throws FileNotFoundException {
        init(new FileInputStream(file));
    }

    private void init(InputStream in) {
        if (in == null) {
            throw new NullPointerException("input stream can't be null!");
        }
        mInput = in;
        String json = IOUtils.getString(mInput);
        init(json);
    }

    private void init(String json) throws JsonSyntaxException{
        if (!TextUtils.isEmpty(json)) {
            Type type = new TypeToken<List<DanmakuData>>() {}.getType();
            mDataList = new Gson().fromJson(json, type);
        }
    }

    @Override
    public List<DanmakuData> data() {
        return mDataList;
    }

    @Override
    public void release() {
        IOUtils.closeQuietly(mInput);
        mInput = null;
        mDataList.clear();
    }

}
