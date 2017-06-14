package ar.edu.itba.ss.particle;

import java.util.function.Function;

public class Pair {
	
	static final double EPSILON = 1e-7;
	public double x, y;
	
	public Pair(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	
	public void add(Pair p){
		add(p.x, p.y);
	}

	public void add(double x, double y) {
		this.x+=x;
		this.y+=y;
	}
	
	public void applyFunction(Function<Double, Double> f){
		x = f.apply(x);
		y = f.apply(y);
	}
	
	public double abs(){
		return Pair.abs(this);
	}
	
	public String toString(){
		return "("+x+", "+y+")";
	}
	
	public Pair clone() {
		return new Pair(x, y);
	}
	
	static public Pair sum(Pair p1, Pair p2){
		return new Pair(p1.x+p2.x, p1.y+p2.y);
	}

	public void times(double a){
		this.applyFunction(x->a*x);
	}
	
	static public Pair sub(Pair p1, Pair p2){
		return new Pair(p1.x-p2.x, p1.y-p2.y);
	}
	
	static public double abs(Pair p){
		return Math.sqrt(abs2(p));
	}
	
	static public double abs2(Pair p){
		return p.x*p.x+p.y*p.y;
	}
	
	static public double dist2(Pair p1, Pair p2){
		return abs2(sub(p1, p2));
	}
	
	static public double scalarProd(Pair p1, Pair p2){
		return p1.x*p2.x+p1.y*p2.y;
	}


	public void normalize() {
		double norm = abs(this);
		applyFunction(x->x/norm);
	}

}
