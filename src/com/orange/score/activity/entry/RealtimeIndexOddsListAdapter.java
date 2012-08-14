package com.orange.score.activity.entry;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.score.R;
import com.orange.score.model.index.CompanyManager;
import com.orange.score.model.index.DaxiaoOdds;
import com.orange.score.model.index.Odds;
import com.orange.score.model.index.OddsGroup;
import com.orange.score.model.index.OupeiOdds;
import com.orange.score.model.index.YapeiOdds;
import com.orange.score.model.match.Match;
import com.orange.score.model.match.MatchStatusType;
import com.orange.score.utils.DataUtils;

public class RealtimeIndexOddsListAdapter extends BaseExpandableListAdapter{
	
	private Context context;
	private List<OddsGroup> groups;
	private CompanyManager companyManager;
	
	public RealtimeIndexOddsListAdapter(Context context, List<OddsGroup> groups, CompanyManager companyManager) {
		this.context = context;
		this.groups = groups;
		this.companyManager = companyManager;
	}
	
	class Holder{
		TextView companyName;
		TextView homeTeamChupan;
		TextView awayTeamChupan;
		TextView chupan;
		TextView homeTeamOdds;
		TextView awayTeamOdds;
		TextView instantOdds;
	}
	
	class TitleHolder{
		ImageView groupImage;
		TextView leagueName;
		TextView matchTime;
		TextView homeTeamName;
		TextView awayTeamName;
		TextView finishScore;
		
		LinearLayout titleLayout;
		TextView homeColumn;
		TextView middleColumn;
		TextView awayColumn;
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
		Holder holder = null;
		if (convertView != null) {
			holder = (Holder) convertView.getTag();
		} else {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(R.layout.realindex_odds_child_item, null);
			holder.homeTeamChupan = (TextView) convertView.findViewById(R.id.homeTeamChupan);
			holder.awayTeamChupan = (TextView) convertView.findViewById(R.id.awayTeamChupan);
			holder.chupan = (TextView) convertView.findViewById(R.id.chupan);
			holder.homeTeamOdds = (TextView) convertView.findViewById(R.id.homeTeamOdds);
			holder.awayTeamOdds = (TextView) convertView.findViewById(R.id.awayTeamOdds);
			holder.instantOdds = (TextView) convertView.findViewById(R.id.instantOdds);
			holder.companyName = (TextView) convertView.findViewById(R.id.companyName);
			convertView.setTag(holder);
		}
		
