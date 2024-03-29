DepositoryApp = new Ext.Application({

//Ext.regApplication({
    
    name: 'DepositoryApp',
    
    isLaunched: 0,
    
    launch: function() {
        		
        console.log("repository javascript launched");
        
        // define all views here
       
        
        // set launched flag
        DepositoryApp.isLaunched = 1;

        window.repository.showWebview();
        
//		娴�����绉��
//		testDisplayJifenLocally();
//      testDisplayJifenRemote();

//        娴��璧��
//      testDisplayScheduleLocally();
//      testDisplayScheduleRemote();

//		娴��灏��姒�//      testDisplayScorerLocally();
//      testDisplayScorerRemote();
        
//		娴��澶у�
//      testDisplayDaxiaoLocally();
//      testDisplayDaxiaoRemote();

//		娴��璁╃�
//      testDisplayRangqiuLocally();
//      testDisplayRangqiuRemote();
    }

});

function isAppLaunched(){
	return DepositoryApp.isLaunched;
}

function hideView(view){
	console.log("hide view " + view);
	if (view != null && view.mainPanel != undefined){
		view.mainPanel.hide();
	}
}

function hideAllView(){
	hideView(DepositoryApp.daxiaoView);
	hideView(DepositoryApp.jifenView);
	hideView(DepositoryApp.rangqiuView);
	hideView(DepositoryApp.scheduleView);
	hideView(DepositoryApp.scorerView);
}

function setCurrentView(panel){	
	hideAllView();
	DepositoryApp.viewport = panel;
	panel.show();	
}

function getJifenView()
{
	if (DepositoryApp.jifenView != null)
		return DepositoryApp.jifenView;
	else
		return new JifenView();
}


function displayJifen(reload, leagueId, season, lang, data){
	if (reload) {
		if (data != undefined) {
			if (jifenManager.readData(data) == false) {
				return false;
			}
		} else if (jifenManager.requestDataFromServer(leagueId, season, lang) == false){
			return false;
		}
	}
	DepositoryApp.jifenView = getJifenView();	
	setCurrentView(DepositoryApp.jifenView.mainPanel);	
	DepositoryApp.jifenView.updateView(jifenManager);
	return true;	
}


function getscheduleView()
{
	if (DepositoryApp.scheduleView != null)
		return DepositoryApp.scheduleView;
	else
		return new ScheduleView();
}


function displaySchedule(reload, leagueId, season, round, lang, data){
	if(round == undefined){
		round = "";
	}
	if(lang == undefined){
		lang = "";
	}
    var value = "";
	if (reload) {
		if (data != undefined) {
            value = scheduleManager.readData(data);
			if (value == false) {
				return "";
			}
		} else {
            value = scheduleManager.requestDataFromServer(leagueId, season, round ,lang);
            if (value == false){
                value = "";
            }
        }
	}
	DepositoryApp.scheduleView = getscheduleView();	
	setCurrentView(DepositoryApp.scheduleView.mainPanel);	
	DepositoryApp.scheduleView.updateView(scheduleManager);
	return value;	
}

function getScorerView()
{
	if (DepositoryApp.scorerView != null)
		return DepositoryApp.scorerView;
	else
		return new ScorerView();
}


function displayScorer(reload, leagueId, season, lang, data){
	if (reload) {
		if (data != undefined) {
			if (scorerManager.readData(data) == false) {
				return false;
			}
		} else if (scorerManager.requestDataFromServer(leagueId, season, lang) == false){
			return false;
		}
	}
	DepositoryApp.scorerView = getScorerView();	
	setCurrentView(DepositoryApp.scorerView.mainPanel);	
	DepositoryApp.scorerView.updateView(scorerManager);
	return true;	
}


function getDaxiaoView()
{
	if (DepositoryApp.daxiaoView != null)
		return DepositoryApp.daxiaoView;
	else
		return new DaxiaoView();
}


function displayDaxiao(reload, leagueId, season, lang, data){
	if (reload) {
		if (data != undefined) {
			if (daxiaoManager.readData(data) == false) {
				return false;
			}
		} else if (daxiaoManager.requestDataFromServer(leagueId, season, lang) == false){
			return false;
		}
	}
	DepositoryApp.daxiaoView = getDaxiaoView();	
	setCurrentView(DepositoryApp.daxiaoView.mainPanel);	
	DepositoryApp.daxiaoView.updateView(daxiaoManager);
	return true;	
}

function getRangqiuView()
{
	if (DepositoryApp.rangqiuView != null)
		return DepositoryApp.rangqiuView;
	else
		return new RangqiuView();
}


function displayRangqiu(reload, leagueId, season, lang, data){
	if (reload) {
		if (data != undefined) {
			if (rangqiuManager.readData(data) == false) {
				return false;
			}
		} else if (rangqiuManager.requestDataFromServer(leagueId, season, lang) == false){
			return false;
		}
	}
	DepositoryApp.rangqiuView = getRangqiuView();	
	setCurrentView(DepositoryApp.rangqiuView.mainPanel);	
	DepositoryApp.rangqiuView.updateView(rangqiuManager);
	return true;	
}