package model;

import utils.RandomUtils;

public class Particle {
	
	private int id;
	protected Point position;
	protected Point velocity;
	private double radius;
	private double mass;
	

	public Particle(int id, double x, double y, double vx, double vy, double m, double r) {
		this.id = id;
		this.position = new Point(x, y);
		this.velocity = new Point(vx, vy);
		this.mass = m;
		this.radius = r;
	}
	
	public Particle(int id, double x, double y, double velAbs, double m, double r) {
		this.id = id;
		this.position = new Point(x, y);
		double angle = RandomUtils.getRandomDouble(0, 2*Math.PI);
		this.velocity = new Point(velAbs * Math.cos(angle), velAbs * Math.sin(angle));
		this.mass = m;
		this.radius = r;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return position.x;
	}
	
	public double getY(){
		return position.y;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public double getXVelocity(){
		return velocity.x;
	}
	
	public double getYVelocity(){
		return velocity.y;
	}
	
	public Point getVelocity(){
		return velocity;
	}
	
	public double getMass(){
		return mass;
	}

	public double getRadius() {
		return radius;
	}
	
	public void updatePosition(double x, double y) {
		this.position = new Point(x, y);
	}
	
	
	public void updateVelocity(double x, double y) {
		this.velocity = new Point(x, y);
	}
	
	
	public void move(double time){
		Point deltaPosition = velocity.clone();
		deltaPosition.applyFunction(v->v*time);
		position.add(deltaPosition);
	}
	
	public static <T extends Particle> boolean areOverlapped(T p, T q){
		return Point.dist2(p.position, q.position) <= Math.pow(p.getRadius()+q.getRadius(),2);
	}

	public double getSpeed() {
		return velocity.abs();
	}
	
	public void addMass(double mass) {
		this.mass+=mass;
	}
	
	public void addRadius(double radius) {
		this.radius+=radius;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Particle other = (Particle) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	
}
