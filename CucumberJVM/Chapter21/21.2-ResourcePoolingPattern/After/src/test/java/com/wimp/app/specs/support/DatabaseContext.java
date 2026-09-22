package com.wimp.app.specs.support;


/**
 * Context class that holds information about a pooled database.
 * <p>
 * Each pooled database has a unique DatabaseContext class instance, therefore
 * it can also be used to store any database-related information (e.g., whether
 * it is dirty and needs to be reset on next use).
 */
public final class DatabaseContext {
    private final String url;
    private final String username;
    private final String password;
    private boolean isLeased;

    public DatabaseContext(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.isLeased = true;
    }

    public String url() {
        return url;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public boolean isLeased() {
        return isLeased;
    }

    public void setLeased(boolean leased) {
        isLeased = leased;
    }
}
