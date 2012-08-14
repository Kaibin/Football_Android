package com.orange.score.activity.more;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orange.score.R;
import com.orange.score.activity.entry.PageableAdapter;
import com.orange.score.model.match.Match;

public class WeeklyScheduleAdapter extends PageableAdapter<Match>{
	
	class Holder{
		
		TextView leagueName;
		TextView date;
		TextView homeTeamName;
		TextView awayTeamName;
		
	}

	public WeeklyScheduleAdapter(List<Match> list, Context context) {
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
	            convertView = LayoutInflater.from(context).inflate(R.layout.more_weeklyschedule_item, null);
	            holder.leagueName = (TextView)convertView.findViewById(R.id.finalScore_leagueName);
	            holder.date = (TextView)convertView.findViewById(R.id.finalScore_date);
	            holder.homeTeamName = (TextView)convertView.findViewById(R.id.finalScore_hometeamName);
	            holder.awayTeamName = (TextView)convertView.findViewById(R.id.finalScore_awayteamName);
	            convertView.setTag(holder);   
	        }
			
			if (position % 2 ==0) {
				convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
			}
			else{
				convertView.setBackgroundColor(context.getResources().getColor(R.color.more_match_item_color));
			}
			
			if (position + startIndex >= list.size()){        	
			     	return null;
			}
			final Match match = list.get(position + startIndex);
			
			holder.homeTeamName.setText(match.getHomeTeamName());        
	        
	        holder.awayTeamName.setText(match.getAwayTeamName());
	        
	        holder.leagueName.setText(match.getLeagueName());
	        
	        //20120201163000
	        String day = getDateFromString(match.getDate());
	        holder.date.setText(day);
			
	        return convertView;
	 }

	private String getDateFromString(String date) {
		if(date == null || date.length()<12) {
			return "";
		} else {
			return String.format("%sÈÕ%s:%s", date.substring(6, 8), date.substring(8, 10),
					date.substring(10,12));
		}
	}

}
