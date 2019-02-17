package com.tomgibara.intgeom;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Rectangle;
import java.util.AbstractList;
import java.util.List;

public final class IntRect {

	// statics

	private static int clamp(int n, int min, int max) {
		return Math.max(Math.min(n, max), min);
	}

	public static final IntRect ZERO_RECT = new IntRect(0, 0, 0, 0);

	public static final IntRect UNIT_SQUARE = new IntRect(0, 0, 1, 1);

	public static IntRect rectangle(int x, int y, int w, int h) {
		if (w < 0) throw new IllegalArgumentException("negative w");
		if (h < 0) throw new IllegalArgumentException("negative h");
		return new IntRect(x, y, x + w, y + h);
	}

	public static IntRect rectangle(IntCoords coords, IntDimensions dimensions) {
		if (coords == null) throw new IllegalArgumentException("null coords");
		if (dimensions == null) throw new IllegalArgumentException("null dimensions");
		return new IntRect(coords.x, coords.y, coords.x + dimensions.width, coords.y + dimensions.height);
	}

	public static IntRect bounded(int x1, int y1, int x2, int y2) {
		return new IntRect(min(x1,x2), min(y1,y2), max(x1,x2), max(y1,y2));
	}

	public static IntRect atOrigin(int w, int h) {
		if (w < 0) throw new IllegalArgumentException("negative w");
		if (h < 0) throw new IllegalArgumentException("negative h");
		return new IntRect(0, 0, w, h);
	}

	public static IntRect squareAtOrigin(int size) {
		if (size < 0) throw new IllegalArgumentException("negative size");
		return size == 1 ? UNIT_SQUARE : new IntRect(0, 0, size, size);
	}

	public static IntRect atOrigin(IntDimensions dimension) {
		if (dimension == null) throw new IllegalArgumentException("null dimension");
		return new IntRect(0, 0, dimension.width, dimension.height);
	}

	public static IntRect centeredAtOrigin(int cornerX, int cornerY) {
		cornerX = Math.abs(cornerX);
		cornerY = Math.abs(cornerY);
		return new IntRect(-cornerX, -cornerY, cornerX, cornerY);
	}

	public static IntRect centeredAtOrigin(IntDimensions halfDimension) {
		if (halfDimension == null) throw new IllegalArgumentException("null halfDimension");
		return new IntRect(-halfDimension.width, -halfDimension.height, halfDimension.width, halfDimension.height);
	}

	public static IntRect point(int x, int y) {
		return new IntRect(x,y,x,y);
	}

	public static IntRect point(IntCoords coords) {
		return point(coords.x, coords.y);
	}

	public static IntRect unit(int x, int y) {
		return new IntRect(x, y, x+1, y+1);
	}

	public static IntRect unit(IntCoords coords) {
		return unit(coords.x, coords.y);
	}

	// fields

	public final int minX;
	public final int minY;
	public final int maxX;
	public final int maxY;

	// constructors

