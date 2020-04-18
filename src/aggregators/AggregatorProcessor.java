package aggregators;

import fileprocessors.StockFileReader;
import java.io.IOException;
import java.util.List;

public class AggregatorProcessor <T extends Aggregator>{

T aggregator;
String file;

    public AggregatorProcessor(T aggregator, String file) {
        super();
        this.aggregator = aggregator;
        this.file = file;
    }
    
    public double runAggregator(int colIdx) throws IOException{
        StockFileReader sfr = new StockFileReader(file);
        List<String> lines = sfr.readFileData();
        
       colIdx--;
       for(String line: lines){
       String [] numbers = line.split(",");
       Number value = Double.parseDouble(numbers[colIdx]);
       aggregator.add(value.doubleValue());
       }
       
       double number = aggregator.calculate();
       return number;
    }


    
    
  
	
	
}
