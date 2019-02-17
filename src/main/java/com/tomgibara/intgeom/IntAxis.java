package com.tomgibara.intgeom;

public enum IntAxis {

	X(true), Y(false);

	public final boolean horizontal;
	public final boolean vertical;

	private IntAxis(boolean horizontal) {
		this.horizontal = horizontal;
		this.vertical = !horizontal;
	}

	public IntAxis other() {
		return this == X ? Y : X;
	}

}
