import { PlayerName } from './event-objects/player-events';
import {KnownBoardCellState, Move} from './event-objects/board-events';
import obelisk from 'obelisk.js';
import {EventAggregator} from 'aurelia-event-aggregator';
import {bindable,inject} from 'aurelia-framework';
import {MoveResponseEvent} from './event-objects/move-response-event';
import { GameParams } from 'event-objects/game-params';




@inject(obelisk,EventAggregator,GameParams)
export class DashboardBoard {
  @bindable player: String;
  battleshipCanvas;
  pixelView;
  

  constructor(private obelisk:obelisk, private ea: EventAggregator, private gameParams: GameParams){
  }



  attached(){
    //Subscribe to aurelia's event aggregator to receive 'DashboardEvents' 
     this.ea.subscribe(MoveResponseEvent, (msg: MoveResponseEvent) => {
      
      //Render the board based on who this board belongs to (Player1 or Player2)
      this.renderBoard(msg[<PlayerName>this.player +'Board']);

      //Figure out if the move for this event belongs to this boar'ds player
      if(<PlayerName>this.player===msg.move.player){
        //Kick off the missle animation for the player's board
        this.dropMissile(msg[<PlayerName>this.player+'Board'],msg.move.x,msg.move.y,90,0xFF0000);
        //Publish the action from the move event to display on the dashboard
        this.ea.publish('Action',{action: msg.move.action})
      }
    });


    let canvas = this.battleshipCanvas;
    let point = new obelisk.Point(300,100);
    //the center of the screen
    this.pixelView = new obelisk.PixelView(canvas, point);
    this.makeGrid();

    let db = new MoveResponseEvent();
    db.move=new Move();
    let cs:KnownBoardCellState[][];
    cs = [];
    for(let i=0;i<6;i++){
      cs[i]=[];
      for(let j=0;j<6; j++){
        if(i==j){
          cs[i][j]='empty';
        }else if(i%2==0){
          cs[i][j]='empty';
        }else{
          cs[i][j]='empty';
        }
      }
    }

 
   db.Player1Board=cs;
   db.Player2Board=cs;
   db.move.x=0;
   db.move.y=4;
   db.move.player='Player1';
   db.move.action='hit';
   this.ea.publish(db);
  }


  /**
   * Function that renders a missile (a pyramid) on the board
   * @param x x co-ordinate for the missile
   * @param y y co-ordinate for the missile
   * @param z z co-ordinate for the missile
   * @param color color for the missile
   */
  renderMissile(x:number,y:number,z:number,color:number){
    let dimension = new obelisk.PyramidDimension(60);
    let pyColor = new obelisk.PyramidColor().getByRightColor(obelisk.ColorPattern.YELLOW);
    let pyramid = new obelisk.Pyramid(dimension, pyColor);
    this.pixelView.renderObject(pyramid, new obelisk.Point3D(x,y,z));
  }

  /**
   * A function that renders a colored square to indicate whether the move is a hit or a miss
   * @param x x co-ordinate for the square
   * @param y y co-ordiante for the square
   * @param z z co-ordinate for the square
   * @param color for the square
   */
  renderSquare(x:number,y:number,z:number,color:number){
    let brickDimension = new obelisk.BrickDimension(this.gameParams.gameboardDimensions, this.gameParams.gamecellPixelSize);
    let brickColor = new obelisk.SideColor().getByInnerColor(color);
    let brick = new obelisk.Brick(brickDimension, brickColor);
    this.pixelView.renderObject(brick, new obelisk.Point3D(x, y, z));
  }

  /**
   * A function that renders the board with appropriate state for each of the cells
   * @param cells CellState is a an object that consists of three states (hit, miss, or empty)
   */
  renderBoard(cells: KnownBoardCellState[][]){
    this.pixelView.clear();
    this.makeGrid();
    for(let i=0;i<this.gameParams.gameboardDimensions;i++){
      for(let j=0;j<this.gameParams.gameboardDimensions;j++){
        if(cells[i][j]==='hit'){
          this.renderSquare(i*this.gameParams.gamecellPixelSize,j*this.gameParams.gamecellPixelSize,0,0x00FF00);
        }else if(cells[i][j]==='miss'){
          this.renderSquare(i*this.gameParams.gamecellPixelSize,j*this.gameParams.gamecellPixelSize,0,0xFF0000);
        }
      }
    }
  }


  /**
   * Function that animates the board with the dropMissle animation
   * @param cellState An 2 dimensional array of CellState's that contains the state of every cell in the grid
   * @param missileX The co-ordinate of the missile drop
   * @param missileY The y co-ordinate of the missile drop
   * @param missileZ The z co-ordinate of the missile drop
   * @param color The color of the missile
   */
  dropMissile(cellState:KnownBoardCellState[][],missileX: number,missileY:number,missileZ:number,color:number){
    this.renderBoard(cellState);
    this.renderMissile(missileX*this.gameParams.gamecellPixelSize,missileY*this.gameParams.gamecellPixelSize,missileZ,color);
    if(missileZ>-(missileX*missileY*10)){
      missileZ-=5;
      requestAnimationFrame(() => this.dropMissile(cellState,missileX,missileY,missileZ,color));
    }else{
      cellState[missileX][missileY]='miss';
      this.renderBoard(cellState);
    }
  }


  /**
   * The makeGrid function draws the grid based on the properties setup in boardProperties
   */
  makeGrid(){
    let lineColor = new obelisk.LineColor();
    let dimensionX = new obelisk.LineXDimension(this.gameParams.gameboardDimensions * this.gameParams.gamecellPixelSize);
    let dimensionY = new obelisk.LineYDimension(this.gameParams.gameboardDimensions * this.gameParams.gamecellPixelSize);
    let lineX = new obelisk.LineX(dimensionX, lineColor);
    let lineY = new obelisk.LineY(dimensionY, lineColor);

      // Create Grid:
      for (let x = 0; x < this.gameParams.gameboardDimensions + 1; x++) {
        this.pixelView.renderObject(lineX, new obelisk.Point3D(0, x * this.gameParams.gamecellPixelSize, 0));
      }
      for (let y = 0; y < this.gameParams.gameboardDimensions + 1; y++) {
        this.pixelView.renderObject(lineY, new obelisk.Point3D(y * this.gameParams.gamecellPixelSize, 0, 0));
      }
  }

}
 