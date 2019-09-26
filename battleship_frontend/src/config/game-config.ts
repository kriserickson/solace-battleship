/**
 * Configuration for the game
 * solace_hostUrl - WebSocket Host Address for the Solace PubSub+ Broker
 * solace_vpn - VPN for the Solace PubSub+ Broker
 * solace_userName - Username for the PubSub+ Broker
 * solace_password - Password for the PubSub+ Broker
 * allowed_ships - Ships allowed per player
 * gameboard_dimensions - Number of rows and columns for the sqare gameboard
 * 
 * @author Thomas Kunnumpurath, Andrew Roberts
 */
export const gameConfig = {
  solace_hostUrl: "wss://mrrwtxvkmpdxv.messaging.solace.cloud:20004",
  solace_vpn: "msgvpn-zpfs1b9g8px",
  solace_userName: "solace-cloud-client",
  solace_password: "4r43sbnrcsh8cj2r57oav619k7",
  allowed_ships: 5,
  gameboard_dimensions: 5
};
