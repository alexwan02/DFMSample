package org.alexwan.dfmsample.simplesource;

import com.facebook.common.util.UriUtil;

import org.json.JSONException;

import java.io.InputStream;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.parser.IDataSource;

/**
 * SimpleDanmakuLoader
 * Created by alexwan on 16/9/23.
 */
public class SimpleDanmakuLoader implements ILoader {

    private SimpleDanmakuLoader() {
    }

    private static volatile SimpleDanmakuLoader instance;
    private IDataSource<String> mDataSource;

    public static ILoader instance() {
        if (instance == null) {
            synchronized (SimpleDanmakuLoader.class) {
                if (instance == null) {
                    instance = new SimpleDanmakuLoader();
                }
            }
        }
        return instance;
    }

    @Override
    public IDataSource<String> getDataSource() {
        return mDataSource;
    }

    @Override
    public void load(String uri) throws IllegalDataException {
        try {
             mDataSource = new DanmakuSource(UriUtil.parseUriOrNull(uri));
        } catch (Exception e) {
            throw new IllegalDataException(e);
        }
    }

    @Override
    public void load(InputStream in) throws IllegalDataException {
        try {
            mDataSource = new DanmakuSource(in);
        } catch (JSONException e) {
            throw new IllegalDataException(e);
        }
    }
}
