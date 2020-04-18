package fileprocessors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StockFileData {
	
	List<HashMap<String, String>> data = new LinkedList<>();
	
	public void printData(){
		System.out.println(data);
	}
	
	public void addData(List<HashMap<String, String>> dataIn){
		data = dataIn;
	}
}
