package util;
import javax.servlet.http.HttpSession;
public class MySession  {
	HttpSession session;
	public MySession(HttpSession session){
		this.session = session;
	}
	public Object get(String key){
		return this.session.getAttribute(key);
	}
	public void add(String key,Object valeur){
		this.session.setAttribute(key,valeur);
	}
	public void delete(String key){
		this.session.removeAttribute(key);
	}
	public void invalidate(){
		this.session.invalidate();
	}
}