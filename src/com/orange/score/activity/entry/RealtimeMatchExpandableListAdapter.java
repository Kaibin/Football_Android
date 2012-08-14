package com.orange.score.activity.entry;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.score.R;
import com.orange.score.activity.entry.RealtimeMatchListAdapter.Holder;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchGroup;

public class RealtimeMatchExpandableListAdapter extends BaseExpandableListAdapter{

	private static final String TAG = RealtimeMatchExpandableListAdapter.class.getName();
	
	private Context context;
	private List<MatchGroup> groups;

	FollowMatchCallBack followMatchCallBack;
	
	
	public RealtimeMatchExpandableListAdapter(List<MatchGroup> groups, Context context, FollowMatchCallBack followMatchCallBack) {
		super();
		this.context = context;
		this.groups = groups;
		this.followMatchCallBack = followMatchCallBack;
	}
	
	public void updateList(List<MatchGroup> newGroups) {
		this.groups = newGroups;
	}

	static int MAX_TEAM_NAME_WIDTH = 100;
	static int CARD_WIDTH = 20;
	
	static class Holder{
		TextView homeTeamName;
		TextView awayTeamName;
		TextView homeTeamRed;
		TextView awayTeamRed;
		TextView homeTeamYellow;
		TextView awayTeamYellow;
		TextView leagueName;
		TextView chupan;
		TextView matchStatus;
		TextView matchStatusOnly;
		TextView matchStartTime;
		TextView matchScore;
		TextView firstHalfScore;
		
