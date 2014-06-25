//-------------------------------------
//NAME:Ming - Jie Deng
//ID:H54001347
//Date:2013/06/23 from Tainan,Taiwan
//Type:HW4
//Description:使用HashMap鍵值不會重複的特性，每次符合
//"路" "街" "大道" 或 "巷"時 就把讀到的 從頭到關鍵字當Key值
// 丟入HashMap<String,HashMap>中，第二層的HashMap
//是一個整數陣列，用來存放某月的"最大"最小"交易紀錄。
//test_max_month()會在每次push到HashMap時做驗證
//目前最大的鍵值字串，若有相同#max_distinct_month
//則以新增在字串max_month_key後用"+"隔開，之後再判斷
import java.net.*;
import java.nio.charset.Charset;
import java.io.*;

import org.json.*;

import java.util.*;
import java.util.regex.*;
import java.lang.*;


public class TocHw4 {
	
	 int aa =0;
	int count =0;
	int total_price=0;
	static String max_month_key;
	static HashMap hp;
	static int max_price_key = 0;
	static int min_price_key = 0;
	static int max_month_amount =0;
	static int repeat = 1;
	static String cd1 = "路";
	static String cd2 = "街";
	static String cd3 = "大道";
	static String cd4 = "巷";
	static String RoadName = "土地區段位置或建物區門牌";
	static String Year = "交易年月";
	static String Price ="總價元";
	static 	String DataUrl;
	
	static Map mp = new HashMap<String,HashMap<Integer,int[]>>();
	 

	public static void main(String[] args)throws Exception ,JSONException {
		
		int n = args.length;
	
		DataUrl = (n>=1)?args[0].toString():"http://www.datagarage.io/api/538447a07122e8a77dfe2d86";
		 
	 	
		 
		try{
	
			
			InputStream is = new URL(DataUrl).openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,Charset.forName("UTF8")));
			//取得URL資訊
		
			
			String jtext = readAll(br);
			
			JSONTokener jk = new JSONTokener(jtext);
			
			JSONArray ay = new JSONArray(jk);
	
			
		
			
			for(int i=0;i<ay.length();i++){
				JSONTokener jt = new JSONTokener(ay.get(i).toString());	
				JSONObject jb = new JSONObject(jt);
				
				matcher_find(jb);//比對路、街、巷
			}
				
				
			
