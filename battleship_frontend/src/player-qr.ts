import { bindable } from "aurelia-framework";

export class PlayerQr {
    @bindable
    player : string;
    @bindable
    playerJoined: boolean;

    playerQRUrl : string;
    joinStatus: string;
    playerJoinUrl: string;

    constructor(){
    }

    playerJoinedChanged(newValue: boolean, oldValue:boolean){
        if(newValue){
            this.joinStatus = `${this.player} joined!`;
        }
    }

    attached(){
        this.joinStatus = `Waiting for ${this.player} to join...`;
        this.playerJoinUrl= `http://${location.host}/join/${this.player}`;
        this.playerQRUrl = `https://api.qrserver.com/v1/create-qr-code/?data=${encodeURI(this.playerJoinUrl)}&amp;size=200x200&amp;color=00CB95&amp;bgcolor=333333`;
    }
    
}