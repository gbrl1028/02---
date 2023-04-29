package nurimsoft.stat.pivot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nurimsoft.stat.info.CmmtInfo;
import nurimsoft.stat.info.ParamInfo;
import nurimsoft.stat.manager.CmmtInfoManager;
import nurimsoft.webapp.StatHtmlDAO;

public class Dimension {
	
	protected String dbColumn;
	
	protected String code;
	protected String nameKor;
	protected String nameEng;
	
	protected int varOrdSn;
	
	protected int maxLevel = 1;		//최하위 레벨 값
	protected int repeatSelf = 1;		//반복으로 출력할 수(다음 순서의 Dimension들의 조합 수, Default : 1)
	protected int repeatGroup = 1;	//상위 디멘전의 항목 수(Default : 1)
	
	protected List<Item> itemList = new ArrayList<Item>();	//디멘전의 구성요소(코드)
	
	protected Map<String, Item> itemMap = null;
	
	protected ParamInfo paramInfo;
	protected StatHtmlDAO statHtmlDAO;
	protected CmmtInfoManager cmmtInfoManager;
	
	protected Dimension(ParamInfo paramInfo, String classCode, StatHtmlDAO statHtmlDAO, CmmtInfoManager cmmtInfoManager){
		this.code = classCode;
		this.paramInfo = paramInfo;
		this.statHtmlDAO = statHtmlDAO;
		this.cmmtInfoManager = cmmtInfoManager;
	}
	
	protected Dimension(ParamInfo paramInfo, StatHtmlDAO statHtmlDAO){
		//시점인 경우에만
		this(paramInfo, "TIME", statHtmlDAO, null);
	}
	
	//MeasureDimension을 위한 생성자
	protected Dimension(ParamInfo paramInfo){
		this.paramInfo = paramInfo;
	}
	
	public String getDbColumn() {
		return dbColumn;
	}

	public void setDbColumn(String dbColumn) {
		this.dbColumn = dbColumn;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		
		String retName = null;
		
		if(paramInfo.getDataOpt().equals("ko")){
			retName = nameKor;
		}else if(paramInfo.getDataOpt().equals("en")){
			retName = nameEng;
		}else if(paramInfo.getDataOpt().equals("cd")){
			retName = code;
		}else if(paramInfo.getDataOpt().equals("cdko")){
			retName = code + " " + nameKor;
		}else if(paramInfo.getDataOpt().equals("cden")){
			retName = code + " " + nameEng;
		}
		
		if(retName == null){
			retName = "";
		}
		
		return retName;
	}
	
	public String getNameKor(){
		return nameKor;
	}
	
	public String getNameEng(){
		return nameEng;
	}

	public void setNameKor(String nameKor) {
		this.nameKor = nameKor;
	}

	public void setNameEng(String nameEng) {
		this.nameEng = nameEng;
	}

	public void addItem(Item item){
		itemList.add(item);
	}
	
	public void addItem(Item item, int index){
		itemList.add(index, item);
		
		setItemMap();
		itemMap.put(item.getCode(), item);
	}
	
	public Item getItem(int index){
		return (Item)itemList.get(index);
	}
	
	public Item getItem(String key){
		setItemMap();
		return itemMap.get(key);
	}
	
	public void setItemMap(){
		//itemMap이 null 인 경우 map에 담는다..처음 한번만 수행.
		if(itemMap == null){
			itemMap = new HashMap<String, Item>();
			for(Item item : itemList){
				itemMap.put(item.getCode(), item);
			}
		}
	}
	
	public int getItemCount(){
		return itemList.size() == 0 ? 1 : itemList.size();
	}
	
	public int getMaxLevel(){
		return maxLevel;
	}
	
	public void setMaxLevel(int maxLevel){
		this.maxLevel = maxLevel;
	}
	
	public int getRepeatSelf() {
		return repeatSelf;
	}

	public void setRepeatSelf(int repeatSelf) {
		this.repeatSelf = repeatSelf;
	}

	public int getRepeatGroup() {
		return repeatGroup;
	}

	public void setRepeatGroup(int repeatGroup) {
		this.repeatGroup = repeatGroup;
	}
	
	public List<Item> getItemList(){
		return itemList;
	}
	
	public int getVarOrdSn() {
		return varOrdSn;
	}

	public void setVarOrdSn(int varOrdSn) {
		this.varOrdSn = varOrdSn;
	}
	
	public void setItemList(List itemList){
		this.itemList = itemList;
	}
	
}
