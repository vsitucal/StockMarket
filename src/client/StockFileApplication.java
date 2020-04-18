package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fileprocessors.StockFileData;
import fileprocessors.StockFileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class StockFileApplication {
        
        static int symbolc;
	
	public static void main(String args[]) throws IOException{
        //read file
		StockFileReader fr = new StockFileReader("orders.csv");
		List<HashMap<String, String>> dataResult = populateStockFileData(fr.getHeaders(), fr.readFileData());
		StockFileData fileData = new StockFileData();
		fileData.addData(dataResult);
		fileData.printData();
		//System.out.println(dataResult.size());
                
                StockFileReader fr2 = new StockFileReader("stockdata.csv");
		List<HashMap<String, String>> dataResult2 = populateStockFileData(fr2.getHeaders(), fr2.readFileData());
		StockFileData fileData2 = new StockFileData();
		fileData2.addData(dataResult2);
		fileData2.printData();
		//System.out.println(dataResult2.size());
         
                int size2=dataResult2.size();
                int size1=dataResult.size();
        
        //Lists for Symbol, lotSize, and lastPrice         
        
                LinkedList<String> symbols = new LinkedList<>();
                for(int a=0;a<size2;a++){
                    String data2=dataResult2.get(a).get("Symbol");
                    symbols.add(data2);
                }
                //System.out.println(symbols);
                
                LinkedList<Double> lotSize = new LinkedList<>();
                for(int a=0;a<size2;a++){
                    double data2=Double.parseDouble(dataResult2.get(a).get("Lot size"));
                    lotSize.add(data2);
                }
                //System.out.println(lotSize);
                
                LinkedList<Double> lastPrice = new LinkedList<>();
                for(int a=0;a<size2;a++){
                    double data2=Double.parseDouble(dataResult2.get(a).get("Last Price"));
                    lastPrice.add(data2);
                }
                //System.out.println(lastPrice);
              
//Populate orderBook
                HashMap<String,List<Double>> acHash = new HashMap<>();
                for(int n=0;n<symbols.size();n++){
                    //instanciate new acList to forget the old one
                    LinkedList<Double> acList = new LinkedList<>();
                    acList.add(lastPrice.get(n));
                    acList.add(lotSize.get(n));
                    acHash.put(symbols.get(n), acList);
                }
                //System.out.println(acHash.keySet());
                
                List<HashMap<String, String>> orderBook = new LinkedList<>();
                for(int i=0;i<dataResult.size();++i){
                //System.out.println(dataResult.get(i).get("Symbol"));
                    for(int i2=0;i2<dataResult2.size();++i2){
                        //Instanciating a new LinkedList every time a for loop is reiterated
                        LinkedList<String> symbol = new LinkedList<>(acHash.keySet());
                        if(dataResult.get(i).get("Symbol").equals(symbol.get(i2))){
                            double x = Double.parseDouble(dataResult.get(i).get("Quantity"));
                            double y = acHash.get(symbol.get(i2)).get(1);
                            if(x%y==0){           
                                double xx = acHash.get(symbol.get(i2)).get(0);
                                double yy = Double.parseDouble(dataResult.get(i).get("Price"));
                                if((yy<=xx*1.05)&&(yy>=xx*0.95)){
                                    orderBook.add(dataResult.get(i));
                                }
                            }            
                        }
                    }
                }
                System.out.println(orderBook);
                
        //execute trades on orderbook
        HashSet<Integer> remove = new HashSet<>();
        for(int o=0;o<orderBook.size();o++){
            //System.out.println(orderBook.get(o));
            for(int p=0;p<orderBook.size();p++){
                if((!(orderBook.get(o).get("Side").equals(orderBook.get(p).get("Side"))))&&orderBook.get(o).get("Symbol").equals(orderBook.get(p).get("Symbol"))&&orderBook.get(o).get("Price").equals(orderBook.get(p).get("Price"))){
                    
                    double oQ = Double.parseDouble(orderBook.get(o).get("Quantity"));
                    double pQ = Double.parseDouble(orderBook.get(p).get("Quantity"));
                    
                    if(pQ>=oQ){
                        double n = pQ - oQ;
                        orderBook.get(p).put("Quantity", Double.toString(n));
                        remove.add(o);
                        if(n==0){
                        remove.add(p);
                        }
                    }
                    
                    if(oQ>=pQ){
                        double m = oQ - pQ;
                        orderBook.get(o).put("Quantity", Double.toString(m));
                        remove.add(p);
                        if(m==0){
                        remove.add(o);
                        }
                    }
                }
            }
        }
        

        //Iterator<Integer> remove2 = remove.iterator();
        
        //while(remove2.hasNext()){
        //orderBook.remove(remove2.next());
        //}
        
        System.out.println(orderBook);
        
                
	}
	/**
	 * Complete the method body so that it returns the given structure needed to 
	 * populate the data field in the StockFileData class. 
	 * @param headers
	 * @param lines
	 * @return List
	 */
	public static List<HashMap<String, String>> populateStockFileData(List<String> headers, List<String> lines){
		List<HashMap<String, String>> dataResult = new ArrayList<>();
		// Insert your code here..
                
                for(String line: lines){
                    String[] values = line.split(",");
                    
                    HashMap<String, String> headerValue = new HashMap<>();
                    int cnt = 0;
                    for(String value:values){
                        String dval = value;
                        headerValue.put(headers.get(cnt), dval);
                        cnt++;
                    }
                    dataResult.add(headerValue);
                }
                
		return dataResult;
	}
	
	
}
