package nurimsoft.stat.manager;

import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.StatInfo;
import nurimsoft.webapp.StatHtmlDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author leekyujeong
 *
 */
public class DefaultSettingManager extends SettingManager{
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public DefaultSettingManager(StatInfo statInfo, StatHtmlDAO statHtmlDAO){
		super(statInfo, statHtmlDAO);
	}
	
	public void setCondition(){
		//초기 조회 조건 설정
		//초기조회범위를 설정하고 임시 조회 범위 테이블에 데이터를 넣는다.
		
		//A. 통계표 화면정보 가져와서 pivotInfo 생성(대표값이 'Y' 인 것)
		List<Map> scrInfoList = statHtmlDAO.getScrInfo(paramInfo);
		setPivotInfo(scrInfoList);

		//B. OLAP_STL 가져오기(없으면 생성)
		setOlapStl();
		
		setInfosUsingOlapStl();
		
		//DefaultSetting에서는 주기는 1개
		statInfo.setDefaultPeriodStr(prdInfo + "#");
		statInfo.setDefaultPeriodCnt(1);
		
		//PivoInfo 셋팅
		statInfo.setPivotInfo(getPivotInfo());
	}

}
