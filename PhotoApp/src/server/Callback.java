package server;

public interface Callback<T>
{
	public void OnTaskFinished(T result);

}
