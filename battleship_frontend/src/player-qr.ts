import { PlayerName } from './event-objects/player-events';
import { bindable } from "aurelia-framework";

/**
 * Represnts the QRCode that a player uses to join
 * @author Thomas Kunnumpurath
 */
export class PlayerQr {
    //Name of the player
    @bindable
    player : PlayerName;
    @bindable
    playerJoined: boolean;

    //URL of the QR Code image
    playerQRUrl : string;
    //Status message for the player 
    joinStatus: string;
    //URL for the player joined
    playerJoinUrl: string;

    /**
     * Aurelia function that determines whether the playerJoined variable has changed
     * @param newValue the changed status of whether the player has joined 
     * @param oldValue the old status of whether the player has joined
     */
    playerJoinedChanged(newValue: boolean, oldValue:boolean){
        if(newValue){
            this.joinStatus = `${this.player} joined!`;
        }
    }

    /**
     * Aurelia function that is called when the page is loaded
     */
    attached(){
        this.joinStatus = `Waiting for ${this.player} to join...`;
        this.playerJoinUrl= `http://${location.host}/join/${this.player}`;
        this.playerQRUrl = `https://api.qrserver.com/v1/create-qr-code/?data=${encodeURI(this.playerJoinUrl)}&amp;size=200x200&amp;color=00CB95&amp;bgcolor=333333`;
    }
    
}