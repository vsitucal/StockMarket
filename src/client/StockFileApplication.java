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
            
        StockFileApplication sfa = new StockFileApplication();
        //read file
		StockFileReader fr = new StockFileReader("orders.csv");
		List<HashMap<String, String>> dataResult = populateStockFileData(fr.getHeaders(), fr.readFileData());
		StockFileData fileData = new StockFileData();
		fileData.addData(dataResult);
                System.out.println("Orders");
		fileData.printData();
		//System.out.println(dataResult.size());
                
                StockFileReader fr2 = new StockFileReader("stockdata.csv");
		List<HashMap<String, String>> dataResult2 = populateStockFileData(fr2.getHeaders(), fr2.readFileData());
		StockFileData fileData2 = new StockFileData();
		fileData2.addData(dataResult2);
                System.out.println("Stock Data");
		fileData2.printData();
		//System.out.println(dataResult2.size());
         
                int size2=dataResult2.size();
                int size1=dataResult.size();
        
                //Lists for Symbol, lotSize, and lastPrice from stockdata.csv         
        
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
                //Lists for Symbol, Price, and Quantity from orders.csv
                
                LinkedList<Double> orderPrice = new LinkedList<>();
                for(int a=0;a<size1;a++){
                    double data2=Double.parseDouble(dataResult.get(a).get("Price"));
                    orderPrice.add(data2);
                }
                //System.out.println(orderPrice);
                
                LinkedList<String> orderSymbols = new LinkedList<>();
                for(int a=0;a<size1;a++){
                    String data2 = dataResult.get(a).get("Symbol");
                    orderSymbols.add(data2);
                }
                //System.out.println(orderSymbols);
                
                LinkedList<Double> orderQuantity = new LinkedList<>();
                for(int a=0;a<size1;a++){
                    double data2=Double.parseDouble(dataResult.get(a).get("Quantity"));
                    orderQuantity.add(data2);
                }
                //System.out.println(orderQuantity);
              
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
                
                LinkedList<HashMap<String, String>> orderBook = new LinkedList<>();
                for(int i=0;i<dataResult.size();i++){
                //System.out.println(dataResult.get(i).get("Symbol"));
                    for(int i2=0;i2<dataResult2.size();i2++){
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
                
        System.out.println("Order Book");
        System.out.println(orderBook);
        
        //execute trades on orderbook
        
        //Buy Offers in order
        LinkedList<HashMap<String, String>> buyOffer = new LinkedList<>();
        for(int buyCount=0;buyCount<orderBook.size();buyCount++){
            
            if(orderBook.get(buyCount).get("Side").equals("BUY")){
                buyOffer.add(orderBook.get(buyCount));
            }
        }
        
        //Sell Offers in order
        LinkedList<HashMap<String, String>> sellOffer = new LinkedList<>();
        for(int buyCount=0;buyCount<orderBook.size();buyCount++){
            
            if(orderBook.get(buyCount).get("Side").equals("SELL")){
                sellOffer.add(orderBook.get(buyCount));
            }
        }

        System.out.println(orderBook.size());
        sfa.processOrders(orderBook);
       
        }
        
        private void processOrders (LinkedList<HashMap<String, String>> orderBook){
                        for(int loop=0;loop<orderBook.size();loop++){
                for(int loop1=0;loop1<orderBook.size();loop1++){
                    if(orderBook.get(loop).get("Symbol").equals(orderBook.get(loop1).get("Symbol"))){
                        if(orderBook.get(loop).get("Side").equals("BUY")){
                            if(orderBook.get(loop1).get("Side").equals("SELL")){
                                double qty1 = Double.parseDouble(orderBook.get(loop).get("Quantity"));
                                double qty2 = Double.parseDouble(orderBook.get(loop1).get("Quantity"));
                                
                                double buy = Double.parseDouble(orderBook.get(loop).get("Price"));
                                double sell = Double.parseDouble(orderBook.get(loop1).get("Price"));
                                
                                if(buy==sell){
                                    System.out.println("Current:");
                                    System.out.println(orderBook);
                                    System.out.println("Executing trade of"+orderBook.get(loop)+" and "+orderBook.get(loop1));
                                    if(qty1>=qty2){
                                        double n = qty1-qty2;
                                        orderBook.get(loop).put("Quantity", Double.toString(n));
                                        orderBook.get(loop1).put("Quantity", Double.toString(0));
                                        //orderBook.remove(loop1);
                                        if(n==0){
                                        //orderBook.remove(loop);
                                        }
                                    }
                    
                                    if(qty2>=qty1){
                                        double m = qty2-qty1;
                                        orderBook.get(loop1).put("Quantity", Double.toString(m));
                                        orderBook.get(loop).put("Quantity", Double.toString(0));
                                        //orderBook.remove(loop);
                                        if(m==0){
                                        //orderBook.remove(loop1);
                                        }
                                    }
                                    System.out.println("After:");
                                    System.out.println(orderBook);
                                
                                } 
                            } 
                        }
 
                    
                     //if(BUY)
                    }
                }
            }
        }
	
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
