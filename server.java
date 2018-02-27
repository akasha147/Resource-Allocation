
import java.net.*;
import java.util.*;
import java.io.*;

class server
{
	static Vector ClientSockets;
	static Vector LoginNames;
    	String line = null;
    	static int max_process = 0,mr,mp;
    	static int[][] choosing;
    	static boolean[] entering;
	static boolean[] allocated;
	static int[] ordering;
	static String order=null;
	static int used_so_far=0;
	
    
    	server(int mp,int mr) throws Exception
    	{
        	ServerSocket soc=new ServerSocket(5217);
        	ClientSockets=new Vector();
        	LoginNames=new Vector();
		allocated=new boolean[mp];
		choosing =new int[mp][mr];
		entering=new boolean[mr];
		System.out.println("Server has started!");

        	     while(true)
		     {
		
            		Socket CSoc=soc.accept();        
            		ClientThread obClient=new ClientThread(CSoc);
		    }
		
    	}

    	public static void main(String args[]) throws Exception
    	{
		System.out.println("Starting the server...Configuring");
		 Scanner sc=new Scanner(System.in); 
		
		System.out.print("Enter the capacity (max number of users at a time):");
		mp=sc.nextInt();
		
		System.out.print("Enter the number of resources:");
		mr=sc.nextInt();
		
		ordering=new int[mr];
		System.out.println("Enter the ordering in resources:");
		order=Integer.toString(mr)+"@";
		for(int i=0;i<mr;i++)
		{
			ordering[i]=sc.nextInt();
			order=order+Integer.toString(ordering[i]);
			if(i!=mr-1)
			{
				order=order+"@";			
			}	
		}
		
		
        	server ob=new server(mp,mr);
    	}

	class ClientThread extends Thread
	{
    		Socket ClientSocket;
    		DataInputStream din;
    		DataOutputStream dout;
    		int current_process;
		int userorder;
		String LoginName;
    		ClientThread(Socket CSoc) throws Exception
    		{
        		ClientSocket=CSoc;
			din=new DataInputStream(ClientSocket.getInputStream());
        		dout=new DataOutputStream(ClientSocket.getOutputStream());
			LoginName=din.readUTF();
		        if(mp==max_process)
			{
				System.out.println("User "+LoginName+" denied service due to capacity overflow");
				dout.writeUTF("No");
			
			}
			else
			{       for(int i=0;i<mp;i++)
				{
					if(allocated[i]==false)
					{
						current_process=i;
						allocated[i]=true;
						break;					
					}				
				}
				//System.out.println(current_process);
				max_process++;
				used_so_far++;
				userorder=used_so_far;
        			for(int i=0;i<mr;i++)
				{
       				choosing[current_process][i]=0;
     				}  
     				entering[current_process]=false;
       				
				System.out.println("User "+LoginName +" has logged in!!!");
        			LoginNames.add(LoginName);
				dout.writeUTF(order);
        			ClientSockets.add(ClientSocket);    
       				start();
			}
    		}
		public void run()
    		{
			try
			{
   			 	int res_num=-1;
        			while(true)
        			{
            				line = din.readUTF();
          				//System.out.println(line); 
        				 String [] a = line.split("@", 2);
         				 int rel;
           				//System.out.println(a[0]); 
					if(a[0].equals("1"))
	 				{
						//System.out.println(a[1]); 
          					res_num=Integer.parseInt(a[1]);
          					entering[current_process]=true;
          					//System.out.println(res_num);
						int max=-1;
        				        for(int i = 0; i < max_process; i++)
        					{
            						if(max < choosing[i][res_num])
            						{
                						max = choosing[i][res_num];
            						}
	  					}
	    					choosing[current_process][res_num]=1+max;
          					entering[current_process]=false;
	    					System.out.println("User "+LoginName+" enters trying region for resource "+a[1]);
	    					for(int i = 0; i < max_process; i++)
	    				 	{
	        					while(entering[i]){ }
	        					while ((choosing[i][res_num] != 0) && (choosing[i][res_num]<choosing[current_process][res_num])||(choosing[i][res_num]==choosing[current_process][res_num]&&i<current_process)){
                 						System.out.print("");   
								sleep(20*userorder-3);           
							}
							sleep(10*(userorder-1)+3);
             					 }
               					System.out.println("User "+LoginName+" is using the resource "+res_num);
						dout.writeUTF("true");
        				     }	
	  
         				else if(a[0].equals("2"))
	 				 {
	 					 
           					 choosing[current_process][Integer.parseInt(a[1])]=0;
						//System.out.println(current_process+" "+res_num);
						System.out.println("User "+LoginName+" is releasing resource "+Integer.parseInt(a[1]));
						boolean rem=true;						
						for(int i=0;i<mr;i++)
						{

						     if(choosing[current_process][i]!=0)
						     {
						 	rem=false;
							break;	
						     }						
						}
						if(rem)
						{
						       System.out.println("User "+LoginName+" is entering remainder region ");
						}

         					 dout.writeUTF("false");
	   					 
          				} 
					else if(a[0].equals("3"))
	 				 {
	 					
						 for(int i = 0;i < mr; i++)
						 {  
						     if(choosing[current_process][i]!=0)
						     {
						  	System.out.println("User "+LoginName+" is releasing resource "+i);
							choosing[current_process][i]=0;	
						     }
						     
   						 } 
						  max_process--;
						
           					  System.out.println("User "+LoginName+" is logging out");
         					 dout.writeUTF("Server Ok");
						 break;
	   					 
          				}
         			}
			}
	     		catch(Exception ex)
           		{
				ex.printStackTrace();
				stop();
            		}
           	}        
    	}
}
