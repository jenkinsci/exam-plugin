package jenkins.internal.data;

import java.io.Serializable;

public class TCGResult implements Serializable {

    private String message;
    private int code;
    private String exception;

    /**
     * gets the message
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * sets the message
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * gets the code
     *
     * @return the code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * sets the code
     *
     * @param code the code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * gets the exception
     *
     * @return the exception
     */
    public String getException() {
        return this.exception;
    }

    /**
     * sets the exception
     *
     * @param exception the exception
     */
    public void setException(String exception) {
        this.exception = exception;
    }

}
