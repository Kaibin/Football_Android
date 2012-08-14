package com.orange.score.activity.entry;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.score.R;
import com.orange.score.model.match.ScoreUpdate;

public class ScoreUpdateListAdapter extends PageableAdapter<ScoreUpdate> {

	boolean editMode = false;
	
	class Holder{

		Button     deleteButton;
		ImageView updateType;				

		TextView leagueName;
		TextView matchStartTime;

		TextView matchMinutes;

		TextView homeTeamName;
		TextView awayTeamName;

		TextView homeFlag;
		TextView awayFlag;
						
		TextView updateDataName;
		TextView updateDataValue;		
	}
	
	public ScoreUpdateListAdapter(List<ScoreUpdate> list, Context context) {
		super(list, context);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		 
		final Holder holder;  
        if (convertView != null)  
        {  
            holder = (Holder) convertView.getTag();
            
        }else {  
        	
            holder = new Holder();  
            convertView = LayoutInflater.from(context).inflate(R.layout.score_update_item, null);
            
            holder.deleteButton = (Button)convertView.findViewById(R.id.su_delete_button);
            holder.updateType = (ImageView)convertView.findViewById(R.id.su_type_image);
            
            holder.leagueName = (TextView)convertView.findViewById(R.id.su_league_name);
            holder.matchStartTime = (TextView)convertView.findViewById(R.id.su_start_time);
            
            holder.matchMinutes = (TextView)convertView.findViewById(R.id.su_match_minutes);
            
            holder.homeTeamName = (TextView)convertView.findViewById(R.id.su_hometeam_name);
    		holder.awayTeamName = (TextView)convertView.findViewById(R.id.su_awayteam_name);
    		
    		holder.homeFlag = (TextView)convertView.findViewById(R.id.su_from_hometeam);
    		holder.awayFlag = (TextView)convertView.findViewById(R.id.su_from_awayteam);
    		
    		holder.updateDataName = (TextView)convertView.findViewById(R.id.su_data_name);
    		holder.updateDataValue = (TextView)convertView.findViewById(R.id.su_data_value);    		    	
            	            
//    		holder.homeTeamName.getPaint().setFakeBoldText(true);
//    		holder.awayTeamName.getPaint().setFakeBoldText(true);  
    		
            convertView.setTag(holder);                          
        }  
                	                
        if (position + startIndex >= list.size()){        	
        	return null;
        }
        
        final ScoreUpdate scoreUpdate = list.get(position + startIndex);
        
        holder.deleteButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO
				list.remove(scoreUpdate);
				updateList(list);
				notifyDataSetChanged();
			}        	
        });        		
        
		// set delete button display or not
		if (editMode){
			holder.deleteButton.setVisibility(View.VISIBLE);
		}
		else{
			holder.deleteButton.setVisibility(View.GONE);			
		}
		
		// set card / ball image by type
		switch (scoreUpdate.updateType){
		case ScoreUpdate.UPDATE_TYPE_SCORE:
			holder.updateType.setImageResource(R.drawable.ls_ball);		
			holder.homeFlag.setBackgroundResource(R.drawable.ls_img1);
			holder.awayFlag.setBackgroundResource(R.drawable.ls_img1);
			holder.homeFlag.setText("进球");
			holder.awayFlag.setText("进球");
			break;
			
		case ScoreUpdate.UPDATE_TYPE_RED_CARD:
			holder.updateType.setImageResource(R.drawable.ls_redcard);
			holder.homeFlag.setBackgroundResource(R.drawable.ls_img2);
			holder.awayFlag.setBackgroundResource(R.drawable.ls_img2);
			holder.homeFlag.setText("红牌");
			holder.awayFlag.setText("红牌");
			break;
			
		case ScoreUpdate.UPDATE_TYPE_YELLOW_CARD:
			holder.updateType.setImageResource(R.drawable.ls_yellowcard);
			holder.homeFlag.setBackgroundResource(R.drawable.ls_img3);
			holder.awayFlag.setBackgroundResource(R.drawable.ls_img3);
			holder.homeFlag.setText("黄牌");
			holder.awayFlag.setText("黄牌");
			break;			
		}
		
		// set others
		holder.leagueName.setText(scoreUpdate.leagueName);
		holder.leagueName.setTextColor(scoreUpdate.leagueColor);
		
		holder.matchStartTime.setText(scoreUpdate.date);
		
		holder.matchMinutes.setText(scoreUpdate.minutes);
		holder.homeTeamName.setText(scoreUpdate.homeTeamName);
		holder.awayTeamName.setText(scoreUpdate.awayTeamName);
		
		// show home flag
		switch (scoreUpdate.homeAwayFlag){
		case ScoreUpdate.FLAG_BOTH:
			holder.homeFlag.setVisibility(View.VISIBLE);
			holder.awayFlag.setVisibility(View.VISIBLE);
			break;
		case ScoreUpdate.FLAG_HOME:
			holder.homeFlag.setVisibility(View.VISIBLE);
			holder.awayFlag.setVisibility(View.INVISIBLE);
			break;
		case ScoreUpdate.FLAG_AWAY:
			holder.homeFlag.setVisibility(View.INVISIBLE);
			holder.awayFlag.setVisibility(View.VISIBLE);
			break;
		}
		
		// set data
		switch (scoreUpdate.updateType){
		case ScoreUpdate.UPDATE_TYPE_SCORE:
			holder.updateDataName.setText("比分");
			holder.updateDataValue.setText(scoreUpdate.score);
			holder.homeFlag.setTextColor(Color.WHITE);
			holder.awayFlag.setTextColor(Color.WHITE);
			break;
			
		case ScoreUpdate.UPDATE_TYPE_RED_CARD:
			holder.updateDataName.setText("比数");
			holder.updateDataValue.setText(scoreUpdate.redCardScore);
			holder.homeFlag.setTextColor(Color.WHITE);
			holder.awayFlag.setTextColor(Color.WHITE);
			break;
			
		case ScoreUpdate.UPDATE_TYPE_YELLOW_CARD:
			holder.updateDataName.setText("比数");
			holder.updateDataValue.setText(scoreUpdate.yellowCardScore);
			int yellowCardTextColor = Color.argb(0xFF, 0x79, 0x52, 0x0);
			holder.homeFlag.setTextColor(yellowCardTextColor);
			holder.awayFlag.setTextColor(yellowCardTextColor);			
			break;					
		}
        
        return convertView;
    }

	public void turnOnEditMode(){
		editMode = true;
	}
	
	public void turnOffEditMode(){
		editMode = false;
	}

	
	
}
