package com.orange.score.utils;

import com.orange.score.model.match.MatchStatusType;

public class DataUtils {

	public static String toChuPanString(String chupanStringValue)
	{
		if (chupanStringValue == null || chupanStringValue.length() == 0)
			return "";

	    final String[] GoalCnMandarin = {"Æ½ÊÖ","Æ½ÊÖ/°ëÇò","°ëÇò","°ëÇò/Ò»Çò","Ò»Çò","Ò»Çò/Çò°ë","Çò°ë","Çò°ë/Á½Çò","Á½Çò","Á½Çò/Á½Çò°ë","Á½Çò°ë","Á½Çò°ë/ÈýÇò","ÈýÇò","ÈýÇò/ÈýÇò°ë","ÈýÇò°ë","ÈýÇò°ë/ËÄÇò","ËÄÇò","ËÄÇò/ËÄÇò°ë","ËÄÇò°ë","ËÄÇò°ë/ÎåÇò","ÎåÇò","ÎåÇò/ÎåÇò°ë","ÎåÇò°ë","ÎåÇò°ë/ÁùÇò","ÁùÇò","ÁùÇò/ÁùÇò°ë","ÁùÇò°ë","ÁùÇò°ë/ÆßÇò","ÆßÇò","ÆßÇò/ÆßÇò°ë","ÆßÇò°ë","ÆßÇò°ë/°ËÇò","°ËÇò","°ËÇò/°ËÇò°ë","°ËÇò°ë","°ËÇò°ë/¾ÅÇò","¾ÅÇò","¾ÅÇò/¾ÅÇò°ë","¾ÅÇò°ë","¾ÅÇò°ë/Ê®Çò","Ê®Çò"};
	    	    
	    String Goal2Cn = "";
	    double chupanDoubleValue = Double.parseDouble(chupanStringValue);
	    int Goal2CnIndex = (int)(Math.abs(chupanDoubleValue*4));
	    
	    if (Goal2CnIndex >= GoalCnMandarin.length){
	        return Goal2Cn;
	    }

	    Goal2Cn =  GoalCnMandarin[Goal2CnIndex];	  	    
	    if (chupanDoubleValue >= 0){
	        return Goal2Cn;
	    }
	    else{
	        return "ÊÜ" + Goal2Cn;
	    }
	    /*
	     ÅÌ¿ÚÊý×Ö×ªÎÄ×Ö£¬¹©²Î¿¼£¬ÐèÒªÖ§³Ö¸÷ÖÖÓïÑÔ
	     private string[] GoalCn ={"Æ½ÊÖ","Æ½ÊÖ/°ëÇò","°ëÇò","°ëÇò/Ò»Çò","Ò»Çò","Ò»Çò/Çò°ë","Çò°ë","Çò°ë/Á½Çò","Á½Çò","Á½Çò/Á½Çò°ë","Á½Çò°ë","Á½Çò°ë/ÈýÇò","ÈýÇò","ÈýÇò/ÈýÇò°ë","ÈýÇò°ë","ÈýÇò°ë/ËÄÇò","ËÄÇò","ËÄÇò/ËÄÇò°ë","ËÄÇò°ë","ËÄÇò°ë/ÎåÇò","ÎåÇò","ÎåÇò/ÎåÇò°ë","ÎåÇò°ë","ÎåÇò°ë/ÁùÇò","ÁùÇò","ÁùÇò/ÁùÇò°ë","ÁùÇò°ë","ÁùÇò°ë/ÆßÇò","ÆßÇò","ÆßÇò/ÆßÇò°ë","ÆßÇò°ë","ÆßÇò°ë/°ËÇò","°ËÇò","°ËÇò/°ËÇò°ë","°ËÇò°ë","°ËÇò°ë/¾ÅÇò","¾ÅÇò","¾ÅÇò/¾ÅÇò°ë","¾ÅÇò°ë","¾ÅÇò°ë/Ê®Çò","Ê®Çò"};
	     public string Goal2GoalCn(float goal) {  	//Êý×ÖÅÌ¿Ú×ªºººº×Ö
	     string Goal2GoalCn = "";
	     try{
	     if (Convert.IsDBNull(goal)) {Goal2GoalCn = "";	}
	     else if(goal >= 0){Goal2GoalCn = GoalCn[Convert.ToInt32(goal * 4)];}
	     else	{Goal2GoalCn = "ÊÜ" + GoalCn[Math.Abs(Convert.ToInt32(goal * 4))];}
	     }
	     catch{Goal2GoalCn=goal.ToString();}
	     return Goal2GoalCn;
	     } 
	     */

	}
	
