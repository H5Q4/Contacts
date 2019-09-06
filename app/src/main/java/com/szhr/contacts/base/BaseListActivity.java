package com.szhr.contacts.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.szhr.contacts.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("Registered")
public class BaseListActivity extends BaseActivity {
    public static final int INDICATOR_TYPE_INDEX = 1;
    public static final int INDICATOR_TYPE_CYCLE = 2;
    public static final int INDICATOR_TYPE_CUSTOM = 3;

    public static final String ITEM_NAME = "name";
    public static final String ITEM_EXTRA = "extra";

    protected ListView listView;
    protected int currentSelectedPosition;
    private View lastSelectedView;

    private int indicatorType;

    protected List<Map<String, String>> items;
    protected ViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        listView = findViewById(R.id.listView);
        listView.setItemsCanFocus(true);

        items = new ArrayList<>();
        adapter = new ViewAdapter(this);
        indicatorType = INDICATOR_TYPE_INDEX;

        listView.setAdapter(adapter);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currentSelectedPosition = position;
                if (lastSelectedView != null) {
                    lastSelectedView.setVisibility(View.GONE);
                }
                View extraTv = view.findViewById(R.id.extraTv);
                lastSelectedView = extraTv;
                if (extraTv != null && items.get(currentSelectedPosition).get("extra") != null) {
                    extraTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (lastSelectedView != null) {
                    lastSelectedView.setVisibility(View.GONE);
                }
                lastSelectedView = null;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onClickListItem(view, i);
            }
        });
        listView.setSelection(0);
    }

    protected void addListItem(String name, String extra) {
        Map<String, String> item = new HashMap<>();
        item.put(ITEM_NAME, name);
        item.put(ITEM_EXTRA, extra);
        items.add(item);
        notifyDataSetChanged();
    }

    protected void addListItem(String name) {
        Map<String, String> item = new HashMap<>();
        item.put(ITEM_NAME, name);
        items.add(item);
        notifyDataSetChanged();
    }

    protected void setListData(String[] names) {
        for (String name : names) {
            Map<String, String> item = new HashMap<>();
            item.put(ITEM_NAME, name);
            items.add(item);
        }
        notifyDataSetChanged();
    }

    protected void onClickListItem(View view, int position) {
        // Empty implementation
    }

    protected void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置图标类型(INDICATOR_TYPE_INDEX：显示序号，INDICATOR_TYPE_CYCLE：显示圆形，INDICATOR_TYPE_CUSTOM：自定义图标)
     */
    protected void setIndicatorType(int type){
        if(type == INDICATOR_TYPE_INDEX || type == INDICATOR_TYPE_CYCLE || type == INDICATOR_TYPE_CUSTOM ){
            indicatorType = type;
            notifyDataSetChanged();
        } else {
            throw new RuntimeException("No such indicator type.");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (currentSelectedPosition == 0) {
                    listView.setSelection(listView.getCount() - 1);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (currentSelectedPosition == listView.getCount() - 1) {
                    listView.setSelection(0);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    private class ViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ViewAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_base_list, null);
                holder.indicatorTv = convertView.findViewById(R.id.indicatorTv);
                holder.nameTv = convertView.findViewById(R.id.nameTv);
                holder.extraTv = convertView.findViewById(R.id.extraTv);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, String> item = items.get(i);

            holder.nameTv.setText(item.get(ITEM_NAME));
            holder.extraTv.setText(item.get(ITEM_EXTRA));
            holder.indicatorTv.setText("");

            switch (indicatorType) {
               case INDICATOR_TYPE_INDEX:
                   holder.indicatorTv.setText(String.valueOf(i + 1));
                   holder.indicatorTv.setBackgroundResource(R.drawable.selector_number_indicator_bg);
                   break;
                case INDICATOR_TYPE_CYCLE:
                    holder.indicatorTv.setBackgroundResource(R.drawable.selector_circle_indicator);
                    break;
                case INDICATOR_TYPE_CUSTOM:
                    holder.indicatorTv.setBackgroundResource(getItemIndicatorDrawable(i));
            }

            return convertView;
        }


    }

    protected int getItemIndicatorDrawable(int i) {
        throw new RuntimeException("Not implemented.");
    }

    static class ViewHolder {
        TextView indicatorTv;
        TextView nameTv;
        TextView extraTv;
    }
}
