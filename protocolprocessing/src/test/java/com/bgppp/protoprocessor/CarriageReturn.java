package com.bgppp.protoprocessor;

public class CarriageReturn {

	public static void main(String args[]) throws InterruptedException{
		/*Thread thread = new Thread();
		String downloading = "Downloading[=>                    ]";*/
		//System.out.print(downloading);
		/*for(int i=0;i<20;i++){
			thread.sleep(1000);
			System.out.print("\r");
			downloading = downloading.substring(0, 12+i)+"=>"+downloading.substring(12+i+2);
			System.out.print(downloading);
		}*/
		String[] fields = new String[]{"asdasd","asdasdasd","asdasd","2wefw","asdasd","asdasdas"};
		printTable(fields);
	}
	
	public static void printTable(String[] columns){
		for(int i=0;i<columns.length;i++){
			for(int j=0;j<columns[i].length()+3;j++){
				if((i==0&&j==0) ||(i==columns.length-1 && j==columns[i].length()+2))
					System.out.print("+");
				else
					System.out.print("-");
			}
		}
		System.out.println("");
		for(int i=0;i<columns.length;i++){
/*			for(int j=0;j<columns[i].length()+2;j++){
				if(j==0&&i==0 || j==columns[i].length()+1)
*/			System.out.print("| "+columns[i]+" ");
/*				else
					System.out.print("");
			}*/
		}
		System.out.println("|");
		/*for(int i=0; i<columns.length;i++){
			
		}*/
	}
}
