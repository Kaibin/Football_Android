package com.orange.score;

import com.orange.score.constants.LanguageType;
import com.orange.score.constants.ScoreType;
import com.orange.score.network.ScoreNetworkRequest;

import android.app.Activity;
import android.os.Bundle;

public class ScoreActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//        ScoreNetworkRequest.getAllMatch(LanguageType.MANDARY, ScoreType.ALL);        
//        ScoreNetworkRequest.getMatchLiveChange();
    }
}