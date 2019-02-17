package com.tomgibara.intgeom;

import java.util.Comparator;

//TODO add support for quarter turns
public enum IntDir {

	LESS_X( IntVector.UNIT_NEG_X, (c1, c2) -> Integer.compare(c2.x, c1.x), (r1, r2) -> Integer.compare(r2.minX, r1.minX) ),
	MORE_X( IntVector.UNIT_X,     (c1, c2) -> Integer.compare(c1.x, c2.x), (r1, r2) -> Integer.compare(r1.maxX, r2.maxX) ),
	LESS_Y( IntVector.UNIT_NEG_Y, (c1, c2) -> Integer.compare(c2.y, c1.y), (r1, r2) -> Integer.compare(r2.minY, r1.minY) ),
	MORE_Y( IntVector.UNIT_Y,     (c1, c2) -> Integer.compare(c1.y, c2.y), (r1, r2) -> Integer.compare(r1.maxY, r2.maxY) );

	public final IntVector unitVector;
	public final Comparator<IntCoords> coordsComparator;
	public final Comparator<IntRect> rectComparator;
	public final IntAxis axis;

	private IntDir(
			IntVector             unitVector,
			Comparator<IntCoords> coordsComparator,
			Comparator<IntRect>   rectComparator
			) {
		this.unitVector       = unitVector;
		this.coordsComparator = coordsComparator;
		this.rectComparator   = rectComparator;
		axis = unitVector.isParallelToXAxis() ? IntAxis.X : IntAxis.Y;
	}

	public IntDir reverse() {
		switch (this) {
		case LESS_X : return MORE_X;
		case MORE_X : return LESS_X;
		case LESS_Y : return MORE_Y;
		case MORE_Y : return LESS_Y;
		default: throw new IllegalStateException();
		}
	}

	public int difference(IntCoords a, IntCoords b) {
		switch (this) {
		case LESS_X : return a.x - b.x;
		case MORE_X : return b.x - a.x;
		case LESS_Y : return a.y - b.y;
		case MORE_Y : return b.y - a.y;
		default: throw new IllegalStateException();
		}
	}

	public int gap(IntRect a, IntRect b) {
		switch (this) {
		case LESS_X : return a.minX - b.maxX;
		case MORE_X : return b.minX - a.maxX;
		case LESS_Y : return a.minY - b.maxY;
		case MORE_Y : return b.minY - a.maxY;
		default: throw new IllegalStateException();
		}
	}

	public IntCoords moved(IntCoords coords) {
		switch (this) {
		case LESS_X : return new IntCoords(coords.x - 1, coords.y    );
		case MORE_X : return new IntCoords(coords.x + 1, coords.y    );
		case LESS_Y : return new IntCoords(coords.x    , coords.y - 1);
		case MORE_Y : return new IntCoords(coords.x    , coords.y + 1);
		default: throw new IllegalStateException();
		}
	}

	public IntCoords moved(IntCoords coords, int distance) {
		switch (this) {
		case LESS_X : return new IntCoords(coords.x - distance, coords.y           );
		case MORE_X : return new IntCoords(coords.x + distance, coords.y           );
		case LESS_Y : return new IntCoords(coords.x           , coords.y - distance);
		case MORE_Y : return new IntCoords(coords.x           , coords.y + distance);
		default: throw new IllegalStateException();
		}
	}

}
