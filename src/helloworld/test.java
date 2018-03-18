package helloworld;
/*
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.*;
import org.jgrapht.graph.*;
*/
import org.apache.commons.math3.fitting.GaussianCurveFitter;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;

import java.lang.Math;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
public class test {
	
	static float poly(int high_rate,double[] xishu,double x)
	{
		float a = 0;
		for(int i=0;i<=high_rate;i++)
		{
			a = (float)(a+ xishu[i]*Math.pow(x, i));
		}
		return a;
	}
	
	static float normpdf(float x,double mu,double delta,float xishu)//正态分布函数
	{
		double r = -((Math.pow(x-mu,2))/(2*Math.pow(delta,2)));
    	return xishu*(float) (Math.pow(Math.E,r)/(Math.pow(2*Math.PI, 0.5)*delta));
	}
	
	static Vector<Double> slide_nd(Vector<Double> y)
	{
		Vector<Double> result_y = new Vector<Double>();
		//先暂定滑动系数为3
		for(int i=0;i<y.size()-3;i++)
		{
			double sum = 0;
			for(int j=i;j<i+4;j++)
			{
				sum = y.get(j)+sum;
			}
			result_y.add(sum/4);
		}
		return result_y;
	}
	
	static double[] polyfit(int degree,Vector<Double> x,Vector<Double> y)
	{
		PolynomialCurveFitter polynomialCurveFitter = PolynomialCurveFitter.create(degree);
		ArrayList weightedObservedPoints = new ArrayList(); 
    	for(int i=0;i<x.size();i++)
    	{
    		WeightedObservedPoint weightedObservedPoint = new WeightedObservedPoint(1, x.get(i) ,y.get(i) );
            weightedObservedPoints.add(weightedObservedPoint);
    	}
    	return polynomialCurveFitter.fit(weightedObservedPoints);
	}
	
	static void read_from_file(File x,Vector<String> a) throws IOException
	{
		InputStreamReader readx = new InputStreamReader(new FileInputStream(x),"GBK");
		BufferedReader bufferedReader = new BufferedReader(readx);
		String lineTxt = null;
		StringBuffer txt = new StringBuffer();
		while((lineTxt = bufferedReader.readLine()) != null)
		{
			txt.append(lineTxt);
		}
		readx.close();
		String l = txt.toString();
		for(int i=0;i<l.split(",").length;i++)
		{
			a.add(l.split(",")[i]);
		}
	}
	
	public static void main(String[] args) throws IOException
    {
		
		File filex = new File("x");
		File filey = new File("y");
		FileOutputStream file =new FileOutputStream(new File("data.txt"));
		Vector<String> x1 = new Vector<String>();
		Vector<String> y1 = new Vector<String>();
		read_from_file(filex,x1);
		read_from_file(filey,y1);
		double mu = 0;
		double delta = 0;
		int j=0;
		while(j<=y1.size()-1)
		{
			if(Float.parseFloat(y1.get(j))<0)
			{
				System.out.println("error!");
				y1.remove(j);
				x1.remove(j);
			}
			else
			{
				j++;
			}
		}
		
		for(int q=1;q<=1440;q++)
		{
			
			if(q<=360)
			{
				file.write("0,".getBytes());
			}
			else if(q>=361&&q<=600)//在这里先对数据滑动平均滤波，再用插值法
			{
				float sum = 0;
				if(q<=380)
				{
					sum = (float)Double.parseDouble(y1.get(q))*50;
					System.out.println(String.valueOf(q)+":"+(String.valueOf(sum)+","));
					file.write((String.valueOf(sum)+",").getBytes());
				}
				else
				{
					Vector<Double> x = new Vector<Double>();
					Vector<Double> y = new Vector<Double>();
					//Vector<Double> x2 = new Vector<Double>();
					
					for(int i=360;i<=q;i++)
					{
						x.add(Double.parseDouble(x1.get(i))-360);
						y.add(Double.parseDouble(y1.get(i)));
					}
					x.remove(0);
					x.remove(x.size()-1);
					x.remove(x.size()-1);
					Vector<Double> y2 = slide_nd(y);
					
					
					double[] xishu = polyfit(1,x,y2);
					
					float n = 0;
		        	for(int i1 = q;i1<=q+49;i1++)
		        	{
		        		float v = poly(1,xishu,i1-360);
		        		if(v>0)
		        		{
		        			n = n+v;
		        		}
		        	}
		        	
					System.out.println(String.valueOf(q)+":"+(String.valueOf(n)+","));
					file.write((String.valueOf(n)+",").getBytes());
				}
			}
			else if(q>600&&q<=720)
			{
				//先滤波
				Vector<Double> x3 = new Vector<Double>();
				Vector<Double> y3 = new Vector<Double>();
				
				for(int i=360;i<=q;i=i+5)
				{
					
					
					
					x3.add(Double.parseDouble(x1.get(i))-360+2);
					
					double r = 0;
					for(int b = 0;b<5;b++)
					{
						r = r+Double.parseDouble(y1.get(i+b));
					}
					r = r/5;
					
					y3.add(r);
				}
				
				Vector<Double> y4 = slide_nd(y3);
				x3.remove(0);
				x3.remove(x3.size()-1);
				x3.remove(x3.size()-1);
				
				WeightedObservedPoints obs = new WeightedObservedPoints();
	        	for(int j1=0;j1<x3.size();j1++)
	        	{
	        		obs.add(x3.get(j1),y4.get(j1));
	        	}
	        	double[] parameters = GaussianCurveFitter.create().fit(obs.toList());
	        	//float c = normpdf((float)q,parameters[1],parameters[2],10000);
	        	float sum = 0;
	        	for(int i1 = q;i1<=q+49;i1++)
	        	{
	        		sum = sum+normpdf((float)(i1-360),parameters[1],parameters[2],10000);
	        	}
	        	
	        	System.out.println(String.valueOf(q)+":"+(String.valueOf(sum)+","));
	        	file.write((String.valueOf(sum)+",").getBytes());
	        	if(q==720)
	        	{
	        		/*System.out.println(parameters[1]);
	        		System.out.println(parameters[2]);*/
	        		mu = parameters[1];
	        		delta = parameters[2];
	        	}
			}
			else if(q>=721 && q<=1080)
			{
				float sum = 0;
				for(int i1 = q;i1<=q+49;i1++)
	        	{
	        		sum = sum+normpdf((float)(i1-360),(double)mu,(double)delta,10000);
	        	}
				System.out.println(String.valueOf(q)+":"+(String.valueOf(sum)+","));
	        	file.write((String.valueOf(sum)+",").getBytes());
			}
			else 
			{
				if(q!=1440)
				{
					file.write("0,".getBytes());
				}
				else
				{
					file.write("0".getBytes());
				}
			}
		}
        
        
        file.close();
    }
	
	
	
	
	
	
}
