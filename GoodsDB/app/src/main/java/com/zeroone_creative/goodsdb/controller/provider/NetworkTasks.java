package com.zeroone_creative.goodsdb.controller.provider;

import com.android.volley.Request;

public enum NetworkTasks {
    //login
    GoodsPost(1, Request.Method.POST),
    GoodsGet(2, Request.Method.GET),
    UserLogin(3, Request.Method.POST),
    GoodsSearch(4, Request.Method.GET),
    GoodsEdit(5, Request.Method.PUT),
    LikePost(6, Request.Method.POST),
    LikeDelete(7, Request.Method.DELETE),
    GoodsDelete(8, Request.Method.DELETE),
	;
	public int id;
	//Request
	public int method;
	
	private NetworkTasks(int id, int method) {
		this.id = id;
		this.method = method;
	}
}
