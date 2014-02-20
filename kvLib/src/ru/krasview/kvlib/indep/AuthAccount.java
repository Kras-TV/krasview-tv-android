package ru.krasview.kvlib.indep;

public class AuthAccount {
	
	public final static int AUTH_TYPE_UNKNOWN = 0;//без логина, как гость
	public final static int AUTH_TYPE_TV = 1;//как абонент красноярской сети
	public final static int AUTH_TYPE_KRASVIEW = 2;//как пользователь krasview
	public final static int AUTH_TYPE_GUEST = 3;//как неавторизованный пользователь
	public final static int AUTH_TYPE_KRASVIEW_SOCIAL = 4;//как пользователь krasview через социальную сеть 
	
	private static AuthAccount instance;
	
	private AuthAccount(){
		super();
	};
	
	public static AuthAccount getInstance(){
		if(instance == null){
			instance = new AuthAccount();
		}
		return instance;
	}
	
	private int mType = AUTH_TYPE_UNKNOWN;
	private String mLogin = "";
	private String mPassword = "";
	private String mHash = "1";
	private String mTvHash = "1";
	
	private int getType(){
		return mType;
	}
	
	public String getLogin(){
		return mLogin;
	}

	public String getPassword(){
		return mPassword;
	}
	
	public String getHash(){
		return mHash;
	}
	
	public String getTvHash(){
		return mTvHash;
	}
	
	
	public void setType(int type){
		mType = type;
	}

	public void setLogin(String login){
		mLogin = login;
	}
	
	public void setPassword(String password){
		mPassword = password;
	}
	
	public void setHash(String hash){
		mHash = hash;
	}
	
	public void setTvHash(String tvHash){
		mTvHash = tvHash;
	}
	
	public boolean isKrasviewAccount(){
		return mType == AuthAccount.AUTH_TYPE_KRASVIEW 
				|| mType == AuthAccount.AUTH_TYPE_KRASVIEW_SOCIAL;
	}
	
	public boolean isTVAccount(){
		return isKrasviewAccount() || getType() == AuthAccount.AUTH_TYPE_TV;
	}
	
	public boolean isSocialNetworkAccount(){
		return mType == AuthAccount.AUTH_TYPE_KRASVIEW_SOCIAL;
	}
	
	public boolean isUnknownAccount(){
		return AuthAccount.getInstance().getType() == AuthAccount.AUTH_TYPE_UNKNOWN;
	}
	
	
}
