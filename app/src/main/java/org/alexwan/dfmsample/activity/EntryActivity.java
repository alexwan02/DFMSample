package org.alexwan.dfmsample.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.desmond.ripple.RippleCompat;

import org.alexwan.dfmsample.R;
import org.alexwan.dfmsample.adapter.SimpleAdapter;
import org.alexwan.dfmsample.databinding.ActivityEntryBinding;

import java.util.Arrays;

/**
 * EntryActivity
 * Created by alexwan on 16/10/17.
 */
public class EntryActivity extends AppCompatActivity {

//    private ActivityEntryBinding mBinding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEntryBinding mBinding = DataBindingUtil.setContentView(this , R.layout.activity_entry);
        RippleCompat.init(this);
        setSupportActionBar(mBinding.toolbar);
        String[] samples = getResources().getStringArray(R.array.danmaku_samples);
        SimpleAdapter adapter = new SimpleAdapter(this , Arrays.asList(samples));
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setAdapter(adapter);
    }

}
