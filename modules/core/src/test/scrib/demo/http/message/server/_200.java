package demo.http.message.server;

import demo.http.Http;

public class _200 extends StatusCode
{
	private static final long serialVersionUID = 1L;

	public _200(String reason)
	{
		super(Http._200, reason);
	}
}