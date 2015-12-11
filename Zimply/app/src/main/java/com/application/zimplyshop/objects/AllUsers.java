package com.application.zimplyshop.objects;

import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.SignupObject;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class AllUsers implements Serializable {

	public static AllUsers sInstance;

	private ArrayList<AddressObject> objs;

	public ArrayList<AddressObject> getObjs() {
		if(objs==null){
			objs = new ArrayList<>();
		}
		return objs;
	}

	public void setObjs(ArrayList<AddressObject> objs) {
		if(this.objs == null){
			this.objs= new ArrayList<>();
		}
		this.objs = objs;
	}

	public static AllUsers getInstance() {
		if (sInstance == null) {
			sInstance = new AllUsers();
		}
		return sInstance;
	}

	public Object parseUserSignup(String responseString) {
		return new Gson().fromJson(responseString, SignupObject.class);
	}

    public void swapAddress(int position){
        if(objs.size()>position ) {
            AddressObject obj = objs.get(0);
            objs.set(0,objs.get(position));
            objs.set(position,obj);
        }
    }
}
