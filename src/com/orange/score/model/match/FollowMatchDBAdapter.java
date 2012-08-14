package com.orange.score.model.match;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class FollowMatchDBAdapter {

	
	private static final String DB_NAME = "ScoreDB.db";
	private static final String TABLE_FOLLOW_MATCH = "T_FOLLOW_MATCH";
	private static final int DB_VERSION = 1;
	
	public static final String COLUMN_MATCH_ID = "match_id";
	public static final String COLUMN_LEAGUE_ID = "league_id";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_START_DATE = "start_date";
	public static final String COLUMN_HOME_TEAM_NAME = "home_team_name";
	public static final String COLUMN_AWAY_TEAM_NAME = "away_team_name";
	public static final String COLUMN_HOME_TEAM_SCORE = "home_team_score";
	public static final String COLUMN_AWAY_TEAM_SCORE = "away_team_score";
	public static final String COLUMN_HOME_TEAM_FIRSTHALF_SCORE = "home_team_firsthalf_score";
	public static final String COLUMN_AWAY_TEAM_FIRSTHALF_SCORE = "away_team_firsthalf_score";
	public static final String COLUMN_HOME_TEAM_RED = "home_team_red";
	public static final String COLUMN_AWAY_TEAM_RED = "away_team_red";
	public static final String COLUMN_HOME_TEAM_YELLOW = "home_team_yellow";
	public static final String COLUMN_AWAY_TEAM_YELLOW = "away_team_yellow";
	public static final String COLUMN_CROWN_CHUPAN = "crown_chupan";
	public static final String COLUMN_LEAGUE_NAME = "league_name";
	public static final String COLUMN_IS_FOLLOW = "is_follow";
	
	private static final String COLUMN_HOME_CANTONESE_NAME = "home_cantonese_name";
	private static final String COLUMN_AWAY_CANTONESE_NAME = "away_cantonese_name";
	private static final String COLUMN_HOME_TEAM_RANK = "home_rank";
	private static final String COLUMN_AWAY_TEAM_RANK = "away_rank";
	private static final String COLUMN_HOME_TEAM_IMAGE_URL = "home_image";
	private static final String COLUMN_AWAY_TEAM_IMAGE_URL = "away_image";
	private static final String COLUMN_HAS_LINEUP = "has_lineup";

	
	public static final String SQL_CREATE_DB = "create table " + TABLE_FOLLOW_MATCH + 
							" (" + COLUMN_MATCH_ID + " TEXT primary key" +
							", " + COLUMN_LEAGUE_ID + " TEXT not null" +
							", " + COLUMN_STATUS + " INTEGER not null" +
							", " + COLUMN_DATE + " TEXT not null" +
							", " + COLUMN_START_DATE + " TEXT" +
							", " + COLUMN_HOME_TEAM_NAME + " TEXT not null" +
							", " + COLUMN_AWAY_TEAM_NAME + " TEXT not null" +
							", " + COLUMN_HOME_TEAM_SCORE + " TEXT" +
							", " + COLUMN_AWAY_TEAM_SCORE + " TEXT" +
							", " + COLUMN_HOME_TEAM_FIRSTHALF_SCORE + " TEXT" +
							", " + COLUMN_AWAY_TEAM_FIRSTHALF_SCORE + " TEXT" +
							", " + COLUMN_HOME_TEAM_RED + " TEXT" +
							", " + COLUMN_AWAY_TEAM_RED + " TEXT" +
							", " + COLUMN_HOME_TEAM_YELLOW + " TEXT" +
							", " + COLUMN_AWAY_TEAM_YELLOW + " TEXT" +
							", " + COLUMN_CROWN_CHUPAN + " TEXT" +
							", " + COLUMN_LEAGUE_NAME + " TEXT" +
							", " + COLUMN_IS_FOLLOW + " INTEGER not null" +
							", " + COLUMN_HOME_CANTONESE_NAME + " TEXT" +
							", " + COLUMN_AWAY_CANTONESE_NAME + " TEXT" +
							", " + COLUMN_HOME_TEAM_RANK + " TEXT" +
							", " + COLUMN_AWAY_TEAM_RANK + " TEXT" +
							", " + COLUMN_HOME_TEAM_IMAGE_URL + " TEXT" +
							", " + COLUMN_AWAY_TEAM_IMAGE_URL + " TEXT" +
							", " + COLUMN_HAS_LINEUP + " TEXT" +
							");";
	
	private static final String TAG = "DBAdapter";
	
	
	
	private SQLiteDatabase db;
	private final Context context;
	private MyDbHelper dbHelper;
	
	public FollowMatchDBAdapter(Context _context){
		this.context = _context;
		dbHelper = new MyDbHelper(context, DB_NAME, null, DB_VERSION);		
	}
	
	public synchronized FollowMatchDBAdapter open() throws SQLException{
		if (db != null){
			if (db.isOpen()){
				return this;
			}
		}
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public synchronized void close(){
		
		if (db == null || !db.isOpen()){
			Log.d(TAG, "<close db> but db is null or not open");
			return;
		}
		
		db.close();
		db = null;
	}
	
	private ContentValues fromMatch(Match match){	
		
		ContentValues contentValues = new ContentValues();

		contentValues.put(COLUMN_MATCH_ID, match.matchId);
		contentValues.put(COLUMN_LEAGUE_ID, match.leagueId);
		contentValues.put(COLUMN_STATUS, match.status);
		contentValues.put(COLUMN_DATE, match.date);
		contentValues.put(COLUMN_START_DATE, match.startDate);		
		contentValues.put(COLUMN_HOME_TEAM_NAME, match.homeTeamName);
		contentValues.put(COLUMN_AWAY_TEAM_NAME, match.awayTeamName);
		contentValues.put(COLUMN_HOME_TEAM_SCORE, match.homeTeamScore);
		contentValues.put(COLUMN_AWAY_TEAM_SCORE, match.awayTeamScore);
		contentValues.put(COLUMN_HOME_TEAM_FIRSTHALF_SCORE, match.homeTeamFirstHalfScore);
		contentValues.put(COLUMN_AWAY_TEAM_FIRSTHALF_SCORE, match.awayTeamFirstHalfScore);
		contentValues.put(COLUMN_HOME_TEAM_RED, match.homeTeamRed);
		contentValues.put(COLUMN_AWAY_TEAM_RED, match.awayTeamRed);
		contentValues.put(COLUMN_HOME_TEAM_YELLOW, match.homeTeamYellow);
		contentValues.put(COLUMN_AWAY_TEAM_YELLOW, match.awayTeamYellow);
		contentValues.put(COLUMN_CROWN_CHUPAN, match.crownChupan);
		contentValues.put(COLUMN_LEAGUE_NAME, match.getLeagueName());
		contentValues.put(COLUMN_IS_FOLLOW, 1);
		
		contentValues.put(COLUMN_HOME_CANTONESE_NAME, match.homeCantoneseName);
		contentValues.put(COLUMN_AWAY_CANTONESE_NAME, match.awayCantoneseName);
		contentValues.put(COLUMN_HOME_TEAM_RANK, match.homeTeamRank);
		contentValues.put(COLUMN_AWAY_TEAM_RANK, match.awayTeamRank);
		contentValues.put(COLUMN_HOME_TEAM_IMAGE_URL, match.homeTeamImageUrl);
		contentValues.put(COLUMN_AWAY_TEAM_IMAGE_URL, match.awayTeamImageUrl);
		contentValues.put(COLUMN_HAS_LINEUP, match.hasLineup);
		
		return contentValues;
	}
	
	public synchronized long insertEntry(Match match){		
		
		if (db == null || !db.isOpen()){
			Log.d(TAG, "<insertEntry> but db is null or not open");
			return -1;
		}
		
		long result = db.insert(TABLE_FOLLOW_MATCH, null, fromMatch(match));
		Log.d(TAG, "insert entry result = " + result);
		return result;
	}
	
	public synchronized long removeEntry(String matchId){
		if (db == null || !db.isOpen()){
			Log.d(TAG, "<removeEntry> but db is null or not open");
			return -1;
		}

		long result = db.delete(TABLE_FOLLOW_MATCH, 
				COLUMN_MATCH_ID + "='" + matchId + "'",
				null);
		
		Log.d(TAG, "delete entry, matchId=" + matchId + ", result = " + result);
		return result;
	}
	
	public synchronized Cursor getAllEntries(){
		if (db == null || !db.isOpen()){
			Log.d(TAG, "<findEntryByMatch> but db is null or not open");
			return null;
		}
		
		return db.query(TABLE_FOLLOW_MATCH, null, null, null, null, null, null);
	}
	
	public synchronized Cursor findEntryByMatch(Match match) {
		if (db == null || !db.isOpen()){
			Log.d(TAG, "<findEntryByMatch> but db is null or not open");
			return null;
		}
		
		String where = COLUMN_MATCH_ID + "='" + match.getMatchId() + "'";	
		return db.query(TABLE_FOLLOW_MATCH, null, where, null, null, null, null);
	}
	
	public synchronized int updateEntry(Match match){
		if (db == null || !db.isOpen()){
			Log.d(TAG, "<updateEntry> but db is null or not open");
			return -1;
		}		
		
		Log.d(TAG, "update entry, match = " + match.toString());
		String where = COLUMN_MATCH_ID + "='" + match.getMatchId() + "'";		
		return db.update(TABLE_FOLLOW_MATCH, fromMatch(match), where, null);
	}
	
	private static class MyDbHelper extends SQLiteOpenHelper{

		public MyDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_DB);			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// do nothing here at this moment
		}
		
	}

	
}
