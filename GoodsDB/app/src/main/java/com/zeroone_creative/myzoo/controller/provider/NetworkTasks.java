package com.zeroone_creative.myzoo.controller.provider;

import com.android.volley.Request;

public enum NetworkTasks {
    //login
    Regist(1, Request.Method.POST),
    Create(2, Request.Method.POST),
    Gallery(3, Request.Method.GET),

	;
	public int id;
	//Request
	public int method;
	
	private NetworkTasks(int id, int method) {
		this.id = id;
		this.method = method;
	}
}
