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
		
		File filex = new File("a");
		File filey = new File("b");
		FileOutputStream file =new FileOutputStream(new File("data.txt"));
		Vector<String> x1 = new Vector<String>();
		Vector<String> y1 = new Vector<String>();
		read_from_file(filex,x1);
		read_from_file(filey,y1);
		
		int j=0;
		while(j<=y1.size()-1)
		{
			if(Float.parseFloat(y1.get(j))<0)
			{
				y1.remove(j);
				x1.remove(j);
			}
			else
			{
				j++;
			}
		}
		//System.out.println(x1.size());
		//System.out.println(y1.size());
		
		for(int q=30;q<=300;q=q+10)
		{
			if(q<=200)//在q<200之前用多项式拟合
			{
				Vector<Double> x = new Vector<Double>();
				Vector<Double> y = new Vector<Double>();
				for(int k=0;k<q;k=k+5)
				{
					x.add(Double.parseDouble(x1.get(k)));
					y.add(Double.parseDouble(y1.get(k)));
				}
				double[] xishu = polyfit(4,x,y);
				float n = poly(4,xishu,q);
				file.write((String.valueOf(n)+",").getBytes());
			}
			else
			{
				WeightedObservedPoints obs = new WeightedObservedPoints();
	        	for(int i=0;i<q;i=i+10)
	        	{
	        		obs.add(Double.parseDouble(x1.get(i)),Double.parseDouble(y1.get(i)));
	        	}
	        	double[] parameters = GaussianCurveFitter.create().fit(obs.toList());
	        	//System.out.println(q/10+":");
	        	float c = normpdf((float)q,parameters[1],parameters[2],10000);
	        	file.write((String.valueOf(c)+",").getBytes());
	        	if(q==300)
	        	{
	        		for(int g=310;g<600;g=g+10)
	        		{
	        			float n = normpdf((float)g,parameters[1],parameters[2],10000);
	                	file.write((String.valueOf(n)+",").getBytes());
	        		}
	        	}
			}
			
        	//System.out.print("\n");
		}
        
        
    }
	
	
	
	
	
	
}