			//System.out.println("Done");
			//System.out.println(max_month_key);
			//System.out.println("Output:");
			if(repeat==1){
				Map sp = (HashMap)mp.get(max_month_key) ;
	
					
				System.out.print(max_month_key);
				
				
				print_max_and_min_price(sp);

				System.out.println("");
				
			}else{
				String [] names = max_month_key.split(":");
				for(String name:names){
					
					Map sp = (HashMap)mp.get(name);
					
					System.out.print(name);
					
					print_max_and_min_price(sp);
					
					System.out.println("");
					
				}
				
			}
			
			
		}catch(FileNotFoundException e){e.printStackTrace();}
		catch(JSONException e){e.printStackTrace();}
	
		

	}
	public  static String readAll(Reader rd) throws IOException{
		
		//System.out.println("Starting reading the file");
		StringBuilder sb = new StringBuilder();
		int cp;
		
		while((cp=rd.read())!=-1){
			sb.append((char)cp);
			
		}
		
		//System.out.println("Done reading the files");
		
		return sb.toString();
	}
	
	
	public static void matcher_find(JSONObject jb) throws JSONException{
		//要把資料記錄在HashMap<String,HashMap> mp 中
		//JSONObject jb 是JSONArray ay 中的其中一個元素
		
		String address = jb.get(RoadName).toString();//主要比對那欄
		int year = jb.getInt(Year);//取的年月資訊
		Map tmp = null;
		
		Pattern cd1_pattern = Pattern.compile(cd1);//路
		Pattern cd2_pattern = Pattern.compile(cd2);//街
		Pattern cd3_pattern = Pattern.compile(cd3);//大道
		Pattern cd4_pattern = Pattern.compile(cd4);//巷
		
		Matcher cd1_matcher = cd1_pattern.matcher(address);
		Matcher cd2_matcher = cd2_pattern.matcher(address);
		Matcher cd3_matcher = cd3_pattern.matcher(address);
		Matcher cd4_matcher = cd4_pattern.matcher(address);
		Matcher mt =null;
		boolean if_find = true;
		if(cd1_matcher.find())mt = cd1_matcher;
		else if(cd2_matcher.find())mt = cd2_matcher;
		else if(cd3_matcher.find())mt = cd3_matcher;
		else if(cd4_matcher.find())mt = cd4_matcher;
		else if_find=false;
		 
		if(if_find){//路
			//System.out.println("find 路");
			int ed =mt.end();;
			if(mt.find())ed = mt.end();//若路名有"路" "街"等
			
			
			String key = address.substring(0, ed);//只取到"路"前的字串
			
			
			if(mp.containsKey(key)){
			//第一層HashMap	
					tmp =(HashMap) mp.get(key);//某個路的資料(第二層HashMap)
					
					if(tmp.containsKey(year)){
						
						int[] year_max_min_price = record_max_min_price(jb,tmp,key,year);
						//正確路這個"年月"中最大與最小交易金額
						tmp.put(year,year_max_min_price );
						test_max_month(tmp, key,year);//比對目前最大的
				
					}else{
						//有路的資料 但沒有此年月的金額資料
						
						int[] new_max_min_price = new int[2];
						new_max_min_price[0] =(int) jb.get(Price);//max
						new_max_min_price[1] =(int) jb.get(Price);//min
							
						
						tmp.put(year,new_max_min_price);
						test_max_month( tmp, key,year);//比對目前最大的
					}
				
				
			}else{
				//第一層HashMap
				tmp = new HashMap<Integer,int[]>();
				int[] new_max_min_price = new int[2];
				new_max_min_price[0] =(int) jb.get(Price);//max
				new_max_min_price[1] =(int) jb.get(Price);//min
			
				
				
				tmp.put(year,new_max_min_price);
				test_max_month( tmp, key,year);//比對目前最大的
				mp.put(key, tmp);
			}
			
			
			
		 
		}
	}
public static int[] record_max_min_price  (JSONObject jb,Map tmp,String key,int year)throws JSONException{
		
		int input_price = (int)jb.getInt(Price);//新的金額資料
		int[] year_price =(int[]) tmp.get(year);//取得年度價格資料
		int max_price = year_price[0];//年度中最大金額
		int min_price = year_price[1];//年度中最大小金額
		
		
		if(input_price>max_price)
			year_price[0] = input_price;
		if(input_price<min_price)
			year_price[1] = input_price;
		
		
			return year_price ;	
			
		
  }
public static void test_max_month(Map tmp,String key,int year){
	
	
	if(tmp.size()>max_month_amount){
	    
		repeat = 1;
		
		max_month_key = key; 
		max_month_amount = tmp.size();
									
				
	}else if(tmp.size()==max_month_amount){//重複時並未覆蓋掉上一筆資料 所以要檢查
		String[] checks = max_month_key.split(":");
		for(String check:checks){
			if(check.compareTo(key)==0)
				return;
		}
				
		repeat ++;
		max_month_key += ":";
		max_month_key += key;
		
			
	}
	
}
public static void print_max_and_min_price(Map sp){
	Set ss = sp.keySet();
	Iterator it = ss.iterator();
	int max_price_amount =0;
	int min_price_amount=2147483647;
	while(it.hasNext()){
		int yy;
		int [] vv = (int[])sp.get((yy=(int)it.next()));
		if(vv[0]>max_price_amount)
			max_price_amount = vv[0];
		if(vv[1]<min_price_amount)
			min_price_amount = vv[1];
		
		
	}
	System.out.print(",最高成交價:"+max_price_amount);
	System.out.print(", 最低成交價:"+min_price_amount);
}
	

}