	IntRect(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	// methods

	public int width() {
		return maxX - minX;
	}

	public int height() {
		return maxY - minY;
	}

	public IntDimensions dimensions() {
		return new IntDimensions(width(), height());
	}

	public IntCoords minimumCoords() {
		return IntCoords.at(minX, minY);
	}

	public IntCoords maximumCoords() {
		return IntCoords.at(maxX, maxY);
	}

	public int centerX() {
		return (minX + maxX) / 2;
	}

	public int centerY() {
		return (minY + minY) / 2;
	}

	public IntCoords edgeCenter(IntDir edgeDir) {
		switch (edgeDir) {
		case LESS_X: return new IntCoords(minX     , centerY());
		case MORE_X: return new IntCoords(maxX     , centerY());
		case LESS_Y: return new IntCoords(centerX(), minY     );
		case MORE_Y: return new IntCoords(centerX(), maxY     );
		default: throw new IllegalArgumentException();
		}
	}

	public boolean isPoint() {
		return minX == maxX && minY == maxY;
	}

	public boolean isUnit() {
		return maxX == minX + 1 && maxY == minY + 1;
	}

	public boolean isDegenerate() {
		return minX == maxX || minY == maxY;
	}

	public boolean containsPoint(int x, int y) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	public boolean containsPoint(IntCoords coords) {
		return containsPoint(coords.x, coords.y);
	}

	public boolean containsUnit(int x, int y) {
		return x >= minX && x < maxX && y>= minY && y < maxY;
	}

	public boolean containsUnit(IntCoords coords) {
		return containsUnit(coords.x, coords.y);
	}

	public boolean containsRect(IntRect that) {
		return
			this.minX <= that.minX &&
			this.maxX >= that.maxX &&
			this.minY <= that.minY &&
			this.maxY >= that.maxY;
	}

	public boolean intersectsRect(IntRect that) {
		return
			this.minX < that.maxX &&
			this.maxX > that.minX &&
			this.minY < that.maxY &&
			this.maxY > that.minY;
	}

	public boolean xLessThan(IntRect that) {
		return this.maxX <= that.minX;
	}

	public boolean yLessThan(IntRect that) {
		return this.maxY <= that.minY;
	}

	public IntCoords boundedPoint(IntCoords coords) {
		int x = clamp(coords.x, minX, maxX);
		int y = clamp(coords.y, minY, maxY);
		return x == coords.x && y == coords.y ? coords : new IntCoords(x, y);
	}

	public IntCoords boundedUnit(IntCoords coords) {
		if (isDegenerate()) throw new IllegalStateException("degenerate");
		int x = clamp(coords.x, minX, maxX - 1);
		int y = clamp(coords.y, minY, maxY - 1);
		return x == coords.x && y == coords.y ? coords : new IntCoords(x, y);
	}

	public int rowMajorIndex(IntCoords coords) {
		coords = boundedUnit(coords).relativeTo(this);
		return coords.y * width() + coords.x;
	}

	public int columnMajorIndex(IntCoords coords) {
		coords = boundedUnit(coords).relativeTo(this);
		return coords.x * height() + coords.y;
	}

	public IntRect translatedToOrigin() {
		return minX == 0 && minY == 0 ? this : new IntRect(0, 0, width(), height());
	}

	public IntRect translatedBy(int x, int y) {
		if (x == 0 && y == 0) return this;
		return new IntRect(minX + x, minY + y, maxX + x, maxY + y);
	}

	public IntRect translatedBy(IntVector vector) {
		return translatedBy(vector.x, vector.y);
	}

	public IntRect translatedByNegative(IntVector vector) {
		return translatedBy(-vector.x, -vector.y);
	}

	public IntRect resized(IntDir direction, int size) {
		if (size >= 0) {
			switch (direction) {
			case LESS_X: return new IntRect(maxX - size, minY, maxX, maxY);
			case LESS_Y: return new IntRect(minX, maxY - size, maxX, maxY);
			case MORE_X: return new IntRect(minX, minY, minX + size, maxY);
			case MORE_Y: return new IntRect(minX, minY, maxX, minY + size);
			default: throw new IllegalStateException();
			}
		} else {
			switch (direction) {
			case LESS_X: return new IntRect(maxX, minY, maxX + size, maxY);
			case LESS_Y: return new IntRect(minX, maxY, maxX, maxY + size);
			case MORE_X: return new IntRect(minX - size, minY, minX, maxY);
			case MORE_Y: return new IntRect(minX, minY - size, maxX, minY);
			default: throw new IllegalStateException();
			}
		}
	}

	public IntRect relativeTo(IntCoords coords) {
		return translatedBy(-coords.x, -coords.y);
	}

	public IntRect relativeTo(IntRect that) {
		return translatedBy(-that.minX, -that.minY);
	}

	public IntRect centeredIn(IntRect that) {
		int dx = that.centerX() - this.centerX();
		int dy = that.centerY() - this.centerY();
		return translatedBy(dx, dy);
	}

	public IntRect growToIncludePoint(int x, int y) {
		return containsPoint(x, y) ? this :
			new IntRect(min(minX, x), min(minY, x), max(maxX, x), max(maxY, y));
	}

	public IntRect growToIncludePoint(IntCoords coords) {
		return growToIncludePoint(coords.x, coords.y);
	}

	public IntRect growToIncludeUnit(IntCoords coords) {
		return growToIncludeUnit(coords.x, coords.y);
	}

	public IntRect growToIncludeUnit(int x, int y) {
		return containsUnit(x, y) ? this :
			new IntRect(min(minX, x), min(minY, x), max(maxX, x+1), max(maxY, y+1));
	}

	public IntRect scaled(int s) {
		if (s == 0) return ZERO_RECT;
		return s < 0 ?
				new IntRect(maxX * s, maxY * s, minX * s, minY * s) :
				new IntRect(minX * s, minY * s, maxX * s, maxY * s);
	}

	public IntRect growToIncludeRect(IntRect that) {
		if (this.containsRect(that)) return this;
		if (that.containsRect(this)) return that;
		return new IntRect(
				min(this.minX, that.minX),
				min(this.minY, that.minY),
				max(this.maxX, that.maxX),
				max(this.maxY, that.maxY)
				);
	}

	public IntRect intersectRect(IntRect that) {
		if (this.containsRect(that)) return that;
		if (that.containsRect(this)) return this;
		return new IntRect(
				max(this.minX, that.minX),
				max(this.minY, that.minY),
				min(this.maxX, that.maxX),
				min(this.maxY, that.maxY)
				);
	}

	public IntRect plus(IntMargins margins) {
		if (margins.isVoid()) return this;
		return new IntRect(
				this.minX + margins.minX,
				this.minY + margins.minY,
				this.maxX + margins.maxX,
				this.maxY + margins.maxY
				);
	}

	public IntRect minus(IntMargins margins) {
		if (margins.isVoid()) return this;
		return new IntRect(
				this.minX - margins.minX,
				this.minY - margins.minY,
				this.maxX - margins.maxX,
				this.maxY - margins.maxY
				);
	}

	// when you add the returned margins to that, you get this
	public IntMargins difference(IntRect that) {
		return IntMargins.offsets(
				this.minX - that.minX,
				this.maxX - that.maxX,
				this.minY - that.minY,
				this.maxY - that.maxY
				);
	}

	public IntRange projectToX() {
		return new IntRange(minX, maxX);
	}

	public IntRange projectToY() {
		return new IntRange(minY, maxY);
	}

	public IntRange projectAlongAxis(IntAxis axis) {
		return axis.vertical ? projectToX() : projectToY();
	}

	public IntRange projectAgainstAxis(IntAxis axis) {
		return axis.vertical ? projectToY() : projectToX();
	}

	public IntVector vectorToMinimumCoords() {
		return new IntVector(minX, minY);
	}

	public IntVector vectorToMaximumCoords() {
		return new IntVector(maxX, maxY);
	}

	public List<IntCoords> unitsInRowOrder() {
		return new AbstractList<IntCoords>() {
			private final int width = width();
			private final int height = height();
			@Override public int size() { return width * height; }
			@Override public IntCoords get(int index) { return new IntCoords(index % width, index / width); };
		};
	}

	public List<IntCoords> unitsInColumnOrder() {
		return new AbstractList<IntCoords>() {
			private final int width = width();
			private final int height = height();
			@Override public int size() { return width * height; }
			@Override public IntCoords get(int index) { return new IntCoords(index / height, index % height); };
		};
	}

	public Rectangle toRectangle() {
		return new Rectangle(minX, minY, width(), height());
	}

	// object methods

	@Override
	public int hashCode() {
		return minX + (31 * minY + (31 * maxX + (31 * maxY)));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof IntRect)) return false;
		IntRect that = (IntRect) obj;
		return
				this.minX == that.minX &&
				this.minY == that.minY &&
				this.maxX == that.maxX &&
				this.maxY == that.maxY;
	}

	@Override
	public String toString() {
		return "mX: " + minX + ", mY:" + minY + ", MX:" + maxX + ", MY:" + maxY; 
	}

}
