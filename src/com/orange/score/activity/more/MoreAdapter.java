package com.orange.score.activity.more;

import java.util.List;

import com.orange.score.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoreAdapter extends ArrayAdapter<String> {
	
    private List<String> dataList;
    
    private int resource; 
    
    private static final String TAG = "MoreAdapter";

    public MoreAdapter(Context context, int rescourceID, List<String> data) {
    	super(context, rescourceID, data);
    	this.resource = rescourceID;
        this.dataList = data;
    }
    
    static class ViewHolder {
        TextView text;
        ImageView icon;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        RelativeLayout relativeLayout; 
        
        TextView textView;
        
        ImageView imageView;
        
        if(convertView == null) { 
        	relativeLayout = new RelativeLayout(getContext()); 
            String inflater = Context.LAYOUT_INFLATER_SERVICE; 
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater); 
            vi.inflate(resource, relativeLayout, true); 
        } else { 
        	relativeLayout = (RelativeLayout)convertView; 
        }
        
        textView = (TextView) relativeLayout.findViewById(R.id.more_item_textview1);
        imageView = (ImageView) relativeLayout.findViewById(R.id.more_itme_imageview);
        
        switch (position) {
		case 0:
			relativeLayout.setBackgroundResource(R.drawable.more_item_top_color);
			textView.setText(dataList.get(0));
			imageView.setBackgroundResource(R.drawable.more_item_0);
			break;
		case 1:
			relativeLayout.setBackgroundResource(R.drawable.more_item_middle_color);
			textView.setText(dataList.get(1));
			imageView.setBackgroundResource(R.drawable.more_item_1);
			break;
		case 2:
			relativeLayout.setBackgroundResource(R.drawable.more_item_middle_color);
			textView.setText(dataList.get(2));
			imageView.setBackgroundResource(R.drawable.more_item_2);
			break;
		case 3:
			relativeLayout.setBackgroundResource(R.drawable.more_item_middle_color);
			textView.setText(dataList.get(3));
			imageView.setBackgroundResource(R.drawable.more_item_3);
			break;
		case 4:
			relativeLayout.setBackgroundResource(R.drawable.more_item_middle_color);
			textView.setText(dataList.get(4));
			imageView.setBackgroundResource(R.drawable.more_item_4);
			break;
		case 5:
			relativeLayout.setBackgroundResource(R.drawable.more_item_middle_color);
			textView.setText(dataList.get(5));
			imageView.setBackgroundResource(R.drawable.more_item_5);
			break;
		case 6:
			relativeLayout.setBackgroundResource(R.drawable.more_item_middle_color);
			textView.setText(dataList.get(6));
			imageView.setBackgroundResource(R.drawable.more_item_6);
			break;
		case 7:
			relativeLayout.setBackgroundResource(R.drawable.more_item_middle_color);
			textView.setText(dataList.get(7));
			imageView.setBackgroundResource(R.drawable.more_item_7);
			break;
		case 8:
			relativeLayout.setBackgroundResource(R.drawable.more_item_middle_color);
			textView.setText(dataList.get(8));
			imageView.setBackgroundResource(R.drawable.more_item_11);
			break;
		case 9:
			relativeLayout.setBackgroundResource(R.drawable.more_item_bottom_color);
			textView.setText(dataList.get(9));
			imageView.setBackgroundResource(R.drawable.more_item_8);
			break;
		default:
			break;
		}
        
        return relativeLayout;
    }

}

