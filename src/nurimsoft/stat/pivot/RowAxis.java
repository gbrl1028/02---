package nurimsoft.stat.pivot;

import java.util.ArrayList;
import java.util.List;

public class RowAxis {
	
	List<Dimension> list = new ArrayList<Dimension>();
	
	public int getDimemsionCount(){
		return list == null? 0 : list.size();
	}
	
	public void addDimension(Dimension dimension){
		list.add(dimension);
	}
	
	public void addDimension(Dimension dimension, int index){
		list.add(index, dimension);
	}
	
	public List<Dimension> getDimensionList(){
		return list;
	}
	
	public Dimension getDimension(int index){
		return (Dimension)list.get(index);
	}
	public void removeDimension(Dimension dimension, int index){
		list.remove(index);
	}
}
