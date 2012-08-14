package com.orange.score.activity.more;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orange.score.R;
import com.orange.score.activity.entry.PageableAdapter;

import com.orange.score.model.match.Match;

public class FinalScoreAdapter extends PageableAdapter<Match> {
	
	class Holder{
		
		RelativeLayout rLayout;
		TextView leagueName;
		TextView date;
		TextView homeTeamName;
		TextView awayTeamName;
		TextView score;
		
	}

	public FinalScoreAdapter(List<Match> list, Context context) {
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
	            convertView = LayoutInflater.from(context).inflate(R.layout.more_finalscore_item, null);
	            holder.rLayout = (RelativeLayout)convertView.findViewById(R.id.more_finalScore_RL);
	            holder.leagueName = (TextView)convertView.findViewById(R.id.finalScore_leagueName);
	            holder.date = (TextView)convertView.findViewById(R.id.finalScore_date);
	            holder.homeTeamName = (TextView)convertView.findViewById(R.id.finalScore_hometeamName);
	            holder.awayTeamName = (TextView)convertView.findViewById(R.id.finalScore_awayteamName);
	            holder.score = (TextView)convertView.findViewById(R.id.finalScore_score);
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
	        
	        String day = getDayofMonth(match.getDate());
	        holder.date.setText(day);
	        
	        if(match.needDisplayScore()) {
	        	String finalScore = String.format("%s:%s", match.getHomeTeamScore(), match.getAwayTeamScore());
	   	        String halfScore = String.format("(%s:%s)", match.getHomeTeamFirstHalfScore(),
	   	        		match.getAwayTeamFirstHalfScore());
	   	     
	   	        SpannableStringBuilder multiWord = new SpannableStringBuilder(); 
	   	        multiWord.append(finalScore);
	   			multiWord.append(halfScore);
	   			multiWord.setSpan(new ForegroundColorSpan(Color.RED), 0, finalScore.length(), 
	   						Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 
	   			multiWord.setSpan(new ForegroundColorSpan(Color.BLACK), finalScore.length(),
	   					multiWord.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
	   			holder.score.setText(multiWord);
	   			
	        } else {
	        	String str = match.getStatusString();
	        	int color = match.getMatchStatusColor();
	        	holder.score.setText(str);
	        	holder.score.setTextColor(color);
	        }
	        
	        return convertView;
	 }

	private String getDayofMonth(String date) {
		if (date == null || date.length()<8)
			return "";
		else 
			return date.substring(6, 8).concat("ÈÕ");
	}
	 
	

}
