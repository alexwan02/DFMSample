package org.alexwan.dfmsample.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.desmond.ripple.RippleCompat;

import org.alexwan.dfmsample.R;
import org.alexwan.dfmsample.databinding.ItemSimpleBinding;

import java.util.List;

/**
 * SimpleAdapter
 * Created by alexwan on 16/10/17.
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {

    private List<String> datas;
    private LayoutInflater mLayoutInflater;
    private Context context;
    public SimpleAdapter(Context context, List<String> datas) {
        mLayoutInflater = LayoutInflater.from(context);
        this.datas = datas;
        this.context = context;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSimpleBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_simple, parent, false);
        return SimpleViewHolder.create(binding);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.setValues(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    static class SimpleViewHolder extends ViewHolder {
        private ItemSimpleBinding mBinding;

        static SimpleViewHolder create(ItemSimpleBinding binding) {
            return new SimpleViewHolder(binding);
        }

        SimpleViewHolder(ItemSimpleBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            RippleCompat.apply(binding.getRoot(), ContextCompat.getColor(binding.getRoot().getContext() , R.color.color_white));
        }

        void setValues(String name) {
            mBinding.setName(name);
            mBinding.executePendingBindings();
        }
    }
}