		Odds odds = this.groups.get(groupPosition).children.get(childPosition);
		if (odds == null) {
			return null;
		}
		holder.companyName.setText(companyManager.getCompanyNameById(odds.getCompanyId()));
		switch (companyManager.getOddsType()) {
		case YAPEI:
			if ((odds instanceof YapeiOdds)) {
				YapeiOdds yapeiOdds = (YapeiOdds)odds;
				holder.homeTeamChupan.setText(yapeiOdds.getHomeTeamChupan());
				holder.awayTeamChupan.setText(yapeiOdds.getAwayTemaChupan());
				holder.chupan.setText(DataUtils.toChuPanString(yapeiOdds.getChupan()));
				holder.homeTeamOdds.setText(yapeiOdds.getHomeTeamOdds());
				holder.awayTeamOdds.setText(yapeiOdds.getAwayTemaOdds());
				holder.instantOdds.setText(DataUtils.toChuPanString(yapeiOdds.getInstantOdds()));
				if (odds.isOddsJustUpdate()) {
					if (yapeiOdds.homeTeamOddsFlag == 1) {
						holder.homeTeamOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (yapeiOdds.homeTeamOddsFlag == -1) {
						holder.homeTeamOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
					if (yapeiOdds.awayTeamOddsFlag == 1) {
						holder.awayTeamOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (yapeiOdds.awayTeamOddsFlag == -1) {
						holder.awayTeamOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
					if (yapeiOdds.pankouFlag == 1) {
						holder.instantOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (yapeiOdds.pankouFlag == -1) {
						holder.instantOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
				} else {
					holder.homeTeamOdds.setBackgroundResource(R.drawable.setbar_bg);
					holder.awayTeamOdds.setBackgroundResource(R.drawable.setbar_bg);
					holder.instantOdds.setBackgroundResource(R.drawable.setbar_bg);
				}
			}
			break;
		case OUPEI:
			if (odds instanceof OupeiOdds) {
				OupeiOdds oupeiOdds = (OupeiOdds)odds;
				holder.homeTeamChupan.setText(oupeiOdds.getHomeWinChupei());
				holder.awayTeamChupan.setText(oupeiOdds.getAwayWinChupei());
				holder.chupan.setText(oupeiOdds.getDrawWinChupei());
				holder.homeTeamOdds.setText(oupeiOdds.getHomeWinInstantOdds());
				holder.awayTeamOdds.setText(oupeiOdds.getAwayWinInstantOdds());
				holder.instantOdds.setText(oupeiOdds.getDrawInstantOdds());
				if (odds.isOddsJustUpdate()) {
					if (oupeiOdds.homeTeamOddsFlag == 1) {
						holder.homeTeamOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (oupeiOdds.homeTeamOddsFlag == -1) {
						holder.homeTeamOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
					if (oupeiOdds.awayTeamOddsFlag == 1) {
						holder.awayTeamOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (oupeiOdds.awayTeamOddsFlag == -1) {
						holder.awayTeamOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
					if (oupeiOdds.pankouFlag == 1) {
						holder.instantOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (oupeiOdds.pankouFlag == -1) {
						holder.instantOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
				} else {
					holder.homeTeamOdds.setBackgroundResource(R.drawable.setbar_bg);
					holder.awayTeamOdds.setBackgroundResource(R.drawable.setbar_bg);
					holder.instantOdds.setBackgroundResource(R.drawable.setbar_bg);
				}
			}
			break;
		case DAXIAO:
			if (odds instanceof DaxiaoOdds) {
				DaxiaoOdds daxiaoOdds = (DaxiaoOdds)odds;
				holder.homeTeamChupan.setText(daxiaoOdds.getBigChupan());
				holder.awayTeamChupan.setText(daxiaoOdds.getSmallChupan());
				holder.chupan.setText(DataUtils.toDaxiaoPanKouString(daxiaoOdds.getChupan()));
				holder.homeTeamOdds.setText(daxiaoOdds.getBigOdds());
				holder.awayTeamOdds.setText(daxiaoOdds.getSmallChupan());
				holder.instantOdds.setText(DataUtils.toDaxiaoPanKouString(daxiaoOdds.getInstantOdds()));
				if (odds.isOddsJustUpdate()) {
					if (daxiaoOdds.homeTeamOddsFlag == 1) {
						holder.homeTeamOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (daxiaoOdds.homeTeamOddsFlag == -1) {
						holder.homeTeamOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
					if (daxiaoOdds.awayTeamOddsFlag == 1) {
						holder.awayTeamOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (daxiaoOdds.awayTeamOddsFlag == -1) {
						holder.awayTeamOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
					if (daxiaoOdds.pankouFlag == 1) {
						holder.instantOdds.setBackgroundColor(Color.argb(255, 0xFE, 0xE4, 0xCB));
					} else if (daxiaoOdds.pankouFlag == -1) {
						holder.instantOdds.setBackgroundColor(Color.argb(255, 0xEA, 0xFD, 0xAE));
					}
				} else {
					holder.homeTeamOdds.setBackgroundResource(R.drawable.setbar_bg);
					holder.awayTeamOdds.setBackgroundResource(R.drawable.setbar_bg);
					holder.instantOdds.setBackgroundResource(R.drawable.setbar_bg);
				}
			}
			break;
		default:
			break;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.realindex_odds_group_item, null);
			holder.groupImage = (ImageView) convertView.findViewById(R.id.icon_image);
			holder.leagueName = (TextView) convertView.findViewById(R.id.leagueName);
			holder.homeTeamName = (TextView) convertView.findViewById(R.id.homeTeamName);
			holder.awayTeamName = (TextView) convertView.findViewById(R.id.awayTeamName);
			holder.matchTime = (TextView) convertView.findViewById(R.id.matchTime);
			holder.finishScore = (TextView) convertView.findViewById(R.id.finishScore);
			holder.titleLayout = (LinearLayout) convertView.findViewById(R.id.odds_title_row);
			holder.homeColumn = (TextView) convertView.findViewById(R.id.homeColumn);
			holder.middleColumn = (TextView) convertView.findViewById(R.id.middleColumn);
			holder.awayColumn = (TextView) convertView.findViewById(R.id.awayColumn);
			convertView.setTag(holder);
		}
		
		if (isExpanded) {
			holder.groupImage.setBackgroundResource(R.drawable.odds_up);
			holder.titleLayout.setVisibility(View.VISIBLE);
		} else {
			holder.groupImage.setBackgroundResource(R.drawable.odds_down);
			holder.titleLayout.setVisibility(View.GONE);
		}
		
		Match match = this.groups.get(groupPosition).match;
		if (match != null) {
			holder.homeTeamName.setText(match.getHomeTeamName());
			holder.awayTeamName.setText(match.getAwayTeamName());
			holder.matchTime.setText( match.getMatchTime().substring(4, 6) + "/"+ match.getMatchTime().substring(6, 8));
			holder.leagueName.setText(match.getLeagueName());
			
			String finishScore = "VS";
			if (match.getStatus() == MatchStatusType.FINISH) {
				finishScore = match.getHomeTeamScore() + ":" + match.getAwayTeamScore();
			}
			holder.finishScore.setText(finishScore);
		}
		
		switch (companyManager.getOddsType()) {
		case YAPEI:
			holder.homeColumn.setText("上盘");
			holder.middleColumn.setText("盘口");
			holder.awayColumn.setText("下盘");
			break;
		case OUPEI:
			holder.homeColumn.setText("主胜");
			holder.middleColumn.setText("和");
			holder.awayColumn.setText("客胜");
			break;
		case DAXIAO:
			holder.homeColumn.setText("大球");
			holder.middleColumn.setText("盘口");
			holder.awayColumn.setText("小球");
			break;
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
		// TODO Auto-generated method stub
		return false;
	}

}
