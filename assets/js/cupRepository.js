CupRepositoryApp = new Ext.Application({

//Ext.regApplication({
    
    name: 'MatchDetailApp',
    
    isLaunched: 0,
    
    launch: function() {
        		
        console.log("cup repository javascript launched");
        
        // define all views here
        
        // set launched flag
        CupRepositoryApp.isLaunched = 1;
        
        window.cupRepository.showWebview();
        
        //娴��灏��绉��
//        testDisplayCupGroupPointsLocally();
//        testDisplayCupGroupPointsRemote();
        
        //娴��灏��璧��璧��
//        testDisplayCupScheduleResultLocally();
//        testDisplayCupScheduleResultRemote();
        
        //娴��锛����己绛��瀹��璧��璧��
//        testDisplayCupGroupResultLocally();
//        testDisplayCupGroupResultRemote();

    }

});

function isAppLaunched(){
	return CupRepositoryApp.isLaunched;
}


function hideView(view){
	console.log("hide view " + view);
	if (view != null && view.mainPanel != undefined){
		view.mainPanel.hide();
	}
}

function hideAllView(){
	hideView(CupRepositoryApp.cupGroupPointsView);
	hideView(CupRepositoryApp.cupGroupResultView);
	hideView(CupRepositoryApp.cupScheduleResultView);
}

function setCurrentView(panel){	
	hideAllView();
	CupRepositoryApp.viewport = panel;
	panel.show();	
}

function getCupGroupPointsView()
{
	if (CupRepositoryApp.cupGroupPointsView != null)
		return CupRepositoryApp.cupGroupPointsView;
	else
		return new CupGroupPointsView();
}

function getCupGroupResultView()
{
	if (CupRepositoryApp.cupGroupResultView != null)
		return CupRepositoryApp.cupGroupResultView;
	else
		return new CupGroupResultView();
}

function getCupScheduleResultView()
{
	if (CupRepositoryApp.cupScheduleResultView != null)
		return CupRepositoryApp.cupScheduleResultView;
	else
		return new CupScheduleResultView();
}


function displayCupGroupPoints(reload, leagueId, season, groupId, lang, data){
	if (reload) {
		if (data != undefined) {
			if (cupScheduleManager.readData(data) == false) {
				return false;
			}
		} 
		else if (cupScheduleManager.requestDataFromServer(leagueId, season, groupId, lang) == false){
			return false;
		}
	}
	CupRepositoryApp.cupGroupPointsView = getCupGroupPointsView();	
	setCurrentView(CupRepositoryApp.cupGroupPointsView.mainPanel);	
	CupRepositoryApp.cupGroupPointsView.updateView(cupScheduleManager);
	return true;	
}


function displayCupGroupResult(reload, leagueId, season, groupId, lang, data){
	if (reload) {
		if (data != undefined) {
			if (cupScheduleManager.readData(data) == false) {
				return false;
			}
		} 
		else if (cupScheduleManager.requestDataFromServer(leagueId, season, groupId, lang) == false){
			return false;
		}
	}
	CupRepositoryApp.cupGroupResultView = getCupGroupResultView();	
	setCurrentView(CupRepositoryApp.cupGroupResultView.mainPanel);	
	CupRepositoryApp.cupGroupResultView.updateView(cupScheduleManager);
	return true;	
}

function displayCupScheduleResult(reload, leagueId, season, groupId, lang, data){
	if (reload) {
		if (data != undefined) {
			if (cupScheduleManager.readData(data) == false) {
				return false;
			}
		} 
		else if (cupScheduleManager.requestDataFromServer(leagueId, season, groupId, lang) == false){
			return false;
		}
	}
	CupRepositoryApp.cupScheduleResultView = getCupScheduleResultView();	
	setCurrentView(CupRepositoryApp.cupScheduleResultView.mainPanel);	
	CupRepositoryApp.cupScheduleResultView.updateView(cupScheduleManager);
	return true;	
}