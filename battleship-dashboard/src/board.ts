import { BoardState, Player, CellState, Move } from './event-objects/board-events';
import obelisk from 'obelisk.js';
import {EventAggregator} from 'aurelia-event-aggregator';
import {bindable,inject} from 'aurelia-framework';
import DashboardEvent from './event-objects/dashboard-events';

interface BoardProperties {
  units: number,
  size: number
}


@inject(obelisk,EventAggregator)
export class Board {
  @bindable player: String;
  battleshipCanvas;
  pixelView;
  boardProperties : BoardProperties;

  constructor(private obelisk:obelisk, private ea: EventAggregator){
    this.boardProperties = {
      'units':5,
      'size':60
    };
  }



  attached(){
     this.ea.subscribe(DashboardEvent, msg => {
      this.renderBoard(msg.boards[<Player>this.player].boardState);
      if(<Player>this.player===msg.move.player){
        this.dropCube(msg.boards[<Player>this.player].boardState,msg.move.x,msg.move.y,90,0xFF0000);
      }
    });
    let canvas = this.battleshipCanvas;
    if(canvas){
      let point = new obelisk.Point(300,100);
      //the center of the screen
      this.pixelView = new obelisk.PixelView(canvas, point);
      // this.dropCube(0,0,90,0x008000);
      this.makeGrid();
    }

    let db = new DashboardEvent();
    db.boards=[];
    db.move=new Move();
    let bs = new BoardState();
    let cs:CellState[][];
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

   bs.boardState = cs;
   db.boards['Player1']=bs;
   db.boards['Player2']=bs;
   db.move.x=0;
   db.move.y=4;
   db.move.player='Player1';
   this.ea.publish(db);
  }

  renderCube(x:number,y:number,z:number,color:number){
    let dimensionCube = new obelisk.CubeDimension(this.boardProperties.size, this.boardProperties.size, this.boardProperties.size);
    let cubeColor = new obelisk.CubeColor().getByHorizontalColor(color);
    let cube = new obelisk.Cube(dimensionCube, cubeColor);
    this.pixelView.renderObject(cube, new obelisk.Point3D(x, y, z,color)); 
  }

  renderBoard(cells: CellState[][]){
    this.pixelView.clear();
    this.makeGrid();
    for(let i=0;i<this.boardProperties.units;i++){
      for(let j=0;j<this.boardProperties.units;j++){
        if(cells[i][j]==='hit'){
          this.renderCube(i*this.boardProperties.size,j*this.boardProperties.size,0,0x00FF00);
        }else if(cells[i][j]==='miss'){
          this.renderCube(i*this.boardProperties.size,j*this.boardProperties.size,0,0xFF0000);
        }
      }
    }
  }


  dropCube(cellState:CellState[][],cubesX: number,cubesY:number,cubesZ:number,color:number){
    // this.pixelView.clear();
    // this.makeGrid();
    console.log(cubesX);
    this.renderBoard(cellState);

    this.renderCube(cubesX*this.boardProperties.size,cubesY*this.boardProperties.size,cubesZ,color);

    
    if(cubesZ>-(cubesX*cubesY*10)){
      cubesZ-=5;
      requestAnimationFrame(() => this.dropCube(cellState,cubesX,cubesY,cubesZ,color));
    }else{
      cellState[cubesX][cubesY]='miss';
      console.log(cellState);
      this.renderBoard(cellState);
    }
  }


  makeGrid(){
      // Let's build a grid...  
    // Setup lines
    let lineColor = new obelisk.LineColor();
    let dimensionX = new obelisk.LineXDimension(this.boardProperties.units * this.boardProperties.size);
    let dimensionY = new obelisk.LineYDimension(this.boardProperties.units * this.boardProperties.size);
    let lineX = new obelisk.LineX(dimensionX, lineColor);
    let lineY = new obelisk.LineY(dimensionY, lineColor);

      // Create Grid:
      for (let x = 0; x < this.boardProperties.units + 1; x++) {
        this.pixelView.renderObject(lineX, new obelisk.Point3D(0, x * this.boardProperties.size, 0));
      }
      for (let y = 0; y < this.boardProperties.units + 1; y++) {
        this.pixelView.renderObject(lineY, new obelisk.Point3D(y * this.boardProperties.size, 0, 0));
      }
  }

}
 