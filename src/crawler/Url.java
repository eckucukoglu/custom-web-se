package crawler;

import java.util.Comparator;

/**
* Url class contains url address and
* depth of a url. Depth are computed
* wrt initial seed page url address.
*
* @author EmreCan
*
*/
public class Url implements Comparable<Url>, Comparator<Url>{
	
	private String url;
	private int depth;
	
	/**
	* Each Url object has string web url
	* and int value as a depth value.
	*
	* @param url
	* @param depth
	*/
	Url (String url, int depth) {
		this.url = url;
		this.depth = depth;
	}
	
	/**
	* Copy constructor.
	*
	* @param url
	*/
	Url (Url url) {
		this.url = url.url;
		this.depth = url.depth;
	}
	
	/**
	*
	* @return depth of a Url.
	*/
	public int getDepth () {
		return depth;
	}
	
	/**
	*
	* @return address of a Url.
	*/
	public String getUrl () {
		return url;
	}
	
	@Override
	public int compare(Url o1, Url o2) {
		return o1.getUrl().compareTo(o2.getUrl());
	}
	
	@Override
	public int compareTo(Url arg0) {
		return this.url.compareTo(arg0.getUrl());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Url other = (Url) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		
		return true;
	}
	
}
