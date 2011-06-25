package edu.cwru.apo;

public interface AsyncRestRequestListener<U, T> {
	public void onRestRequestComplete(U method, T result);
}
