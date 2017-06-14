package model;

public abstract class VerletParticle extends Particle {

	private Point oldPosition;
	private double pressure;

	public VerletParticle(int id, double x, double y, double vx, double vy, double m, double r) {
		super(id, x, y, vx, vy, m, r);
	}

	public abstract Point getOwnForce();

	public abstract Point[] getForce(Particle p);

	public Point getOldPosition() {
		return this.oldPosition;
	}

	public void updatePosition(double x, double y) {
		this.oldPosition = position;
		position = new Point(x, y);
	}

	public void updateOldPosition(Point force, double dt) {
		double x = position.x + (-dt) * velocity.x + (dt * dt) * force.x / (2 * getMass());
		double y = position.y + (-dt) * velocity.y + (dt * dt) * force.y / (2 * getMass());
		oldPosition = new Point(x, y);
	}

	public double getKineticEnergy() {
		return 0.5 * getMass() * Math.pow(getSpeed(), 2);
	}

	public double getPressure() {
		return pressure;
	}

	public void addPressure(Point normalForce) {
		this.pressure += normalForce.abs() / getArea();
	}

	public void resetPressure() {
		pressure = 0;
	}

	private double getArea() {
		return 2 * Math.PI * getRadius();
	}
}
