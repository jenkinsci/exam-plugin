/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * <p>
 * 3. Neither the name of MicroNova AG nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jenkins.internal.enumeration;

/**
 * Enum for the data base systems supported by EXAM.
 * <ul>
 * <li>Example for an Oracle SID (e.g. XE) URL is jdbc:oracle:thin:@localhost:1521:XE</li>
 * <li>Example for an Oracle Service URL is jdbc:oracle:thin:@//localhost:1521/exam</li>
 * <li>Example for a MySQL URL is jdbc:mysql://localhost:3306/exam</li>
 * </ul>
 *
 * @author spreuer
 */
public enum DbKind {
    
    /**
     * Oracle 10g.
     */
    ORACLE_SERVICE(/* userString */"Oracle Service", //$NON-NLS-1$
            /* scriptDirectoryString */"oracle", //$NON-NLS-1$
            /* jdbcDriverClass */"oracle.jdbc.driver.OracleDriver", //$NON-NLS-1$
            /* urlPrefix */"jdbc:oracle:thin:@", //$NON-NLS-1$
            /* defaultPort */1521, //
            /* schemaDelimiter */"/", //$NON-NLS-1$
            /* urlPostfix */""), //$NON-NLS-1$
    
    /**
     * Oracle 10g, Express Edition.
     */
    ORACLE_SID(/* userString */"Oracle SID (e.g. XE)", //$NON-NLS-1$
            /* scriptDirectoryString */"oracle", //$NON-NLS-1$
            /* jdbcDriverClass */"oracle.jdbc.driver.OracleDriver", //$NON-NLS-1$
            /* urlPrefix */"jdbc:oracle:thin:@", //$NON-NLS-1$
            /* defaultPort */1521, //
            /* schemaDelimiter */":", //$NON-NLS-1$
            /* urlPostfix */""), //$NON-NLS-1$
    
    /**
     * PostgreSQL.
     */
    POSTGRESQL(/* userString */"PostgreSQL", //$NON-NLS-1$
            /* scriptDirectoryString */"postgresql", //$NON-NLS-1$
            /* jdbcDriverClass */"org.postgresql.Driver", //$NON-NLS-1$
            /* urlPrefix */"jdbc:postgresql://", //$NON-NLS-1$
            /* defaultPort */5432, //
            /* schemaDelimiter */"/", //$NON-NLS-1$
            /* urlPostfix */"?" + "loginTimeout=30");//$NON-NLS-1$//$NON-NLS-2$
    
    /**
     * Get the possible names of the enum.
     *
     * @return the names of the enum as String array
     */
    public static String[] getUserStrings() {
        return new String[] { ORACLE_SERVICE.getUserString(), ORACLE_SID.getUserString(),
                POSTGRESQL.getUserString() };
    }
    
    protected String userString;
    protected String scriptDirectoryString;
    protected String jdbcDriverClass;
    protected String urlPrefix;
    protected int defaultPort;
    protected String urlPostfix;
    protected String schemaDelimiter;
    
    /**
     * Private constructor, as all literals must be created here.
     *
     * @param userString      String
     * @param jdbcDriverClass String
     * @param urlPrefix       String
     *                        Constant used in {@link #getURL(String, int, String, String)}
     * @param defaultPort     int
     */
    DbKind(String userString, String scriptDirectoryString, String jdbcDriverClass, String urlPrefix, int defaultPort,
            String schemaDelimiter, String urlPostfix) {
        this.userString = userString;
        this.scriptDirectoryString = scriptDirectoryString;
        this.jdbcDriverClass = jdbcDriverClass;
        this.urlPrefix = urlPrefix;
        this.urlPostfix = urlPostfix;
        this.schemaDelimiter = schemaDelimiter;
        this.defaultPort = defaultPort;
    }
    
    /**
     * Get the string needed for the database connect string.
     *
     * @param host         host name/IP of the database.
     * @param port         port of the database as integer.
     * @param serviceOrSid Service or SID of the database to open.
     * @param schema       Schema name in the database to open.
     *
     * @return The string needed for the database connect string.
     */
    public String getURL(String host, int port, String serviceOrSid, String schema) {
        return getURL(host, Integer.toString(port), serviceOrSid, schema);
    }
    
    /**
     * Get the string needed for the database connect string.
     *
     * @param host         host name/IP of the database.
     * @param port         port of the database as string.
     * @param serviceOrSid Service or SID of the database to open.
     * @param schema       Schema name in the database to open.
     *
     * @return The string needed for the database connect string.
     */
    public String getURL(String host, String port, String serviceOrSid, String schema) {
        StringBuilder b = new StringBuilder();
        b.append(this.urlPrefix);
        
        // This sequence is common to ALL JDBC connect strings:
        b.append(host);
        b.append(':');
        b.append(port);
        
        b.append(this.schemaDelimiter);
        switch (this) {
            case ORACLE_SERVICE:
                b = new StringBuilder();
                b.append(this.urlPrefix);
                addHostPort(host, port, b);
                
                b.append(serviceOrSid);
                break;
            
            case ORACLE_SID:
                b.append(serviceOrSid);
                break;
            
            case POSTGRESQL:
                b.append(schema);
                break;
            
            default:
                break;
        }
        
        b.append(this.urlPostfix);
        return b.toString();
    }
    
    private void addHostPort(String host, String port, StringBuilder b) {
        // must take care of any possible combination here
        boolean hasHost = host != null && !host.isEmpty();
        boolean hasPort = port != null && !port.isEmpty();
        
        if (hasHost) {
            b.append("//");//$NON-NLS-1$
            b.append(host);
            
            if (!hasPort) {
                b.append(this.schemaDelimiter);
            }
        }
        
        if (hasPort) {
            if (!hasHost) {
                b.append("//");//$NON-NLS-1$
            }
            b.append(':');
            b.append(port);
            b.append(this.schemaDelimiter);
        }
    }
    
    /**
     * Get the string to display to the user.
     *
     * @return The string to display to the user.
     */
    public String getUserString() {
        return this.userString;
    }
    
    /**
     * Get the default port for the database system.
     *
     * @return The default port.
     */
    public int getDefaultPort() {
        return this.defaultPort;
    }
    
    /**
     * Get the JDBC driver for the database system.
     *
     * @return The JDBC driver.
     */
    public String getJDBCDriver() {
        return this.jdbcDriverClass;
    }
    
    // More sets are added when needed, but usually there is no need to
    // distinguish the two Oracle variants...
    
} // DbKind
