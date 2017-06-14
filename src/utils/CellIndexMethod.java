package utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Particle;
import model.Point;

public class CellIndexMethod <T extends Particle> {

	Set<T>[][] matrix;
	private double cellLength;
	private List<T> particles;
	private boolean periodicBounds;
	private double rc;
	private int m;
	private double l;
	
	//OJO
	public CellIndexMethod(List<T> particle, double l, double cellLength, double rc, boolean periodicBounds){
		this(particle, l, (int)Math.ceil(l/cellLength), rc, periodicBounds);
	}
	
	private CellIndexMethod(List<T> particles, double l, int m, double rc, boolean periodicBounds) {
		cellLength = l / m;
		this.l = l;
		this.m = m;
		this.periodicBounds = periodicBounds;
		this.rc = rc;
		this.particles = particles;
		if (!validProperties(particles, l, m, rc)) {
			throw new IllegalArgumentException();
		}
		fillMatrix(particles, m);
	}

	public Set<T>[][] getMatrix() {
		return matrix;
	}

	public boolean isPeriodicBounds() {
		return periodicBounds;
	}

	public double getRc() {
		return rc;
	}

	public double getL() {
		return l;
	}

	public Map<T, Set<T>> getNeighbours() {
		Map<T, Set<T>> neighbours = new HashMap<T, Set<T>>();
		fillMatrix(particles, m);
		for (T p : particles) {
			neighbours.put(p, new HashSet<T>());
		}
		int m = matrix.length;
		for (int x = 0; x < m; x++) {
			for (int y = 0; y < m; y++) {
				Set<T> particlesInCell = matrix[x][y];
				for (T p : particlesInCell) {
					int[] dx = { 0, 0, 1, 1, 1 };
					int[] dy = { 0, 1, 1, 0, -1 };
					for (int k = 0; k < 5; k++) {
						int xx = x + dx[k];
						int yy = y + dy[k];
						if (!periodicBounds) {
							if (xx >= m || yy < 0 || yy >= m) {
								continue;
							}
						}
						xx = (xx + m) % m;
						yy = (yy + m) % m;
						for (T q : matrix[xx][yy]) {
							addNeighbour(p, q, neighbours);
						}
					}
				}
			}
		}
		return neighbours;

	}

	private void addNeighbour(T p, T q, Map<T, Set<T>> neighbours) {
		if (p.equals(q)) {
			return;
		}

		Point pp = p.getPosition().clone();
		Point qq = q.getPosition().clone();

		if (periodicBounds) {

			if (pp.x < qq.x && pp.x + 2 * cellLength < qq.x) {
				pp.x += l;
			}

			if (qq.x < pp.x && qq.x + 2 * cellLength < pp.x) {
				qq.x += l;
			}

			if (pp.y < qq.y && pp.y + 2 * cellLength < qq.y) {
				pp.y += l;
			}

			if (qq.y < pp.y && qq.y + 2 * cellLength < pp.y) {
				qq.y += l;
			}
		}

		double dist2 = Point.dist2(pp, qq);
		double r = rc + p.getRadius() + q.getRadius();

		if (dist2 < r * r) {
			neighbours.get(p).add(q);
			neighbours.get(q).add(p);
		}
	}

	@SuppressWarnings("unchecked")
	private void fillMatrix(List<T> particles, int m) {
		matrix = new Set[m][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				matrix[i][j] = new HashSet<T>();
			}
		}
		for (T particle : particles) {
			Point p = particle.getPosition();
			try{
				matrix[(int) (p.x / cellLength)][(int) (p.y / cellLength)].add(particle);
			}catch(Exception e){
				System.err.println(p.x+" "+p.y+"  id: "+particle.getId());
			}
		}
	}

	private boolean validProperties(List<T> particles, double l, int m, double rc) {
		double max1 = 0;
		double max2 = 0;
		for (T p : particles) {
			if (p.getRadius() >= max1) {
				max2 = max1;
				max1 = p.getRadius();
			} else {
				if (p.getRadius() >= max2) {
					max2 = p.getRadius();
				}
			}
		}
		return (cellLength >= rc + max1 + max2);
	}
	
	/**
	 * 
	 * @param particle - particle before move to new position
	 * @param x - new x coordinate
	 * @param y - new y coordinate
	 */
	public void moveTo(T particle, Point point){
		/*Point p = particle.getPosition();
		int prevX = (int) (p.x / cellLength);
		int prevY = (int) (p.y / cellLength);
		int postX = (int) (point.x / cellLength);
		int postY = (int) (point.y / cellLength);
		if(prevX != postX || prevY != postY){
			matrix[prevX][prevY].remove(particle);
			matrix[postX][postY].add(particle);
		}*/
	}
}
