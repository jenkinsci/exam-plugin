/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *        list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this
 *        list of conditions and the following disclaimer in the documentation and/or
 *        other materials provided with the distribution.
 *
 *     3. Neither the name of MicroNova AG nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
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

import org.junit.Test;

import static org.junit.Assert.*;

public class DbKindTest {

    @Test
    public void getUserStrings() {
        String[] expect = new String[]{"Oracle Service","Oracle SID (e.g. XE)","PostgreSQL"};
        assertArrayEquals(expect, DbKind.getUserStrings());
    }

    @Test
    public void getURL() {
        String url = "";
        url = DbKind.POSTGRESQL.getURL("localhost",8080,"service","mySchema");
        assertEquals("jdbc:postgresql://localhost:8080/mySchema?loginTimeout=30", url);
        url = DbKind.ORACLE_SID.getURL("localhost",8080,"service","mySchema");
        assertEquals("jdbc:oracle:thin:@localhost:8080:service", url);

        url = DbKind.ORACLE_SERVICE.getURL("localhost",8080,"service","mySchema");
        assertEquals("jdbc:oracle:thin:@//localhost:8080/service", url);
        url = DbKind.ORACLE_SERVICE.getURL("localhost","","service","mySchema");
        assertEquals("jdbc:oracle:thin:@//localhost/service", url);
        url = DbKind.ORACLE_SERVICE.getURL("",8080,"service","mySchema");
        assertEquals("jdbc:oracle:thin:@//:8080/service", url);
    }


    @Test
    public void getUserString() {
        assertEquals("PostgreSQL", DbKind.POSTGRESQL.getUserString());
        assertEquals("Oracle Service", DbKind.ORACLE_SERVICE.getUserString());
        assertEquals("Oracle SID (e.g. XE)", DbKind.ORACLE_SID.getUserString());
    }

    @Test
    public void getDefaultPort() {
        assertEquals(5432, DbKind.POSTGRESQL.getDefaultPort());
        assertEquals(1521, DbKind.ORACLE_SERVICE.getDefaultPort());
        assertEquals(1521, DbKind.ORACLE_SID.getDefaultPort());
    }

    @Test
    public void getJDBCDriver() {
        assertEquals("org.postgresql.Driver", DbKind.POSTGRESQL.getJDBCDriver());
        assertEquals("oracle.jdbc.driver.OracleDriver", DbKind.ORACLE_SERVICE.getJDBCDriver());
        assertEquals("oracle.jdbc.driver.OracleDriver", DbKind.ORACLE_SID.getJDBCDriver());
    }
}
