package car;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.io.*;

public class main {
	public static void main(String[] args) {
		
		/*Car car = new Car(0,0.01);
		double velocity = 27;
		ArrayList<Double> response = new ArrayList<Double>(); 
		String data = new String();
		StringBuilder s = new StringBuilder();
		
		for(double i=0; i<=30; i+=0.01){
			response.add(car.command(velocity, i));
		}
		
		for(int i=0; i<response.size(); i++){
			if(i<response.size()-1)
				s.append(String.valueOf(response.get(i))).append(",\n");
			else
				s.append(String.valueOf(response.get(i)));
		}
		
		data = s.toString();
		
		try {
			PrintWriter pw = new PrintWriter("data.csv", "UTF-8");
			pw.println(data);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("DONE!");
	}*/
		
		VelocityMap map = new VelocityMap("table.txt");
		Set<Map.Entry<Pair, Pair>> entries = map.getMap().entrySet();
		Iterator<Map.Entry<Pair, Pair>> it = entries.iterator();
		StringBuilder sb = new StringBuilder();
		
		while(it.hasNext()){
			Map.Entry<Pair, Pair> entry = it.next();
			Pair key = entry.getKey();
			Pair value = entry.getValue();
			sb.append(key.toString() + " <==> " + value.toString() + "\n");
		}
		
		try {
			PrintWriter pw = new PrintWriter("data.txt", "UTF-8");
			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
