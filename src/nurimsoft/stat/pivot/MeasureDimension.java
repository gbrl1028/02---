package nurimsoft.stat.pivot;

import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.util.PropertyManager;
import nurimsoft.webapp.StatHtmlDAO;

public class MeasureDimension extends Dimension{
	
	public MeasureDimension(ParamInfo paramInfo){
		super(paramInfo);
	}
	
	public void setItemsWeight(){
		
		//원데이터
		Item item = new Item( "DTVAL_CO", PropertyManager.getInstance().getProperty("string.measure.val.ko"), PropertyManager.getInstance().getProperty("string.measure.val.en") );
		item.setDataOpt(paramInfo.getDataOpt());
		this.addItem(item);
		
		//가중치
		item = new Item( "WGT_CO", PropertyManager.getInstance().getProperty("string.measure.wgt.ko"), PropertyManager.getInstance().getProperty("string.measure.wgt.en") );
		item.setDataOpt(paramInfo.getDataOpt());
		this.addItem(item);
		
	}
	
	public void setItemsAnal(String funcName, String funcNameEng){
		//원데이터
		Item item = new Item( "DTVAL_CO", PropertyManager.getInstance().getProperty("string.measure.val.ko"), PropertyManager.getInstance().getProperty("string.measure.val.en") );
		item.setDataOpt(paramInfo.getDataOpt());
		this.addItem(item);
		
		//분석
		item = new Item( "ANAL_CO", funcName, funcNameEng);
		item.setDataOpt(paramInfo.getDataOpt());
		this.addItem(item);
	}

}
