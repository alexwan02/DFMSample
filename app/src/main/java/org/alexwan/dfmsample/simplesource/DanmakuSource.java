package org.alexwan.dfmsample.simplesource;

import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.util.IOUtils;

/**
 * DanmakuSource
 * Created by alexwan on 16/9/23.
 */
public class DanmakuSource implements IDataSource<String> {
    private static final String TAG = DanmakuSource.class.getSimpleName();
    private String mJson;
    private InputStream mInput;

    public DanmakuSource(String json) throws JSONException {
        init(json);
    }

    public DanmakuSource(InputStream in) throws JSONException {
        init(in);
    }

    private void init(InputStream in) throws JSONException {
        if (in == null) {
            throw new NullPointerException("input stream cannot be null!");
        }
        mInput = in;
        String json = IOUtils.getString(mInput);
        init(json);
    }

    public DanmakuSource(URL url) throws JSONException, IOException {
        this(url.openStream());
    }

    public DanmakuSource(File file) throws FileNotFoundException, JSONException {
        init(new FileInputStream(file));
    }

    public DanmakuSource(Uri uri) throws IOException, JSONException {
        String scheme = uri.getScheme();
        if (SCHEME_HTTP_TAG.equalsIgnoreCase(scheme) || SCHEME_HTTPS_TAG.equalsIgnoreCase(scheme)) {
            init(new URL(uri.getPath()).openStream());
        } else if (SCHEME_FILE_TAG.equalsIgnoreCase(scheme)) {
            init(new FileInputStream(uri.getPath()));
        }
    }

    private void init(String json) throws JSONException {
        if (!TextUtils.isEmpty(json)) {
            mJson = json;
        }
    }

    @Override
    public String data() {
        return mJson;
    }

    @Override
    public void release() {
        IOUtils.closeQuietly(mInput);
        mInput = null;
        mJson = null;
    }
}
