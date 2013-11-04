package dqcup.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ZipRead {
	private Set<String> citySet;
	public ZipRead(){
		citySet = new HashSet<String>();
	}
	
	public Set<String> getCitySet() {
		return citySet;
	}

	public void setCitySet(Set<String> citySet) {
		this.citySet = citySet;
	}

	public Map<String,StateCityObject> zipFileRead(String filePath){
		File file = new File(filePath);
		Map<String,StateCityObject> zipMap = new HashMap<String,StateCityObject>();
		try{
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String line = null;
			while(null != (line = br.readLine())){
				String[] s = line.split("\t");
				if(s[0].equals("US")){
					StateCityObject sco = new StateCityObject();			
					sco.city = s[2];
					sco.state = s[4];
					zipMap.put(s[1], sco);
					citySet.add(s[2]);
				}

			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return zipMap;	
	}
}
