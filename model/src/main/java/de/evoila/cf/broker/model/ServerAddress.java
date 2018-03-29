/**
 * 
 */
package de.evoila.cf.broker.model;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class ServerAddress {
	
<<<<<<< HEAD
	private String name;

	private String ip;

	private int port;
=======
	private String ip;
	private int port;
	private String name;
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f

	public ServerAddress() {
		super();
	}

	public ServerAddress(String name) {
		super();
		this.name = name;
	}

	public ServerAddress(String name, String ip) {
		super();
		this.name = name;
		this.ip = ip;
	}

	public ServerAddress(String name, String ip, int port) {
		super();
		this.name = name;
		this.ip = ip;
		this.port = port;
	}

	public ServerAddress(ServerAddress address) {
		super();
		this.name = address.name;
		this.ip = address.ip;
		this.port = address.port;
	}

<<<<<<< HEAD
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
=======
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
}
