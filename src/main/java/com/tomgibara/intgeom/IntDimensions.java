package com.tomgibara.intgeom;

public final class IntDimensions {

	public static IntDimensions NOTHING = new IntDimensions(0, 0);
	public static IntDimensions ONE_BY_ONE = new IntDimensions(1, 1);
	public static IntDimensions MAXIMUM = new IntDimensions(Integer.MAX_VALUE, Integer.MAX_VALUE);

	public static IntDimensions of(int width, int height) {
		if (width < 0) throw new IllegalArgumentException("negative width");
		if (height < 0) throw new IllegalArgumentException("negative height");
		return new IntDimensions(width, height);
	}

	public static IntDimensions horizontal(int width) {
		if (width < 0) throw new IllegalArgumentException("negative width");
		return new IntDimensions(width, 0);
	}

	public static IntDimensions vertical(int height) {
		if (height < 0) throw new IllegalArgumentException("negative height");
		return new IntDimensions(0, height);
	}

	public static IntDimensions square(int size) {
		if (size < 0) throw new IllegalArgumentException("negative size");
		return new IntDimensions(size, size);
	}

	public final int width;
	public final int height;

	IntDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public boolean isNothing() {
		return width == 0 && height == 0;
	}

	public boolean isDegenerate() {
		return width == 0 || height == 0;
	}

	public boolean meets(IntDimensions that) {
		return this.width >= that.width && this.height >= that.height;
	}

	public boolean metBy(IntDimensions that) {
		return this.width <= that.width && this.height <= that.height;
	}

	public boolean exceeds(IntDimensions that) {
		return this.width > that.width && this.height >= that.height || this.width >= that.width && this.height > that.height;
	}

	public boolean exceededBy(IntDimensions that) {
		return that.exceeds(this);
	}

	public boolean extendsToPoint(IntCoords coords) {
		return coords.x >= 0 && coords.y >= 0 && coords.x <= width && coords.y <= height;
	}

	public boolean extendsToPoint(int x, int y) {
		return x >= 0 && y >= 0 && x <= width && y <= height;
	}

	public boolean extendsToUnit(IntCoords coords) {
		return coords.x >= 0 && coords.y >= 0 && coords.x < width && coords.y < height;
	}

	public boolean extendsToUnit(int x, int y) {
		return x >= 0 && y >= 0 && x < width && y < height;
	}

	public int dimension(IntAxis axis) {
		return axis == IntAxis.X ? width : height;
	}

	public IntDimensions scale(int scale) {
		if (scale < 0) throw new IllegalArgumentException("negative scale");
		switch (scale) {
		case 0 : return NOTHING;
		case 1 : return this;
		default: return new IntDimensions(width * scale, height * scale);
		}
	}

	public IntDimensions scale(int sx, int sy) {
		if (sx < 0) throw new IllegalArgumentException("negative sx");
		if (sy < 0) throw new IllegalArgumentException("negative sy");
		return sx == sy ? scale(sx) : new IntDimensions(width * sx, height * sy);
	}

	public IntDimensions withWidth(int width) {
		if (width == this.width) return this;
		if (width < 0) throw new IllegalArgumentException("negative width");
		return new IntDimensions(width, height);
	}

	public IntDimensions withHeight(int height) {
		if (height == this.height) return this;
		if (height < 0) throw new IllegalArgumentException("negative height");
		return new IntDimensions(width, height);
	}

	public IntDimensions withDimension(IntAxis axis, int dimension) {
		return axis == IntAxis.X ? withWidth(dimension) : withHeight(dimension);
	}

	public IntDimensions growToInclude(IntDimensions that) {
		if (this.exceeds(that)) return this;
		if (that.exceeds(this)) return that;
		return new IntDimensions(Math.max(this.width, that.width), Math.max(this.height, that.height));
	}

	public IntDimensions extendedBy(IntDimensions that) {
		if (this.isNothing()) return that;
		if (that.isNothing()) return this;
		return new IntDimensions(this.width + that.width, this.height + that.height);
	}

	// the dimensions of a rectangle of these dimensions after the margins were added
	public IntDimensions plus(IntMargins margins) {
		return margins.isVoid() ? this :
			new IntDimensions(Math.abs(width + margins.maxX - margins.minX), Math.abs(height + margins.maxY - margins.minY));
	}

	// the dimensions of a rectangle of these dimensions after the margins were subtracted
	public IntDimensions minus(IntMargins margins) {
		return margins.isVoid() ? this :
			new IntDimensions(Math.abs(width - margins.maxX + margins.minX), Math.abs(height - margins.maxY + margins.minY));
	}

	public IntRange rangeOfX() {
		return new IntRange(0, width);
	}

	public IntRange rangeOfY() {
		return new IntRange(0, height);
	}

	public IntRange projectAgainstAxis(IntAxis axis) {
		return axis.horizontal ? rangeOfX() : rangeOfY();
	}

	public IntRange projectAlongAxis(IntAxis axis) {
		return axis.horizontal ? rangeOfY() : rangeOfX();
	}

	public IntRect toRect() {
		return new IntRect(0, 0, width, height);
	}

	public IntRect toRect(IntCoords min) {
		return toRect(min.x, min.y);
	}

	public IntRect toRect(int minX, int minY) {
		return new IntRect(minX, minY, minX + width, minY + height);
	}

	public int area() {
		return width * height;
	}

	@Override
	public int hashCode() {
		return width + 31 * height;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof IntDimensions)) return false;
		IntDimensions that = (IntDimensions) obj;
		return this.width == that.width && this.height == that.height;
	}

	@Override
	public String toString() {
		return width + "x" + height;
	}
}
