package jenkins.internal.data;

/**
 * Version information of this API
 */
public class ApiVersion {
    private int major;
    private int minor;
    private int fix;

    /**
     * Constructor of version information of this API
     */
    public ApiVersion() {
        this.major = 1;
        this.minor = 0;
        this.fix = 0;
    }

    public int getMajor() {
        return this.major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return this.minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getFix() {
        return this.fix;
    }

    public void setFix(int fix) {
        this.fix = fix;
    }
}
