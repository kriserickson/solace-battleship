/**
 * SolaceClient
 * Initialize Solace consumer using values
 * @author Andrew Roberts
 */

package com.solace.battleship_backend;

import com.solacesystems.jcsmp.XMLMessageConsumer;

public class SolaceClient {



    public SolaceClient(String SOLACE_URI, String SOLACE_VPN, String SOLACE_USER, String SOLACE_PASSWORD) {
        System.out.println(SOLACE_URI + SOLACE_VPN + SOLACE_USER + SOLACE_PASSWORD);
    }
}
