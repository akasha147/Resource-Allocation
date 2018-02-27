import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;

class client implements Runnable
{
    	Socket soc;    
    	Thread t=null;
    	DataOutputStream dout;
    	DataInputStream din,inputLine;	
	String LN;
        Scanner sc;
    	client(String LoginName) throws Exception
    	{
       		soc=new Socket("127.0.0.1",5217);
        	sc=new Scanner(System.in);
        	dout = new DataOutputStream(soc.getOutputStream());
      		din = new DataInputStream(soc.getInputStream());  
		LN=LoginName;      
        	dout.writeUTF(LoginName);
		inputLine = new DataInputStream(new BufferedInputStream(System.in));
		
		
        	t=new Thread(this);
        	t.start();
	}
   
    	public static void main(String args[]) throws Exception
    	{
        	client Cli=new client(args[0]);
                       
    	}    
    	public void run()
    	{        
		int[] ordering;
		int max_resources;
		try
                {
		String response=din.readUTF();
		if(response.equals("No"))
		{
		System.out.println("Server reached maximum capacity!!Try again later");
		t.stop();
	
		}
		else
		{
		String[] a=response.split("@",-2);
		max_resources=Integer.parseInt(a[0]);
		ordering=new int[max_resources];
		for(int i=0;i<max_resources;i++)
		{
			ordering[i]=Integer.parseInt(a[i+1]);		
		}
		System.out.println("User "+LN+" has logged on to the server");
		System.out.println("Number of resources available: "+max_resources);
		System.out.println("Ordering of Resources: ");
		for(int i=0;i<max_resources;i++)
		{
			System.out.print(ordering[i]);
			if(i!=max_resources-1&&max_resources!=1)
			{
			   	System.out.print(",");			
			}
				
		}
		System.out.println("\nThe following options are possible");
		System.out.println("1.Request\n2.Release\n3.Quit");
		System.out.println("Enter your choice: ");
        	while(true)
        	{
        		try
            		{
             			int max=-1;
        			String held[]=new String[max_resources];
				for(int i=0;i<max_resources;i++)
				{
					held[i]="false";		
				}
				String line,line1=null;
				while ((line = inputLine.readLine()) != null) 
				{
          				if(!line.equals("1")&&!line.equals("2")&&!line.equals("3"))
					{
  						System.out.println("Invalid Choice!!Try again");				
					}
					else if(line.equals("1"))
					{
						if(max==max_resources-1)
						{
						       System.out.println("You cannot request for new resources!!");						
						}
						else
						{
						System.out.println("Available Resources: ");
						for(int i=max+1;i<max_resources;i++)
						{
							System.out.print(ordering[i]);
							if(i!=max_resources-1&&max!=max_resources-2)
							{
			   					System.out.print(",");			
							}
				
						}		
						System.out.print("\n");				
						System.out.print("Enter the resource number: ");
						int k=sc.nextInt();
					   	
						while(k>=max_resources||k<0)
						{
							System.out.print("Invalid Input Try again!!\nEnter the resource number: ");
							k=sc.nextInt();
						}
						line1=Integer.toString(k);
                                           	if(held[Integer.parseInt(line1)].equals("true"))
					    	{
					      		System.out.println("You already possess the resource");
                                            	}
						else if(max==max_resources-1)
						{
						       System.out.println("You cannot request for new resources!!");						
						}
					   	else if(ordering[Integer.parseInt(line1)]<max)
					    	{
                                            		System.out.println("Violating the order..Not possible");
                                            	}
					    	else
					    	{
					    		System.out.println("Requesting lock from server");
					  		line=line+"@"+line1;
					    		int m=Integer.parseInt(line1);
							for(int i=0;i<max_resources;i++)
							{
								if(ordering[i]==m)
								{
								  max=i;
								  break;
								}						
							}
					    		//System.out.println(line);	
					    		dout.writeUTF(line);
					    		held[Integer.parseInt(line1)]=din.readUTF();
					   		System.out.println("Server granted lock");	
					 	}
						}
 					}
				
					else if(line.equals("2"))
					{        
						if(max==-1)
						{
							System.out.println("You dont have any resource to release");						
						} 
						else
						{
						System.out.println("Resources held by you: ");
						for(int i=0;i<max_resources;i++)
						{
							if(held[i].equals("true"))
							{
							System.out.print(i);
							if(i!=max_resources-1&&max!=max_resources-2)
							{
			   					System.out.print(",");			
							}
							}
				
						}		
						System.out.print("\n");				
						System.out.print("Enter the resource number: ");
						int k=sc.nextInt();
					   	
						while(k>=max_resources||k<0)
						{
							System.out.print("Invalid Input Try again!!\nEnter the resource number: ");
							k=sc.nextInt();
						}
						line1=Integer.toString(k);
				                if(held[Integer.parseInt(line1)].equals("false"))
					    	{
					      		System.out.println("You dont have the lock for the resource!!");
                                            	}
                                            	else
						{
					  		System.out.println("Releasing lock..Informing the server");
							line=line+"@"+line1;
					   		dout.writeUTF(line);
					   		held[Integer.parseInt(line1)]=din.readUTF();
					     		int i;
					   		for(i=max_resources-1;i>=0;i--)
							{
 								if(held[ordering[i]].equals("true"))
								{
	 								max=i;
									break;					
    								} 					
 							}
							if(i==-1)
							{
                                           			max=-1;					
							}
							//System.out.println(max);
							System.out.println("Resource unlocked");
						}
						}
									
          				}
					else if(line.equals("3"))
					{
						dout.writeUTF(line);
						System.out.println("Logging out..Informing the server");
						for(int i=max_resources-1;i>=0;i--)
							{
 							   held[i]="false"; 					
 							}
						max=-1;
						System.out.println("Server response: "+din.readUTF());
						t.stop();					
					}
				System.out.print("Enter your choice: ");          			
				}
				
        		
			}
                	catch(Exception ex)
            		{
                		ex.printStackTrace();
				t.stop();
            		}
			}
        
	}	}
	catch(Exception ex)
            		{
                		ex.printStackTrace();
            		}	
	}    	
	}