		Button   followButton;
	}

	
	static class TitleHolder{
		TextView leagueName;
		ImageView groupImage;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, 
			boolean isLastChild, View convertView, ViewGroup parent) {
			 
			final Holder holder;  
	        if (convertView != null)  
	        {  
	            holder = (Holder) convertView.getTag();
	            
	        }else {  
	        	                	
	            holder = new Holder();  
	            convertView = LayoutInflater.from(context).inflate(R.layout.realtime_match_item, null);
	            
	            holder.homeTeamName = (TextView)convertView.findViewById(R.id.hometeam_name);
	    		holder.awayTeamName = (TextView)convertView.findViewById(R.id.awayteam_name);
	    		holder.homeTeamRed = (TextView)convertView.findViewById(R.id.home_red);
	    		holder.awayTeamRed = (TextView)convertView.findViewById(R.id.away_red);
	    		holder.homeTeamYellow = (TextView)convertView.findViewById(R.id.home_yellow);
	    		holder.awayTeamYellow = (TextView)convertView.findViewById(R.id.away_yellow);
	    		holder.leagueName = (TextView)convertView.findViewById(R.id.match_league_name);
	    		holder.chupan = (TextView)convertView.findViewById(R.id.match_chupan);
	    		holder.matchStatus = (TextView)convertView.findViewById(R.id.match_status);
	    		holder.matchStatusOnly = (TextView)convertView.findViewById(R.id.match_status_only);
	    		holder.matchStartTime = (TextView)convertView.findViewById(R.id.match_start_time);
	    		holder.matchScore = (TextView)convertView.findViewById(R.id.match_score);
	    		holder.firstHalfScore = (TextView)convertView.findViewById(R.id.match_half_score);
	    		holder.followButton = (Button)convertView.findViewById(R.id.follow_star_button);
	            	            
	            convertView.setTag(holder);                          
	        }  
	        
	        final Match match = this.groups.get(groupPosition).children.get(childPosition);
	        
	        holder.followButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					followMatchCallBack.followUnfollowMatch(match);
					holder.followButton.setSelected(!holder.followButton.isSelected());
				}        	
	        });
	        
	        if (match.isFollow()){
	        	holder.followButton.setSelected(true);
	        }
	        else{
	        	holder.followButton.setSelected(false);
	        }

	        holder.homeTeamName.setText(match.getHomeTeamName());        
	        
	        holder.awayTeamName.setText(match.getAwayTeamName());
	        
	        holder.leagueName.setText(match.getLeagueName());
	        holder.leagueName.setTextColor(match.getLeagueColor());
	        
	        holder.matchStartTime.setText(match.getDateString());
	        

	        
	        holder.homeTeamRed.setText(match.getHomeTeamRed());
	        
	        holder.homeTeamYellow.setText(match.getHomeTeamYellow());

	        holder.awayTeamRed.setText(match.getAwayTeamRed());

	        setCardData(match.getHomeTeamYellow(), convertView, holder.homeTeamYellow);
	        setCardData(match.getHomeTeamRed(), convertView, holder.homeTeamRed);        
	        setCardData(match.getAwayTeamRed(), convertView, holder.awayTeamRed);
	        setCardData(match.getAwayTeamYellow(), convertView, holder.awayTeamYellow);
	        
	        int homeTeamMaxWidth = MAX_TEAM_NAME_WIDTH;	// you should align this value with layout XML
	        if (!hasCard(match.getHomeTeamYellow()))
	        	homeTeamMaxWidth += CARD_WIDTH;
	        if (!hasCard(match.getHomeTeamRed()))
	            homeTeamMaxWidth += CARD_WIDTH;
	        holder.homeTeamName.setMaxWidth(homeTeamMaxWidth);        
	        
	        int awayTeamMaxWidth = MAX_TEAM_NAME_WIDTH; // you should align this value with layout XML
	        if (!hasCard(match.getAwayTeamYellow()))
	        	awayTeamMaxWidth += CARD_WIDTH;
	        if (!hasCard(match.getAwayTeamRed()))
	        	awayTeamMaxWidth += CARD_WIDTH;
	        holder.awayTeamName.setMaxWidth(awayTeamMaxWidth);

	        if (match.needDisplayScore()){
	        	
	        	String minutes = match.getMatchStatusOrMinutString();
	        	
	        	if (minutes.contains("'")){
		        	SpannableStringBuilder multiWord = new SpannableStringBuilder(); 
			        multiWord.append(minutes);
					multiWord.setSpan(new ForegroundColorSpan(match.getMatchStatusColor()), 0, minutes.length() - 1, 
								Spannable.SPAN_INCLUSIVE_INCLUSIVE); 
					
					if ((System.currentTimeMillis()/1000) % 2 == 0){
						multiWord.setSpan(new ForegroundColorSpan(Color.RED), minutes.length() - 1,  minutes.length(),
								Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					}
					else{
						multiWord.setSpan(new ForegroundColorSpan(Color.LTGRAY), minutes.length() - 1,  minutes.length(),
								Spannable.SPAN_INCLUSIVE_INCLUSIVE);					
					}
					holder.matchStatus.setText(multiWord);
	        	}
	        	else{
	            	holder.matchStatus.setText(minutes);        		
	            	holder.matchStatus.setTextColor(match.getMatchStatusColor());
	        	}
	        	
	        	holder.matchStatus.setVisibility(View.VISIBLE);
	        
	        	holder.matchScore.setText(match.getHomeTeamScore() + ":" + match.getAwayTeamScore());
	        	holder.matchScore.setVisibility(View.VISIBLE);
	        	holder.matchScore.setTextColor(match.getMatchScoreColor());
	        	
	        	holder.matchStatusOnly.setVisibility(View.INVISIBLE);
	        }
	        else{
	        	holder.matchStatusOnly.setText(match.getMatchStatusString());
	        	holder.matchStatusOnly.setVisibility(View.VISIBLE);
	        	holder.matchStatusOnly.setTextColor(match.getMatchStatusColor());
	        	
	        	holder.matchStatus.setVisibility(View.INVISIBLE);
	        
	        	holder.matchScore.setVisibility(View.INVISIBLE);
	        }
	        
	        holder.firstHalfScore.setText("(" + match.getHomeTeamFirstHalfScore() + ":" 
	        		+ match.getAwayTeamFirstHalfScore() + ")");
	        if (match.hasFirstHalfScore()){
	        	holder.firstHalfScore.setVisibility(View.VISIBLE);
	        }
	        else{
	        	holder.firstHalfScore.setVisibility(View.INVISIBLE);
	        }
	        
	        if (match.hasChupan()){
	        	holder.chupan.setVisibility(View.VISIBLE);
	        	holder.chupan.setText(match.getCrownChupanString());
	        }
	        else{
	        	holder.chupan.setVisibility(View.INVISIBLE);
	        }
	        
	        if (match.isScoreJustUpdate()){
	        	Log.d(TAG, "score is just updated, set special background");
	        	convertView.setBackgroundResource(R.drawable.kive_li2);
	        }
	        else{
	        	convertView.setBackgroundResource(R.drawable.realtime_match_line_background);
	        }
	        
	        return convertView;
	    }


	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TitleHolder holder = null;
		if (convertView != null) {
			holder = (TitleHolder) convertView.getTag();
		} else {
			  holder = new TitleHolder();  
	          convertView = LayoutInflater.from(context).inflate(R.layout.realtime_match_group_title_item, null);
			  holder.leagueName = (TextView) convertView.findViewById(R.id.leagueName);
			  holder.groupImage = (ImageView) convertView.findViewById(R.id.icon_image);
	          convertView.setTag(holder);
		}
		
		String leagueName = this.groups.get(groupPosition).leagueName;
		holder.leagueName.setText(leagueName);
		holder.groupImage.setBackgroundResource(R.drawable.odds_down);
		if (isExpanded) {
			holder.groupImage.setBackgroundResource(R.drawable.odds_up);
		}
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private boolean hasCard(String cardValue) {
		return (cardValue != null && cardValue.length() > 0 && Integer
				.parseInt(cardValue) > 0) ? true : false;
	}

	private void setCardData(String cardValue, View superView, TextView cardView) {
		boolean hasCard = hasCard(cardValue);
		if (hasCard) {
			cardView.setText(cardValue);
			cardView.setVisibility(View.VISIBLE);
		} else {
			cardView.setVisibility(View.GONE);
		}
}
	
}
