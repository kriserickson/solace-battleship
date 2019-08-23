
package com.solace.battleship_backend;

import com.solacesystems.jcsmp.*;
import io.github.cdimascio.dotenv.Dotenv;


public class App 
{
  private XMLMessageConsumer consumer;
  private XMLMessageProducer producer;

  public void run(String[] args) throws JCSMPException{
    // load in environment variables
    Dotenv dotenv = Dotenv.load();
    final String SOLACE_HOST_URI = dotenv.get("SOLACE_HOST_URI");
    final String SOLACE_CLIENT_USERNAME = dotenv.get("SOLACE_CLIENT_USERNAME");
    final String SOLACE_MSG_VPN = dotenv.get("SOLACE_MSG_VPN");
    final String SOLACE_CLIENT_PASSWORD = dotenv.get("SOLACE_CLIENT_PASSWORD");

    // configure Solace session
    final JCSMPProperties properties = new JCSMPProperties();
    properties.setProperty(JCSMPProperties.HOST, SOLACE_HOST_URI);     // host:port
    properties.setProperty(JCSMPProperties.USERNAME, SOLACE_CLIENT_USERNAME); // client-username
    properties.setProperty(JCSMPProperties.VPN_NAME,  SOLACE_MSG_VPN); // message-vpn
    if(!SOLACE_CLIENT_PASSWORD.isEmpty()) {
      properties.setProperty(JCSMPProperties.PASSWORD, SOLACE_CLIENT_PASSWORD); // client-password
    }
    final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
    session.connect();

    // setup battleship application
//    String matchId = "Example match";
//    for(int i = 0; i<
//    final Match match = new Match(matchId);
  }

  public static void main( String[] args ) throws JCSMPException
  {
    App app = new App();
    app.run(args);
  }
}
