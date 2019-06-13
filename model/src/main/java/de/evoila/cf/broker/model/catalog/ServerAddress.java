package de.evoila.cf.broker.model.catalog;

/**
 * @author Christian Brinker.
 */
public class ServerAddress {

	private String name;

	private String ip;

	private int port;

	private boolean backup;

	public ServerAddress() {
	}

	public ServerAddress(String name) {
		this(name, null);
	}

	public ServerAddress(String name, String ip) {
		this(name, ip, 0);
	}

	public ServerAddress(String name, String ip, int port) {
		this(name, ip, port, false);
	}

	public ServerAddress(String name, String ip, int port, boolean backup) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.backup = backup;
    }

	public ServerAddress(ServerAddress address) {
	    this(address.getName(), address.getIp(), address.getPort());
	}

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

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }
}