	public static String fatmatDate(String date) {
    	//20120110183000 to 01/10 18:30
    	StringBuilder builder = new StringBuilder();
        builder.append(date.substring(4,6))
    					.append("/")
    					.append(date.substring(6,8))
    					.append(" ")
    					.append(date.substring(8, 10))
    					.append(":")
    					.append(date.substring(10, 12));
        
        return builder.toString();
    									
    }
	
	public static Boolean isStarted(int status)
	{
		switch (status) {

        case MatchStatusType.FIRST_HALF:
            return true;
        
        case MatchStatusType.SECOND_HALF:
            return true;
        
        case MatchStatusType.MIDDLE:
            return true;
        
        case MatchStatusType.FINISH:
            return true;

        case MatchStatusType.OVERTIME:
            return true;
            
        case MatchStatusType.TBD:
            return false;
        
        case MatchStatusType.KILL:
            return false;
        
        case MatchStatusType.PAUSE:
            return false;
        
        case MatchStatusType.POSTPONE:
            return false;
        
        case MatchStatusType.CANCEL:
            return false;
        
        case MatchStatusType.NOT_STARTED:
             return false;
             
        default:
			return false;
		}
		
	}

	public static String toMatchStatusString(int status)
	{
	    switch (status) {

	        case MatchStatusType.FIRST_HALF:
	            return "ÉÏ°ë³¡";
	        
	        case MatchStatusType.SECOND_HALF:
	            return "ÏÂ°ë³¡";
	        
	        case MatchStatusType.MIDDLE:
	            return "ÖÐ³¡";
	        
	        case MatchStatusType.FINISH:
	            return "Íê³¡";

	        case MatchStatusType.OVERTIME:
	            return "¼ÓÊ±";
	            
	        case MatchStatusType.TBD:
	            return "´ý¶¨";
	        
	        case MatchStatusType.KILL:
	            return "ÑüÕ¶";
	        
	        case MatchStatusType.PAUSE:
	            return "ÖÐ¶Ï";
	        
	        case MatchStatusType.POSTPONE:
	            return "ÍÆ³Ù";
	        
	        case MatchStatusType.CANCEL:
	            return "È¡Ïû";
	        
	        case MatchStatusType.NOT_STARTED:
	             return "Î´¿ªÈü";
	        
	        default:
	            return "Î´¿ªÈü";
	    }
	}

	public static String toDaxiaoPanKouString(String panKouStringValue)
	{
	    if (panKouStringValue == null || panKouStringValue.length() == 0) {
	        return "";
	    }

	    final String[] goalCn = {"0", "0/0.5", "0.5", "0.5/1", "1", "1/1.5", "1.5", "1.5/2", "2", "2/2.5", "2.5", "2.5/3", "3", "3/3.5", "3.5", "3.5/4", "4", "4/4.5", "4.5", "4.5/5", "5", "5/5.5", "5.5", "5.5/6", "6", "6/6.5", "6.5", "6.5/7", "7", "7/7.5", "7.5", "7.5/8", "8", "8/8.5", "8.5", "8.5/9", "9", "9/9.5", "9.5", "9.5/10", "10", "10/10.5", "10.5", "10.5/11", "11", "11/11.5", "11.5", "11.5/12", "12", "12/12.5", "12.5", "12.5/13", "13", "13/13.5", "13.5", "13.5/14", "14"};	    	    
	    float panKouFloat = Float.parseFloat(panKouStringValue);	    
	    if (panKouFloat > 14.0f)
	    {
	        return panKouStringValue;
	    }
	    
	    int index = (int)(Math.abs(panKouFloat*4));
	    if (index >= goalCn.length)
	    	return "";

	    return goalCn[index];
	}

}
